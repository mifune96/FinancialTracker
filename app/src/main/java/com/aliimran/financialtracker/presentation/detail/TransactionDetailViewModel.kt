package com.aliimran.financialtracker.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aliimran.financialtracker.domain.model.Transaction
import com.aliimran.financialtracker.domain.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TransactionDetailUiState(
    val transaction: Transaction? = null,
    val showDeleteDialog: Boolean = false,
)

@HiltViewModel
class TransactionDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: TransactionRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(TransactionDetailUiState())
    val uiState: StateFlow<TransactionDetailUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()
    private val transactionId = savedStateHandle.get<Long>("transactionId") ?: -1L

    init {
        refresh()
    }

    /** Re-fetches the transaction from the database. Called on init and on resume. */
    fun refresh() {
        if (transactionId <= 0L) return
        viewModelScope.launch {
            val tx = repository.getTransactionById(transactionId)
            _uiState.update { it.copy(transaction = tx) }
        }
    }

    fun onDeleteClick() {
        _uiState.update { it.copy(showDeleteDialog = true) }
    }

    fun onDismissDelete() {
        _uiState.update { it.copy(showDeleteDialog = false) }
    }

    fun onConfirmDelete() {
        val tx = _uiState.value.transaction ?: return
        viewModelScope.launch {
            repository.deleteTransaction(tx)
            _uiEvent.emit(UiEvent.ShowSnackbar("Transaksi berhasil dihapus"))
            _uiEvent.emit(UiEvent.NavigateBack)
        }
    }

    sealed class UiEvent {
        data class ShowSnackbar(val message: String) : UiEvent()
        data object NavigateBack : UiEvent()
    }
}
