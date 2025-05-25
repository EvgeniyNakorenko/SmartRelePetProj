package com.example.detectcontroller.domain.server

import com.example.detectcontroller.data.remote.ApiService
import com.example.detectcontroller.data.remote.RetrofitClient
import com.example.detectcontroller.data.remote.remDTO.GetSettingsDTOrmode1
import com.example.detectcontroller.data.remote.remDTO.GetSettingsDTOrmode3
import com.example.detectcontroller.data.remote.remDTO.GetSettingsDTOrmode4
import com.example.detectcontroller.data.remote.remDTO.GetSettingsDTOrmode5
import com.example.detectcontroller.data.remote.remDTO.GetSettingsDTOrmode6
import com.example.detectcontroller.data.remote.remDTO.RequestDataDTO
import com.example.detectcontroller.data.remote.remDTO.SettingsBaseDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GetServerSettingsUseCase {

    suspend fun execute(requestDataDTO: RequestDataDTO): Result<SettingsBaseDTO> {
        return withContext(Dispatchers.IO) {
            try {
                val apiService = RetrofitClient.getClient("http://82.97.247.240:3000/")
                    .create(ApiService::class.java)

                val response = apiService.getServerSettings(requestDataDTO.copy(com = "rv"))

                if (response.isSuccessful) {
                    val jsonResponse = response.body()?.string() ?: ""
                    val jsonObject = JSONObject(jsonResponse)

                    Result.success(
                        when (jsonObject.getString("rmode").toInt()) {
                            6 -> GetSettingsDTOrmode6(
                                rmode = jsonObject.getString("rmode").toInt(),
                                ntrm = jsonObject.getString("ntrm").toInt(),
                                rtype = jsonObject.getString("ntrm").toInt(),
                                mtrm = jsonObject.getString("ntrm").toInt(),
                                th = jsonObject.getString("ntrm").toInt(),
                                tl = jsonObject.getString("ntrm").toInt(),
                                kos = jsonObject.getString("ntrm").toInt(),
                                kp = jsonObject.getString("ntrm").toInt(),
                                kd = jsonObject.getString("ntrm").toFloat(),
                                ki = jsonObject.getString("ntrm").toFloat(),
                                dir = jsonObject.getString("ntrm").toInt(),
                            )

                            3 -> GetSettingsDTOrmode3(
                                rmode = jsonObject.getString("rmode").toInt(),
                                tOn = jsonObject.getString("tOn").toString(),
                                prton = jsonObject.getString("prton").toInt(),
                                upm = jsonObject.getString("upm").toInt(),
                                ulh = jsonObject.getString("ulh").toInt(),
                                ull = jsonObject.getString("ull").toInt(),
                                ipm = jsonObject.getString("ipm").toInt(),
                                ilh = jsonObject.getString("ilh").toFloat(),
                                ill = jsonObject.getString("ill").toFloat(),
                                ppm = jsonObject.getString("ppm").toInt(),
                                plh = jsonObject.getString("plh").toInt(),
                                tpm = jsonObject.getString("tpm").toInt(),
                                tlh = jsonObject.getString("tlh").toInt(),
                            )

                            4 -> GetSettingsDTOrmode4(
                                rmode = jsonObject.getString("rmode").toInt(),
                                tRTCOn = jsonObject.getString("tRTCOn").toString(),
                                tRTCOff = jsonObject.getString("tRTCOff").toString(),
                                prton = jsonObject.getString("prton").toInt(),
                                upm = jsonObject.getString("upm").toInt(),
                                ulh = jsonObject.getString("ulh").toInt(),
                                ull = jsonObject.getString("ull").toInt(),
                                ipm = jsonObject.getString("ipm").toInt(),
                                ilh = jsonObject.getString("ilh").toFloat(),
                                ill = jsonObject.getString("ill").toFloat(),
                                ppm = jsonObject.getString("ppm").toInt(),
                                plh = jsonObject.getString("plh").toInt(),
                                tpm = jsonObject.getString("tpm").toInt(),
                                tlh = jsonObject.getString("tlh").toInt(),
                            )

                            5 -> GetSettingsDTOrmode5(
                                rmode = jsonObject.getString("rmode").toInt(),
                                tClOn = jsonObject.getString("rmode").toString(),
                                tClOff = jsonObject.getString("rmode").toString(),
                                prton = jsonObject.getString("rmode").toInt(),
                                upm = jsonObject.getString("rmode").toInt(),
                                ulh = jsonObject.getString("rmode").toInt(),
                                ull = jsonObject.getString("rmode").toInt(),
                                ipm = jsonObject.getString("rmode").toInt(),
                                ilh = jsonObject.getString("rmode").toFloat(),
                                ill = jsonObject.getString("rmode").toFloat(),
                                ppm = jsonObject.getString("rmode").toInt(),
                                plh = jsonObject.getString("rmode").toInt(),
                                tpm = jsonObject.getString("rmode").toInt(),
                                tlh = jsonObject.getString("rmode").toInt(),
                            )

                            else -> GetSettingsDTOrmode1(
                                rmode = jsonObject.getString("rmode").toInt(),
                                prton = jsonObject.getString("prton").toInt(),
                                upm = jsonObject.getString("upm").toInt(),
                                ulh = jsonObject.getString("ulh").toInt(),
                                ull = jsonObject.getString("ull").toInt(),
                                ipm = jsonObject.getString("ipm").toInt(),
                                ilh = jsonObject.getString("ilh").toFloat(),
                                ill = jsonObject.getString("ill").toFloat(),
                                ppm = jsonObject.getString("ppm").toInt(),
                                plh = jsonObject.getString("plh").toInt(),
                                tpm = jsonObject.getString("tpm").toInt(),
                                tlh = jsonObject.getString("tlh").toInt(),
                            )
                        }
                    )
                } else {
                    Result.failure(Exception("Ошибка: ${response.code()} - ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}

//
//class GetServerSettingsUseCase {
//    suspend fun execute(requestDataDTO: RequestDataDTO): Result<SettingsBaseDTO> {
//        return withContext(Dispatchers.IO) {
//            try {
//                val httpClient = OkHttpClient.Builder()
//                    .connectTimeout(30, TimeUnit.SECONDS)
//                    .writeTimeout(30, TimeUnit.SECONDS)
//                    .readTimeout(30, TimeUnit.SECONDS)
//                    .build()
//                val jsonData = Json.encodeToString(requestDataDTO.copy(com = "rv"))
//
//                val requestBody = jsonData.toRequestBody("application/json".toMediaType())
//                val request = Request.Builder()
//                    .url("http://82.97.247.240:3000/qsapi/var")
//                    .post(requestBody)
//                    .build()
//
//                httpClient.newCall(request).execute().use { response ->
//                    if (response.isSuccessful) {
//                        val jsonResponse = response.body?.string() ?: ""
//                        val jsonObject = JSONObject(jsonResponse)
//
//                        Result.success(
//                            when (jsonObject.getString("rmode").toInt()) {
//                                6 -> GetSettingsDTOrmode6(
//                                    rmode = jsonObject.getString("rmode").toInt(),
//                                    ntrm = jsonObject.getString("ntrm").toInt(),
//                                    rtype = jsonObject.getString("ntrm").toInt(),
//                                    mtrm = jsonObject.getString("ntrm").toInt(),
//                                    th = jsonObject.getString("ntrm").toInt(),
//                                    tl = jsonObject.getString("ntrm").toInt(),
//                                    kos = jsonObject.getString("ntrm").toInt(),
//                                    kp = jsonObject.getString("ntrm").toInt(),
//                                    kd = jsonObject.getString("ntrm").toFloat(),
//                                    ki = jsonObject.getString("ntrm").toFloat(),
//                                    dir = jsonObject.getString("ntrm").toInt(),
//                                )
//
//                                3 -> GetSettingsDTOrmode3(
//                                    rmode = jsonObject.getString("rmode").toInt(),
//                                    tOn = jsonObject.getString("tOn").toString(),
//                                    prton = jsonObject.getString("prton").toInt(),
//                                    upm = jsonObject.getString("upm").toInt(),
//                                    ulh = jsonObject.getString("ulh").toInt(),
//                                    ull = jsonObject.getString("ull").toInt(),
//                                    ipm = jsonObject.getString("ipm").toInt(),
//                                    ilh = jsonObject.getString("ilh").toFloat(),
//                                    ill = jsonObject.getString("ill").toFloat(),
//                                    ppm = jsonObject.getString("ppm").toInt(),
//                                    plh = jsonObject.getString("plh").toInt(),
//                                    tpm = jsonObject.getString("tpm").toInt(),
//                                    tlh = jsonObject.getString("tlh").toInt(),
//                                )
//
//                                4 -> GetSettingsDTOrmode4(
//                                    rmode = jsonObject.getString("rmode").toInt(),
//                                    tRTCOn = jsonObject.getString("tRTCOn").toString(),
//                                    tRTCOff = jsonObject.getString("tRTCOff").toString(),
//                                    prton = jsonObject.getString("prton").toInt(),
//                                    upm = jsonObject.getString("upm").toInt(),
//                                    ulh = jsonObject.getString("ulh").toInt(),
//                                    ull = jsonObject.getString("ull").toInt(),
//                                    ipm = jsonObject.getString("ipm").toInt(),
//                                    ilh = jsonObject.getString("ilh").toFloat(),
//                                    ill = jsonObject.getString("ill").toFloat(),
//                                    ppm = jsonObject.getString("ppm").toInt(),
//                                    plh = jsonObject.getString("plh").toInt(),
//                                    tpm = jsonObject.getString("tpm").toInt(),
//                                    tlh = jsonObject.getString("tlh").toInt(),
//                                )
//
//                                5 -> GetSettingsDTOrmode5(
//                                    rmode = jsonObject.getString("rmode").toInt(),
//                                    tClOn = jsonObject.getString("rmode").toString(),
//                                    tClOff = jsonObject.getString("rmode").toString(),
//                                    prton = jsonObject.getString("rmode").toInt(),
//                                    upm = jsonObject.getString("rmode").toInt(),
//                                    ulh = jsonObject.getString("rmode").toInt(),
//                                    ull = jsonObject.getString("rmode").toInt(),
//                                    ipm = jsonObject.getString("rmode").toInt(),
//                                    ilh = jsonObject.getString("rmode").toFloat(),
//                                    ill = jsonObject.getString("rmode").toFloat(),
//                                    ppm = jsonObject.getString("rmode").toInt(),
//                                    plh = jsonObject.getString("rmode").toInt(),
//                                    tpm = jsonObject.getString("rmode").toInt(),
//                                    tlh = jsonObject.getString("rmode").toInt(),
//                                )
//
//                                else -> GetSettingsDTOrmode1(
//                                    rmode = jsonObject.getString("rmode").toInt(),
//                                    prton = jsonObject.getString("prton").toInt(),
//                                    upm = jsonObject.getString("upm").toInt(),
//                                    ulh = jsonObject.getString("ulh").toInt(),
//                                    ull = jsonObject.getString("ull").toInt(),
//                                    ipm = jsonObject.getString("ipm").toInt(),
//                                    ilh = jsonObject.getString("ilh").toFloat(),
//                                    ill = jsonObject.getString("ill").toFloat(),
//                                    ppm = jsonObject.getString("ppm").toInt(),
//                                    plh = jsonObject.getString("plh").toInt(),
//                                    tpm = jsonObject.getString("tpm").toInt(),
//                                    tlh = jsonObject.getString("tlh").toInt(),
//                                )
//                            }
//                        )
//                    } else {
//                        Result.failure(Exception("Ошибка: ${response.code} - ${response.message}"))
//                    }
//                }
//            } catch (e: Exception) {
//                Result.failure(e)
//            }
//        }
//
//    }
//}