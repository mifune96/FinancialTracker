package com.aliimran.financialtracker.domain.usecase.category

import com.aliimran.financialtracker.domain.model.Category
import com.aliimran.financialtracker.domain.repository.CategoryRepository
import com.aliimran.financialtracker.domain.usecase.base.NoParamFlowUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/** Reactive list of all categories regardless of type. */
class GetAllCategoriesUseCase @Inject constructor(
    private val repository: CategoryRepository,
) : NoParamFlowUseCase<List<Category>>() {

    override fun execute(): Flow<List<Category>> =
        repository.getAllCategories()
}
