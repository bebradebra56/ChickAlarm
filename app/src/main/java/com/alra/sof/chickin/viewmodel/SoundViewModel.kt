package com.alra.sof.chickin.viewmodel

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.alra.sof.chickin.data.models.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class SoundViewModel(application: android.app.Application) : AndroidViewModel(application) {
    
    private val _soundScenes = MutableStateFlow(createDefaultScenes())
    val soundScenes: StateFlow<List<SoundScene>> = _soundScenes.asStateFlow()
    
    private val _selectedScene = MutableStateFlow<SoundScene?>(null)
    val selectedScene: StateFlow<SoundScene?> = _selectedScene.asStateFlow()
    
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()
    
    private val mediaPlayers = mutableMapOf<String, MediaPlayer>()
    private val context = application.applicationContext
    
    fun selectScene(scene: SoundScene) {
        stopCurrentScene()
        _selectedScene.value = scene
    }
    
    fun playScene() {
        val scene = _selectedScene.value ?: return
        _isPlaying.value = true
        
        scene.layers.forEach { layer ->
            if (layer.isEnabled) {
                playLayer(layer)
            }
        }
    }
    
    fun pauseScene() {
        _isPlaying.value = false
        mediaPlayers.values.forEach { it.pause() }
    }
    
    fun stopScene() {
        _isPlaying.value = false
        mediaPlayers.values.forEach { 
            it.stop()
            it.release()
        }
        mediaPlayers.clear()
    }
    
    fun updateLayerVolume(layerId: String, volume: Float) {
        _selectedScene.value?.let { scene ->
            val updatedLayers = scene.layers.map { layer ->
                if (layer.id == layerId) {
                    layer.copy(volume = volume)
                } else layer
            }
            _selectedScene.value = scene.copy(layers = updatedLayers)
            
            // Update media player volume
            mediaPlayers[layerId]?.setVolume(volume, volume)
        }
    }
    
    fun toggleLayer(layerId: String) {
        _selectedScene.value?.let { scene ->
            val updatedLayers = scene.layers.map { layer ->
                if (layer.id == layerId) {
                    layer.copy(isEnabled = !layer.isEnabled)
                } else layer
            }
            _selectedScene.value = scene.copy(layers = updatedLayers)
            
            if (_isPlaying.value) {
                val layer = updatedLayers.find { it.id == layerId }
                if (layer?.isEnabled == true) {
                    playLayer(layer)
                } else {
                    mediaPlayers[layerId]?.pause()
                }
            }
        }
    }
    
    fun deleteScene(sceneId: String) {
        _soundScenes.value = _soundScenes.value.filter { it.id != sceneId }
        if (_selectedScene.value?.id == sceneId) {
            stopCurrentScene()
            _selectedScene.value = null
        }
    }
    
    private fun playLayer(layer: SoundLayer) {
        try {
            // Try to play from raw resources first
            val resourceId = getResourceIdForSound(layer.soundId)
            if (resourceId != 0) {
                val mediaPlayer = MediaPlayer().apply {
                    setDataSource(context, Uri.parse("android.resource://${context.packageName}/$resourceId"))
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                    setVolume(layer.volume, layer.volume)
                    isLooping = true
                    prepare()
                    start()
                }
                mediaPlayers[layer.id] = mediaPlayer
            } else {
                // Fallback to system sounds
                playSystemSound(layer)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Fallback to system sounds
            playSystemSound(layer)
        }
    }
    
    private fun getResourceIdForSound(soundId: String): Int {
        return when (soundId) {
            "rooster" -> context.resources.getIdentifier("rooster", "raw", context.packageName)
            "birds" -> context.resources.getIdentifier("birds", "raw", context.packageName)
            "wind" -> context.resources.getIdentifier("wind", "raw", context.packageName)
            "forest_birds" -> context.resources.getIdentifier("forest_birds", "raw", context.packageName)
            "stream" -> context.resources.getIdentifier("stream", "raw", context.packageName)
            "leaves" -> context.resources.getIdentifier("leaves", "raw", context.packageName)
            "waves" -> context.resources.getIdentifier("waves", "raw", context.packageName)
            "seagulls" -> context.resources.getIdentifier("seagulls", "raw", context.packageName)
            "rain" -> context.resources.getIdentifier("rain", "raw", context.packageName)
            "thunder" -> context.resources.getIdentifier("thunder", "raw", context.packageName)
            "white_noise" -> context.resources.getIdentifier("white_noise", "raw", context.packageName)
            else -> 0
        }
    }
    
    private fun playSystemSound(layer: SoundLayer) {
        try {
            val systemUri = when (layer.id) {
                "rooster" -> RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                "birds", "forest_birds" -> RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                "wind", "leaves" -> RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
                "waves", "stream" -> RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                "rain", "thunder" -> RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                "seagulls" -> RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
                "white_noise" -> RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                else -> RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            }
            
            val mediaPlayer = MediaPlayer().apply {
                setDataSource(context, systemUri)
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                setVolume(layer.volume, layer.volume)
                isLooping = true
                prepare()
                start()
            }
            mediaPlayers[layer.id] = mediaPlayer
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun stopCurrentScene() {
        stopScene()
    }
    
    override fun onCleared() {
        super.onCleared()
        stopScene()
    }
    
    private fun createDefaultScenes(): List<SoundScene> {
        return listOf(
            SoundScene(
                id = "village_morning",
                name = "Village Morning",
                description = "Peaceful farm sounds with roosters",
                isCustom = false,
                layers = listOf(
                    SoundLayer(
                        id = "rooster",
                        name = "Rooster",
                        soundId = "rooster",
                        volume = 0.8f,
                        isEnabled = true
                    ),
                    SoundLayer(
                        id = "birds",
                        name = "Birds",
                        soundId = "birds",
                        volume = 0.6f,
                        isEnabled = true
                    ),
                    SoundLayer(
                        id = "wind",
                        name = "Wind",
                        soundId = "wind",
                        volume = 0.4f,
                        isEnabled = false
                    )
                )
            ),
            SoundScene(
                id = "forest_awakening",
                name = "Forest Awakening",
                description = "Nature sounds for gentle wake-up",
                isCustom = false,
                layers = listOf(
                    SoundLayer(
                        id = "forest_birds",
                        name = "Forest Birds",
                        soundId = "forest_birds",
                        volume = 0.7f,
                        isEnabled = true
                    ),
                    SoundLayer(
                        id = "stream",
                        name = "Stream",
                        soundId = "stream",
                        volume = 0.5f,
                        isEnabled = true
                    ),
                    SoundLayer(
                        id = "leaves",
                        name = "Leaves",
                        soundId = "leaves",
                        volume = 0.3f,
                        isEnabled = false
                    )
                )
            ),
            SoundScene(
                id = "ocean_waves",
                name = "Ocean Waves",
                description = "Calming ocean sounds",
                isCustom = false,
                layers = listOf(
                    SoundLayer(
                        id = "waves",
                        name = "Waves",
                        soundId = "waves",
                        volume = 0.8f,
                        isEnabled = true
                    ),
                    SoundLayer(
                        id = "seagulls",
                        name = "Seagulls",
                        soundId = "seagulls",
                        volume = 0.4f,
                        isEnabled = false
                    )
                )
            ),
            SoundScene(
                id = "rain_forest",
                name = "Rain Forest",
                description = "Gentle rain with forest ambiance",
                isCustom = false,
                layers = listOf(
                    SoundLayer(
                        id = "rain",
                        name = "Rain",
                        soundId = "rain",
                        volume = 0.7f,
                        isEnabled = true
                    ),
                    SoundLayer(
                        id = "thunder",
                        name = "Thunder",
                        soundId = "thunder",
                        volume = 0.3f,
                        isEnabled = false
                    )
                )
            ),
            SoundScene(
                id = "white_noise",
                name = "White Noise",
                description = "Soothing white noise for focus",
                isCustom = false,
                layers = listOf(
                    SoundLayer(
                        id = "white_noise",
                        name = "White Noise",
                        soundId = "white_noise",
                        volume = 0.6f,
                        isEnabled = true
                    )
                )
            )
        )
    }
}