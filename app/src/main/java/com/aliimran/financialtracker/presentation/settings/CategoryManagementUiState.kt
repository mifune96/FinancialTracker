package com.aliimran.financialtracker.presentation.settings

import com.aliimran.financialtracker.domain.model.Category
import com.aliimran.financialtracker.domain.model.TransactionType

/**
 * Immutable state snapshot for the Category Management screen.
 *
 * @param selectedType           Controls which tab is active (EXPENSE / INCOME).
 * @param expenseCategories      Reactive list of EXPENSE categories from Room.
 * @param incomeCategories       Reactive list of INCOME categories from Room.
 * @param isLoading              True during initial data fetch.
 * @param deletionTarget         Non-null when the delete-confirmation dialog is visible.
 * @param deletionTxCount        Number of transactions linked to [deletionTarget].
 *                               Shown in the confirmation message to warn the user.
 * @param formState              Non-null when the Add/Edit dialog is open.
 *                               Null = dialog closed.
 * @param isSaving               True while a DB write operation is in flight.
 * @param errorMessage           Non-null on any repository error.
 * @param successMessage         One-shot success toast (cleared after display).
 */
data class CategoryManagementUiState(
    val selectedType       : TransactionType    = TransactionType.EXPENSE,
    val expenseCategories  : List<Category>     = emptyList(),
    val incomeCategories   : List<Category>     = emptyList(),
    val isLoading          : Boolean            = true,
    val deletionTarget     : Category?          = null,
    val deletionTxCount    : Int                = 0,
    val formState          : CategoryFormState? = null,
    val isSaving           : Boolean            = false,
    val errorMessage       : String?            = null,
    val successMessage     : String?            = null,
) {
    /** The list currently visible based on [selectedType]. */
    val activeCategories: List<Category>
        get() = if (selectedType == TransactionType.EXPENSE) expenseCategories else incomeCategories

    val isDeleteDialogVisible: Boolean get() = deletionTarget != null
    val isFormDialogVisible  : Boolean get() = formState != null
}

/**
 * Mutable form state held while the Add/Edit category dialog is open.
 *
 * @param editingCategory  The original [Category] when editing; null when adding.
 * @param name             Current text-field value.
 * @param iconResName      Selected icon resource name.
 * @param color            Selected ARGB color integer.
 * @param type             Fixed to the current tab's [TransactionType].
 */
data class CategoryFormState(
    val editingCategory : Category?         = null,
    val name            : String            = "",
    val iconResName     : String            = "ic_cat_general",
    val color           : Int               = 0xFF00C853.toInt(),
    val type            : TransactionType   = TransactionType.EXPENSE,
) {
    val isEditMode : Boolean get() = editingCategory != null
    val isNameValid: Boolean get() = name.isNotBlank()
    val hasChanges : Boolean
        get() = editingCategory == null || (
            name        != editingCategory.name        ||
            iconResName != editingCategory.iconResName ||
            color       != editingCategory.color
        )

    /** Builds the [Category] domain object ready for persistence. */
    fun toCategory(): Category = Category(
        id          = editingCategory?.id ?: 0L,
        name        = name.trim(),
        iconResName = iconResName,
        type        = type,
        color       = color,
    )
}

/**
 * Predefined ARGB color palette for the category color picker.
 * Covers Material 3 tonal palette ranges for accessible contrast.
 */
val CATEGORY_COLOR_PALETTE: List<Int> = listOf(
    0xFFE53935.toInt(), // Red
    0xFFE91E63.toInt(), // Pink
    0xFF9C27B0.toInt(), // Purple
    0xFF673AB7.toInt(), // Deep Purple
    0xFF3F51B5.toInt(), // Indigo
    0xFF1E88E5.toInt(), // Blue
    0xFF00ACC1.toInt(), // Cyan
    0xFF00897B.toInt(), // Teal
    0xFF43A047.toInt(), // Green
    0xFF7CB342.toInt(), // Light Green
    0xFFF9A825.toInt(), // Amber
    0xFFFB8C00.toInt(), // Orange
    0xFFF4511E.toInt(), // Deep Orange
    0xFF6D4C41.toInt(), // Brown
    0xFF546E7A.toInt(), // Blue Grey
    0xFF757575.toInt(), // Grey
)
