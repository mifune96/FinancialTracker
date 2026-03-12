package com.aliimran.financialtracker.presentation.profile

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AppShortcut
import androidx.compose.material.icons.outlined.Block
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aliimran.financialtracker.presentation.theme.PrimaryYellow

/**
 * "Saya" (Profile) screen — the 4th tab in the bottom navigation.
 *
 * Layout (top → bottom):
 * ┌─────────────────────────────────────────────┐
 * │  Saya                                       │  TopAppBar
 * ├─────────────────────────────────────────────┤
 * │  ┌─────────────────────────────────────┐    │
 * │  │  [👤]   Tamu                        │    │  ← ProfileHeaderCard
 * │  │         Masuk untuk sinkronisasi  → │    │
 * │  └─────────────────────────────────────┘    │
 * │                                             │
 * │  ┌─────────────────── gradient ──────────┐  │
 * │  │ 👑   Pusat Premium              ✨    │  │  ← PremiumCard
 * │  │      Akses semua fitur eksklusif      │  │
 * │  │                 [Coba Gratis 7 Hari→] │  │
 * │  └───────────────────────────────────────┘  │
 * │                                             │
 * │  ┌─────────────────────────────────────┐    │
 * │  │  👥 Rekomendasikan ke teman      ›  │    │  ┐
 * │  │─────────────────────────────────────│    │  │  ← MenuCard
 * │  │  🚫 Blokir Iklan                 ›  │    │  │
 * │  │─────────────────────────────────────│    │  │
 * │  │  ⚙️  Pengaturan                  ›  │    │  │
 * │  │─────────────────────────────────────│    │  │
 * │  │  📱 Aplikasi Kami Lainnya        ›  │    │  ┘
 * │  └─────────────────────────────────────┘    │
 * │                                             │
 * │  Financial Tracker  v1.0.0                  │  Footer
 * └─────────────────────────────────────────────┘
 *
 * @param onNavigateToSettings  Navigates to [SettingsScreen].
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateToSettings           : () -> Unit = {},
    // Kept for backward-compat with NavGraph that injects this param
    onNavigateToCategoryManagement : () -> Unit = {},
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Saya", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
                windowInsets = androidx.compose.foundation.layout.WindowInsets(0.dp),
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->

        LazyColumn(
            modifier       = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(
                start  = 16.dp,
                end    = 16.dp,
                top    = 8.dp,
                bottom = 80.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {

            // ── 1. Menu Card ──────────────────────────────────
            item {
                MenuCard {
                    MenuItem(
                        icon    = Icons.Outlined.Share,
                        label   = "Rekomendasikan ke teman",
                        onClick = {
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT,
                                    "Coba aplikasi Catatan Keuangan — catat keuangan jadi mudah! 💰")
                            }
                            context.startActivity(Intent.createChooser(intent, "Bagikan via"))
                        },
                    )
                    MenuDivider()
                    MenuItem(
                        icon    = Icons.Outlined.Settings,
                        label   = "Pengaturan",
                        onClick = onNavigateToSettings,
                    )
                }
            }

            // ── 4. Footer ─────────────────────────────────────
            item {
                AppFooter()
            }

            item { Spacer(Modifier.navigationBarsPadding()) }
        }
    }
}

// ─────────────────────────────────────────────────────────────
// 1 — Profile Header Card
// ─────────────────────────────────────────────────────────────

/**
 * Displays the user's avatar, display name, and a "Masuk" (Sign In)
 * call-to-action.  When the user is not authenticated (the default
 * guest state), it shows a placeholder avatar with the guest label.
 */
