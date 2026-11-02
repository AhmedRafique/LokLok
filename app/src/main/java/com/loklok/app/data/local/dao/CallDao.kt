package com.loklok.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.loklok.app.data.local.entity.CallHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CallDao {

    @Query("SELECT * FROM call_history ORDER BY timestamp DESC")
    fun getAllCallHistory(): Flow<List<CallHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCallRecord(call: CallHistoryEntity)

    @Query("DELETE FROM call_history WHERE callId = :callId")
    suspend fun deleteCallRecord(callId: String)

    @Query("DELETE FROM call_history")
    suspend fun clearCallHistory()
}
