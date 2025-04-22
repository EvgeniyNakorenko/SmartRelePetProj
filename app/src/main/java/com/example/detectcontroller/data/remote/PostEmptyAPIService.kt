package com.example.detectcontroller.data.remote

import com.example.detectcontroller.data.remote.remDTO.DefaultRequestDataDTO
import com.example.detectcontroller.data.remote.remDTO.RegResponseDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface PostEmptyAPIService {

    @POST("/reg")
    suspend fun sendData(@Body requestData: DefaultRequestDataDTO): Response<RegResponseDTO>
}
