package com.example.testforcalendarcounter.module

import android.content.Context
import androidx.room.Room
import com.example.testforcalendarcounter.data.dao.CigaretteDao
import com.example.testforcalendarcounter.dataBase.CigaretteDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): CigaretteDatabase {
        return Room.databaseBuilder(
            appContext,
            CigaretteDatabase::class.java,
            "cigarette_db"
        ).build()
    }

    @Provides
    fun provideCigaretteDao(database: CigaretteDatabase): CigaretteDao {
        return database.cigaretteDao()
    }
}