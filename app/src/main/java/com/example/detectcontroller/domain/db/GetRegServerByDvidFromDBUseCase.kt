package com.example.detectcontroller.domain.db

import com.example.detectcontroller.data.local.AppDatabase
import com.example.detectcontroller.data.local.locDTO.RegServerEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetRegServerByDvidFromDBUseCase(private val database: AppDatabase) {

    suspend fun execute(dvid: String): RegServerEntity? {
        return withContext(Dispatchers.IO) {
            database.uiStateDao().getRegServerByDvid(dvid)
        }
    }
}
