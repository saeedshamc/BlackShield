package com.sentinel.di

import android.content.Context
import androidx.room.Room
import com.sentinel.data.local.database.SentinelDatabase
import com.sentinel.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideApplicationScope(): CoroutineScope =
        CoroutineScope(SupervisorJob() + Dispatchers.Default)
}

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): SentinelDatabase =
        Room.databaseBuilder(context, SentinelDatabase::class.java, Constants.DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .build()

    @Provides fun provideRuleDao(db: SentinelDatabase) = db.ruleDao()
    @Provides fun provideProfileDao(db: SentinelDatabase) = db.profileDao()
    @Provides fun provideLogDao(db: SentinelDatabase) = db.logDao()
    @Provides fun provideLockedAppDao(db: SentinelDatabase) = db.lockedAppDao()
    @Provides fun provideDecoyDataDao(db: SentinelDatabase) = db.decoyDataDao()
    @Provides fun provideButtonTriggerDao(db: SentinelDatabase) = db.buttonTriggerDao()
    @Provides fun provideSettingsDao(db: SentinelDatabase) = db.settingsDao()
}
