package com.example.detectcontroller.domain.db

import com.example.detectcontroller.data.local.AppDatabase
import com.example.detectcontroller.data.local.locDTO.LastEventsServerEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class InsertLastEventServerInDBUseCase(private val database: AppDatabase) {
    suspend fun execute(lastEventsServerEntity: LastEventsServerEntity) {
        withContext(Dispatchers.IO) {
            database.uiStateDao().insertLastEventServer(lastEventsServerEntity)
        }
    }
}