package com.example.detectcontroller.data.remote.remDTO

import kotlinx.serialization.Serializable

@Serializable
data class RequestDataDTO(
    val dvid: String = "0",
    val tkn: String = "0",
    val typedv: Int = 0,
    val num: Int = 0,
    var com: String = ""
)
