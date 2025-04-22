package com.example.detectcontroller.domain.registration

import com.example.detectcontroller.data.remote.PostEmptyAPIService
import com.example.detectcontroller.data.remote.remDTO.DefaultRequestDataDTO
import com.example.detectcontroller.data.remote.remDTO.RegResponseDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RegGetDataWIFIUseCase {

    suspend fun execute(): Result<RegResponseDTO> {
        return withContext(Dispatchers.IO) {
            try {
                val apiService = createRetrofit().create(PostEmptyAPIService::class.java)
                val requestData = DefaultRequestDataDTO(" ", " ")
                val response = apiService.sendData(requestData)
                if (response.isSuccessful) {
                    val regResponse = response.body()
                    regResponse?.let {
                        Result.success(it)
                    } ?: Result.failure(Exception("Пустое тело ответа"))
                } else {
                    Result.failure(HttpException(response))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

}

fun createRetrofit(): Retrofit {
    val httpClient = OkHttpClient.Builder()
    return Retrofit.Builder()
        .baseUrl("http://192.168.1.110/reg/") // Базовый URL
        .addConverterFactory(GsonConverterFactory.create()) // Конвертер JSON
        .client(httpClient.build())
        .build()
}