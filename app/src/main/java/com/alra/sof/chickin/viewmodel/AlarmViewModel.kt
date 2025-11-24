package com.alra.sof.chickin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alra.sof.chickin.data.models.*
import com.alra.sof.chickin.regoif.presentation.app.ChickAlarmApplication
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalTime

class AlarmViewModel : ViewModel() {
    private val repository = ChickAlarmApplication.instance.alarmRepository
    private val scheduler = ChickAlarmApplication.instance.alarmScheduler

    private val _alarms = MutableStateFlow<List<Alarm>>(emptyList())
    val alarms: StateFlow<List<Alarm>> = _alarms.asStateFlow()

    private val _selectedAlarm = MutableStateFlow<Alarm?>(null)
    val selectedAlarm: StateFlow<Alarm?> = _selectedAlarm.asStateFlow()

    init {
        loadAlarms()
    }

    private fun loadAlarms() {
        viewModelScope.launch {
            repository.allAlarms.collect { alarmList ->
                _alarms.value = alarmList
            }
        }
    }

    fun addAlarm(alarm: Alarm) {
        viewModelScope.launch {
            repository.insertAlarm(alarm)
            if (alarm.isEnabled) {
                scheduler.scheduleAlarm(alarm)
            }
        }
    }

    fun updateAlarm(alarm: Alarm) {
        viewModelScope.launch {
            repository.updateAlarm(alarm)
            if (alarm.isEnabled) {
                scheduler.scheduleAlarm(alarm)
            } else {
                scheduler.cancelAlarm(alarm)
            }
        }
    }

    fun deleteAlarm(alarmId: String) {
        viewModelScope.launch {
            val alarm = repository.getAlarmById(alarmId)
            alarm?.let {
                repository.deleteAlarm(it)
                scheduler.cancelAlarm(it)
            }
        }
    }

    fun toggleAlarm(alarmId: String) {
        viewModelScope.launch {
            val alarm = repository.getAlarmById(alarmId)
            alarm?.let {
                val updatedAlarm = it.copy(isEnabled = !it.isEnabled)
                repository.updateAlarm(updatedAlarm)
                if (updatedAlarm.isEnabled) {
                    scheduler.scheduleAlarm(updatedAlarm)
                } else {
                    scheduler.cancelAlarm(updatedAlarm)
                }
            }
        }
    }

    fun selectAlarm(alarm: Alarm?) {
        _selectedAlarm.value = alarm
    }

    fun createNewAlarm(): Alarm {
        return Alarm(
            time = LocalTime.of(7, 0),
            label = "New Alarm"
        )
    }
}

