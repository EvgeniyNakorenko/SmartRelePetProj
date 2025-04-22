package com.example.detectcontroller.domain.db

import com.example.detectcontroller.data.local.AppDatabase
import com.example.detectcontroller.data.remote.remDTO.StatusEventServerDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LoadEventServerFromDBUseCase(private val database: AppDatabase) {
    suspend fun execute(): List<StatusEventServerDTO> {
        return withContext(Dispatchers.IO) {
            val dataList = database.uiStateDao().getAllEventsServer()
            if (dataList.isEmpty()) {
                emptyList()
            } else {
                dataList.map { data ->
                    StatusEventServerDTO(
                        id = data.id,
                        timeev = data.timeev,
                        rstate = data.rstate,
                        value = data.value,
                        name = data.name,
                    )
                }
            }
        }
    }
}
