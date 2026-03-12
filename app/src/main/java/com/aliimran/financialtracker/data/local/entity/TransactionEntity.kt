package com.aliimran.financialtracker.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity mapping to the "transactions" table.
 *
 * FK constraint on [categoryId] → [CategoryEntity.id].
 *   ON DELETE SET DEFAULT → orphaned rows keep category_id = 1
 *   (the "General" fallback) rather than being silently deleted.
 *
 * Indices on category_id (FK join) and timestamp (date-range queries)
 * are critical for History-screen query performance.
 */
@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity         = CategoryEntity::class,
            parentColumns  = ["id"],
            childColumns   = ["category_id"],
            onDelete       = ForeignKey.SET_DEFAULT,
        )
    ],
    indices = [
        Index("category_id"),
        Index("timestamp"),
    ],
)
data class TransactionEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    /** Stored as enum name string, e.g. "EXPENSE". Converted by [Converters]. */
    val type: String,

    val amount: Double,

    @ColumnInfo(name = "category_id", defaultValue = "1")
    val categoryId: Long,

    /** Unix epoch milliseconds — indexed for fast strftime() range queries. */
    val timestamp: Long,

    val note: String = "",

    @ColumnInfo(name = "image_uri")
    val imageUri: String? = null,
)
