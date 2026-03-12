package com.aliimran.financialtracker.presentation.addtransaction

import android.net.Uri
import com.aliimran.financialtracker.domain.model.Category
import com.aliimran.financialtracker.domain.model.TransactionType
import java.text.NumberFormat
import java.time.LocalDate
import java.util.Locale

/**
 * Immutable snapshot of the entire "Add Transaction" screen state.
 *
 * [amountRaw] stores only raw digit characters (no formatting), which keeps
 * the numpad logic simple. [amountFormatted] and [amountValue] are computed
 * properties derived from [amountRaw] — never stored separately.
 *
 * @param selectedType               Expense / Income / Transfer tab.
 * @param amountRaw                  Raw digit string, e.g. "1500000".
 * @param selectedCategory           The tapped category chip, or null if none selected.
 * @param categories                 List fetched from Room for the current [selectedType].
 * @param isCategoriesLoading        True while the category Flow is first collecting.
 * @param note                       Free-text note from the TextField.
 * @param selectedDate               Date the transaction occurred (defaults to today).
 * @param imageUri                   Optional URI of an attached receipt image.
 * @param isDatePickerVisible        Controls Material 3 DatePickerDialog visibility.
 * @param isImageSourcePickerVisible Controls the camera/gallery chooser bottom-sheet.
 * @param isSaving                   True while the Room insert coroutine is running.
 * @param errorMessage               Non-null after a validation or persistence error.
 */
data class TransactionInputUiState(
    val selectedType               : TransactionType = TransactionType.EXPENSE,
    val amountRaw                  : String          = "",
    val selectedCategory           : Category?       = null,
    val categories                 : List<Category>  = emptyList(),
    val isCategoriesLoading        : Boolean         = false,
    val note                       : String          = "",
    val selectedDate               : LocalDate       = LocalDate.now(),
    val imageUri                   : Uri?            = null,
    val isDatePickerVisible        : Boolean         = false,
    val isImageSourcePickerVisible : Boolean         = false,
    val isSaving                   : Boolean         = false,
    val errorMessage               : String?         = null,
) {
    // ── Derived amount properties ─────────────────────────────

    /**
     * The monetary value as a Double.
     * Returns 0.0 when [amountRaw] is empty or not parseable.
     */
    val amountValue: Double
        get() = amountRaw.toDoubleOrNull() ?: 0.0

    /**
     * Thousand-separated display string using Indonesian locale.
     * e.g. "1500000" → "1.500.000"
     * Empty input displays as "0".
     */
    val amountFormatted: String
        get() {
            if (amountRaw.isEmpty()) return "0"
            return amountRaw.toLongOrNull()?.let { value ->
                NumberFormat.getNumberInstance(Locale("id", "ID"))
                    .apply { maximumFractionDigits = 0 }
                    .format(value)
            } ?: amountRaw
        }

    // ── Validation ────────────────────────────────────────────

    val isAmountValid    : Boolean get() = amountValue > 0
    val isCategoryValid  : Boolean get() = selectedCategory != null

    /**
     * The Save button / numpad confirm key is only enabled when:
     * - Amount > 0
     * - A category is selected
     * - No save operation is currently running
     */
    val canSave: Boolean get() = isAmountValid && isCategoryValid && !isSaving
}
