package com.example.detectcontroller.data.remote

import com.example.detectcontroller.data.remote.remDTO.DefaultRequestDataDTO
import com.example.detectcontroller.data.remote.remDTO.DeleteEventDTO
import com.example.detectcontroller.data.remote.remDTO.RegResponseDTO
import com.example.detectcontroller.data.remote.remDTO.RequestDataDTO
import com.example.detectcontroller.data.remote.remDTO.SendServerSettingsMode012DTO
import com.example.detectcontroller.data.remote.remDTO.SendServerSettingsMode3DTO
import com.example.detectcontroller.data.remote.remDTO.SendServerSettingsMode4DTO
import com.example.detectcontroller.data.remote.remDTO.SendServerSettingsMode5DTO
import com.example.detectcontroller.data.remote.remDTO.SendSettingsDTO
import com.example.detectcontroller.data.remote.remDTO.StatusRegServer
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

//interface ApiService {
//    @POST("qsapi/sen")
//    fun sendData(@Body requestDataDTO: RequestDataDTO): Call<ResponseBody>
//}

interface ApiService {
    @POST("qsapi/sen")
    fun sendData(@Body requestDataDTO: RequestDataDTO): Call<ResponseBody>

    @POST("qsapi/var")
    suspend fun sendSettings(@Body settings: SendSettingsDTO): Response<ResponseBody>

    @POST("qsapi/var")
    suspend fun sendSettingsMode012(@Body settings: SendServerSettingsMode012DTO): Response<ResponseBody>

    @POST("qsapi/var")
    suspend fun sendSettingsMode5(@Body settings: SendServerSettingsMode5DTO): Response<ResponseBody>

    @POST("qsapi/var")
    suspend fun sendSettingsMode4(@Body settings: SendServerSettingsMode4DTO): Response<ResponseBody>

    @POST("qsapi/var")
    suspend fun sendSettingsMode3(@Body settings: SendServerSettingsMode3DTO): Response<ResponseBody>

    @POST("qsapi/var")
    suspend fun sendGoMode(@Body requestData: RequestDataDTO): Response<ResponseBody>

    @POST("qsapi/var")
    suspend fun getServerSettings(@Body requestData: RequestDataDTO): Response<ResponseBody>

    @POST("qsapi/event")
    suspend fun deleteEvent(@Body deleteEventDTO: DeleteEventDTO): Response<ResponseBody>

    @POST("qsapi/reg")
    suspend fun sendWifiRegistrationData(@Body requestDataDTO: RequestDataDTO): Response<StatusRegServer>

    @POST("qsapi/event")
    suspend fun checkEvent(@Body requestData: RequestDataDTO): Response<ResponseBody>

    @POST("/reg")
    suspend fun sendRegData(@Body requestData: DefaultRequestDataDTO): Response<RegResponseDTO>
}
