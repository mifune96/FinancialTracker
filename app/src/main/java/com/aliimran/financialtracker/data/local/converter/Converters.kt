package com.aliimran.financialtracker.data.local.converter

import androidx.room.TypeConverter
import com.aliimran.financialtracker.domain.model.TransactionType

/**
 * Room TypeConverters for non-primitive column types.
 *
 * [TransactionType] is persisted as its enum name string (e.g. "EXPENSE")
 * to keep the SQLite schema readable and queries human-friendly.
 */
class Converters {

    @TypeConverter
    fun fromTransactionType(type: TransactionType): String = type.name

    @TypeConverter
    fun toTransactionType(value: String): TransactionType =
        TransactionType.valueOf(value)
}
