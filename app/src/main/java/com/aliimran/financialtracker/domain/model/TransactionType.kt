package com.aliimran.financialtracker.domain.model

/**
 * Sealed enum representing every possible transaction classification.
 * TRANSFER handles wallet-to-wallet or account-to-account moves.
 */
enum class TransactionType { EXPENSE, INCOME, TRANSFER }
