package com.aliimran.financialtracker.presentation.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aliimran.financialtracker.domain.model.Category
import com.aliimran.financialtracker.domain.model.TransactionType
import com.aliimran.financialtracker.presentation.components.CategoryFormDialog
import com.aliimran.financialtracker.presentation.theme.ExpenseRed
import com.aliimran.financialtracker.presentation.theme.IncomeGreen
import com.aliimran.financialtracker.presentation.theme.PrimaryYellow
import com.aliimran.financialtracker.util.Extensions.toComposeColor
import com.aliimran.financialtracker.util.IconMapper
import kotlinx.coroutines.flow.collectLatest

/**
 * Category Management screen — allows the user to Add, Edit, and Delete
 * custom Expense/Income categories.
 *
 * Screen anatomy:
 * ┌─────────────────────────────────────────────┐
 * │ ← Pengaturan Kategori                       │ TopAppBar
 * ├─────────────────────────────────────────────┤
 * │ [  Pengeluaran  ]  [   Pemasukan   ]        │ Type TabRow
 * ├─────────────────────────────────────────────┤
 * │  ● Makanan & Minum       🖊  🗑           │
 * │  ● Transport             🖊  🗑           │ Category list
 * │  ● Belanja               🖊  🗑           │ (swipeable in future)
 * │  …                                          │
 * ├─────────────────────────────────────────────┤
 * │          [+  Tambah Kategori]               │ FAB
 * └─────────────────────────────────────────────┘
 *
 * Dialogs:
 *  • [CategoryFormDialog] — Add/Edit (shown when [formState] != null)
 *  • Delete confirmation [AlertDialog] (shown when [deletionTarget] != null)
 *
 * Deletion safety:
 *  • The "General/Umum" category (id=1) is protected — delete button is hidden.
 *  • For all other categories, the confirmation dialog shows how many
 *    transactions will be reassigned to "Umum" via FK ON DELETE SET DEFAULT.
 *
 * @param onNavigateBack   Pops this screen off the back stack.
 * @param viewModel        Hilt-provided [CategoryManagementViewModel].
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryManagementScreen(
    onNavigateBack : () -> Unit,
    viewModel      : CategoryManagementViewModel = hiltViewModel(),
) {
    val uiState           by viewModel.uiState.collectAsState()
    val snackbarHostState  = remember { SnackbarHostState() }

    // ── One-shot events ───────────────────────────────────────
    LaunchedEffect(Unit) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is CategoryManagementViewModel.UiEvent.ShowSnackbar ->
                    snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    // ── Add/Edit dialog ───────────────────────────────────────
    uiState.formState?.let { form ->
        CategoryFormDialog(
            formState       = form,
            onNameChange    = viewModel::onFormNameChanged,
            onIconSelected  = viewModel::onFormIconSelected,
            onColorSelected = viewModel::onFormColorSelected,
            onConfirm       = viewModel::onFormConfirm,
            onDismiss       = viewModel::onFormDismiss,
            isSaving        = uiState.isSaving,
        )
    }

    // ── Delete confirmation dialog ────────────────────────────
    uiState.deletionTarget?.let { target ->
        DeleteConfirmationDialog(
            categoryName = target.name,
            txCount      = uiState.deletionTxCount,
            onConfirm    = viewModel::onDeleteConfirm,
            onDismiss    = viewModel::onDeleteCancel,
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text       = "Pengaturan Kategori",
                        fontWeight = FontWeight.Bold,
                        fontSize   = 18.sp,
                    )
                },
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
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text("Tambah Kategori", fontWeight = FontWeight.SemiBold) },
                icon = { Icon(Icons.Outlined.Add, null) },
                onClick            = viewModel::onAddClick,
                modifier           = Modifier.navigationBarsPadding(),
                containerColor     = PrimaryYellow,
                contentColor       = Color.White,
                elevation          = FloatingActionButtonDefaults.elevation(6.dp),
            )
        },
        floatingActionButtonPosition = FabPosition.Center,
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->

        Column(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        ) {

            // ── Type tabs ─────────────────────────────────────
            CategoryTypeTabRow(
                selectedType   = uiState.selectedType,
                onTabSelected  = viewModel::onTabSelected,
            )

            // ── Category list ─────────────────────────────────
            when {
                uiState.isLoading -> LoadingState()
                uiState.activeCategories.isEmpty() -> EmptyCategoryState(
                    type    = uiState.selectedType,
                    onClick = viewModel::onAddClick,
                )
                else -> {
                    AnimatedVisibility(
                        visible = true,
                        enter   = fadeIn(tween(200)) + slideInHorizontally(),
                        exit    = fadeOut() + slideOutHorizontally(),
                    ) {
                        LazyColumn(
                            contentPadding = PaddingValues(
                                top    = 8.dp,
                                bottom = 100.dp,   // FAB clearance
                            ),
                        ) {
                            itemsIndexed(
                                items = uiState.activeCategories,
                                key   = { _, c -> c.id },
                            ) { index, category ->
                                CategoryManagementRow(
                                    category    = category,
                                    isProtected = category.id == CategoryManagementViewModel.FALLBACK_CATEGORY_ID,
                                    onEdit      = { viewModel.onEditClick(category) },
                                    onDelete    = { viewModel.onDeleteRequest(category) },
                                )
                                if (index < uiState.activeCategories.lastIndex) {
                                    HorizontalDivider(
                                        modifier  = Modifier.padding(start = 72.dp, end = 16.dp),
                                        color     = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                                        thickness = 0.5.dp,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
// Tab Row
// ─────────────────────────────────────────────────────────────

@Composable
private fun CategoryTypeTabRow(
    selectedType  : TransactionType,
    onTabSelected : (TransactionType) -> Unit,
) {
    val tabs  = listOf(TransactionType.EXPENSE to "Pengeluaran", TransactionType.INCOME to "Pemasukan")
    val idx   = tabs.indexOfFirst { it.first == selectedType }
    val color = if (selectedType == TransactionType.EXPENSE) ExpenseRed else IncomeGreen

    TabRow(
        selectedTabIndex = idx,
        containerColor   = MaterialTheme.colorScheme.surface,
        indicator        = { positions ->
            SecondaryIndicator(
                modifier = Modifier.tabIndicatorOffset(positions[idx]),
                color    = color,
            )
        },
    ) {
        tabs.forEachIndexed { index, (type, label) ->
            Tab(
                selected = index == idx,
                onClick  = { onTabSelected(type) },
                text     = {
                    Text(
                        label,
                        fontWeight = if (index == idx) FontWeight.Bold else FontWeight.Normal,
                        color      = if (index == idx) color
                                     else MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                },
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────
// Category Row
// ─────────────────────────────────────────────────────────────

@Composable
private fun CategoryManagementRow(
    category    : Category,
    isProtected : Boolean,
    onEdit      : () -> Unit,
    onDelete    : () -> Unit,
    modifier    : Modifier = Modifier,
) {
    val chipColor = category.color.toComposeColor()

    Row(
        modifier          = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // ── Icon chip ─────────────────────────────────────────
        Box(
            modifier         = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(chipColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector        = IconMapper[category.iconResName],
                contentDescription = category.name,
                tint               = chipColor,
                modifier           = Modifier.size(22.dp),
            )
        }

        Spacer(Modifier.width(14.dp))

        // ── Name + protected badge ────────────────────────────
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text       = category.name,
                style      = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
            )
            if (isProtected) {
                Text(
                    text  = "Kategori bawaan",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        // ── Action buttons ────────────────────────────────────
        IconButton(onClick = onEdit) {
            Icon(
                imageVector        = Icons.Outlined.Edit,
                contentDescription = "Edit",
                tint               = MaterialTheme.colorScheme.primary,
                modifier           = Modifier.size(20.dp),
            )
        }

        // Hide delete for the protected "General" category
        if (!isProtected) {
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector        = Icons.Outlined.Delete,
                    contentDescription = "Hapus",
                    tint               = MaterialTheme.colorScheme.error,
                    modifier           = Modifier.size(20.dp),
                )
            }
        } else {
            Spacer(Modifier.size(48.dp))
        }
    }
}

// ─────────────────────────────────────────────────────────────
// Delete Confirmation Dialog
// ─────────────────────────────────────────────────────────────

@Composable
private fun DeleteConfirmationDialog(
    categoryName : String,
    txCount      : Int,
    onConfirm    : () -> Unit,
    onDismiss    : () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Hapus Kategori?", fontWeight = FontWeight.Bold)
        },
        text = {
            Column {
                Text(
                    text  = "Kategori \"$categoryName\" akan dihapus secara permanen.",
                    style = MaterialTheme.typography.bodyMedium,
                )
                if (txCount > 0) {
                    Spacer(Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f))
                            .padding(12.dp),
                    ) {
                        Text(
                            text  = "⚠ $txCount transaksi terkait akan dipindahkan ke " +
                                    "kategori \"Umum\" secara otomatis.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Hapus", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        },
        shape = RoundedCornerShape(20.dp),
    )
}

// ─────────────────────────────────────────────────────────────
// Empty State
// ─────────────────────────────────────────────────────────────

@Composable
private fun EmptyCategoryState(
    type    : TransactionType,
    onClick : () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier            = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text("🗂", fontSize = 48.sp)
        Spacer(Modifier.height(12.dp))
        Text(
            "Belum ada kategori ${if (type == TransactionType.EXPENSE) "pengeluaran" else "pemasukan"}",
            style      = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            textAlign  = TextAlign.Center,
        )
        Spacer(Modifier.height(8.dp))
        TextButton(onClick = onClick) {
            Text("+ Tambah sekarang", color = PrimaryYellow, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun LoadingState(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        androidx.compose.material3.CircularProgressIndicator()
    }
}
