package com.aliimran.financialtracker.domain.usecase.category

import com.aliimran.financialtracker.domain.model.Category
import com.aliimran.financialtracker.domain.model.TransactionType
import com.aliimran.financialtracker.domain.repository.CategoryRepository
import com.aliimran.financialtracker.domain.usecase.base.FlowUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/** Reactive list of categories filtered by [TransactionType]. */
class GetCategoriesUseCase @Inject constructor(
    private val repository: CategoryRepository,
) : FlowUseCase<TransactionType, List<Category>>() {

    override fun execute(params: TransactionType): Flow<List<Category>> =
        repository.getCategoriesByType(params)
}
