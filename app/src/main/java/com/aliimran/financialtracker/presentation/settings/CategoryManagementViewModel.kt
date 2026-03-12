package com.aliimran.financialtracker.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aliimran.financialtracker.domain.model.Category
import com.aliimran.financialtracker.domain.model.TransactionType
import com.aliimran.financialtracker.domain.repository.TransactionRepository
import com.aliimran.financialtracker.domain.usecase.category.AddCategoryUseCase
import com.aliimran.financialtracker.domain.usecase.category.DeleteCategoryUseCase
import com.aliimran.financialtracker.domain.usecase.category.GetCategoriesUseCase
import com.aliimran.financialtracker.domain.usecase.category.UpdateCategoryUseCase
import com.aliimran.financialtracker.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Category Management screen.
 *
 * Responsibilities:
 *  1. Load EXPENSE and INCOME category lists reactively.
 *  2. Drive the Add/Edit dialog lifecycle via [CategoryFormState].
 *  3. Guard category deletion:
 *     a. Prevent deleting the "General" fallback (id = 1).
 *     b. Fetch the linked transaction count and display it in the
 *        confirmation dialog so the user understands the impact.
 *     c. After confirmation, delete — Room's FK (ON DELETE SET DEFAULT)
 *        automatically reassigns orphaned transactions to category_id = 1.
 *  4. Emit one-shot [UiEvent]s for snackbars.
 */
@HiltViewModel
class CategoryManagementViewModel @Inject constructor(
    private val getCategories       : GetCategoriesUseCase,
    private val addCategory         : AddCategoryUseCase,
    private val updateCategory      : UpdateCategoryUseCase,
    private val deleteCategory      : DeleteCategoryUseCase,
    private val transactionRepository: TransactionRepository,
) : ViewModel() {

    companion object {
        /** ID of the immutable "General / Umum" fallback category. Never deletable. */
        const val FALLBACK_CATEGORY_ID = 1L
    }

    private val _uiState = MutableStateFlow(CategoryManagementUiState())
    val uiState: StateFlow<CategoryManagementUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    init {
        observeExpenseCategories()
        observeIncomeCategories()
    }

    // ── Observe both category lists in parallel ───────────────

    private fun observeExpenseCategories() {
        viewModelScope.launch {
            getCategories(TransactionType.EXPENSE).collect { resource ->
                when (resource) {
                    is Resource.Success -> _uiState.update {
                        it.copy(expenseCategories = resource.data, isLoading = false)
                    }
                    is Resource.Error   -> _uiState.update {
                        it.copy(errorMessage = resource.message, isLoading = false)
                    }
                    Resource.Loading    -> Unit
                }
            }
        }
    }

    private fun observeIncomeCategories() {
        viewModelScope.launch {
            getCategories(TransactionType.INCOME).collect { resource ->
                when (resource) {
                    is Resource.Success -> _uiState.update {
                        it.copy(incomeCategories = resource.data, isLoading = false)
                    }
                    is Resource.Error   -> _uiState.update { it.copy(errorMessage = resource.message) }
                    Resource.Loading    -> Unit
                }
            }
        }
    }

    // ── Tab Selection ─────────────────────────────────────────

    fun onTabSelected(type: TransactionType) =
        _uiState.update { it.copy(selectedType = type) }

    // ── Add Dialog ────────────────────────────────────────────

    fun onAddClick() {
        val type = _uiState.value.selectedType
        _uiState.update {
            it.copy(
                formState = CategoryFormState(
                    type        = type,
                    iconResName = "ic_cat_general",
                    color       = CATEGORY_COLOR_PALETTE.first(),
                )
            )
        }
    }

    // ── Edit Dialog ───────────────────────────────────────────

    fun onEditClick(category: Category) {
        _uiState.update {
            it.copy(
                formState = CategoryFormState(
                    editingCategory = category,
                    name            = category.name,
                    iconResName     = category.iconResName,
                    color           = category.color,
                    type            = category.type,
                )
            )
        }
    }

    // ── Form field handlers ───────────────────────────────────

    fun onFormNameChanged(name: String) =
        _uiState.update { it.copy(formState = it.formState?.copy(name = name)) }

    fun onFormIconSelected(resName: String) =
        _uiState.update { it.copy(formState = it.formState?.copy(iconResName = resName)) }

    fun onFormColorSelected(color: Int) =
        _uiState.update { it.copy(formState = it.formState?.copy(color = color)) }

    fun onFormDismiss() =
        _uiState.update { it.copy(formState = null) }

    /**
     * Submits the form — calls either [addCategory] or [updateCategory]
     * depending on whether [CategoryFormState.isEditMode].
     */
    fun onFormConfirm() {
        val form = _uiState.value.formState ?: return
        if (!form.isNameValid) {
            _uiState.update { it.copy(formState = form.copy(name = form.name)) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }

            val category = form.toCategory()
            val result   = if (form.isEditMode) updateCategory(category)
                           else                  addCategory(category)

            when (result) {
                is Resource.Success -> {
                    _uiState.update { it.copy(isSaving = false, formState = null) }
                    val msg = if (form.isEditMode) "Kategori berhasil diperbarui"
                              else "Kategori berhasil ditambahkan"
                    _uiEvent.emit(UiEvent.ShowSnackbar(msg))
                }
                is Resource.Error   -> _uiState.update {
                    it.copy(isSaving = false, errorMessage = result.message)
                }
                Resource.Loading    -> Unit
            }
        }
    }

    // ── Delete Flow ───────────────────────────────────────────

    /**
     * First step of deletion — validates the target and fetches the linked
     * transaction count, then shows the confirmation dialog.
     *
     * Guard: The "General/Umum" category (id = 1) can never be deleted
     * because it is the FK fallback target.
     */
    fun onDeleteRequest(category: Category) {
        if (category.id == FALLBACK_CATEGORY_ID) {
            viewModelScope.launch {
                _uiEvent.emit(UiEvent.ShowSnackbar("Kategori 'Umum' tidak dapat dihapus"))
            }
            return
        }

        viewModelScope.launch {
            // Fetch linked transaction count to show in the warning dialog
            val count = transactionRepository.getTransactionCountByCategory(category.id)
            _uiState.update {
                it.copy(deletionTarget = category, deletionTxCount = count)
            }
        }
    }

    /** Called when the user cancels the delete-confirmation dialog. */
    fun onDeleteCancel() =
        _uiState.update { it.copy(deletionTarget = null, deletionTxCount = 0) }

    /**
     * Final step of deletion — user confirmed the dialog.
     * Room's FK constraint (ON DELETE SET DEFAULT) automatically reassigns
     * all transactions that referenced [deletionTarget] to category_id = 1.
     */
    fun onDeleteConfirm() {
        val target = _uiState.value.deletionTarget ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }

            when (val result = deleteCategory(target)) {
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(isSaving = false, deletionTarget = null, deletionTxCount = 0)
                    }
                    _uiEvent.emit(UiEvent.ShowSnackbar("Kategori '${target.name}' dihapus"))
                }
                is Resource.Error   -> {
                    _uiState.update {
                        it.copy(isSaving = false, deletionTarget = null, deletionTxCount = 0,
                            errorMessage = result.message)
                    }
                }
                Resource.Loading    -> Unit
            }
        }
    }

    fun onClearError() = _uiState.update { it.copy(errorMessage = null) }

    // ── Events ────────────────────────────────────────────────
    sealed class UiEvent {
        data class ShowSnackbar(val message: String) : UiEvent()
    }
}
