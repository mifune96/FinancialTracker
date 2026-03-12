package com.aliimran.financialtracker.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

/**
 * Room multi-table result holder produced by @Transaction DAO queries.
 *
 * Using @Embedded + @Relation avoids N+1 queries — Room resolves the
 * relationship in a single query batch rather than per-item lookups.
 *
 * [category] is nullable in the unlikely event the FK cascade has not yet
 * resolved (should not occur with ON DELETE SET DEFAULT in place).
 */
data class TransactionWithCategory(

    @Embedded
    val transaction: TransactionEntity,

    @Relation(
        parentColumn = "category_id",
        entityColumn = "id",
    )
    val category: CategoryEntity?,
)
