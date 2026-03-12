package com.aliimran.financialtracker.domain.usecase.category

import com.aliimran.financialtracker.domain.model.Category
import com.aliimran.financialtracker.domain.repository.CategoryRepository
import com.aliimran.financialtracker.domain.usecase.base.SuspendUseCase
import javax.inject.Inject

/** Updates an existing category entry. */
class UpdateCategoryUseCase @Inject constructor(
    private val repository: CategoryRepository,
) : SuspendUseCase<Category, Unit>() {

    override suspend fun execute(params: Category) {
        require(params.id > 0)           { "Cannot update a category without a valid id." }
        require(params.name.isNotBlank()) { "Category name must not be blank." }
        repository.updateCategory(params)
    }
}
