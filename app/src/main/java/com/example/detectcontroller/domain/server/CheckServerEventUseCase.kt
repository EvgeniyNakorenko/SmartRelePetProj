package com.example.detectcontroller.domain.server

import co.yml.charts.common.extensions.isNotNull
import com.example.detectcontroller.data.remote.ApiService
import com.example.detectcontroller.data.remote.RetrofitClient
import com.example.detectcontroller.data.remote.remDTO.RequestDataDTO
import com.example.detectcontroller.data.remote.remDTO.StatusEventServerDTO
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


class CheckServerEventUseCase {

    suspend fun execute(requestDataDTO: RequestDataDTO): Result<StatusEventServerDTO> {
        return withContext(Dispatchers.IO) {
            try {
                val apiService = RetrofitClient.getClient("http://82.97.247.240:3000/")
                    .create(ApiService::class.java)

                val response = apiService.checkEvent(requestDataDTO.copy(com = "rev"))

                if (response.isSuccessful) {
                    val jsonResponse = response.body()?.string() ?: ""
                    val jsonObject = JSONObject(jsonResponse)

                    Result.success(
                        StatusEventServerDTO(
                            id = jsonObject.getInt("id"),
                            timeev = jsonObject.getString("timeev"),
                            rstate = jsonObject.getString("rstate"),
                            value = when {
                                jsonObject.has("irl") -> "irl" + jsonObject.getString("irl")
                                jsonObject.has("pwr") -> "pwr" + jsonObject.getString("pwr")
                                jsonObject.has("url") -> "url" + jsonObject.getString("url")
                                jsonObject.has("rmode") -> "rmode" + jsonObject.getString("rmode")
                                else -> ""
                            },
                            name = jsonObject.getString("name"),
                        )
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

//class CheckServerEventUseCase {
//
//    suspend fun execute(requestDataDTO: RequestDataDTO): Result<StatusEventServerDTO> {
//        return withContext(Dispatchers.IO) {
//            try {
//                val httpClient = OkHttpClient.Builder()
//                    .connectTimeout(30, TimeUnit.SECONDS)
//                    .writeTimeout(30, TimeUnit.SECONDS)
//                    .readTimeout(30, TimeUnit.SECONDS)
//                    .build()
//
//                // Сериализация объекта в JSON
//                val jsonData = Json.encodeToString(requestDataDTO.copy(com = "rev"))
//
//                val requestBody = jsonData.toRequestBody("application/json".toMediaType())
//                val request = Request.Builder()
//                    .url("http://82.97.247.240:3000/qsapi/event")
//                    .post(requestBody)
//                    .build()
//
//                httpClient.newCall(request).execute().use { response ->
//                    if (response.isSuccessful) {
//                        val jsonResponse = response.body?.string() ?: ""
//                        val jsonObject = JSONObject(jsonResponse)
//
//                        Result.success(
//                            StatusEventServerDTO(
//                                id = jsonObject.getInt("id"),
//                                timeev = jsonObject.getString("timeev"),
//                                rstate = jsonObject.getString("rstate"),
//                                value = when {
//                                    jsonObject.has("irl") -> "irl" + jsonObject.getString("irl")
//                                    jsonObject.has("pwr") -> "pwr" + jsonObject.getString("pwr")
//                                    jsonObject.has("url") -> "url" + jsonObject.getString("url")
//                                    jsonObject.has("rmode") -> "rmode" + jsonObject.getString("rmode")
//                                    else -> ""
//                                },
//                                name = jsonObject.getString("name"),
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
