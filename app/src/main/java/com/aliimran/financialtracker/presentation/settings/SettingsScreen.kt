package com.aliimran.financialtracker.presentation.settings

import android.widget.Toast

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.MonetizationOn
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aliimran.financialtracker.presentation.theme.PrimaryYellow
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.flow.collectLatest

/**
 * Settings screen — accessed via "Pengaturan" on the Profile (Saya) tab.
 *
 * Contains grouped preference items:
 *  • Pengaturan Umum  → Category, Currency, Language, Theme
 *  • Notifikasi       → Daily reminder toggle
 *  • Data             → Export CSV, Delete all data
 *  • Tentang          → Rate, Help, Version
 *
 * @param onNavigateBack             Pops this screen off the back stack.
 * @param onNavigateToCategoryManagement  Pushes the Category Management screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack                  : () -> Unit,
    onNavigateToCategoryManagement  : () -> Unit,
    onNavigateToFaq                 : () -> Unit,
    viewModel                       : SettingsViewModel = hiltViewModel(),
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Permission launcher for POST_NOTIFICATIONS (Android 13+)
    val notificationPermissionLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.RequestPermission(),
    ) { isGranted ->
        if (isGranted) {
            viewModel.toggleReminder(true)
            Toast.makeText(context, "Pengingat harian diaktifkan", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Izin notifikasi ditolak", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is SettingsViewModel.UiEvent.ShowToast ->
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title  = { Text("Hapus Semua Data?", fontWeight = FontWeight.Bold) },
            text   = {
                Text(
                    "Semua transaksi dan kategori kustom akan dihapus secara permanen. " +
                    "Tindakan ini tidak dapat dibatalkan.",
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    viewModel.deleteAllTransactions()
                }) {
                    Text("Hapus", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Batal") }
            },
            shape = RoundedCornerShape(20.dp),
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pengaturan", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
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

        LazyColumn(
            modifier       = Modifier.fillMaxSize().padding(innerPadding),
            contentPadding = PaddingValues(bottom = 32.dp),
        ) {

            // ── Pengaturan Umum ───────────────────────────────
            item { SectionHeader("Pengaturan Umum") }
            item {
                SettingsCard {
                    NavigationRow(
                        icon     = Icons.Outlined.Category,
                        tint     = PrimaryYellow,
                        label    = "Pengaturan Kategori",
                        subtitle = "Kelola kategori pengeluaran & pemasukan",
                        onClick  = onNavigateToCategoryManagement,
                    )
                    CardDivider()
                    NavigationRow(
                        icon     = Icons.Outlined.MonetizationOn,
                        tint     = Color(0xFF00ACC1),
                        label    = "Mata Uang",
                        subtitle = "Rupiah Indonesia (IDR)",
                        onClick  = { Toast.makeText(context, "Segera hadir", Toast.LENGTH_SHORT).show() },
                    )
                    CardDivider()
                    NavigationRow(
                        icon     = Icons.Outlined.Language,
                        tint     = Color(0xFF3949AB),
                        label    = "Bahasa",
                        subtitle = "Bahasa Indonesia",
                        onClick  = { Toast.makeText(context, "Segera hadir", Toast.LENGTH_SHORT).show() },
                    )
                    CardDivider()
                    NavigationRow(
                        icon     = Icons.Outlined.Palette,
                        tint     = Color(0xFF8E24AA),
                        label    = "Tampilan",
                        subtitle = "Tema Terang",
                        onClick  = { Toast.makeText(context, "Segera hadir", Toast.LENGTH_SHORT).show() },
                    )
                }
            }

            // ── Notifikasi ────────────────────────────────────
            item { SectionHeader("Notifikasi") }
            item {
                SettingsCard {
                    SwitchRow(
                        icon    = Icons.Outlined.Notifications,
                        tint    = Color(0xFFF4511E),
                        label   = "Pengingat Harian",
                        subtitle = "Ingatkan saya untuk mencatat pengeluaran",
                        checked = viewModel.isReminderEnabled,
                        onCheckedChange = { enabled ->
                            if (enabled) {
                                // Check notification permission (Android 13+)
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                                    val hasNotifPermission = androidx.core.content.ContextCompat.checkSelfPermission(
                                        context,
                                        android.Manifest.permission.POST_NOTIFICATIONS,
                                    ) == android.content.pm.PackageManager.PERMISSION_GRANTED
                                    if (!hasNotifPermission) {
                                        notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                                        return@SwitchRow
                                    }
                                }
                                viewModel.toggleReminder(true)
                                Toast.makeText(context, "Pengingat harian diaktifkan", Toast.LENGTH_SHORT).show()
                            } else {
                                viewModel.toggleReminder(false)
                                Toast.makeText(context, "Pengingat harian dinonaktifkan", Toast.LENGTH_SHORT).show()
                            }
                        },
                    )
                }
            }

            // ── Data ──────────────────────────────────────────
            item { SectionHeader("Data") }
            item {
                SettingsCard {
                    NavigationRow(
                        icon     = Icons.Outlined.FileDownload,
                        tint     = Color(0xFF43A047),
                        label    = "Ekspor Data",
                        subtitle = "Simpan transaksi sebagai file CSV",
                        onClick  = { viewModel.exportToCsv(context) },
                    )
                    CardDivider()
                    NavigationRow(
                        icon       = Icons.Outlined.DeleteForever,
                        tint       = MaterialTheme.colorScheme.error,
                        label      = "Hapus Semua Data",
                        subtitle   = "Hapus semua transaksi secara permanen",
                        labelColor = MaterialTheme.colorScheme.error,
                        onClick    = { showDeleteDialog = true },
                    )
                }
            }

            // ── Tentang ───────────────────────────────────────
            item { SectionHeader("Tentang") }
            item {
                SettingsCard {
                    NavigationRow(
                        icon    = Icons.Outlined.Star,
                        tint    = Color(0xFFFFB300),
                        label   = "Beri Rating",
                        subtitle = "Nilai aplikasi di Play Store",
                        onClick = { Toast.makeText(context, "Segera hadir", Toast.LENGTH_SHORT).show() },
                    )
                    CardDivider()
                    NavigationRow(
                        icon    = Icons.AutoMirrored.Outlined.HelpOutline,
                        tint    = Color(0xFF546E7A),
                        label   = "Bantuan & FAQ",
                        onClick = onNavigateToFaq,
                    )
                    CardDivider()
                    StaticRow(
                        icon    = Icons.Outlined.Person,
                        tint    = Color(0xFF757575),
                        label   = "Versi Aplikasi",
                        value   = "1.0.0",
                    )
                }
            }

            item { Spacer(Modifier.navigationBarsPadding()) }
        }
    }
}

// ─────────────────────────────────────────────────────────────
// Reusable Settings Building Blocks
// ─────────────────────────────────────────────────────────────

@Composable
private fun SectionHeader(title: String) {
    Text(
        text          = title.uppercase(),
        style         = MaterialTheme.typography.labelSmall,
        color         = MaterialTheme.colorScheme.onSurfaceVariant,
        letterSpacing = 1.sp,
        modifier      = Modifier.padding(start = 16.dp, top = 20.dp, bottom = 6.dp, end = 16.dp),
    )
}

@Composable
private fun SettingsCard(content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface),
    ) { content() }
}

@Composable
private fun CardDivider() {
    HorizontalDivider(
        modifier  = Modifier.padding(start = 56.dp),
        color     = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
        thickness = 0.5.dp,
    )
}

@Composable
private fun NavigationRow(
    icon       : ImageVector,
    tint       : Color,
    label      : String,
    subtitle   : String?  = null,
    labelColor : Color    = MaterialTheme.colorScheme.onSurface,
    onClick    : () -> Unit,
) {
    Row(
        modifier          = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconPill(icon = icon, tint = tint)
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(label, fontWeight = FontWeight.Medium, color = labelColor)
            if (subtitle != null) {
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        Icon(
            Icons.Outlined.ChevronRight, null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp),
        )
    }
}

@Composable
private fun SwitchRow(
    icon            : ImageVector,
    tint            : Color,
    label           : String,
    subtitle        : String?  = null,
    checked         : Boolean,
    onCheckedChange : (Boolean) -> Unit,
) {
    Row(
        modifier          = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconPill(icon = icon, tint = tint)
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(label, fontWeight = FontWeight.Medium)
            if (subtitle != null) {
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        Switch(
            checked         = checked,
            onCheckedChange = onCheckedChange,
            colors          = SwitchDefaults.colors(
                checkedThumbColor = PrimaryYellow,
                checkedTrackColor = PrimaryYellow.copy(alpha = 0.3f),
            ),
        )
    }
}

@Composable
private fun StaticRow(
    icon    : ImageVector,
    tint    : Color,
    label   : String,
    value   : String,
) {
    Row(
        modifier          = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconPill(icon = icon, tint = tint)
        Spacer(Modifier.width(14.dp))
        Text(label, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun IconPill(icon: ImageVector, tint: Color) {
    Box(
        modifier         = Modifier
            .size(36.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(tint.copy(alpha = 0.12f)),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, null, tint = tint, modifier = Modifier.size(20.dp))
    }
}
