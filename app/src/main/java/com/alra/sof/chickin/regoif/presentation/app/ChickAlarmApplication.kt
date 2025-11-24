package com.alra.sof.chickin.regoif.presentation.app

import android.app.Application
import android.util.Log
import android.view.WindowManager
import com.alra.sof.chickin.alarm.AlarmScheduler
import com.alra.sof.chickin.data.database.ChickAlarmDatabase
import com.alra.sof.chickin.data.repository.AchievementRepository
import com.alra.sof.chickin.data.repository.AlarmRepository
import com.alra.sof.chickin.data.repository.SleepRepository
import com.alra.sof.chickin.data.repository.StatsRepository
import com.alra.sof.chickin.regoif.presentation.di.chickAlarmModule
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.appsflyer.attribution.AppsFlyerRequestListener
import com.appsflyer.deeplink.DeepLink
import com.appsflyer.deeplink.DeepLinkListener
import com.appsflyer.deeplink.DeepLinkResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query


sealed interface ChickAlarmAppsFlyerState {
    data object ChickAlarmDefault : ChickAlarmAppsFlyerState
    data class ChickAlarmSuccess(val chickAlarmData: MutableMap<String, Any>?) :
        ChickAlarmAppsFlyerState
    data object ChickAlarmError : ChickAlarmAppsFlyerState
}

interface ChickAlarmAppsApi {
    @Headers("Content-Type: application/json")
    @GET(CHICK_ALARM_LIN)
    fun chickAlarmGetClient(
        @Query("devkey") devkey: String,
        @Query("device_id") deviceId: String,
    ): Call<MutableMap<String, Any>?>
}
private const val CHICK_ALARM_APP_DEV = "V7soa425uyD97WCaQ7ywge"
private const val CHICK_ALARM_LIN = "com.alra.sof.chickin"
class ChickAlarmApplication : Application() {

    private var chickAlarmConversionTimeoutJob: Job? = null
    private var chickAlarmDeepLinkData: MutableMap<String, Any>? = null

    val database by lazy { ChickAlarmDatabase.getDatabase(this) }
    val alarmRepository by lazy { AlarmRepository(database.alarmDao()) }
    val sleepRepository by lazy { SleepRepository(database.sleepDao()) }
    val statsRepository by lazy { StatsRepository(alarmRepository, sleepRepository) }
    val achievementRepository by lazy { AchievementRepository(alarmRepository, sleepRepository) }
    val alarmScheduler by lazy { AlarmScheduler(this) }

    private var chickAlarmIsResumed = false

