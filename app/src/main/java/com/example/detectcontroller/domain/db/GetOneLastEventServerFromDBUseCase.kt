package com.example.detectcontroller.domain.db

import com.example.detectcontroller.data.local.AppDatabase
import com.example.detectcontroller.data.local.locDTO.EventServerEntity
import com.example.detectcontroller.data.local.locDTO.RegServerEntity
import com.example.detectcontroller.data.remote.remDTO.StatusEventServerDTO
import com.example.detectcontroller.domain.DBRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class GetOneLastEventServerFromDBUseCase(private val repository: DBRepository) {
    suspend fun execute(id: Int?): StatusEventServerDTO? {
        return withContext(Dispatchers.IO) {
            repository.getOneLastEventServerFromDB(id)
        }

    }
}

