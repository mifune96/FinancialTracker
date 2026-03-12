package com.aliimran.financialtracker.data.mapper

import com.aliimran.financialtracker.data.local.entity.CategoryEntity
import com.aliimran.financialtracker.domain.model.Category
import com.aliimran.financialtracker.domain.model.TransactionType

/** Bi-directional mapper between [CategoryEntity] (data) and [Category] (domain). */

fun CategoryEntity.toDomain(): Category = Category(
    id          = id,
    name        = name,
    iconResName = iconResName,
    type        = TransactionType.valueOf(type),
    color       = color,
)

fun Category.toEntity(): CategoryEntity = CategoryEntity(
    id          = id,
    name        = name,
    iconResName = iconResName,
    type        = type.name,
    color       = color,
)
