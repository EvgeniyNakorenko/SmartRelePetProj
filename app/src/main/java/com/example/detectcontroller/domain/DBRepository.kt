package com.example.detectcontroller.domain

import com.example.detectcontroller.data.local.locDTO.LastEventsServerEntity
import com.example.detectcontroller.data.local.locDTO.RegServerEntity
import com.example.detectcontroller.data.remote.remDTO.StatusEventServerDTO
import com.example.detectcontroller.data.remote.remDTO.UiStateDTO
import com.example.detectcontroller.domain.models.ErrorServerMod


interface DBRepository {

    suspend fun getUiStates(): List<UiStateDTO>
    suspend fun deleteLastEventServerById(id: Int)
    suspend fun deleteLastEventDBById(id: Int)
    suspend fun getEventServerFromDB(): List<StatusEventServerDTO>
    suspend fun getOneLastEventServerFromDB(id: Int): StatusEventServerDTO?
    suspend fun saveEventServerInDB(statusEventServerDTO: StatusEventServerDTO)
    suspend fun insertRegServer(regServerEntity: RegServerEntity)
    suspend fun getAllRegServer(): List<RegServerEntity>
    suspend fun saveDataServerInDB(uiStateDTO: UiStateDTO)
    suspend fun insertLastEventServerDB(lastEventsServerEntity: LastEventsServerEntity)
    suspend fun insertError(error: ErrorServerMod)
    suspend fun getAllErrors(): List<ErrorServerMod>
    suspend fun clearOldErrors(threshold: Long)
}