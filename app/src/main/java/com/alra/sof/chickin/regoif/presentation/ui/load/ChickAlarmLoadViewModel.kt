package com.alra.sof.chickin.regoif.presentation.ui.load

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alra.sof.chickin.regoif.data.shar.ChickAlarmSharedPreference
import com.alra.sof.chickin.regoif.data.utils.ChickAlarmSystemService
import com.alra.sof.chickin.regoif.domain.usecases.ChickAlarmGetAllUseCase
import com.alra.sof.chickin.regoif.presentation.app.ChickAlarmAppsFlyerState
import com.alra.sof.chickin.regoif.presentation.app.ChickAlarmApplication
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChickAlarmLoadViewModel(
    private val chickAlarmGetAllUseCase: ChickAlarmGetAllUseCase,
    private val chickAlarmSharedPreference: ChickAlarmSharedPreference,
    private val chickAlarmSystemService: ChickAlarmSystemService
) : ViewModel() {

    private val _chickAlarmHomeScreenState: MutableStateFlow<ChickAlarmHomeScreenState> =
        MutableStateFlow(ChickAlarmHomeScreenState.ChickAlarmLoading)
    val chickAlarmHomeScreenState = _chickAlarmHomeScreenState.asStateFlow()

    private var chickAlarmGetApps = false


    init {
        viewModelScope.launch {
            when (chickAlarmSharedPreference.chickAlarmAppState) {
                0 -> {
                    if (chickAlarmSystemService.chickAlarmIsOnline()) {
                        ChickAlarmApplication.chickAlarmConversionFlow.collect {
                            when(it) {
                                ChickAlarmAppsFlyerState.ChickAlarmDefault -> {}
                                ChickAlarmAppsFlyerState.ChickAlarmError -> {
                                    chickAlarmSharedPreference.chickAlarmAppState = 2
                                    _chickAlarmHomeScreenState.value =
                                        ChickAlarmHomeScreenState.ChickAlarmError
                                    chickAlarmGetApps = true
                                }
                                is ChickAlarmAppsFlyerState.ChickAlarmSuccess -> {
                                    if (!chickAlarmGetApps) {
                                        chickAlarmGetData(it.chickAlarmData)
                                        chickAlarmGetApps = true
                                    }
                                }
                            }
                        }
                    } else {
                        _chickAlarmHomeScreenState.value =
                            ChickAlarmHomeScreenState.ChickAlarmNotInternet
                    }
                }
                1 -> {
                    if (chickAlarmSystemService.chickAlarmIsOnline()) {
                        if (ChickAlarmApplication.CHICK_ALARM_FB_LI != null) {
                            _chickAlarmHomeScreenState.value =
                                ChickAlarmHomeScreenState.ChickAlarmSuccess(
                                    ChickAlarmApplication.CHICK_ALARM_FB_LI.toString()
                                )
                        } else if (System.currentTimeMillis() / 1000 > chickAlarmSharedPreference.chickAlarmExpired) {
                            Log.d(ChickAlarmApplication.CHICK_ALARM_MAIN_TAG, "Current time more then expired, repeat request")
                            ChickAlarmApplication.chickAlarmConversionFlow.collect {
                                when(it) {
                                    ChickAlarmAppsFlyerState.ChickAlarmDefault -> {}
                                    ChickAlarmAppsFlyerState.ChickAlarmError -> {
                                        _chickAlarmHomeScreenState.value =
                                            ChickAlarmHomeScreenState.ChickAlarmSuccess(
                                                chickAlarmSharedPreference.chickAlarmSavedUrl
                                            )
                                        chickAlarmGetApps = true
                                    }
                                    is ChickAlarmAppsFlyerState.ChickAlarmSuccess -> {
                                        if (!chickAlarmGetApps) {
                                            chickAlarmGetData(it.chickAlarmData)
                                            chickAlarmGetApps = true
                                        }
                                    }
                                }
                            }
                        } else {
                            Log.d(ChickAlarmApplication.CHICK_ALARM_MAIN_TAG, "Current time less then expired, use saved url")
                            _chickAlarmHomeScreenState.value =
                                ChickAlarmHomeScreenState.ChickAlarmSuccess(
                                    chickAlarmSharedPreference.chickAlarmSavedUrl
                                )
                        }
                    } else {
                        _chickAlarmHomeScreenState.value =
                            ChickAlarmHomeScreenState.ChickAlarmNotInternet
                    }
                }
                2 -> {
                    _chickAlarmHomeScreenState.value =
                        ChickAlarmHomeScreenState.ChickAlarmError
                }
            }
        }
    }


    private suspend fun chickAlarmGetData(conversation: MutableMap<String, Any>?) {
        val chickAlarmData = chickAlarmGetAllUseCase.invoke(conversation)
        if (chickAlarmSharedPreference.chickAlarmAppState == 0) {
            if (chickAlarmData == null) {
                chickAlarmSharedPreference.chickAlarmAppState = 2
                _chickAlarmHomeScreenState.value =
                    ChickAlarmHomeScreenState.ChickAlarmError
            } else {
                chickAlarmSharedPreference.chickAlarmAppState = 1
                chickAlarmSharedPreference.apply {
                    chickAlarmExpired = chickAlarmData.chickAlarmExpires
                    chickAlarmSavedUrl = chickAlarmData.chickAlarmUrl
                }
                _chickAlarmHomeScreenState.value =
                    ChickAlarmHomeScreenState.ChickAlarmSuccess(chickAlarmData.chickAlarmUrl)
            }
        } else  {
            if (chickAlarmData == null) {
                _chickAlarmHomeScreenState.value =
                    ChickAlarmHomeScreenState.ChickAlarmSuccess(chickAlarmSharedPreference.chickAlarmSavedUrl)
            } else {
                chickAlarmSharedPreference.apply {
                    chickAlarmExpired = chickAlarmData.chickAlarmExpires
                    chickAlarmSavedUrl = chickAlarmData.chickAlarmUrl
                }
                _chickAlarmHomeScreenState.value =
                    ChickAlarmHomeScreenState.ChickAlarmSuccess(chickAlarmData.chickAlarmUrl)
            }
        }
    }


    sealed class ChickAlarmHomeScreenState {
        data object ChickAlarmLoading : ChickAlarmHomeScreenState()
        data object ChickAlarmError : ChickAlarmHomeScreenState()
        data class ChickAlarmSuccess(val data: String) : ChickAlarmHomeScreenState()
        data object ChickAlarmNotInternet: ChickAlarmHomeScreenState()
    }
}