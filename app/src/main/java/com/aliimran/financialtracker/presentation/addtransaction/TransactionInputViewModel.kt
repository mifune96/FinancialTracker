package com.aliimran.financialtracker.presentation.addtransaction

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aliimran.financialtracker.domain.model.Transaction
import com.aliimran.financialtracker.domain.model.TransactionType
import com.aliimran.financialtracker.domain.repository.TransactionRepository
import com.aliimran.financialtracker.domain.usecase.transaction.AddTransactionUseCase
import com.aliimran.financialtracker.domain.usecase.category.GetCategoriesUseCase
import com.aliimran.financialtracker.domain.model.Category
import com.aliimran.financialtracker.util.DateFormatter.toEpochMilli
import com.aliimran.financialtracker.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject

/**
 * ViewModel for the Add Transaction screen.
 *
 * Responsibilities:
 *  1. Load categories reactively from Room whenever [TransactionType] changes.
 *  2. Process [NumpadKey] events and build/mutate the [amountRaw] string.
 *  3. Validate inputs and persist a new [Transaction] via [AddTransactionUseCase].
 *  4. Emit one-shot [UiEvent]s for navigation and snackbars.
 *
 * The ViewModel holds NO Android framework types except [Uri] (needed for
 * image attachment).  All Room/Hilt dependencies are injected interfaces.
 */
