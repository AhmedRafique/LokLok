package com.loklok.app.core.di

import android.content.Context
import androidx.room.Room
import com.loklok.app.data.local.LokLokDatabase
import com.loklok.app.data.local.dao.CallDao
import com.loklok.app.data.local.dao.ChatDao
import com.loklok.app.data.local.dao.ContactDao
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
    fun provideDatabase(@ApplicationContext context: Context): LokLokDatabase {
        return Room.databaseBuilder(
            context,
            LokLokDatabase::class.java,
            LokLokDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideChatDao(database: LokLokDatabase): ChatDao = database.chatDao()

    @Provides
    fun provideCallDao(database: LokLokDatabase): CallDao = database.callDao()

    @Provides
    fun provideContactDao(database: LokLokDatabase): ContactDao = database.contactDao()
}
