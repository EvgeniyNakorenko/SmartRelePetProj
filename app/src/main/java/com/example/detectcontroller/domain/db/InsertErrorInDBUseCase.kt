package com.example.detectcontroller.domain.db

import com.example.detectcontroller.data.local.locDTO.LastEventsServerEntity
import com.example.detectcontroller.domain.DBRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class InsertErrorInDBUseCase (private val repository: DBRepository){
    suspend fun execute(lastEventsServerEntity: LastEventsServerEntity) {
        withContext(Dispatchers.IO) {
            repository.insertLastEventServerDB(lastEventsServerEntity)
        }
    }

}

//class InsertLastEventServerInDBUseCase(private val repository: DBRepository) {
//    suspend fun execute(lastEventsServerEntity: LastEventsServerEntity) {
//        withContext(Dispatchers.IO) {
//            repository.insertLastEventServerDB(lastEventsServerEntity)
//        }
//    }
//}