@HiltViewModel
class TransactionInputViewModel @Inject constructor(
    private val addTransactionUseCase : AddTransactionUseCase,
    private val getCategoriesUseCase  : GetCategoriesUseCase,
    private val transactionRepository: TransactionRepository,
    @dagger.hilt.android.qualifiers.ApplicationContext
    private val appContext            : Context,
) : ViewModel() {

    /** Non-null when editing an existing transaction. */
    private var editTransactionId: Long? = null

    private val _uiState = MutableStateFlow(TransactionInputUiState())
    val uiState: StateFlow<TransactionInputUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    /** Tracks the active category-loading job so it can be cancelled on type switch. */
    private var categoryLoadingJob: Job? = null

    init {
        // Load EXPENSE categories on launch (default selected type).
        loadCategoriesForType(TransactionType.EXPENSE)
    }

    // ── Edit Mode ─────────────────────────────────────────────

    /**
     * Called from AddTransactionScreen when editing. Loads the
     * existing transaction and populates the UI state fields.
     */
    fun loadForEdit(transactionId: Long) {
        if (editTransactionId != null) return   // already loaded
        editTransactionId = transactionId
        viewModelScope.launch {
            val tx = transactionRepository.getTransactionById(transactionId) ?: return@launch
            val date = java.time.Instant.ofEpochMilli(tx.timestamp)
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDate()

            // Switch type + load matching categories
            _uiState.update {
                it.copy(
                    selectedType = tx.type,
                    amountRaw    = tx.amount.toLong().toString(),
                    note         = tx.note,
                    selectedDate = date,
                    imageUri     = tx.imageUri?.let { uri -> Uri.parse(uri) },
                )
            }
            loadCategoriesForType(tx.type)

            // Wait a bit for categories to load, then select the right one
            kotlinx.coroutines.delay(300)
            val cats = _uiState.value.categories
            val match = cats.find { it.id == tx.categoryId }
            if (match != null) {
                _uiState.update { it.copy(selectedCategory = match) }
            }
        }
    }

    // ── Type Selection ────────────────────────────────────────

    /**
     * Called when the user taps a tab in the TransactionTypeTabRow.
     * Resets the selected category because EXPENSE categories are not
     * valid for INCOME selections (and vice versa).
     */
    fun onTypeSelected(type: TransactionType) {
        _uiState.update {
            it.copy(
                selectedType         = type,
                selectedCategory     = null,
                isCategoriesLoading  = true,
            )
        }
        loadCategoriesForType(type)
    }

    private fun loadCategoriesForType(type: TransactionType) {
        categoryLoadingJob?.cancel()
        categoryLoadingJob = viewModelScope.launch {
            getCategoriesUseCase(type).collect { resource ->
                when (resource) {
                    is Resource.Success -> _uiState.update {
                        it.copy(
                            categories          = resource.data,
                            isCategoriesLoading = false,
                        )
                    }
                    is Resource.Error   -> _uiState.update {
                        it.copy(
                            isCategoriesLoading = false,
                            errorMessage        = resource.message,
                        )
                    }
                    Resource.Loading    -> Unit
                }
            }
        }
    }

    // ── Numpad Input ──────────────────────────────────────────

    /**
     * Processes a single [NumpadKey] press and updates [amountRaw].
     *
     * Rules:
     *  - Max 15 raw digits (prevents overflow beyond 999,999,999,999,999 IDR).
     *  - Leading zeros are stripped: pressing 0 on empty input stays "0".
     *  - [NumpadKey.TripleZero] appends "000" if within the digit limit.
     *  - [NumpadKey.Backspace] drops the last character; empty result → "".
     */
    fun onNumpadKey(key: NumpadKey) {
        val current = _uiState.value.amountRaw
        val maxDigits = 15

        val updated = when (key) {
            is NumpadKey.Digit -> {
                when {
                    // Ignore leading zeros
                    current.isEmpty() && key.value == 0 -> current
                    current.length >= maxDigits          -> current
                    else                                 -> current + key.value.toString()
                }
            }
            NumpadKey.TripleZero -> {
                when {
                    current.isEmpty()              -> current  // Don't allow "000" as first input
                    current.length + 3 > maxDigits -> current  // Would exceed max
                    else                           -> current + "000"
                }
            }
            NumpadKey.Backspace -> {
                if (current.isEmpty()) current else current.dropLast(1)
            }
        }

        _uiState.update { it.copy(amountRaw = updated) }
    }

    // ── Category, Note, Date, Image ───────────────────────────

    fun onCategorySelected(category: Category) =
        _uiState.update { it.copy(selectedCategory = category, errorMessage = null) }

    fun onNoteChanged(note: String) =
        _uiState.update { it.copy(note = note) }

    fun onDateSelected(date: LocalDate) =
        _uiState.update { it.copy(selectedDate = date, isDatePickerVisible = false) }

    fun onImageSelected(uri: Uri?) =
        _uiState.update { it.copy(imageUri = uri, isImageSourcePickerVisible = false) }

    fun onToggleDatePicker() =
        _uiState.update { it.copy(isDatePickerVisible = !it.isDatePickerVisible) }

    fun onDismissDatePicker() =
        _uiState.update { it.copy(isDatePickerVisible = false) }

    fun onToggleImageSourcePicker() =
        _uiState.update { it.copy(isImageSourcePickerVisible = !it.isImageSourcePickerVisible) }

    fun onDismissImageSourcePicker() =
        _uiState.update { it.copy(isImageSourcePickerVisible = false) }

    fun onClearError() =
        _uiState.update { it.copy(errorMessage = null) }

    // ── Save Transaction ──────────────────────────────────────

    /**
     * Validates all inputs, constructs a domain [Transaction], and
     * delegates persistence to [AddTransactionUseCase].
     *
     * On success  → emits [UiEvent.ShowSnackbar] + [UiEvent.NavigateBack].
     * On failure  → sets [errorMessage] in state so the UI shows a Snackbar.
     *
     * The function returns early (no-op) if [canSave] is false, protecting
     * against double-tap races on the Save button.
     */
    fun onSave() {
        val state = _uiState.value
        if (!state.canSave) {
            // Surface validation errors to the user
            val msg = when {
                !state.isAmountValid   -> "Nominal harus lebih dari nol"
                !state.isCategoryValid -> "Pilih kategori terlebih dahulu"
                else                   -> return
            }
            _uiState.update { it.copy(errorMessage = msg) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }

            val persistedImageUri = copyImageToInternalStorage(state.imageUri)

            val transaction = Transaction(
                id         = editTransactionId ?: 0L,
                type       = state.selectedType,
                amount     = state.amountValue,
                categoryId = state.selectedCategory!!.id,
                timestamp  = state.selectedDate.toEpochMilli(),
                note       = state.note.trim(),
                imageUri   = persistedImageUri,
            )

            try {
                if (editTransactionId != null) {
                    transactionRepository.updateTransaction(transaction)
                    _uiEvent.emit(UiEvent.ShowSnackbar("Transaksi berhasil diperbarui ✓"))
                    _uiEvent.emit(UiEvent.NavigateBack)
                } else {
                    when (val result = addTransactionUseCase(transaction)) {
                        is Resource.Success -> {
                            _uiEvent.emit(UiEvent.ShowSnackbar("Transaksi berhasil disimpan ✓"))
                            _uiEvent.emit(UiEvent.NavigateBack)
                        }
                        is Resource.Error   -> _uiState.update {
                            it.copy(isSaving = false, errorMessage = result.message)
                        }
                        Resource.Loading    -> Unit
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isSaving = false, errorMessage = e.message) }
            }
        }
    }
    // ── Image persistence helper ──────────────────────────────

    /**
     * Copies a temporary content:// URI to internal storage so the image
     * survives app restarts. Returns the permanent file:// URI string,
     * or null if no image.
     */
    private fun copyImageToInternalStorage(uri: Uri?): String? {
        if (uri == null) return null
        // If already a file:// URI (e.g. editing existing transaction), keep it
        if (uri.scheme == "file") return uri.toString()

        return try {
            val imagesDir = File(appContext.filesDir, "transaction_images")
            if (!imagesDir.exists()) imagesDir.mkdirs()
            val destFile = File(imagesDir, "${UUID.randomUUID()}.jpg")
            appContext.contentResolver.openInputStream(uri)?.use { input ->
                destFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            destFile.toUri().toString()
        } catch (e: Exception) {
            // Fallback: just store the original URI
            uri.toString()
        }
    }

    // ── One-shot UI Events ────────────────────────────────────

    sealed class UiEvent {
        data class ShowSnackbar(val message: String) : UiEvent()
        data object NavigateBack : UiEvent()
    }
}
