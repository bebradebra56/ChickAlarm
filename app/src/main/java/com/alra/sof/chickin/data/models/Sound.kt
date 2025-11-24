package com.alra.sof.chickin.data.models

import java.util.UUID

data class SoundScene(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String,
    val layers: List<SoundLayer> = emptyList(),
    val isCustom: Boolean = false
)

data class SoundLayer(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val soundId: String,
    val volume: Float = 1.0f, // 0.0 to 1.0
    val isEnabled: Boolean = true
)

enum class SoundType {
    ROOSTER, NATURE, AMBIENT, WHITE_NOISE, MUSIC
}

data class Sound(
    val id: String,
    val name: String,
    val type: SoundType,
    val resourcePath: String = ""
)

val defaultSounds = listOf(
    Sound("gentle_rooster", "Gentle Rooster", SoundType.ROOSTER),
    Sound("farm_morning", "Farm Morning", SoundType.ROOSTER),
    Sound("jazz_rooster", "Jazz Rooster", SoundType.ROOSTER),
    Sound("forest_dawn", "Forest Dawn", SoundType.NATURE),
    Sound("rain_morning", "Rain & Birds", SoundType.NATURE),
    Sound("ocean_waves", "Ocean Waves", SoundType.NATURE),
    Sound("white_noise", "White Noise", SoundType.WHITE_NOISE),
    Sound("pink_noise", "Pink Noise", SoundType.WHITE_NOISE),
    Sound("brown_noise", "Brown Noise", SoundType.WHITE_NOISE)
)

val defaultScenes = listOf(
    SoundScene(
        name = "Village Morning",
        description = "Wake up to countryside sounds",
        layers = listOf(
            SoundLayer(name = "Rooster", soundId = "gentle_rooster"),
            SoundLayer(name = "Birds", soundId = "forest_dawn", volume = 0.6f)
        )
    ),
    SoundScene(
        name = "Forest Dawn",
        description = "Natural awakening in the woods",
        layers = listOf(
            SoundLayer(name = "Birds", soundId = "forest_dawn"),
            SoundLayer(name = "Light Rain", soundId = "rain_morning", volume = 0.4f)
        )
    ),
    SoundScene(
        name = "Rainy Morning",
        description = "Gentle rain with rooster call",
        layers = listOf(
            SoundLayer(name = "Rain", soundId = "rain_morning", volume = 0.7f),
            SoundLayer(name = "Rooster", soundId = "farm_morning", volume = 0.5f)
        )
    )
)

