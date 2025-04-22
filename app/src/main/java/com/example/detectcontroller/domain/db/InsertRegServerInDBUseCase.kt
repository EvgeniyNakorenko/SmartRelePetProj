package com.example.detectcontroller.domain.db

import com.example.detectcontroller.data.local.AppDatabase
import com.example.detectcontroller.data.local.locDTO.RegServerEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class InsertRegServerInDBUseCase (private val database: AppDatabase) {
    suspend fun execute(regServerEntity: RegServerEntity) {
        withContext(Dispatchers.IO) {
            database.uiStateDao().insertRegServer(regServerEntity)
        }
    }
}