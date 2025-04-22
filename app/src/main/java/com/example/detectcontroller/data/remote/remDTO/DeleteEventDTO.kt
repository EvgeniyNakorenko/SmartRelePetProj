package com.example.detectcontroller.data.remote.remDTO

import kotlinx.serialization.Serializable

@Serializable
data class DeleteEventDTO (
    val dvid: String,
    val tkn: String,
    val typedv: Int,
    val num: Int,
    var com: String,
    var id : Int
)
