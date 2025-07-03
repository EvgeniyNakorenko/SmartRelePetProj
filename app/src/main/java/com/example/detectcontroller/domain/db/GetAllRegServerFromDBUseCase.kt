package com.example.detectcontroller.domain.db

import com.example.detectcontroller.data.local.AppDatabase
import com.example.detectcontroller.data.local.locDTO.RegServerEntity
import com.example.detectcontroller.data.remote.remDTO.StatusEventServerDTO
import com.example.detectcontroller.domain.DBRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


//class GetAllRegServerFromDBUseCase (private val database: AppDatabase) {
//
//    suspend fun execute(): List<RegServerEntity?> {
//        return withContext(Dispatchers.IO) {
//            database.uiStateDao().getAllRegServer()
//        }
//    }
//}

class GetAllRegServerFromDBUseCase (private val repository: DBRepository) {

    suspend fun execute(): List<RegServerEntity?> {
        return withContext(Dispatchers.IO) {
            repository.getAllRegServer()
        }
    }
}



//class GetOneLastEventServerFromDBUseCase(private val repository: DBRepository) {
//    suspend fun execute(id: Int): StatusEventServerDTO? {
//        return withContext(Dispatchers.IO) {
//            repository.getOneLastEventServerFromDB(id)
//        }
//
//    }
//}