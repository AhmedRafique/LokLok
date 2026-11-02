package com.loklok.app.data.repository

import com.loklok.app.data.local.dao.ContactDao
import com.loklok.app.data.local.entity.ContactEntity
import com.loklok.app.domain.model.Contact
import com.loklok.app.domain.repository.ContactRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContactRepositoryImpl @Inject constructor(
    private val contactDao: ContactDao
) : ContactRepository {

    override fun getContacts(): Flow<List<Contact>> {
        return contactDao.getAllContacts().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getLokLokContacts(): Flow<List<Contact>> {
        return contactDao.getLokLokContacts().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun syncContacts(): Result<Int> {
        // Seeds initial sample contacts for testing & production sync demonstration
        val sampleContacts = listOf(
            ContactEntity(
                id = "c1",
                name = "Ahmed Al-Mansoori",
                phoneNumber = "+971501234567",
                isLokLokUser = true,
                avatarUrl = null,
                statusBio = "Available for HD calls",
                registeredUserId = "usr_ahmed"
            ),
            ContactEntity(
                id = "c2",
                name = "Fatima Zahra",
                phoneNumber = "+971529876543",
                isLokLokUser = true,
                avatarUrl = null,
                statusBio = "In a meeting, message only",
                registeredUserId = "usr_fatima"
            ),
            ContactEntity(
                id = "c3",
                name = "Omar Farooq",
                phoneNumber = "+971554321987",
                isLokLokUser = true,
                avatarUrl = null,
                statusBio = "Using Lok Lok!",
                registeredUserId = "usr_omar"
            ),
            ContactEntity(
                id = "c4",
                name = "John Doe",
                phoneNumber = "+14155552671",
                isLokLokUser = false,
                avatarUrl = null,
                statusBio = null,
                registeredUserId = null
            )
        )

        contactDao.insertContacts(sampleContacts)
        return Result.success(sampleContacts.size)
    }

    private fun ContactEntity.toDomain(): Contact {
        return Contact(
            id = id,
            name = name,
            phoneNumber = phoneNumber,
            isLokLokUser = isLokLokUser,
            avatarUrl = avatarUrl,
            statusBio = statusBio,
            registeredUserId = registeredUserId
        )
    }
}
