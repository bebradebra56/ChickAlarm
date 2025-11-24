package com.alra.sof.chickin.regoif.presentation.di

import com.alra.sof.chickin.regoif.data.repo.ChickAlarmRepository
import com.alra.sof.chickin.regoif.data.shar.ChickAlarmSharedPreference
import com.alra.sof.chickin.regoif.data.utils.ChickAlarmPushToken
import com.alra.sof.chickin.regoif.data.utils.ChickAlarmSystemService
import com.alra.sof.chickin.regoif.domain.usecases.ChickAlarmGetAllUseCase
import com.alra.sof.chickin.regoif.presentation.pushhandler.ChickAlarmPushHandler
import com.alra.sof.chickin.regoif.presentation.ui.load.ChickAlarmLoadViewModel
import com.alra.sof.chickin.regoif.presentation.ui.view.ChickAlarmViFun
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val chickAlarmModule = module {
    factory {
        ChickAlarmPushHandler()
    }
    single {
        ChickAlarmRepository()
    }
    single {
        ChickAlarmSharedPreference(get())
    }
    factory {
        ChickAlarmPushToken()
    }
    factory {
        ChickAlarmSystemService(get())
    }
    factory {
        ChickAlarmGetAllUseCase(
            get(), get(), get()
        )
    }
    factory {
        ChickAlarmViFun(get())
    }
    viewModel {
        ChickAlarmLoadViewModel(get(), get(), get())
    }
}