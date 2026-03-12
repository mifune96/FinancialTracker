package com.aliimran.financialtracker.domain.model

/**
 * Domain model for a transaction category (e.g. "Food", "Salary").
 *
 * @param id          Auto-generated PK.
 * @param name        Human-readable label shown in the UI.
 * @param iconResName Drawable resource name string (e.g. "ic_food").
 * @param type        Whether this category is valid for EXPENSE or INCOME.
 * @param color       Material ARGB color for icon background chips.
 */
data class Category(
    val id: Long = 0L,
    val name: String,
    val iconResName: String,
    val type: TransactionType,
    val color: Int = 0xFF6200EE.toInt(),
)
