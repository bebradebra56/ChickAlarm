package com.alra.sof.chickin.regoif.domain.usecases

import android.util.Log
import com.alra.sof.chickin.regoif.data.repo.ChickAlarmRepository
import com.alra.sof.chickin.regoif.data.utils.ChickAlarmPushToken
import com.alra.sof.chickin.regoif.data.utils.ChickAlarmSystemService
import com.alra.sof.chickin.regoif.domain.model.ChickAlarmEntity
import com.alra.sof.chickin.regoif.domain.model.ChickAlarmParam
import com.alra.sof.chickin.regoif.presentation.app.ChickAlarmApplication

class ChickAlarmGetAllUseCase(
    private val chickAlarmRepository: ChickAlarmRepository,
    private val chickAlarmSystemService: ChickAlarmSystemService,
    private val chickAlarmPushToken: ChickAlarmPushToken,
) {
    suspend operator fun invoke(conversion: MutableMap<String, Any>?) : ChickAlarmEntity?{
        val params = ChickAlarmParam(
            chickAlarmLocale = chickAlarmSystemService.chickAlarmGetLocale(),
            chickAlarmPushToken = chickAlarmPushToken.chickAlarmGetToken(),
            chickAlarmAfId = chickAlarmSystemService.chickAlarmGetAppsflyerId()
        )
        Log.d(ChickAlarmApplication.CHICK_ALARM_MAIN_TAG, "Params for request: $params")
        return chickAlarmRepository.chickAlarmGetClient(params, conversion)
    }



}