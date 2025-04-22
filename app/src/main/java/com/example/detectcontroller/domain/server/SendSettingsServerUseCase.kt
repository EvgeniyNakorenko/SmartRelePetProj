package com.example.detectcontroller.domain.server


import com.example.detectcontroller.data.remote.remDTO.SendSettingsDTO
import com.example.detectcontroller.data.remote.remDTO.StatusRegServer
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

class SendSettingsServerUseCase {

    suspend fun execute(sendSettingsDTO: SendSettingsDTO): Result<StatusRegServer> {
        return withContext(Dispatchers.IO) {
            try {
                val httpClient = OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build()

                // Сериализация объекта в JSON
                val jsonData = Json.encodeToString(sendSettingsDTO)

                val requestBody = jsonData.toRequestBody("application/json".toMediaType())
                val request = Request.Builder()
                    .url("http://82.97.247.240:3000/qsapi/var")
                    .post(requestBody)
                    .build()

                httpClient.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        val jsonResponse = response.body?.string() ?: ""
                        val jsonObject = JSONObject(jsonResponse)

                        Result.success(
                            StatusRegServer(
                                status = jsonObject.getString("status").toInt()
                            )
                        )
                    } else {
                        Result.failure(Exception("Ошибка: ${response.code} - ${response.message}"))
                    }
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}

//
//class SendSettingsServerUseCase {
//
//    suspend fun execute(sendSettingsDTO: SendSettingsDTO): Result<StatusRegServer> {
//        return withContext(Dispatchers.IO) {
//            try {
//                val httpClient = OkHttpClient.Builder()
//                    .connectTimeout(30, TimeUnit.SECONDS)
//                    .writeTimeout(30, TimeUnit.SECONDS)
//                    .readTimeout(30, TimeUnit.SECONDS)
//                    .build()
//
//                val jsonData = JSONObject().apply {
//                    put("dvid", sendSettingsDTO.dvid)
//                    put("tkn", sendSettingsDTO.tkn)
//                    put("typedv", sendSettingsDTO.typedv)
//                    put("num", sendSettingsDTO.num)
//                    put("com", sendSettingsDTO.com)
//                    put("prton", sendSettingsDTO.prton)
//                    put("upm", sendSettingsDTO.upm)
//                    put("ulh", sendSettingsDTO.ulh)
//                    put("ull", sendSettingsDTO.ull)
//                    put("ipm", sendSettingsDTO.ipm)
//                    put("ilh", sendSettingsDTO.ilh)
//                    put("ill", sendSettingsDTO.ill)
//                    put("ppm", sendSettingsDTO.ppm)
//                    put("plh", sendSettingsDTO.plh)
//                    put("tpm", sendSettingsDTO.tpm)
//                    put("tlh", sendSettingsDTO.tlh)
//                }.toString()
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
//                            StatusRegServer(
//                                status = jsonObject.getString("status").toInt(),
//                            )
//                        )
//                    } else {
//                        Result.failure(Exception("Ошибка: ${response.code} - ${response.message}"))
//                    }
//                }
//            } catch (e: Exception) {
//                Result.failure(e)
//            }
//        }
//    }
//}
//
