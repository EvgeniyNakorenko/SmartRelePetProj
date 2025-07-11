package com.example.detectcontroller.domain.registration


import com.example.detectcontroller.data.remote.ApiService
import com.example.detectcontroller.data.remote.RetrofitClient
import com.example.detectcontroller.data.remote.remDTO.RequestDataDTO
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
import retrofit2.HttpException
import java.util.concurrent.TimeUnit

//class RegSendDataWIFIUseCase {
//
//    suspend fun execute(requestDataDTO: RequestDataDTO): Result<StatusRegServer> {
//        return withContext(Dispatchers.IO) {
//            try {
//                val httpClient = OkHttpClient.Builder()
//                    .connectTimeout(30, TimeUnit.SECONDS)
//                    .writeTimeout(30, TimeUnit.SECONDS)
//                    .readTimeout(30, TimeUnit.SECONDS)
//                    .build()
//
//                // Сериализация объекта в JSON
//                val jsonData = Json.encodeToString(requestDataDTO)
//
//                val requestBody = jsonData.toRequestBody("application/json".toMediaType())
//                val request = Request.Builder()
//                    .url("http://82.97.247.240:3000/qsapi/reg")
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
//                                status = jsonObject.getString("status").toInt()
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


class RegSendDataWIFIUseCase {

    suspend fun execute(requestDataDTO: RequestDataDTO): Result<StatusRegServer> {
        return withContext(Dispatchers.IO) {
            try {
                val apiService = RetrofitClient.getClient("http://82.97.247.240:3000/")
                    .create(ApiService::class.java)

                val response = apiService.sendWifiRegistrationData(requestDataDTO)

                if (response.isSuccessful) {
                    response.body()?.let { statusResponse ->
                        Result.success(StatusRegServer(status = statusResponse.status))
                    } ?: Result.failure(Exception("Empty response body"))
                } else {
                    Result.failure(HttpException(response))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
