package com.example.detectcontroller.data.remote.remDTO

import kotlinx.serialization.Serializable

@Serializable
data class RequestDataDTO(
    val dvid: String = "",
    val tkn: String = "",
    val typedv: Int = 0,
    val num: Int = 0,
    var com: String = ""
)
