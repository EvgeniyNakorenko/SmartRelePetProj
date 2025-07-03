package com.example.detectcontroller.domain.db

import com.example.detectcontroller.data.local.AppDatabase
import com.example.detectcontroller.data.local.locDTO.RegServerEntity
import com.example.detectcontroller.data.remote.remDTO.StatusEventServerDTO
import com.example.detectcontroller.domain.DBRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
//
//class InsertRegServerInDBUseCase (private val database: AppDatabase) {
//    suspend fun execute(regServerEntity: RegServerEntity) {
//        withContext(Dispatchers.IO) {
//            database.uiStateDao().insertRegServer(regServerEntity)
//        }
//    }
//}



class InsertRegServerInDBUseCase (private val repository: DBRepository) {
    suspend fun execute(regServerEntity: RegServerEntity) {
        withContext(Dispatchers.IO) {
            repository.insertRegServer(regServerEntity)
        }
    }
}

//suspend fun execute(statusEventServerDTO: StatusEventServerDTO) {
//    withContext(Dispatchers.IO) {
//        repository.saveEventServerInDB(statusEventServerDTO)
//    }
//}

