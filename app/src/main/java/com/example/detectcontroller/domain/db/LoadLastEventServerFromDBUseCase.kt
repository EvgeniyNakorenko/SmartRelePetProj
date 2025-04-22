package com.example.detectcontroller.domain.db

import com.example.detectcontroller.data.local.AppDatabase
import com.example.detectcontroller.data.remote.remDTO.StatusEventServerDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LoadLastEventServerFromDBUseCase (private val database: AppDatabase) {
    suspend fun execute(): StatusEventServerDTO? {
        return withContext(Dispatchers.IO) {
            val data = database.uiStateDao().getLastEventServer()
            if (data != null) {

                StatusEventServerDTO(
                    id = data.id,
                    timeev = data.timeev,
                    rstate = data.rstate,
                    value = data.value,
                    name = data.name
                )
            } else {
                null
            }
        }
    }
}
