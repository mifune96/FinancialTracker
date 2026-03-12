package com.aliimran.financialtracker.domain.model

/**
 * Core domain model for a single financial transaction.
 *
 * @param id                   Auto-generated PK (0 = unsaved / new).
 * @param type                 EXPENSE | INCOME | TRANSFER.
 * @param amount               Monetary value — always positive Double.
 * @param categoryId           FK → Category.id.
 * @param categoryName         Denormalised for display (no extra join needed).
 * @param categoryIconResName  Drawable resource name string, e.g. "ic_food".
 * @param categoryColor        ARGB Int for icon chip tinting.
 * @param timestamp            Unix epoch milliseconds.
 * @param note                 Free-text annotation by the user.
 * @param imageUri             Optional content URI for an attached receipt image.
 */
data class Transaction(
    val id: Long = 0L,
    val type: TransactionType,
    val amount: Double,
    val categoryId: Long,
    val categoryName: String = "",
    val categoryIconResName: String = "",
    val categoryColor: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val note: String = "",
    val imageUri: String? = null,
)
