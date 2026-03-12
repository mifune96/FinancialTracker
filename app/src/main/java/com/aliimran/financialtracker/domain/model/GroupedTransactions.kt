package com.aliimran.financialtracker.domain.model

import java.time.LocalDate

/**
 * A single day's worth of transactions — drives the grouped list
 * in the History (Riwayat) screen.
 *
 * @param date         Calendar date shared by every item in the group.
 * @param transactions Ordered list of transactions (newest-first).
 * @param dailyTotal   Net signed total: income positive, expense negative.
 */
data class GroupedTransactions(
    val date: LocalDate,
    val transactions: List<Transaction>,
    val dailyTotal: Double = transactions.fold(0.0) { acc, tx ->
        when (tx.type) {
            TransactionType.INCOME   -> acc + tx.amount
            TransactionType.EXPENSE  -> acc - tx.amount
            TransactionType.TRANSFER -> acc
        }
    },
)
