package com.loklok.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.loklok.app.data.local.entity.ContactEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDao {

    @Query("SELECT * FROM contacts ORDER BY isLokLokUser DESC, name ASC")
    fun getAllContacts(): Flow<List<ContactEntity>>

    @Query("SELECT * FROM contacts WHERE isLokLokUser = 1 ORDER BY name ASC")
    fun getLokLokContacts(): Flow<List<ContactEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContacts(contacts: List<ContactEntity>)

    @Query("DELETE FROM contacts")
    suspend fun clearContacts()
}
