package com.aliimran.financialtracker.presentation.settings

import com.aliimran.financialtracker.domain.model.GroupedTransactions
import com.aliimran.financialtracker.domain.model.TransactionType

data class ReportsUiState(
    val startDate: Long = 0L,
    val endDate: Long = 0L,
    val filterType: TransactionType? = null,
    val groupedTransactions: List<GroupedTransactions> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)