    override fun onCreate() {
        super.onCreate()
        instance = this
        val appsflyer = AppsFlyerLib.getInstance()
        chickAlarmSetDebufLogger(appsflyer)
        chickAlarmMinTimeBetween(appsflyer)

        AppsFlyerLib.getInstance().subscribeForDeepLink(object : DeepLinkListener {
            override fun onDeepLinking(p0: DeepLinkResult) {
                when (p0.status) {
                    DeepLinkResult.Status.FOUND -> {
                        chickAlarmExtractDeepMap(p0.deepLink)
                        Log.d(CHICK_ALARM_MAIN_TAG, "onDeepLinking found: ${p0.deepLink}")

                    }

                    DeepLinkResult.Status.NOT_FOUND -> {
                        Log.d(CHICK_ALARM_MAIN_TAG, "onDeepLinking not found: ${p0.deepLink}")
                    }

                    DeepLinkResult.Status.ERROR -> {
                        Log.d(CHICK_ALARM_MAIN_TAG, "onDeepLinking error: ${p0.error}")
                    }
                }
            }

        })


        appsflyer.init(
            CHICK_ALARM_APP_DEV,
            object : AppsFlyerConversionListener {
                override fun onConversionDataSuccess(p0: MutableMap<String, Any>?) {
                    chickAlarmConversionTimeoutJob?.cancel()
                    Log.d(CHICK_ALARM_MAIN_TAG, "onConversionDataSuccess: $p0")

                    val afStatus = p0?.get("af_status")?.toString() ?: "null"
                    if (afStatus == "Organic") {
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                delay(5000)
                                val api = chickAlarmGetApi(
                                    "https://gcdsdk.appsflyer.com/install_data/v4.0/",
                                    null
                                )
                                val response = api.chickAlarmGetClient(
                                    devkey = CHICK_ALARM_APP_DEV,
                                    deviceId = chickAlarmGetAppsflyerId()
                                ).awaitResponse()

                                val resp = response.body()
                                Log.d(CHICK_ALARM_MAIN_TAG, "After 5s: $resp")
                                if (resp?.get("af_status") == "Organic" || resp?.get("af_status") == null) {
                                    chickAlarmResume(ChickAlarmAppsFlyerState.ChickAlarmError)
                                } else {
                                    chickAlarmResume(
                                        ChickAlarmAppsFlyerState.ChickAlarmSuccess(resp)
                                    )
                                }
                            } catch (d: Exception) {
                                Log.d(CHICK_ALARM_MAIN_TAG, "Error: ${d.message}")
                                chickAlarmResume(ChickAlarmAppsFlyerState.ChickAlarmError)
                            }
                        }
                    } else {
                        chickAlarmResume(ChickAlarmAppsFlyerState.ChickAlarmSuccess(p0))
                    }
                }

                override fun onConversionDataFail(p0: String?) {
                    chickAlarmConversionTimeoutJob?.cancel()
                    Log.d(CHICK_ALARM_MAIN_TAG, "onConversionDataFail: $p0")
                    chickAlarmResume(ChickAlarmAppsFlyerState.ChickAlarmError)
                }

                override fun onAppOpenAttribution(p0: MutableMap<String, String>?) {
                    Log.d(CHICK_ALARM_MAIN_TAG, "onAppOpenAttribution")
                }

                override fun onAttributionFailure(p0: String?) {
                    Log.d(CHICK_ALARM_MAIN_TAG, "onAttributionFailure: $p0")
                }
            },
            this
        )

        appsflyer.start(this, CHICK_ALARM_APP_DEV, object :
            AppsFlyerRequestListener {
            override fun onSuccess() {
                Log.d(CHICK_ALARM_MAIN_TAG, "AppsFlyer started")
            }

            override fun onError(p0: Int, p1: String) {
                Log.d(CHICK_ALARM_MAIN_TAG, "AppsFlyer start error: $p0 - $p1")
            }
        })
        chickAlarmStartConversionTimeout()
        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@ChickAlarmApplication)
            modules(
                listOf(
                    chickAlarmModule
                )
            )
        }
    }

    private fun chickAlarmExtractDeepMap(dl: DeepLink) {
        val map = mutableMapOf<String, Any>()
        dl.deepLinkValue?.let { map["deep_link_value"] = it }
        dl.mediaSource?.let { map["media_source"] = it }
        dl.campaign?.let { map["campaign"] = it }
        dl.campaignId?.let { map["campaign_id"] = it }
        dl.afSub1?.let { map["af_sub1"] = it }
        dl.afSub2?.let { map["af_sub2"] = it }
        dl.afSub3?.let { map["af_sub3"] = it }
        dl.afSub4?.let { map["af_sub4"] = it }
        dl.afSub5?.let { map["af_sub5"] = it }
        dl.matchType?.let { map["match_type"] = it }
        dl.clickHttpReferrer?.let { map["click_http_referrer"] = it }
        dl.getStringValue("timestamp")?.let { map["timestamp"] = it }
        dl.isDeferred?.let { map["is_deferred"] = it }
        for (i in 1..10) {
            val key = "deep_link_sub$i"
            dl.getStringValue(key)?.let {
                if (!map.containsKey(key)) {
                    map[key] = it
                }
            }
        }
        Log.d(CHICK_ALARM_MAIN_TAG, "Extracted DeepLink data: $map")
        chickAlarmDeepLinkData = map
    }

    private fun chickAlarmStartConversionTimeout() {
        chickAlarmConversionTimeoutJob = CoroutineScope(Dispatchers.Main).launch {
            delay(30000)
            if (!chickAlarmIsResumed) {
                Log.d(CHICK_ALARM_MAIN_TAG, "TIMEOUT: No conversion data received in 30s")
                chickAlarmResume(ChickAlarmAppsFlyerState.ChickAlarmError)
            }
        }
    }

    private fun chickAlarmResume(state: ChickAlarmAppsFlyerState) {
        chickAlarmConversionTimeoutJob?.cancel()
        if (state is ChickAlarmAppsFlyerState.ChickAlarmSuccess) {
            val convData = state.chickAlarmData ?: mutableMapOf()
            val deepData = chickAlarmDeepLinkData ?: mutableMapOf()
            val merged = mutableMapOf<String, Any>().apply {
                putAll(convData)
                for ((key, value) in deepData) {
                    if (!containsKey(key)) {
                        put(key, value)
                    }
                }
            }
            if (!chickAlarmIsResumed) {
                chickAlarmIsResumed = true
                chickAlarmConversionFlow.value = ChickAlarmAppsFlyerState.ChickAlarmSuccess(merged)
            }
        } else {
            if (!chickAlarmIsResumed) {
                chickAlarmIsResumed = true
                chickAlarmConversionFlow.value = state
            }
        }
    }

    private fun chickAlarmGetAppsflyerId(): String {
        val appsflyrid = AppsFlyerLib.getInstance().getAppsFlyerUID(this) ?: ""
        Log.d(CHICK_ALARM_MAIN_TAG, "AppsFlyer: AppsFlyer Id = $appsflyrid")
        return appsflyrid
    }

    private fun chickAlarmSetDebufLogger(appsflyer: AppsFlyerLib) {
        appsflyer.setDebugLog(true)
    }

    private fun chickAlarmMinTimeBetween(appsflyer: AppsFlyerLib) {
        appsflyer.setMinTimeBetweenSessions(0)
    }

    private fun chickAlarmGetApi(url: String, client: OkHttpClient?): ChickAlarmAppsApi {
        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .client(client ?: OkHttpClient.Builder().build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create()
    }

    companion object {
        lateinit var instance: ChickAlarmApplication
            private set
        var chickAlarmInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
        val chickAlarmConversionFlow: MutableStateFlow<ChickAlarmAppsFlyerState> = MutableStateFlow(
            ChickAlarmAppsFlyerState.ChickAlarmDefault
        )
        var CHICK_ALARM_FB_LI: String? = null
        const val CHICK_ALARM_MAIN_TAG = "ChickAlarmMainTag"
    }
}