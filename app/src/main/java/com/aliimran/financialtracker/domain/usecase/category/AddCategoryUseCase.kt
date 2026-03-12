package com.aliimran.financialtracker.domain.usecase.category

import com.aliimran.financialtracker.domain.model.Category
import com.aliimran.financialtracker.domain.repository.CategoryRepository
import com.aliimran.financialtracker.domain.usecase.base.SuspendUseCase
import javax.inject.Inject

/** Validates and persists a new category. */
class AddCategoryUseCase @Inject constructor(
    private val repository: CategoryRepository,
) : SuspendUseCase<Category, Long>() {

    override suspend fun execute(params: Category): Long {
        require(params.name.isNotBlank()) { "Category name must not be blank." }
        return repository.insertCategory(params)
    }
}
