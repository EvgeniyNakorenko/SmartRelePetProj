package com.example.detectcontroller.data.local.locDTO

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "last_event_server")
data class LastEventsServerEntity(
    @PrimaryKey
    val id: Int,
    val timeev: String,
    val rstate: String,
    val value: String,
    val name: String,
)
