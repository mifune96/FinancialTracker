package com.aliimran.financialtracker.presentation.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aliimran.financialtracker.domain.model.Transaction
import com.aliimran.financialtracker.domain.model.TransactionType
import com.aliimran.financialtracker.domain.repository.TransactionRepository
import com.aliimran.financialtracker.notification.ReminderScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    @ApplicationContext private val appContext: Context,
) : ViewModel() {

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    private val prefs = appContext.getSharedPreferences(
        ReminderScheduler.PREFS_NAME,
        Context.MODE_PRIVATE,
    )

    /** Observable state backed by SharedPreferences. */
    var isReminderEnabled by mutableStateOf(
        prefs.getBoolean(ReminderScheduler.KEY_REMINDER_ENABLED, false)
    )
        private set

    /** Toggle the reminder — persists in SharedPreferences and schedules/cancels the alarm. */
    fun toggleReminder(enabled: Boolean) {
        isReminderEnabled = enabled
        prefs.edit()
            .putBoolean(ReminderScheduler.KEY_REMINDER_ENABLED, enabled)
            .apply()
        if (enabled) {
            ReminderScheduler.schedule(appContext)
        } else {
            ReminderScheduler.cancel(appContext)
        }
    }

    fun deleteAllTransactions() {
        viewModelScope.launch {
            transactionRepository.deleteAllTransactions()
            _uiEvent.emit(UiEvent.ShowToast("Semua data berhasil dihapus"))
        }
    }

    fun exportToCsv(context: Context) {
        viewModelScope.launch {
            try {
                val transactions = transactionRepository.getAllTransactions().first()
                if (transactions.isEmpty()) {
                    _uiEvent.emit(UiEvent.ShowToast("Tidak ada transaksi untuk diekspor"))
                    return@launch
                }

                val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("id", "ID"))
                val numFormat = NumberFormat.getNumberInstance(Locale("id", "ID"))

                val csv = buildString {
                    appendLine("Tanggal,Tipe,Kategori,Nominal,Catatan")
                    for (tx in transactions) {
                        val date = dateFormat.format(Date(tx.timestamp))
                        val type = if (tx.type == TransactionType.EXPENSE) "Pengeluaran" else "Pemasukan"
                        val amount = numFormat.format(tx.amount)
                        val note = tx.note.replace(",", ";").replace("\n", " ")
                        appendLine("$date,$type,${tx.categoryName},$amount,$note")
                    }
                }

                val fileDate = SimpleDateFormat("yyyyMMdd_HHmm", Locale.ROOT).format(Date())
                val file = File(context.cacheDir, "transaksi_$fileDate.csv")
                file.writeText(csv)

                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    file,
                )

                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    this.type = "text/csv"
                    putExtra(Intent.EXTRA_STREAM, uri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                context.startActivity(Intent.createChooser(shareIntent, "Ekspor Data"))

            } catch (e: Exception) {
                _uiEvent.emit(UiEvent.ShowToast("Gagal mengekspor: ${e.message}"))
            }
        }
    }

    sealed class UiEvent {
        data class ShowToast(val message: String) : UiEvent()
    }
}
