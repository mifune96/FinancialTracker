package com.aliimran.financialtracker.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity mapping to the "categories" table.
 * Row id = 1 is reserved for the "General" fallback category
 * referenced by the FK default value in [TransactionEntity].
 */
@Entity(tableName = "categories")
data class CategoryEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    val name: String,

    @ColumnInfo(name = "icon_res_name")
    val iconResName: String,

    /** Stored as enum name string, e.g. "EXPENSE". */
    val type: String,

    /** ARGB color integer for icon background chips. */
    val color: Int = 0xFF6200EE.toInt(),
)
