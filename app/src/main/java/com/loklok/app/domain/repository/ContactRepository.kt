package com.loklok.app.domain.repository

import com.loklok.app.domain.model.Contact
import kotlinx.coroutines.flow.Flow

interface ContactRepository {
    fun getContacts(): Flow<List<Contact>>
    fun getLokLokContacts(): Flow<List<Contact>>
    suspend fun syncContacts(): Result<Int>
}
