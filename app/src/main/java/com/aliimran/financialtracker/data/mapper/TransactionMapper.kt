package com.aliimran.financialtracker.data.mapper

import com.aliimran.financialtracker.data.local.entity.TransactionEntity
import com.aliimran.financialtracker.data.local.entity.TransactionWithCategory
import com.aliimran.financialtracker.domain.model.Transaction
import com.aliimran.financialtracker.domain.model.TransactionType

/** Bi-directional mapper between data layer entities and domain [Transaction]. */

/**
 * Maps [TransactionWithCategory] → [Transaction].
 * Category fields are denormalised onto the domain model so the
 * presentation layer never needs a second lookup.
 */
fun TransactionWithCategory.toDomain(): Transaction = Transaction(
    id                  = transaction.id,
    type                = TransactionType.valueOf(transaction.type),
    amount              = transaction.amount,
    categoryId          = transaction.categoryId,
    categoryName        = category?.name        ?: "General",
    categoryIconResName = category?.iconResName ?: "ic_cat_general",
    categoryColor       = category?.color       ?: 0xFF757575.toInt(),
    timestamp           = transaction.timestamp,
    note                = transaction.note,
    imageUri            = transaction.imageUri,
)

/**
 * Maps [TransactionEntity] → [Transaction] without category data.
 * Use [TransactionWithCategory.toDomain] for list views that show category info.
 */
fun TransactionEntity.toDomain(): Transaction = Transaction(
    id        = id,
    type      = TransactionType.valueOf(type),
    amount    = amount,
    categoryId = categoryId,
    timestamp = timestamp,
    note      = note,
    imageUri  = imageUri,
)

/** Maps domain [Transaction] → [TransactionEntity] for write operations. */
fun Transaction.toEntity(): TransactionEntity = TransactionEntity(
    id         = id,
    type       = type.name,
    amount     = amount,
    categoryId = categoryId,
    timestamp  = timestamp,
    note       = note,
    imageUri   = imageUri,
)
