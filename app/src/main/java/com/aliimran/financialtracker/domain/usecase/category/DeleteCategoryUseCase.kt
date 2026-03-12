package com.aliimran.financialtracker.domain.usecase.category

import com.aliimran.financialtracker.domain.model.Category
import com.aliimran.financialtracker.domain.repository.CategoryRepository
import com.aliimran.financialtracker.domain.usecase.base.SuspendUseCase
import javax.inject.Inject

/** Permanently removes a category from the data source. */
class DeleteCategoryUseCase @Inject constructor(
    private val repository: CategoryRepository,
) : SuspendUseCase<Category, Unit>() {

    override suspend fun execute(params: Category) =
        repository.deleteCategory(params)
}
