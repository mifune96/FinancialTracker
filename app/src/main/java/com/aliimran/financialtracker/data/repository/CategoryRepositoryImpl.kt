package com.aliimran.financialtracker.data.repository

import com.aliimran.financialtracker.data.local.dao.CategoryDao
import com.aliimran.financialtracker.data.mapper.toDomain
import com.aliimran.financialtracker.data.mapper.toEntity
import com.aliimran.financialtracker.domain.model.Category
import com.aliimran.financialtracker.domain.model.TransactionType
import com.aliimran.financialtracker.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Concrete implementation of [CategoryRepository].
 * Delegates all persistence to [CategoryDao] and maps between
 * data and domain models via extension functions.
 */
@Singleton
class CategoryRepositoryImpl @Inject constructor(
    private val categoryDao: CategoryDao,
) : CategoryRepository {

    override fun getCategoriesByType(type: TransactionType): Flow<List<Category>> =
        categoryDao
            .getCategoriesByType(type.name)
            .map { list -> list.map { it.toDomain() } }

    override fun getAllCategories(): Flow<List<Category>> =
        categoryDao
            .getAllCategories()
            .map { list -> list.map { it.toDomain() } }

    override suspend fun getCategoryById(id: Long): Category? =
        categoryDao.getCategoryById(id)?.toDomain()

    override suspend fun insertCategory(category: Category): Long =
        categoryDao.insertCategory(category.toEntity())

    override suspend fun updateCategory(category: Category) =
        categoryDao.updateCategory(category.toEntity())

    override suspend fun deleteCategory(category: Category) =
        categoryDao.deleteCategory(category.toEntity())
}
