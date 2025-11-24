package com.alra.sof.chickin.data.repository

import com.alra.sof.chickin.data.database.SleepDao
import com.alra.sof.chickin.data.database.SleepSessionEntity
import com.alra.sof.chickin.data.models.SleepSession
import com.alra.sof.chickin.data.models.SleepQuality
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class SleepRepository(private val sleepDao: SleepDao) {
    
    val allSessions: Flow<List<SleepSession>> = sleepDao.getAllSessions().map { entities ->
        entities.map { it.toSleepSession() }
    }

    suspend fun getActiveSession(): SleepSession? {
        return sleepDao.getActiveSessions()?.toSleepSession()
    }

    suspend fun insertSession(session: SleepSession) {
        sleepDao.insertSession(session.toEntity())
    }

    suspend fun updateSession(session: SleepSession) {
        sleepDao.updateSession(session.toEntity())
    }

    fun getRecentSessions(limit: Int): Flow<List<SleepSession>> {
        return sleepDao.getRecentCompletedSessions(limit).map { entities ->
            entities.map { it.toSleepSession() }
        }
    }

    private fun SleepSessionEntity.toSleepSession() = SleepSession(
        id = id,
        startTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(startTime), ZoneId.systemDefault()),
        endTime = endTime?.let { LocalDateTime.ofInstant(Instant.ofEpochMilli(it), ZoneId.systemDefault()) },
        quality = quality,
        movements = movements,
        wakeUps = wakeUps,
        notes = notes
    )

    private fun SleepSession.toEntity() = SleepSessionEntity(
        id = id,
        startTime = startTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
        endTime = endTime?.atZone(ZoneId.systemDefault())?.toInstant()?.toEpochMilli(),
        quality = quality,
        movements = movements,
        wakeUps = wakeUps,
        notes = notes
    )
}

