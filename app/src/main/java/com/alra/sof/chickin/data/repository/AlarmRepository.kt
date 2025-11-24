package com.alra.sof.chickin.data.repository

import com.alra.sof.chickin.data.database.AlarmDao
import com.alra.sof.chickin.data.database.AlarmEntity
import com.alra.sof.chickin.data.models.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalTime

class AlarmRepository(private val alarmDao: AlarmDao) {
    
    val allAlarms: Flow<List<Alarm>> = alarmDao.getAllAlarms().map { entities ->
        entities.map { it.toAlarm() }
    }

    val enabledAlarms: Flow<List<Alarm>> = alarmDao.getEnabledAlarms().map { entities ->
        entities.map { it.toAlarm() }
    }

    suspend fun getAlarmById(id: String): Alarm? {
        return alarmDao.getAlarmById(id)?.toAlarm()
    }

    suspend fun insertAlarm(alarm: Alarm) {
        alarmDao.insertAlarm(alarm.toEntity())
    }

    suspend fun updateAlarm(alarm: Alarm) {
        alarmDao.updateAlarm(alarm.toEntity())
    }

    suspend fun deleteAlarm(alarm: Alarm) {
        alarmDao.deleteAlarmById(alarm.id)
    }

    suspend fun setAlarmEnabled(id: String, enabled: Boolean) {
        alarmDao.setAlarmEnabled(id, enabled)
    }

    private fun AlarmEntity.toAlarm() = Alarm(
        id = id,
        time = LocalTime.of(hour, minute),
        label = label,
        isEnabled = isEnabled,
        repeatDays = repeatDays,
        smartWakeWindow = smartWakeWindow,
        challenge = parseChallenge(challengeType, challengeData),
        soundId = soundId,
        vibratePattern = vibratePattern,
        volumeGradual = volumeGradual,
        profile = profile
    )

    private fun Alarm.toEntity() = AlarmEntity(
        id = id,
        hour = time.hour,
        minute = time.minute,
        label = label,
        isEnabled = isEnabled,
        repeatDays = repeatDays,
        smartWakeWindow = smartWakeWindow,
        challengeType = getChallengeType(challenge),
        challengeData = getChallengeData(challenge),
        soundId = soundId,
        vibratePattern = vibratePattern,
        volumeGradual = volumeGradual,
        profile = profile
    )

    private fun parseChallenge(type: String, data: String): WakeChallenge {
        return when (type) {
            "puzzle" -> WakeChallenge.Puzzle(data.toIntOrNull() ?: 1)
            "qr" -> WakeChallenge.QRScan(data)
            "photo" -> WakeChallenge.PhotoMatch(data)
            "steps" -> WakeChallenge.WalkSteps(data.toIntOrNull() ?: 30)
            "speech" -> WakeChallenge.SpeechRecognition(data)
            else -> WakeChallenge.None
        }
    }

    private fun getChallengeType(challenge: WakeChallenge): String {
        return when (challenge) {
            is WakeChallenge.Puzzle -> "puzzle"
            is WakeChallenge.QRScan -> "qr"
            is WakeChallenge.PhotoMatch -> "photo"
            is WakeChallenge.WalkSteps -> "steps"
            is WakeChallenge.SpeechRecognition -> "speech"
            else -> "none"
        }
    }

    private fun getChallengeData(challenge: WakeChallenge): String {
        return when (challenge) {
            is WakeChallenge.Puzzle -> challenge.difficulty.toString()
            is WakeChallenge.QRScan -> challenge.qrCode
            is WakeChallenge.PhotoMatch -> challenge.photoPath
            is WakeChallenge.WalkSteps -> challenge.steps.toString()
            is WakeChallenge.SpeechRecognition -> challenge.phrase
            else -> ""
        }
    }
}

