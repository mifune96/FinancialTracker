package com.aliimran.financialtracker.presentation.settings

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aliimran.financialtracker.domain.model.GroupedTransactions
import com.aliimran.financialtracker.domain.model.Transaction
import com.aliimran.financialtracker.domain.model.TransactionType
import com.aliimran.financialtracker.domain.usecase.transaction.GetTransactionsByDateRangeUseCase
import com.aliimran.financialtracker.util.DateFormatter.toLocalDate
import com.aliimran.financialtracker.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ReportsViewModel @Inject constructor(
    private val getTransactionsByDateRange: GetTransactionsByDateRangeUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReportsUiState())
    val uiState: StateFlow<ReportsUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    private var observationJob: kotlinx.coroutines.Job? = null

    init {
        // Set default date range to current month
        val now = LocalDate.now()
        val startOfMonth = now.withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val endOfMonth = now.withDayOfMonth(now.lengthOfMonth()).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

        _uiState.update {
            it.copy(startDate = startOfMonth, endDate = endOfMonth)
        }
        observeTransactions()
    }

    private fun observeTransactions() {
        observationJob?.cancel()

        val state = _uiState.value
        // Add 1 day (86400000 ms) to endDate so the query includes the entire end date (since it uses < endMs)
        val endMsInclusive = state.endDate + 86400000L 

        val params = GetTransactionsByDateRangeUseCase.Params(
            type = state.filterType,
            startMs = state.startDate,
            endMs = endMsInclusive
        )

        observationJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            getTransactionsByDateRange(params).collect { resource ->
                _uiState.update { current ->
                    when (resource) {
                        is Resource.Loading -> current.copy(isLoading = true)
                        is Resource.Success -> {
                            current.copy(
                                isLoading = false,
                                groupedTransactions = resource.data?.groupByDate() ?: emptyList()
                            )
                        }
                        is Resource.Error -> current.copy(isLoading = false, errorMessage = resource.message)
                    }
                }
            }
        }
    }

    private fun List<Transaction>.groupByDate(): List<GroupedTransactions> =
        groupBy { it.timestamp.toLocalDate() }
            .entries
            .sortedByDescending { it.key }
            .map { (date, txList) ->
                GroupedTransactions(
                    date = date,
                    transactions = txList.sortedByDescending { it.timestamp },
                )
            }

    fun onStartDateSelected(timeMs: Long) {
        _uiState.update { it.copy(startDate = timeMs) }
        observeTransactions()
    }

    fun onEndDateSelected(timeMs: Long) {
        _uiState.update { it.copy(endDate = timeMs) }
        observeTransactions()
    }

    fun onFilterTypeChanged(type: TransactionType?) {
        _uiState.update { it.copy(filterType = type) }
        observeTransactions()
    }

    fun exportToCsv(context: Context) {
        viewModelScope.launch {
            try {
                val transactions = _uiState.value.groupedTransactions.flatMap { it.transactions }
                if (transactions.isEmpty()) {
                    _uiEvent.emit(UiEvent.ShowSnackbar("Tidak ada data untuk diekspor"))
                    return@launch
                }

                val csv = buildString {
                    appendLine("Tanggal,Kategori,Tipe,Nominal,Catatan")
                    val df = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                    
                    var totalIncome = 0.0
                    var totalExpense = 0.0
                    
                    transactions.forEach { tx ->
                        val date = df.format(Date(tx.timestamp))
                        val cat = tx.categoryName
                        val type = tx.type.name
                        val amt = tx.amount
                        val note = tx.note.replace(",", " ")
                        
                        if (tx.type == TransactionType.INCOME) {
                            totalIncome += tx.amount
                        } else if (tx.type == TransactionType.EXPENSE) {
                            totalExpense += tx.amount
                        }
                        
                        // We format the amount to Rupiah
                        val amtStr = com.aliimran.financialtracker.util.CurrencyFormatter.formatRupiah(amt)
                        
                        // Wrap in quotes in case there are commas or spaces
                        appendLine("$date,$cat,$type,\"$amtStr\",\"$note\"")
                    }
                    
                    appendLine()
                    appendLine("RINGKASAN,,,,")
                    val incStr = com.aliimran.financialtracker.util.CurrencyFormatter.formatRupiah(totalIncome)
                    val expStr = com.aliimran.financialtracker.util.CurrencyFormatter.formatRupiah(totalExpense)
                    val net = totalIncome - totalExpense
                    val netStr = com.aliimran.financialtracker.util.CurrencyFormatter.formatRupiah(net)
                    
                    appendLine("Total Pemasukan,,,\"$incStr\",")
                    appendLine("Total Pengeluaran,,,\"$expStr\",")
                    appendLine("Saldo Bersih,,,\"$netStr\",")
                }

                val fileDate = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                val fileName = "laporan_transaksi_$fileDate.csv"

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                    val resolver = context.contentResolver
                    val contentValues = android.content.ContentValues().apply {
                        put(android.provider.MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                        put(android.provider.MediaStore.MediaColumns.MIME_TYPE, "text/csv")
                        put(android.provider.MediaStore.MediaColumns.RELATIVE_PATH, android.os.Environment.DIRECTORY_DOWNLOADS)
                    }
                    val uri = resolver.insert(android.provider.MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                    if (uri != null) {
                        resolver.openOutputStream(uri)?.use { outputStream ->
                            outputStream.write(csv.toByteArray())
                        }
                        _uiEvent.emit(UiEvent.ShowSnackbar("Berhasil disimpan ke folder Downloads"))
                    } else {
                        throw Exception("Gagal membuat file di folder Downloads")
                    }
                } else {
                    val downloadsDir = android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_DOWNLOADS)
                    if (!downloadsDir.exists()) downloadsDir.mkdirs()
                    val file = File(downloadsDir, fileName)
                    file.writeText(csv)
                    _uiEvent.emit(UiEvent.ShowSnackbar("Berhasil disimpan ke folder Downloads"))
                }
            } catch (e: Exception) {
                _uiEvent.emit(UiEvent.ShowSnackbar("Gagal mengekspor: ${e.localizedMessage}"))
            }
        }
    }

    sealed class UiEvent {
        data class ShowSnackbar(val message: String) : UiEvent()
    }
}
