package com.example.detectcontroller.domain.db

import com.example.detectcontroller.data.local.AppDatabase
import com.example.detectcontroller.data.local.locDTO.EventServerEntity
import com.example.detectcontroller.data.local.locDTO.RegServerEntity
import com.example.detectcontroller.data.remote.remDTO.StatusEventServerDTO
import com.example.detectcontroller.domain.DBRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

//class GetOneLastEventServerFromDBUseCase(private val database: AppDatabase) {
//    suspend fun execute(id: Int): StatusEventServerDTO? {
//        return withContext(Dispatchers.IO) {
//            val data = database.uiStateDao().getOneLastEventServer(id)
//            if (data != null) {
//                StatusEventServerDTO(
//                    id = data.id,
//                    timeev = data.timeev,
//                    rstate = data.rstate,
//                    value = data.value,
//                    name = data.name
//                )
//            } else {
//                null
//            }
//        }
//    }
//}


class GetOneLastEventServerFromDBUseCase(private val repository: DBRepository) {
    suspend fun execute(id: Int): StatusEventServerDTO? {
        return withContext(Dispatchers.IO) {
            repository.getOneLastEventServerFromDB(id)
        }

    }
}

//suspend fun execute(): List<RegServerEntity?> {
//    return withContext(Dispatchers.IO) {
//        database.uiStateDao().getAllRegServer()
//    }
//}

//class DeleteLastEventServerByIdFromDBUseCase(private val repository: DBRepository) {
//    suspend fun execute(id: Int) {
//        return withContext(Dispatchers.IO) {
//            repository.deleteLastEventServerById(id)
//            repository.deleteLastEventDBById(id)
//        }
//    }
//}