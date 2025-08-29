package com.example.testforcalendarcounter.module

import android.content.Context
import androidx.room.Room
import com.example.testforcalendarcounter.data.dao.BaselineDao
import com.example.testforcalendarcounter.repository.cigarette.CigaretteRepository
import com.example.testforcalendarcounter.repository.cigarette.CigaretteRepositoryImpl
import com.example.testforcalendarcounter.data.dao.CigaretteDao
import com.example.testforcalendarcounter.data.dao.PackPriceDao
import com.example.testforcalendarcounter.data.dao.TimerDao
import com.example.testforcalendarcounter.data.dao.UserSettingsDao
import com.example.testforcalendarcounter.dataBase.CigaretteDatabase
import com.example.testforcalendarcounter.repository.settings.UserSettingsRepository
import com.example.testforcalendarcounter.repository.settings.UserSettingsRepositoryImpl
import com.example.testforcalendarcounter.repository.cigarette.BaselineRepository
import com.example.testforcalendarcounter.repository.cigarette.BaselineRepositoryImpl
import com.example.testforcalendarcounter.repository.packprice.PackPriceRepository
import com.example.testforcalendarcounter.repository.packprice.PackPriceRepositoryImpl
import com.example.testforcalendarcounter.repository.quiz.QuizRepository
import com.example.testforcalendarcounter.repository.quiz.QuizRepositoryImpl
import com.example.testforcalendarcounter.repository.stats.StatsRepository
import com.example.testforcalendarcounter.repository.stats.StatsRepositoryImpl
import com.example.testforcalendarcounter.repository.timer.TimerRepository
import com.example.testforcalendarcounter.repository.timer.TimerRepositoryImpl
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



    // 1) Provide the database
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): CigaretteDatabase {
        return Room.databaseBuilder(
            context,
            CigaretteDatabase::class.java,
            "cigarette_db"
        ).build()
    }

    // 2) Provide the DAOs
    @Provides
    @Singleton
    fun provideCigaretteDao(db: CigaretteDatabase): CigaretteDao = db.cigaretteDao()

    @Provides
    @Singleton
    fun provideTimerDao(db: CigaretteDatabase): TimerDao = db.timerDao()

    @Provides
    @Singleton
    fun providePackPriceDao(db: CigaretteDatabase): PackPriceDao = db.packPriceDao()

    @Provides
    @Singleton
    fun provideUserSettingsDao(db: CigaretteDatabase): UserSettingsDao = db.userSettingsDao()

    @Provides
    @Singleton
    fun provideBaselineDao(db: CigaretteDatabase): BaselineDao = db.baselineDao()


    // 3) Provide each repository separately
    @Provides
    @Singleton
    fun provideCigaretteRepository(
        cigaretteDao: CigaretteDao
    ): CigaretteRepository {
        return CigaretteRepositoryImpl(cigaretteDao)
    }

    @Provides
    @Singleton
    fun provideTimerRepository(
        timerDao: TimerDao
    ): TimerRepository {
        return TimerRepositoryImpl(timerDao)
    }

    @Provides
    @Singleton
    fun providePackPriceRepository(
        packPriceDao: PackPriceDao
    ): PackPriceRepository {
        return PackPriceRepositoryImpl(packPriceDao)
    }



    @Provides
    @Singleton
    fun provideQuizRepository(
        @ApplicationContext context: Context
    ): QuizRepository {
        return QuizRepositoryImpl(context)
    }


    @Provides @Singleton
    fun provideBaselineRepository(
        baselineDao: BaselineDao,
        userSettingsRepository: UserSettingsRepository,
        cigaretteDao: CigaretteDao,
        io: CoroutineDispatcher
    ): BaselineRepository = BaselineRepositoryImpl(
        baselineDao = baselineDao,
        settingsRepo = userSettingsRepository,
        cigaretteDao = cigaretteDao,
        io = io
    )


    @Provides
    @Singleton
    fun provideStatsRepository(
        cigaretteRepository: CigaretteRepository,
        packPriceRepository: PackPriceRepository,
        cigaretteDao: CigaretteDao
    ): StatsRepository {
        return StatsRepositoryImpl(cigaretteRepository, packPriceRepository, cigaretteDao)
    }


    @Provides
    @Singleton
    fun provideUserSettingsRepository(
        userSettingsDao: UserSettingsDao
    ): UserSettingsRepository {
        return UserSettingsRepositoryImpl(userSettingsDao)
    }



    // 4) Provide a coroutine dispatcher if needed
    @Provides
    fun provideCoroutineDispatcher(): CoroutineDispatcher = Dispatchers.IO
}
