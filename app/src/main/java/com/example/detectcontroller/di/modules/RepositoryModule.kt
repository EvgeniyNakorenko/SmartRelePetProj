package com.example.detectcontroller.di.modules

import com.example.detectcontroller.data.local.DBRepositoryImpl
import com.example.detectcontroller.domain.DBRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {
    @Binds
    fun bindDBRepository(impl: DBRepositoryImpl): DBRepository
}