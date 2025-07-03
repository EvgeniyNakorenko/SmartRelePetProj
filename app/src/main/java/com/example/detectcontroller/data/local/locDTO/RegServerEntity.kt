package com.example.detectcontroller.data.local.locDTO

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "reg_server_data")
data class RegServerEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val dvid: String = "" ,
    val tkn: String = "",
    val com: String = "",
    val typedv: Int = 0,
    val num: Int = 0,
)
