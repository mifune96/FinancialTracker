package com.aliimran.financialtracker.presentation.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.aliimran.financialtracker.domain.model.Transaction
import com.aliimran.financialtracker.domain.model.TransactionType
import com.aliimran.financialtracker.presentation.theme.ExpenseRed
import com.aliimran.financialtracker.presentation.theme.IncomeGreen
import com.aliimran.financialtracker.presentation.theme.PrimaryYellow
import com.aliimran.financialtracker.util.Extensions.toComposeColor
import com.aliimran.financialtracker.util.IconMapper
import kotlinx.coroutines.flow.collectLatest
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDetailScreen(
    transactionId  : Long,
    onNavigateBack : () -> Unit,
    onEdit         : (Long) -> Unit,
    viewModel      : TransactionDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val lifecycleOwner = androidx.compose.ui.platform.LocalLifecycleOwner.current

    // Reload transaction from DB every time this screen resumes (e.g. after edit)
    androidx.compose.runtime.DisposableEffect(lifecycleOwner) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                viewModel.refresh()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
    LaunchedEffect(Unit) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is TransactionDetailViewModel.UiEvent.ShowSnackbar -> { /* handled */ }
                is TransactionDetailViewModel.UiEvent.NavigateBack -> onNavigateBack()
            }
        }
    }

    // ── Delete confirmation dialog ────────────────────────
    if (uiState.showDeleteDialog) {
        AlertDialog(
            onDismissRequest = viewModel::onDismissDelete,
            title = { Text("Hapus Transaksi?", fontWeight = FontWeight.Bold) },
            text  = { Text("Transaksi ini akan dihapus secara permanen.") },
            confirmButton = {
                TextButton(onClick = viewModel::onConfirmDelete) {
                    Text("Hapus", color = ExpenseRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::onDismissDelete) { Text("Batal") }
            },
            shape = RoundedCornerShape(20.dp),
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Transaksi", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
                windowInsets = androidx.compose.foundation.layout.WindowInsets(0.dp),
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            if (uiState.transaction != null) {
                TransactionDetailContent(
                    transaction = uiState.transaction!!,
                    onEdit      = { onEdit(transactionId) },
                    onDelete    = viewModel::onDeleteClick,
                )
            }
        }
    }
}

@Composable
private fun TransactionDetailContent(
    transaction: Transaction,
    onEdit     : () -> Unit,
    onDelete   : () -> Unit,
) {
    val isExpense = transaction.type == TransactionType.EXPENSE
    val amountColor = if (isExpense) ExpenseRed else IncomeGreen
    val prefix = if (isExpense) "- " else "+ "
    val formatter = NumberFormat.getNumberInstance(Locale("id", "ID"))
    val dateFormatter = SimpleDateFormat("EEEE, dd MMMM yyyy • HH:mm", Locale("id", "ID"))
    val chipColor = transaction.categoryColor.toComposeColor()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
    ) {
        // ── Scrollable content area ─────────────────
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(top = 16.dp),
        ) {
            // ── Amount Card ──────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = if (isExpense) "Pengeluaran" else "Pemasukan",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${prefix}Rp${formatter.format(transaction.amount)}",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = amountColor,
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Detail Card ──────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(16.dp),
            ) {
                // Category row
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(chipColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = IconMapper[transaction.categoryIconResName],
                            contentDescription = null,
                            tint = chipColor,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Kategori", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(transaction.categoryName, fontWeight = FontWeight.SemiBold)
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

                // Date row
                DetailRow(label = "Tanggal", value = dateFormatter.format(Date(transaction.timestamp)))

                if (transaction.note.isNotBlank()) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                    DetailRow(label = "Catatan", value = transaction.note)
                }
            }

            // ── Image ────────────────────────────────────
            if (transaction.imageUri != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(16.dp),
                ) {
                    Text("Foto Bukti", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(8.dp))
                    AsyncImage(
                        model = transaction.imageUri,
                        contentDescription = "Bukti transaksi",
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp)),
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // ── Fixed bottom action buttons ─────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            OutlinedButton(
                onClick  = onDelete,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape  = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = ExpenseRed,
                ),
            ) {
                Icon(Icons.Outlined.Delete, null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Hapus", fontWeight = FontWeight.SemiBold)
            }

            Button(
                onClick  = onEdit,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape  = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryYellow,
                    contentColor   = Color.White,
                ),
            ) {
                Icon(Icons.Outlined.Edit, null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Edit", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f, fill = false),
        )
    }
}
