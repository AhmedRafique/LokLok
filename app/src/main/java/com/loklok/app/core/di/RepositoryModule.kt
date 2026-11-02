package com.loklok.app.core.di

import com.loklok.app.data.repository.AuthRepositoryImpl
import com.loklok.app.data.repository.CallRepositoryImpl
import com.loklok.app.data.repository.ChatRepositoryImpl
import com.loklok.app.data.repository.ContactRepositoryImpl
import com.loklok.app.domain.repository.AuthRepository
import com.loklok.app.domain.repository.CallRepository
import com.loklok.app.domain.repository.ChatRepository
import com.loklok.app.domain.repository.ContactRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        impl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindChatRepository(
        impl: ChatRepositoryImpl
    ): ChatRepository

    @Binds
    @Singleton
    abstract fun bindCallRepository(
        impl: CallRepositoryImpl
    ): CallRepository

    @Binds
    @Singleton
    abstract fun bindContactRepository(
        impl: ContactRepositoryImpl
    ): ContactRepository
}
