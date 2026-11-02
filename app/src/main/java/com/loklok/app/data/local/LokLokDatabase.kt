package com.loklok.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.loklok.app.data.local.dao.CallDao
import com.loklok.app.data.local.dao.ChatDao
import com.loklok.app.data.local.dao.ContactDao
import com.loklok.app.data.local.entity.CallHistoryEntity
import com.loklok.app.data.local.entity.ChatMessageEntity
import com.loklok.app.data.local.entity.ContactEntity
import com.loklok.app.data.local.entity.ConversationEntity
import com.loklok.app.domain.model.CallDirection
import com.loklok.app.domain.model.CallMediaType
import com.loklok.app.domain.model.CallState
import com.loklok.app.domain.model.DeliveryStatus
import com.loklok.app.domain.model.MessageType

class Converters {
    @TypeConverter
    fun fromMessageType(value: MessageType): String = value.name

    @TypeConverter
    fun toMessageType(value: String): MessageType = enumValueOf(value)

    @TypeConverter
    fun fromDeliveryStatus(value: DeliveryStatus): String = value.name

    @TypeConverter
    fun toDeliveryStatus(value: String): DeliveryStatus = enumValueOf(value)

    @TypeConverter
    fun fromCallMediaType(value: CallMediaType): String = value.name

    @TypeConverter
    fun toCallMediaType(value: String): CallMediaType = enumValueOf(value)

    @TypeConverter
    fun fromCallDirection(value: CallDirection): String = value.name

    @TypeConverter
    fun toCallDirection(value: String): CallDirection = enumValueOf(value)

    @TypeConverter
    fun fromCallState(value: CallState): String = value.name

    @TypeConverter
    fun toCallState(value: String): CallState = enumValueOf(value)
}

@Database(
    entities = [
        ChatMessageEntity::class,
        ConversationEntity::class,
        CallHistoryEntity::class,
        ContactEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class LokLokDatabase : RoomDatabase() {
    abstract fun chatDao(): ChatDao
    abstract fun callDao(): CallDao
    abstract fun contactDao(): ContactDao

    companion object {
        const val DATABASE_NAME = "loklok.db"
    }
}
