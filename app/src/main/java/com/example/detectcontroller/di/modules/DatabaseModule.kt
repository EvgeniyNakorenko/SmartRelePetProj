package com.example.detectcontroller.di.modules

import android.content.Context
import androidx.room.Room
import com.example.detectcontroller.data.local.AppDatabase
import com.example.detectcontroller.data.local.UiStateDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "app_database"
        )
//            .addMigrations(AppDatabase.MIGRATION_3_4)
            .fallbackToDestructiveMigrationOnDowngrade()
            .build()
    }

    @Provides
    fun provideUiStateDao(database: AppDatabase): UiStateDao {
        return database.uiStateDao()
    }

}