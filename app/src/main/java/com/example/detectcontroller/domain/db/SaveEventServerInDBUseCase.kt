package com.example.detectcontroller.domain.db

import com.example.detectcontroller.data.local.AppDatabase
import com.example.detectcontroller.data.local.locDTO.EventServerEntity
import com.example.detectcontroller.data.remote.remDTO.StatusEventServerDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SaveEventServerInDBUseCase(private val database: AppDatabase) {

    suspend fun execute(statusEventServerDTO: StatusEventServerDTO) {
        withContext(Dispatchers.IO) {
            val eventServerEntity = EventServerEntity(
                id = statusEventServerDTO.id,
                timeev = statusEventServerDTO.timeev,
                rstate = statusEventServerDTO.rstate.toString(),
                value = statusEventServerDTO.value,
                name = statusEventServerDTO.name,
            )
            database.uiStateDao().insertEventServer(eventServerEntity)
        }
    }
}