package com.example.detectcontroller.domain.server

import com.example.detectcontroller.data.remote.ApiService
import com.example.detectcontroller.data.remote.RetrofitClient
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
                val apiService = RetrofitClient.getClient("http://82.97.247.240:3000/")
                    .create(ApiService::class.java)

                val response = apiService.deleteEvent(deleteEventDTO.copy(com = "del"))

                if (response.isSuccessful) {
                    val jsonResponse = response.body()?.string() ?: ""
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
                    Result.failure(Exception("Ошибка: ${response.code()} - ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
