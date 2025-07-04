package com.example.detectcontroller.data.local

import com.example.detectcontroller.data.local.locDTO.EventServerEntity
import com.example.detectcontroller.data.local.locDTO.LastEventsServerEntity
import com.example.detectcontroller.data.local.locDTO.RegServerEntity
import com.example.detectcontroller.data.local.locDTO.UiStateEntity
import com.example.detectcontroller.data.remote.remDTO.StatusEventServerDTO
import com.example.detectcontroller.data.remote.remDTO.UiState
import com.example.detectcontroller.domain.DBRepository
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject

@Module
@InstallIn(SingletonComponent::class)
class DBRepositoryImpl @Inject constructor(
    private val uiStateDao: UiStateDao,
) : DBRepository {

    override suspend fun getUiStates(): List<UiState> {
        return uiStateDao.getFirstTen().map { it.toDomain() }
    }

    override suspend fun deleteLastEventServerById(id: Int) {
        return uiStateDao.deleteLastEventServerById(id)
    }

    override suspend fun deleteLastEventDBById(id: Int) {
        return uiStateDao.deleteLastEventDBById(id)
    }

    override suspend fun getEventServerFromDB(): List<StatusEventServerDTO> {
        return uiStateDao.getAllEventsServer().map { it.toDomain() }
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

    override suspend fun saveDataServerInDB(uiState: UiState) {
        uiStateDao.insertUiState(uiState.toData())
    }

    override suspend fun insertLastEventServerDB(lastEventsServerEntity: LastEventsServerEntity) {
        uiStateDao.insertLastEventServer(lastEventsServerEntity)
    }

    private fun UiState.toData(): UiStateEntity {
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

    private fun UiStateEntity.toDomain(): UiState {
        return UiState(
            timedv = timedv,
            stt = stt,
            url = url,
            irl = irl,
            pwr = pwr,
            frq = frq,
            tmp = tmp,
            rmode = rmode,
            gomode = gomode,
            modes = modes
        )
    }
}