@Composable
private fun ProfileHeaderCard(
    onSignInClick : () -> Unit,
    isSignedIn    : Boolean   = false,
    displayName   : String    = "Tamu",
    modifier      : Modifier  = Modifier,
) {
    Card(
        modifier  = modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(20.dp),
        colors    = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Row(
            modifier          = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {

            // ── Avatar ────────────────────────────────────────
            Box(
                modifier         = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(PrimaryYellow.copy(alpha = 0.3f), PrimaryYellow.copy(alpha = 0.1f))
                        )
                    )
                    .border(2.dp, PrimaryYellow.copy(alpha = 0.4f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector        = Icons.Outlined.Person,
                    contentDescription = "Avatar",
                    tint               = PrimaryYellow,
                    modifier           = Modifier.size(28.dp),
                )
            }

            Spacer(Modifier.width(16.dp))

            // ── Name + Sign-in prompt ─────────────────────────
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text       = displayName,
                    style      = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.height(2.dp))
                if (!isSignedIn) {
                    Text(
                        text  = "Masuk untuk sinkronisasi data",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            Spacer(Modifier.width(12.dp))

            // ── CTA Button ────────────────────────────────────
            if (!isSignedIn) {
                Button(
                    onClick  = onSignInClick,
                    shape    = RoundedCornerShape(12.dp),
                    colors   = ButtonDefaults.buttonColors(
                        containerColor = PrimaryYellow,
                        contentColor   = Color.White,
                    ),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                ) {
                    Icon(
                        imageVector        = Icons.Outlined.PersonAdd,
                        contentDescription = null,
                        modifier           = Modifier.size(16.dp),
                    )
                    Spacer(Modifier.width(6.dp))
                    Text("Masuk", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
// 2 — Premium Center Card
// ─────────────────────────────────────────────────────────────

/**
 * Visually distinct gradient card promoting the "Pusat Premium" feature.
 *
 * Uses a gold-amber [Brush.linearGradient] with decorative emoji overlays.
 * The CTA button calls [onCtaClick] which should trigger an in-app purchase
 * or upgrade flow.
 */
@Composable
private fun PremiumCard(
    onCtaClick : () -> Unit,
    modifier   : Modifier = Modifier,
) {
    val premiumGradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFFFF8F00), // Amber 800
            Color(0xFFF9A825), // Amber 600
            Color(0xFFFFCA28), // Amber 400
        )
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 6.dp, shape = RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .background(brush = premiumGradient)
            .clickable(onClick = onCtaClick)
            .padding(horizontal = 20.dp, vertical = 20.dp),
    ) {

        // ── Decorative sparkles (top-right) ───────────────────
        Text(
            text     = "✨",
            fontSize = 28.sp,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 0.dp),
        )
        Text(
            text     = "⭐",
            fontSize = 14.sp,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 28.dp, end = 8.dp),
        )

        // ── Main content ──────────────────────────────────────
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "👑", fontSize = 26.sp)
                Spacer(Modifier.width(10.dp))
                Text(
                    text       = "Pusat Premium",
                    style      = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color      = Color(0xFF4A2600), // Dark brown for contrast on gold
                )
            }

            Spacer(Modifier.height(6.dp))

            Text(
                text  = "Akses semua fitur eksklusif tanpa batas",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF4A2600).copy(alpha = 0.8f),
            )

            Spacer(Modifier.height(16.dp))

            // ── CTA Button ────────────────────────────────────
            Button(
                onClick  = onCtaClick,
                shape    = RoundedCornerShape(12.dp),
                colors   = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4A2600),
                    contentColor   = Color(0xFFFFCA28),
                ),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
            ) {
                Text(
                    text       = "Coba Gratis 7 Hari  →",
                    fontWeight = FontWeight.Bold,
                    fontSize   = 13.sp,
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
// 3 — Menu Card
// ─────────────────────────────────────────────────────────────

/**
 * A rounded card wrapping a column of [MenuItem]s separated by [MenuDivider]s.
 */
@Composable
private fun MenuCard(
    modifier : Modifier = Modifier,
    content  : @Composable () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surface),
    ) {
        content()
    }
}

@Composable
private fun MenuItem(
    icon     : ImageVector,
    label    : String,
    onClick  : () -> Unit,
    badge    : String?  = null,
    modifier : Modifier = Modifier,
) {
    Row(
        modifier          = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 15.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // ── Icon ──────────────────────────────────────────────
        Box(
            modifier         = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(11.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector        = icon,
                contentDescription = null,
                tint               = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
                modifier           = Modifier.size(20.dp),
            )
        }

        Spacer(Modifier.width(14.dp))

        // ── Label ─────────────────────────────────────────────
        Text(
            text       = label,
            style      = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            modifier   = Modifier.weight(1f),
            maxLines   = 1,
            overflow   = TextOverflow.Ellipsis,
        )

        // ── Optional Badge ────────────────────────────────────
        if (badge != null) {
            Spacer(Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        brush = Brush.linearGradient(
                            listOf(Color(0xFFFF8F00), Color(0xFFFFCA28))
                        )
                    )
                    .padding(horizontal = 8.dp, vertical = 3.dp),
            ) {
                Text(
                    text       = badge,
                    fontSize   = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color      = Color(0xFF4A2600),
                )
            }
            Spacer(Modifier.width(6.dp))
        }

        // ── Chevron ───────────────────────────────────────────
        Icon(
            imageVector        = Icons.Outlined.ChevronRight,
            contentDescription = null,
            tint               = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            modifier           = Modifier.size(20.dp),
        )
    }
}

@Composable
private fun MenuDivider() {
    HorizontalDivider(
        modifier  = Modifier.padding(start = 70.dp, end = 16.dp),
        color     = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
        thickness = 0.5.dp,
    )
}

// ─────────────────────────────────────────────────────────────
// 4 — Footer
// ─────────────────────────────────────────────────────────────

@Composable
private fun AppFooter(modifier: Modifier = Modifier) {
    Column(
        modifier            = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text  = "💰 Catatan Keuangan",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text  = "Versi 1.0.0",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
        )
    }
}
