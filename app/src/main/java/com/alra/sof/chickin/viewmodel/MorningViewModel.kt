package com.alra.sof.chickin.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.alra.sof.chickin.data.models.*
import com.alra.sof.chickin.regoif.presentation.app.ChickAlarmApplication
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class MorningViewModel(application: Application) : AndroidViewModel(application) {
    private val sleepRepository = ChickAlarmApplication.instance.sleepRepository
    
    private val _currentScenario = MutableStateFlow(
        MorningScenario(name = "My Morning Routine", tasks = defaultMorningTasks)
    )
    val currentScenario: StateFlow<MorningScenario> = _currentScenario.asStateFlow()

    private val _isScenarioActive = MutableStateFlow(false)
    val isScenarioActive: StateFlow<Boolean> = _isScenarioActive.asStateFlow()

    fun toggleTaskCompletion(taskId: String) {
        _currentScenario.value = _currentScenario.value.copy(
            tasks = _currentScenario.value.tasks.map { task ->
                if (task.id == taskId) {
                    task.copy(isCompleted = !task.isCompleted)
                } else {
                    task
                }
            }
        )
    }

    fun startScenario() {
        _isScenarioActive.value = true
        // Reset all tasks
        _currentScenario.value = _currentScenario.value.copy(
            tasks = _currentScenario.value.tasks.map { it.copy(isCompleted = false) }
        )
    }

    fun completeScenario() {
        _isScenarioActive.value = false
        
        // Mark routine as completed in sleep session
        viewModelScope.launch {
            val currentSession = sleepRepository.getActiveSession()
            currentSession?.let { session ->
                val updatedSession = session.copy(
                    notes = session.notes + "routine_completed"
                )
                sleepRepository.updateSession(updatedSession)
            }
        }
        
        // Reset all tasks for next time
        _currentScenario.value = _currentScenario.value.copy(
            tasks = _currentScenario.value.tasks.map { it.copy(isCompleted = false) }
        )
    }

    fun addTask(task: MorningTask) {
        val tasks = _currentScenario.value.tasks.toMutableList()
        tasks.add(task.copy(order = tasks.size))
        _currentScenario.value = _currentScenario.value.copy(tasks = tasks)
    }

    fun removeTask(taskId: String) {
        _currentScenario.value = _currentScenario.value.copy(
            tasks = _currentScenario.value.tasks.filter { it.id != taskId }
        )
    }

    fun getCompletionPercentage(): Float {
        val tasks = _currentScenario.value.tasks
        if (tasks.isEmpty()) return 0f
        val completed = tasks.count { it.isCompleted }
        return completed.toFloat() / tasks.size.toFloat()
    }
}

