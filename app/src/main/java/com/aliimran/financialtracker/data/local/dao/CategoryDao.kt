package com.aliimran.financialtracker.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.aliimran.financialtracker.data.local.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    // ── Read ──────────────────────────────────────────────────

    /** All categories, alphabetically sorted. */
    @Query("SELECT * FROM categories ORDER BY name ASC")
    fun getAllCategories(): Flow<List<CategoryEntity>>

    /**
     * Categories filtered by [type] string (e.g. "EXPENSE").
     * The string value is stored in the DB by [Converters].
     */
    @Query("SELECT * FROM categories WHERE type = :type ORDER BY name ASC")
    fun getCategoriesByType(type: String): Flow<List<CategoryEntity>>

    /** Single-row lookup. */
    @Query("SELECT * FROM categories WHERE id = :id LIMIT 1")
    suspend fun getCategoryById(id: Long): CategoryEntity?

    // ── Write ─────────────────────────────────────────────────

    /** Inserts a new row. Returns auto-generated row id. */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertCategory(entity: CategoryEntity): Long

    /**
     * Bulk insert for database pre-population (seeds default categories).
     * IGNORE strategy skips duplicate rows silently.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCategories(entities: List<CategoryEntity>)

    /** Full-row update matched by primary key. */
    @Update
    suspend fun updateCategory(entity: CategoryEntity)

    /** Hard-deletes the matching row. */
    @Delete
    suspend fun deleteCategory(entity: CategoryEntity)
}
