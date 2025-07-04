package com.example.detectcontroller.data.local.locDTO

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "ErrorEntity")
data class ErrorEntity (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val errorCode: Int,
    val errorMessage: String,
    val timestamp: Long,
    val deviceId: String?
)

