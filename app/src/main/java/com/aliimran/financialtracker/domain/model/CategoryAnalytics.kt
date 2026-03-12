package com.aliimran.financialtracker.domain.model

/**
 * Analytics breakdown for a single category within a time period.
 *
 * @param category         The associated [Category] domain object.
 * @param totalAmount      Sum of all transaction amounts in this category.
 * @param percentage       Share of [totalAmount] relative to the period grand total (0–100f).
 * @param transactionCount Number of individual transactions in this category.
 * @param sweepAngle       Pre-calculated arc angle for the Donut chart (0f–360f).
 */
data class CategoryAnalytics(
    val category         : Category,
    val totalAmount      : Double,
    val percentage       : Float,
    val transactionCount : Int,
    val sweepAngle       : Float = percentage * 3.6f,  // percentage → degrees
)
