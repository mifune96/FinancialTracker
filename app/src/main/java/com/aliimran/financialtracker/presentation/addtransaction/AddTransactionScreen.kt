package com.aliimran.financialtracker.presentation.addtransaction

import android.Manifest
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.aliimran.financialtracker.domain.model.TransactionType
import com.aliimran.financialtracker.presentation.components.AmountDisplay
import com.aliimran.financialtracker.presentation.components.CategoryChip
import com.aliimran.financialtracker.presentation.components.CategoryGrid
import com.aliimran.financialtracker.presentation.components.CustomNumpad
import com.aliimran.financialtracker.presentation.theme.ExpenseRed
import com.aliimran.financialtracker.presentation.theme.IncomeGreen
import com.aliimran.financialtracker.presentation.theme.TransferBlue
import com.aliimran.financialtracker.util.DateFormatter.toEpochMilli
import com.aliimran.financialtracker.util.DateFormatter.toShortDateString
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

/**
 * Add Transaction screen — the core data-entry flow of the app.
 *
 * Screen anatomy (top → bottom):
 * ┌─────────────────────────────────────┐
 * │ ← Tambah Transaksi                  │ TopAppBar
 * ├─────────────────────────────────────┤
 * │ [Pengeluaran] [Pemasukan] [Transfer]│ TransactionTypeTabRow
 * ├─────────────────────────────────────┤
 * │       Rp  1.500.000                 │ AmountDisplay (colored by type)
 * ├──────────────────────────────────── ┤
 * │  📅 Sel, 12 Mar 2024  | 📷 Lampir  │ ─── Scrollable ───
 * │  [ Catatan...                     ] │
 * │  Kategori                           │
 * │  [🍔][🚗][🛒][🎮]                   │ CategoryGrid
 * │  [💊][📚][🏠][✈]                    │
 * ├─────────────────────────────────────┤
 * │  7  │  8  │  9                      │ ─── Fixed ───
 * │  4  │  5  │  6                      │ CustomNumpad
 * │  1  │  2  │  3                      │
 * │ 000 │  0  │  ⌫                      │
 * │  [  ✓  Simpan Transaksi  ]          │
 * └─────────────────────────────────────┘
 *
 * @param onNavigateBack     Pops the back stack (called on save success or back press).
 * @param editTransactionId  If > 0, loads existing transaction for editing.
 * @param viewModel          Hilt-provided [TransactionInputViewModel].
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    onNavigateBack    : () -> Unit,
    editTransactionId : Long = -1L,
    viewModel         : TransactionInputViewModel = hiltViewModel(),
) {
    val uiState           by viewModel.uiState.collectAsState()
    val snackbarHostState  = remember { SnackbarHostState() }
    val focusManager       = LocalFocusManager.current
    val context            = LocalContext.current
    val scope              = rememberCoroutineScope()
    val isEditMode         = editTransactionId > 0L

    // Track note-field focus so we can hide the numpad while typing notes
    var isNoteFocused by remember { mutableStateOf(false) }

    // ── Load existing transaction for edit ─────────────────────
    LaunchedEffect(editTransactionId) {
        if (isEditMode) {
            viewModel.loadForEdit(editTransactionId)
        }
    }

    // ── Collect one-shot events ───────────────────────────────
    LaunchedEffect(Unit) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is TransactionInputViewModel.UiEvent.ShowSnackbar ->
                    snackbarHostState.showSnackbar(event.message)
                TransactionInputViewModel.UiEvent.NavigateBack ->
                    onNavigateBack()
            }
        }
    }

    // ── Camera / Gallery launchers ────────────────────────────

    // Gallery: no permission needed on API 33+ (PickVisualMedia)
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri -> viewModel.onImageSelected(uri) }

    // Camera: requires a temp file and CAMERA permission
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) viewModel.onImageSelected(tempCameraUri)
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            tempCameraUri = createTempImageUri(context)
            tempCameraUri?.let { cameraLauncher.launch(it) }
        }
    }

    // ── Date Picker Dialog ────────────────────────────────────
    if (uiState.isDatePickerVisible) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = uiState.selectedDate.toEpochMilli(),
        )
        DatePickerDialog(
            onDismissRequest = viewModel::onDismissDatePicker,
            confirmButton    = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val selected = Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                            viewModel.onDateSelected(selected)
                        } ?: viewModel.onDismissDatePicker()
                    }
                ) { Text("OK", fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = viewModel::onDismissDatePicker) { Text("Batal") }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // ── Image Source Picker (BottomSheet) ─────────────────────
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    if (uiState.isImageSourcePickerVisible) {
        ModalBottomSheet(
            onDismissRequest = viewModel::onDismissImageSourcePicker,
            sheetState       = sheetState,
        ) {
            ImageSourcePickerSheet(
                onGallery = {
                    scope.launch { sheetState.hide() }
                    viewModel.onDismissImageSourcePicker()
                    galleryLauncher.launch(
                        androidx.activity.result.PickVisualMediaRequest(
                            ActivityResultContracts.PickVisualMedia.ImageOnly
                        )
                    )
                },
                onCamera = {
                    scope.launch { sheetState.hide() }
                    viewModel.onDismissImageSourcePicker()
                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                },
                onRemove = if (uiState.imageUri != null) ({
                    scope.launch { sheetState.hide() }
                    viewModel.onImageSelected(null)
                }) else null,
            )
        }
    }

    // ── Main Scaffold ─────────────────────────────────────────
    Scaffold(
        snackbarHost   = { SnackbarHost(snackbarHostState) },
        topBar         = {
            AddTransactionTopBar(onNavigateBack = onNavigateBack, isEditMode = isEditMode)
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .navigationBarsPadding(),
        ) {
            // ═══ Scrollable top content ═══════════════════════
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
            ) {
                // ── Transaction type tabs ─────────────────────
                TransactionTypeTabRow(
                    selectedType = uiState.selectedType,
                    onTypeSelected = { type ->
                        focusManager.clearFocus()
                        viewModel.onTypeSelected(type)
                    },
                )

                // ── Amount display ────────────────────────────
                AmountDisplay(
                    amountFormatted = uiState.amountFormatted,
                    transactionType = uiState.selectedType,
                    hasImage        = uiState.imageUri != null,
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                // ── Date + Image row ──────────────────────────
                DateImageRow(
                    selectedDate    = uiState.selectedDate,
                    hasImage        = uiState.imageUri != null,
                    imageUri        = uiState.imageUri,
                    onDateClick     = {
                        focusManager.clearFocus()
                        viewModel.onToggleDatePicker()
                    },
                    onImageClick    = {
                        focusManager.clearFocus()
                        viewModel.onToggleImageSourcePicker()
                    },
                )

                // ── Note input ────────────────────────────────
                androidx.compose.material3.OutlinedTextField(
                    value         = uiState.note,
                    onValueChange = viewModel::onNoteChanged,
                    placeholder   = {
                        Text("Catatan (opsional)", style = MaterialTheme.typography.bodySmall)
                    },
                    singleLine    = true,
                    textStyle     = MaterialTheme.typography.bodySmall,
                    modifier      = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 2.dp),
                    colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                        focusedBorderColor   = MaterialTheme.colorScheme.primary,
                    ),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
                )

                // ── Category section ──────────────────────────
                if (uiState.errorMessage != null && !uiState.isCategoryValid) {
                    Text(
                        text     = "⚠ ${uiState.errorMessage}",
                        style    = MaterialTheme.typography.labelSmall,
                        color    = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(horizontal = 16.dp),
                    )
                }

                if (uiState.isCategoriesLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        androidx.compose.material3.CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp,
                        )
                    }
                } else {
                    androidx.compose.foundation.lazy.LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        items(
                            count = uiState.categories.size,
                            key   = { uiState.categories[it].id },
                        ) { index ->
                            val category = uiState.categories[index]
                            CategoryChip(
                                category   = category,
                                isSelected = category.id == uiState.selectedCategory?.id,
                                onClick    = {
                                    focusManager.clearFocus()
                                    viewModel.onCategorySelected(category)
                                },
                            )
                        }
                    }
                }
            }

            // ═══ Fixed bottom: Numpad + Save button ═══════════
            CustomNumpad(
                onKeyPress       = viewModel::onNumpadKey,
                onConfirm        = viewModel::onSave,
                isConfirmEnabled = uiState.canSave,
                isSaving         = uiState.isSaving,
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────
// Top App Bar
// ─────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddTransactionTopBar(onNavigateBack: () -> Unit, isEditMode: Boolean = false) {
    TopAppBar(
        title = {
            Text(
                text       = if (isEditMode) "Edit Transaksi" else "Tambah Transaksi",
                fontWeight = FontWeight.Bold,
                fontSize   = 18.sp,
            )
        },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector        = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Kembali",
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        windowInsets = androidx.compose.foundation.layout.WindowInsets(0.dp),
    )
}

// ─────────────────────────────────────────────────────────────
// Transaction Type Tab Row
// ─────────────────────────────────────────────────────────────

@Composable
private fun TransactionTypeTabRow(
    selectedType   : TransactionType,
    onTypeSelected : (TransactionType) -> Unit,
) {
    val tabs = listOf(
        Triple(TransactionType.EXPENSE,  "Pengeluaran", ExpenseRed),
        Triple(TransactionType.INCOME,   "Pemasukan",   IncomeGreen),
        Triple(TransactionType.TRANSFER, "Transfer",    TransferBlue),
    )
    val selectedIndex = tabs.indexOfFirst { it.first == selectedType }

    TabRow(
        selectedTabIndex = selectedIndex,
        containerColor   = MaterialTheme.colorScheme.surface,
        contentColor     = tabs[selectedIndex].third,
        indicator        = { tabPositions ->
            SecondaryIndicator(
                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedIndex]),
                color    = tabs[selectedIndex].third,
            )
        },
    ) {
        tabs.forEachIndexed { index, (type, label, color) ->
            Tab(
                selected = index == selectedIndex,
                onClick  = { onTypeSelected(type) },
                text     = {
                    Text(
                        text       = label,
                        fontWeight = if (index == selectedIndex) FontWeight.Bold
                                     else FontWeight.Normal,
                        color      = if (index == selectedIndex) color
                                     else MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                },
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────
// Date + Image Row
// ─────────────────────────────────────────────────────────────

@Composable
private fun DateImageRow(
    selectedDate : LocalDate,
    hasImage     : Boolean,
    imageUri     : Uri?,
    onDateClick  : () -> Unit,
    onImageClick : () -> Unit,
    modifier     : Modifier = Modifier,
) {
    Row(
        modifier              = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment     = Alignment.CenterVertically,
    ) {
        // ── Date chip ─────────────────────────────────────────
        Row(
            modifier          = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                .clickable(onClick = onDateClick)
                .padding(horizontal = 12.dp, vertical = 10.dp)
                .weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Icon(
                imageVector        = Icons.Outlined.CalendarMonth,
                contentDescription = null,
                tint               = MaterialTheme.colorScheme.primary,
                modifier           = Modifier.size(18.dp),
            )
            Text(
                text  = selectedDate.toShortDateString(),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
            )
        }

        // ── Image attachment chip ─────────────────────────────
        if (imageUri != null) {
            // Show thumbnail when image is attached
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable(onClick = onImageClick),
            ) {
                AsyncImage(
                    model              = imageUri,
                    contentDescription = "Foto lampiran",
                    contentScale       = ContentScale.Crop,
                    modifier           = Modifier.fillMaxSize(),
                )
                // Small 'x' overlay to hint that tapping opens options
                Box(
                    modifier         = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.5f))
                        .align(Alignment.TopEnd),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector        = Icons.Outlined.Close,
                        contentDescription = null,
                        tint               = Color.White,
                        modifier           = Modifier.size(10.dp),
                    )
                }
            }
        } else {
            Row(
                modifier          = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                    .clickable(onClick = onImageClick)
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Icon(
                    imageVector        = Icons.Outlined.Image,
                    contentDescription = null,
                    tint               = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier           = Modifier.size(18.dp),
                )
                Text(
                    text  = "Lampiran",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
// Note Input Field
// ─────────────────────────────────────────────────────────────

@Composable
private fun NoteInputField(
    value          : String,
    onValueChange  : (String) -> Unit,
    onFocusChanged : (Boolean) -> Unit,
    modifier       : Modifier = Modifier,
) {
    OutlinedTextField(
        value         = value,
        onValueChange = { if (it.length <= 200) onValueChange(it) },
        modifier      = modifier
            .fillMaxWidth()
            .onFocusChanged { onFocusChanged(it.isFocused) },
        placeholder   = {
            Text(
                text  = "Catatan (opsional)…",
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            )
        },
        leadingIcon   = {
            Icon(
                imageVector        = Icons.Outlined.CalendarMonth,
                contentDescription = null,
                tint               = MaterialTheme.colorScheme.primary,
            )
        },
        singleLine    = false,
        maxLines      = 3,
        shape         = RoundedCornerShape(12.dp),
        colors        = OutlinedTextFieldDefaults.colors(
            focusedBorderColor   = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
        ),
        supportingText = {
            Text(
                text  = "${value.length}/200",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        },
    )
}

// ─────────────────────────────────────────────────────────────
// Image Source Picker Bottom Sheet Content
// ─────────────────────────────────────────────────────────────

@Composable
private fun ImageSourcePickerSheet(
    onGallery : () -> Unit,
    onCamera  : () -> Unit,
    onRemove  : (() -> Unit)?,
    modifier  : Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .navigationBarsPadding(),
    ) {
        Text(
            text       = "Lampirkan Foto",
            style      = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier   = Modifier.padding(bottom = 16.dp, top = 8.dp),
        )

        ImageSourceOption(
            icon    = Icons.Outlined.PhotoLibrary,
            label   = "Pilih dari Galeri",
            onClick = onGallery,
        )
        ImageSourceOption(
            icon    = Icons.Outlined.CameraAlt,
            label   = "Ambil Foto",
            onClick = onCamera,
        )
        if (onRemove != null) {
            ImageSourceOption(
                icon    = Icons.Outlined.Close,
                label   = "Hapus Foto",
                onClick = onRemove,
                tint    = MaterialTheme.colorScheme.error,
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun ImageSourceOption(
    icon    : androidx.compose.ui.graphics.vector.ImageVector,
    label   : String,
    onClick : () -> Unit,
    tint    : Color = MaterialTheme.colorScheme.onSurface,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier          = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(24.dp))
        Text(text = label, style = MaterialTheme.typography.bodyLarge, color = tint)
    }
}

// ─────────────────────────────────────────────────────────────
// Helpers
// ─────────────────────────────────────────────────────────────

/**
 * Creates a temporary [Uri] via [FileProvider] for camera output.
 * The file lives in the app's external cache directory.
 */
private fun createTempImageUri(context: android.content.Context): Uri {
    val tempFile = File.createTempFile(
        "receipt_${System.currentTimeMillis()}",
        ".jpg",
        File(context.externalCacheDir, "images").also { it.mkdirs() },
    )
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        tempFile,
    )
}

/**
 * Calculates a sensible fixed height for the [CategoryGrid] based on
 * how many categories are currently loaded.
 * Each row holds 4 chips; each chip row is ~90dp tall.
 */
@Composable
private fun calculateGridHeight(categoryCount: Int): androidx.compose.ui.unit.Dp {
    val rows   = ((categoryCount + 3) / 4).coerceAtLeast(2)   // min 2 rows
    val rowDp  = 90
    return (rows * rowDp).dp
}
