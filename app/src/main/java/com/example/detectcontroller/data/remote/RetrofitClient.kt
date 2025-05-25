package com.example.detectcontroller.data.remote

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


//object RetrofitClient {
//    private var retrofit: Retrofit? = null
//
//    fun getClient(baseUrl: String): Retrofit {
//        if (retrofit == null) {
//            retrofit = Retrofit.Builder()
//                .baseUrl(baseUrl)
//                .addConverterFactory(GsonConverterFactory.create())
//                .client(OkHttpClient.Builder().build())
//                .build()
//        }
//        return retrofit!!
//    }
//}

//object RetrofitClient {
//    private val clients = mutableMapOf<String, Retrofit>()
//
//    fun getClient(baseUrl: String): Retrofit {
//        return clients.getOrPut(baseUrl) {
//            Retrofit.Builder()
//                .baseUrl(baseUrl)
//                .addConverterFactory(GsonConverterFactory.create())
//                .client(OkHttpClient.Builder().build())
//                .build()
//        }
//    }
//}

object RetrofitClient {
    private val clients = mutableMapOf<String, Retrofit>()

    fun getClient(baseUrl: String): Retrofit {
        return clients.getOrPut(baseUrl) {
            Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(
                    OkHttpClient.Builder()
                        .connectTimeout(30, TimeUnit.SECONDS)
                        .writeTimeout(30, TimeUnit.SECONDS)
                        .readTimeout(30, TimeUnit.SECONDS)
                        .build()
                )
                .build()
        }
    }
}
