package com.example.testforcalendarcounter.module

import android.content.Context
import androidx.room.Room
import com.example.testforcalendarcounter.CigaretteRepository
import com.example.testforcalendarcounter.CigaretteRepositoryImpl
import com.example.testforcalendarcounter.data.dao.CigaretteDao
import com.example.testforcalendarcounter.data.dao.timer.TimerDao
import com.example.testforcalendarcounter.dataBase.CigaretteDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers


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
    @Singleton
    fun provideCigaretteDao(database: CigaretteDatabase): CigaretteDao {
        return database.cigaretteDao()
    }

    @Provides
    @Singleton
    fun provideTimerDao(database: CigaretteDatabase): TimerDao {
        return database.timerDao()
    }

    @Provides
    @Singleton
    fun provideCigaretteRepository(
        dao: CigaretteDao,
        timerDao: TimerDao
    ): CigaretteRepository {
        return CigaretteRepositoryImpl(dao, timerDao)
    }

    @Provides
    fun provideCoroutineDispatcher(): CoroutineDispatcher {
        return Dispatchers.IO // Provide a background dispatcher
    }
}
