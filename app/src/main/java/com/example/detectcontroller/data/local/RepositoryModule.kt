package com.example.detectcontroller.data.local

import com.example.detectcontroller.domain.DBRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

//@Module
//@InstallIn(SingletonComponent::class)
//object RepositoryModule {
//    @Provides
//    fun provideDBRepository(uiStateDao: UiStateDao): DBRepository {
//        return DBRepositoryImpl(uiStateDao)
//    }
//}

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {
    @Binds
    fun bindDBRepository(impl: DBRepositoryImpl): DBRepository
}