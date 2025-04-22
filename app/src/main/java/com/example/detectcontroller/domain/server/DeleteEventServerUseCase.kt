package com.example.detectcontroller.domain.server

import com.example.detectcontroller.data.remote.remDTO.DeleteDeviceEventResponseDTO
import com.example.detectcontroller.data.remote.remDTO.DeleteEventDTO
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


class DeleteEventServerUseCase {

    suspend fun execute(deleteEventDTO: DeleteEventDTO): Result<DeleteDeviceEventResponseDTO> {
        return withContext(Dispatchers.IO) {
            try {
                val httpClient = OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build()

                // Сериализация объекта в JSON
                val jsonData = Json.encodeToString(deleteEventDTO.copy(com = "del"))

                val requestBody = jsonData.toRequestBody("application/json".toMediaType())
                val request = Request.Builder()
                    .url("http://82.97.247.240:3000/qsapi/event")
                    .post(requestBody)
                    .build()

                httpClient.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        val jsonResponse = response.body?.string() ?: ""
                        val jsonObject = JSONObject(jsonResponse)

                        when {
                            jsonObject.has("state") && jsonObject.getString("state") == "ok" -> {
                                Result.success(
                                    DeleteDeviceEventResponseDTO(
                                        success = true,
                                        deletedId = jsonObject.getInt("id")
                                    )
                                )
                            }

                            jsonObject.has("status") && jsonObject.getInt("status") == 10 -> {
                                Result.success(
                                    DeleteDeviceEventResponseDTO(
                                        success = false,
                                        deletedId = 0
                                    )
                                )
                            }

                            else -> {
                                Result.failure(Exception("Неожиданный ответ от сервера"))
                            }
                        }
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
//class DeleteEventServerUseCase {
//
//    suspend fun execute(deleteEventDTO: DeleteEventDTO): Result<DeleteDeviceEventResponseDTO> {
//        return withContext(Dispatchers.IO) {
//            try {
//                val httpClient = OkHttpClient.Builder()
//                    .connectTimeout(30, TimeUnit.SECONDS)
//                    .writeTimeout(30, TimeUnit.SECONDS)
//                    .readTimeout(30, TimeUnit.SECONDS)
//                    .build()
//
//                val jsonData = JSONObject().apply {
//                    put("dvid", deleteEventDTO.dvid)
//                    put("tkn", deleteEventDTO.tkn)
//                    put("typedv", deleteEventDTO.typedv)
//                    put("num", deleteEventDTO.num)
//                    put("com", "del")
//                    put("id",deleteEventDTO.id )
//                }.toString()
//
//                val requestBody = RequestBody.create(MediaType.get("application/json"), jsonData)
//                val request = Request.Builder()
//                    .url("http://82.97.247.240:3000/qsapi/event")
//                    .post(requestBody)
//                    .build()
//
//                val response = httpClient.newCall(request).execute()
//
//                if (response.isSuccessful) {
//                    val jsonResponse = response.body()?.string() ?: ""
//                    val jsonObject = JSONObject(jsonResponse)
//
//                    when {
//                        jsonObject.has("state") && jsonObject.getString("state") == "ok" -> {
//                            Result.success(
//                                DeleteDeviceEventResponseDTO(
//                                    success = true,
//                                    deletedId = jsonObject.getInt("id")
//                                )
//                            )
//                        }
//
//                        jsonObject.has("status") && jsonObject.getInt("status") == 10 -> {
//                            Result.success(
//                                DeleteDeviceEventResponseDTO(
//                                    success = false,
//                                    deletedId = 0
//                                )
//                            )
//                        }
//
//                        else -> {
//                            Result.failure(Exception("Неожиданный ответ от сервера"))
//                        }
//                    }
//                } else {
//                    Result.failure(Exception("Ошибка: ${response.code()}"))
//                }
//            } catch (e: Exception) {
//                Result.failure(e)
//            }
//        }
//    }
//}
