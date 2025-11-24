package com.alra.sof.chickin.regoif.data.repo

import android.util.Log
import com.alra.sof.chickin.regoif.domain.model.ChickAlarmEntity
import com.alra.sof.chickin.regoif.domain.model.ChickAlarmParam
import com.alra.sof.chickin.regoif.presentation.app.ChickAlarmApplication.Companion.CHICK_ALARM_MAIN_TAG
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ChickAlarmApi {
    @Headers("Content-Type: application/json")
    @POST("config.php")
    fun chickAlarmGetClient(
        @Body jsonString: JsonObject,
    ): Call<ChickAlarmEntity>
}


private const val CHICK_ALARM_MAIN = "https://chickalarm.com/"
class ChickAlarmRepository {

    suspend fun chickAlarmGetClient(
        chickAlarmParam: ChickAlarmParam,
        chickAlarmConversion: MutableMap<String, Any>?
    ): ChickAlarmEntity? {
        val gson = Gson()
        val api = chickAlarmGetApi(CHICK_ALARM_MAIN, null)

        val chickAlarmJsonObject = gson.toJsonTree(chickAlarmParam).asJsonObject
        chickAlarmConversion?.forEach { (key, value) ->
            val element: JsonElement = gson.toJsonTree(value)
            chickAlarmJsonObject.add(key, element)
        }
        return try {
            val chickAlarmRequest: Call<ChickAlarmEntity> = api.chickAlarmGetClient(
                jsonString = chickAlarmJsonObject,
            )
            val chickAlarmResult = chickAlarmRequest.awaitResponse()
            Log.d(CHICK_ALARM_MAIN_TAG, "Retrofit: Result code: ${chickAlarmResult.code()}")
            if (chickAlarmResult.code() == 200) {
                Log.d(CHICK_ALARM_MAIN_TAG, "Retrofit: Get request success")
                Log.d(CHICK_ALARM_MAIN_TAG, "Retrofit: Code = ${chickAlarmResult.code()}")
                Log.d(CHICK_ALARM_MAIN_TAG, "Retrofit: ${chickAlarmResult.body()}")
                chickAlarmResult.body()
            } else {
                null
            }
        } catch (e: java.lang.Exception) {
            Log.d(CHICK_ALARM_MAIN_TAG, "Retrofit: Get request failed")
            Log.d(CHICK_ALARM_MAIN_TAG, "Retrofit: ${e.message}")
            null
        }
    }


    private fun chickAlarmGetApi(url: String, client: OkHttpClient?) : ChickAlarmApi {
        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .client(client ?: OkHttpClient.Builder().build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create()
    }


}
