package com.example.detectcontroller.data.local

import com.example.detectcontroller.data.local.locDTO.ErrorEntity
import com.example.detectcontroller.data.local.locDTO.EventServerEntity
import com.example.detectcontroller.data.local.locDTO.LastEventsServerEntity
import com.example.detectcontroller.data.local.locDTO.RegServerEntity
import com.example.detectcontroller.data.local.locDTO.UiStateEntity
import com.example.detectcontroller.data.remote.remDTO.StatusEventServerDTO
import com.example.detectcontroller.data.remote.remDTO.UiStateDTO
import com.example.detectcontroller.domain.DBRepository
import com.example.detectcontroller.domain.models.ErrorServerMod
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@Module
@InstallIn(SingletonComponent::class)
class DBRepositoryImpl @Inject constructor(
    private val uiStateDao: UiStateDao,
) : DBRepository {

    override suspend fun getUiStates(): List<UiStateDTO> {
        return uiStateDao.getFirstTen().map { it.toDomain() }
    }

    override suspend fun deleteLastEventServerById(id: Int) {
        return uiStateDao.deleteLastEventServerById(id)
    }

    override suspend fun deleteLastEventDBById(id: Int) {
        return uiStateDao.deleteLastEventDBById(id)
    }

    override suspend fun getEventServerFromDB(): List<StatusEventServerDTO> {
        return uiStateDao.getAllEventsServer().map { it.toDomain().apply { it.timeev = convertTime(it.timeev) } }
    }

    override suspend fun getOneLastEventServerFromDB(id: Int): StatusEventServerDTO? {
        return uiStateDao.getOneLastEventServer(id).toDomain()
    }

    override suspend fun saveEventServerInDB(statusEventServerDTO: StatusEventServerDTO) {
        uiStateDao.insertEventServer(statusEventServerDTO.toData())
    }

    override suspend fun insertRegServer(regServerEntity: RegServerEntity) {
        uiStateDao.insertRegServer(regServerEntity)
    }

    override suspend fun getAllRegServer(): List<RegServerEntity> {
        return uiStateDao.getAllRegServer()
    }

    override suspend fun saveDataServerInDB(uiStateDTO: UiStateDTO) {
        uiStateDao.insertUiState(uiStateDTO.toData())
    }

    override suspend fun insertLastEventServerDB(lastEventsServerEntity: LastEventsServerEntity) {
        uiStateDao.insertLastEventServer(lastEventsServerEntity)
    }

    override suspend fun insertError(error: ErrorServerMod) {
        uiStateDao.insertError(error.toData())
    }

    override suspend fun getAllErrors(): List<ErrorServerMod> {
        return uiStateDao.getAllErrors().map { it.toDomain() }
    }

    override suspend fun clearOldErrors(threshold: Long) {
        uiStateDao.clearOldErrors(threshold)
    }

    private fun convertTime(
        dateTimeString: String,
        zoneId: ZoneId = ZoneId.systemDefault()
    ) : String {
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")
        val dateTime = LocalDateTime.parse(dateTimeString, formatter)
        return dateTime.atZone(zoneId).toEpochSecond().toString()
    }

    private fun ErrorEntity.toDomain(): ErrorServerMod {
        return ErrorServerMod(
            id = id,
            errorCode = errorCode,
            errorMessage = errorMessage,
            timeev = timeev,
            deviceId = deviceId
        )
    }


    private fun ErrorServerMod.toData(): ErrorEntity {
        return ErrorEntity(
            id = id,
            errorCode = errorCode,
            errorMessage = errorMessage,
            timeev = timeev,
            deviceId = deviceId
        )
    }


    private fun UiStateDTO.toData(): UiStateEntity {
        return UiStateEntity(
            timedv = timedv,
            stt = stt,
            url = url,
            irl = irl,
            pwr = pwr,
            frq = frq,
            tmp = tmp,
            rmode = rmode,
            gomode = gomode,
            modes = modes,
            online = online
        )
    }

    private fun StatusEventServerDTO.toData(): EventServerEntity {
        return EventServerEntity(
            id = id,
            timeev = timeev,
            rstate = rstate,
            value = value,
            name = name,
        )
    }

    private fun LastEventsServerEntity.toDomain(): StatusEventServerDTO {
        return StatusEventServerDTO(
            id = id,
            timeev = timeev,
            rstate = rstate,
            value = value,
            name = name,
        )
    }

    private fun EventServerEntity.toDomain(): StatusEventServerDTO {
        return StatusEventServerDTO(
            id = id,
            timeev = timeev,
            rstate = rstate,
            value = value,
            name = name,
        )
    }

    private fun UiStateEntity.toDomain(): UiStateDTO {
        return UiStateDTO(
            timedv = timedv,
            stt = stt,
            url = url,
            irl = irl,
            pwr = pwr,
            frq = frq,
            tmp = tmp,
            rmode = rmode,
            gomode = gomode,
            modes = modes,
            online = online
        )
    }
}