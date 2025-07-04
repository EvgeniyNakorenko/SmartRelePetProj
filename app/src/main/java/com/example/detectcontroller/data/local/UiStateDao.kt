package com.example.detectcontroller.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.detectcontroller.data.local.locDTO.ErrorEntity
import com.example.detectcontroller.data.local.locDTO.EventServerEntity
import com.example.detectcontroller.data.local.locDTO.LastEventsServerEntity
import com.example.detectcontroller.data.local.locDTO.RegServerEntity
import com.example.detectcontroller.data.local.locDTO.UiStateEntity

@Dao
interface UiStateDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUiState(uiStateEntity: UiStateEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEventServer(eventServerEntity: EventServerEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLastEventServer(lastEventsServerEntity: LastEventsServerEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRegServer(regServerEntity: RegServerEntity)

    @Query("SELECT * FROM event_server")
    suspend fun getAllEventsServer(): List<EventServerEntity>

    @Query("SELECT * FROM event_server ORDER BY id DESC LIMIT 1")
    suspend fun getLastEventServer(): EventServerEntity?

    @Query("SELECT * FROM ui_state ORDER BY id DESC LIMIT 200")
    suspend fun getFirstTen(): List<UiStateEntity>

    @Query("SELECT * FROM reg_server_data WHERE dvid = :dvid")
    suspend fun getRegServerByDvid(dvid: String): RegServerEntity?

    @Query("SELECT * FROM reg_server_data ")
    suspend fun getAllRegServer(): List<RegServerEntity>

    @Query("SELECT * FROM last_event_server WHERE id = :id")
    suspend fun getOneLastEventServer(id: Int): LastEventsServerEntity

    @Query("DELETE FROM last_event_server WHERE id = :id")
    suspend fun deleteLastEventServerById(id: Int)

    @Query("DELETE FROM event_server WHERE id = :id")
    suspend fun deleteLastEventDBById(id: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertError(error: ErrorEntity)

    @Query("SELECT * FROM ErrorEntity ORDER BY id DESC LIMIT 20")
    fun getAllErrors(): List<ErrorEntity>

    @Query("DELETE FROM ErrorEntity WHERE timestamp < :threshold")
    suspend fun clearOldErrors(threshold: Long)
    
    //errors
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertEventServer(eventServerEntity: EventServerEntity)
//
}