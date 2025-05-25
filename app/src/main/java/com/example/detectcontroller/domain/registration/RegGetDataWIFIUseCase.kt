package com.example.detectcontroller.domain.registration

import com.example.detectcontroller.data.remote.ApiService
import com.example.detectcontroller.data.remote.RetrofitClient
import com.example.detectcontroller.data.remote.remDTO.DefaultRequestDataDTO
import com.example.detectcontroller.data.remote.remDTO.RegResponseDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class RegGetDataWIFIUseCase {

    suspend fun execute(): Result<RegResponseDTO> {
        return withContext(Dispatchers.IO) {
            try {
//                val apiService = createRetrofit().create(PostEmptyAPIService::class.java)

                val apiService = RetrofitClient.getClient("http://192.168.1.110/")
                    .create(ApiService::class.java)

                val requestData = DefaultRequestDataDTO(" ", " ")
                val response = apiService.sendRegData(requestData)
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

//fun createRetrofit(): Retrofit {
//    val httpClient = OkHttpClient.Builder()
//    return Retrofit.Builder()
//        .baseUrl("http://192.168.1.110/reg/") // Базовый URL
//        .addConverterFactory(GsonConverterFactory.create()) // Конвертер JSON
//        .client(httpClient.build())
//        .build()
//}