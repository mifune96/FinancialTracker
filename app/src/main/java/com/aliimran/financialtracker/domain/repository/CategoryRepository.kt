package com.aliimran.financialtracker.domain.repository

import com.aliimran.financialtracker.domain.model.Category
import com.aliimran.financialtracker.domain.model.TransactionType
import kotlinx.coroutines.flow.Flow

/** Abstract contract for category persistence. */
interface CategoryRepository {

    /** Observes categories whose type matches [type]. */
    fun getCategoriesByType(type: TransactionType): Flow<List<Category>>

    /** Observes all categories regardless of type. */
    fun getAllCategories(): Flow<List<Category>>

    /** One-shot lookup — returns null when id does not exist. */
    suspend fun getCategoryById(id: Long): Category?

    /** Persists a new category. Returns the Room-generated row id. */
    suspend fun insertCategory(category: Category): Long

    /** Replaces the stored row matched by [Category.id]. */
    suspend fun updateCategory(category: Category)

    /** Hard-deletes the row matched by [Category.id]. */
    suspend fun deleteCategory(category: Category)
}
