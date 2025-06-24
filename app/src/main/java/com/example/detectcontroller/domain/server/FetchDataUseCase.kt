package com.example.detectcontroller.domain.server



import com.example.detectcontroller.data.remote.ApiService
import com.example.detectcontroller.data.remote.RetrofitClient
import com.example.detectcontroller.data.remote.remDTO.RequestDataDTO
import com.example.detectcontroller.data.remote.remDTO.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class FetchDataUseCase {

    suspend fun execute(requestDataDTO: RequestDataDTO): Result<UiState> {
        return withContext(Dispatchers.IO) {
            try {
//                val apiService = RetrofitClient.getClient("http://82.97.247.240:3000/").create(
//                    ApiService::class.java)

                val apiService = RetrofitClient.getClient("http://82.97.247.240:3000/")
                    .create(ApiService::class.java)

                val call = apiService.sendData(requestDataDTO)
                val response = call.execute()

                if (response.isSuccessful) {
                    val jsonResponse = response.body()?.string()
                    val jsonObject = JSONObject(jsonResponse.toString())

                    if (jsonObject.has("error")) {
                        val errorCode = jsonObject.getInt("error")
                        val errorMessage = when (errorCode) {
                            1 -> "error 1 Device not registered error 1"
                            2 -> "error 2 JSON syntax"
                            3 -> "error 3 Invalid device type, channel number, or command parameter"
                            else -> "Unknown error"
                        }
                        return@withContext Result.failure(Exception("Error $errorCode: $errorMessage"))
                    }

                    Result.success(
                        UiState(
                            timedv = "timedv: ${jsonObject.getString("timedv")}",
                            stt = "stt: ${jsonObject.getInt("stt")}",
                            url = "url: ${jsonObject.getString("url").trim().toInt()}",
                            irl = "irl: ${jsonObject.getDouble("irl")}",
                            pwr = "pwr: ${jsonObject.getInt("pwr")}",
                            frq = "frq: ${jsonObject.getInt("frq")}",
                            tmp = "tmp: ${jsonObject.getInt("tmp")}",
                            rmode = "${jsonObject.getInt("rmode")}",
                            gomode = "${jsonObject.getInt("gomode")}",
                            modes = "${jsonObject.getInt("modes")}",
                        )
                    )
                } else {
                    Result.failure(Exception("Ошибка: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}


