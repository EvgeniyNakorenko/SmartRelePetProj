package com.example.detectcontroller.data.remote.remDTO

import kotlinx.serialization.Serializable

@Serializable
data class DeleteDeviceEventResponseDTO(
    val success: Boolean,
    val deletedId: Int
)

