package com.aliimran.financialtracker.presentation.settings

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aliimran.financialtracker.domain.model.TransactionType
import com.aliimran.financialtracker.presentation.components.EmptyTransactionsPlaceholder
import com.aliimran.financialtracker.presentation.components.GroupedTransactionList
import kotlinx.coroutines.flow.collectLatest
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    viewModel: ReportsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    
    // For date pickers
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is ReportsViewModel.UiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }

    if (showStartDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = uiState.startDate)
        DatePickerDialog(
            onDismissRequest = { showStartDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { viewModel.onStartDateSelected(it) }
                    showStartDatePicker = false
                }) { Text("Pilih") }
            },
            dismissButton = {
                TextButton(onClick = { showStartDatePicker = false }) { Text("Batal") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showEndDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = uiState.endDate)
        DatePickerDialog(
            onDismissRequest = { showEndDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { viewModel.onEndDateSelected(it) }
                    showEndDatePicker = false
                }) { Text("Pilih") }
            },
            dismissButton = {
                TextButton(onClick = { showEndDatePicker = false }) { Text("Batal") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Laporan", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { viewModel.exportToCsv(context) }) {
                        Icon(Icons.Default.FileDownload, contentDescription = "Ekspor CSV")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Date Range Selection
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val df = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                
                OutlinedButton(onClick = { showStartDatePicker = true }, modifier = Modifier.weight(1f)) {
                    Text(df.format(Date(uiState.startDate)))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text("-")
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedButton(onClick = { showEndDatePicker = true }, modifier = Modifier.weight(1f)) {
                    Text(df.format(Date(uiState.endDate)))
                }
            }

            // Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = uiState.filterType == null,
                    onClick = { viewModel.onFilterTypeChanged(null) },
                    label = { Text("Semua") }
                )
                FilterChip(
                    selected = uiState.filterType == TransactionType.INCOME,
                    onClick = { viewModel.onFilterTypeChanged(TransactionType.INCOME) },
                    label = { Text("Pemasukan") }
                )
                FilterChip(
                    selected = uiState.filterType == TransactionType.EXPENSE,
                    onClick = { viewModel.onFilterTypeChanged(TransactionType.EXPENSE) },
                    label = { Text("Pengeluaran") }
                )
            }

            HorizontalDivider()

            // List
            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    uiState.isLoading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                    uiState.errorMessage != null -> {
                        Text(
                            text = uiState.errorMessage!!,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.align(Alignment.Center).padding(32.dp)
                        )
                    }
                    uiState.groupedTransactions.isEmpty() -> {
                        EmptyTransactionsPlaceholder(modifier = Modifier.align(Alignment.Center))
                    }
                    else -> {
                        GroupedTransactionList(
                            groups = uiState.groupedTransactions,
                            onTransactionClick = { /* Maybe show detail or nothing */ }
                        )
                    }
                }
            }
        }
    }
}
