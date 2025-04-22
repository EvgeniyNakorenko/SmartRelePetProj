package com.example.detectcontroller.data.remote

import com.example.detectcontroller.data.remote.remDTO.RequestDataDTO
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("qsapi/sen")
    fun sendData(@Body requestDataDTO: RequestDataDTO): Call<ResponseBody>
}
