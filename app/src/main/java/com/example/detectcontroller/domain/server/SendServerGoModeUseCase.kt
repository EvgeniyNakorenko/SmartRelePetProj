package com.example.detectcontroller.domain.server


import com.example.detectcontroller.data.remote.ApiService
import com.example.detectcontroller.data.remote.RetrofitClient
import com.example.detectcontroller.data.remote.remDTO.RequestDataDTO
import com.example.detectcontroller.data.remote.remDTO.StatusRegServer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.HttpException

class SendServerGoModeUseCase {

    suspend fun execute(requestDataDTO: RequestDataDTO): Result<StatusRegServer> {
        return withContext(Dispatchers.IO) {
            try {
                val apiService = RetrofitClient.getClient("http://82.97.247.240:3000/")
                    .create(ApiService::class.java)

                val response = apiService.sendGoMode(requestDataDTO)

                if (response.isSuccessful) {
                    val jsonResponse = response.body()?.string() ?: ""
                    val jsonObject = JSONObject(jsonResponse)

                    Result.success(
                        StatusRegServer(
                            status = jsonObject.getString("status").toInt()
                        )
                    )
                } else {
                    Result.failure(HttpException(response))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}

//
//class SendServerGoModeUseCase {
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
//                val jsonData = Json.encodeToString(requestDataDTO)
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

