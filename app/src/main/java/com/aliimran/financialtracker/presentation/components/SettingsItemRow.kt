package com.aliimran.financialtracker.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.WorkspacePremium
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aliimran.financialtracker.presentation.theme.PrimaryYellow

// ─────────────────────────────────────────────────────────────
// Sealed configuration — passed to SettingsItemRow
// ─────────────────────────────────────────────────────────────

/**
 * Defines the trailing content variant for [SettingsItemRow].
 *
 * Using a sealed class allows exhaustive `when` expressions in the
 * renderer and makes it trivial to add new trailing variants
 * (e.g. a badge count) without touching call-sites.
 */
sealed class SettingsTrailing {

    /** A right-pointing chevron (→). Used for navigation items. */
    data object Chevron : SettingsTrailing()

    /** A toggle switch. [checked] reflects current state; [onToggle] handles changes. */
    data class Toggle(
        val checked  : Boolean,
        val onToggle : (Boolean) -> Unit,
    ) : SettingsTrailing()

    /**
     * A static value label (e.g. "IDR", "Terang").
     * Rendered right-aligned with muted styling.
     */
    data class Value(val text: String) : SettingsTrailing()

    /** No trailing widget. Use for purely informational rows. */
    data object None : SettingsTrailing()
}

// ─────────────────────────────────────────────────────────────
// Core composable
// ─────────────────────────────────────────────────────────────

/**
 * Universal single-row building block for every Settings list item.
 *
 * Anatomy:
 * ┌──────────────────────────────────────────────────────────┐
 * │  [IconPill]  Title           [VIP?]  [Trailing widget]  │
 * │              Subtitle (opt)                              │
 * └──────────────────────────────────────────────────────────┘
 *
 * Variants driven by [trailing]:
 *  • [SettingsTrailing.Chevron] — navigation row (tappable)
 *  • [SettingsTrailing.Toggle] — preference switch row
 *  • [SettingsTrailing.Value]  — read-only value display
 *  • [SettingsTrailing.None]   — label-only row
 *
 * @param title         Primary row label.
 * @param icon          Leading icon drawn inside a rounded pill.
 * @param iconTint      Tint colour for the icon and pill background.
 * @param trailing      Which trailing widget to render.
 * @param subtitle      Optional secondary description below [title].
 * @param isDestructive When true, [title] renders in [MaterialTheme.colorScheme.error].
 * @param showVipBadge  When true, a gold "VIP" crown badge appears next to [title].
 * @param onClick       Called when the row is tapped. Ignored for [SettingsTrailing.Toggle].
 * @param modifier      External layout modifier.
 */
@Composable
fun SettingsItemRow(
    title          : String,
    icon           : ImageVector,
    iconTint       : Color,
    trailing       : SettingsTrailing  = SettingsTrailing.Chevron,
    subtitle       : String?           = null,
    isDestructive  : Boolean           = false,
    showVipBadge   : Boolean           = false,
    onClick        : () -> Unit        = {},
    modifier       : Modifier          = Modifier,
) {
    val isClickable = trailing !is SettingsTrailing.Toggle && trailing !is SettingsTrailing.None
    val titleColor  = when {
        isDestructive -> MaterialTheme.colorScheme.error
        else          -> MaterialTheme.colorScheme.onSurface
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .then(if (isClickable) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = 16.dp, vertical = 13.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {

        // ── Leading icon pill ─────────────────────────────────
        Box(
            modifier         = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(iconTint.copy(alpha = 0.13f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector        = icon,
                contentDescription = null,
                tint               = iconTint,
                modifier           = Modifier.size(19.dp),
            )
        }

        // ── Title + subtitle block ────────────────────────────
        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text       = title,
                    style      = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color      = titleColor,
                )
                if (showVipBadge) {
                    VipBadge()
                }
            }
            if (subtitle != null) {
                Text(
                    text  = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        // ── Trailing widget ───────────────────────────────────
        when (trailing) {
            SettingsTrailing.Chevron -> Icon(
                imageVector        = Icons.Outlined.ChevronRight,
                contentDescription = null,
                tint               = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                modifier           = Modifier.size(20.dp),
            )

            is SettingsTrailing.Toggle -> Switch(
                checked         = trailing.checked,
                onCheckedChange = trailing.onToggle,
                colors          = SwitchDefaults.colors(
                    checkedThumbColor = PrimaryYellow,
                    checkedTrackColor = PrimaryYellow.copy(alpha = 0.3f),
                ),
            )

            is SettingsTrailing.Value -> {
                Text(
                    text  = trailing.text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.width(2.dp))
                Icon(
                    imageVector        = Icons.Outlined.ChevronRight,
                    contentDescription = null,
                    tint               = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                    modifier           = Modifier.size(18.dp),
                )
            }

            SettingsTrailing.None -> { /* nothing */ }
        }
    }
}

// ─────────────────────────────────────────────────────────────
// VIP Badge
// ─────────────────────────────────────────────────────────────

/**
 * Compact gold crown badge indicating a premium-only feature.
 * Rendered inline next to [SettingsItemRow]'s title when [showVipBadge] is true.
 */
@Composable
fun VipBadge(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFFFF8F00), Color(0xFFFFCA28)),
                )
            )
            .padding(horizontal = 6.dp, vertical = 2.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Icon(
            imageVector        = Icons.Outlined.WorkspacePremium,
            contentDescription = "VIP",
            tint               = Color(0xFF4A2600),
            modifier           = Modifier.size(11.dp),
        )
        Text(
            text       = "VIP",
            fontSize   = 9.sp,
            fontWeight = FontWeight.ExtraBold,
            color      = Color(0xFF4A2600),
            letterSpacing = 0.5.sp,
        )
    }
}

// ─────────────────────────────────────────────────────────────
// Convenience wrappers — shorthand for common patterns
// ─────────────────────────────────────────────────────────────

/** Navigation row: title + optional subtitle + chevron. */
@Composable
fun SettingsNavRow(
    title        : String,
    icon         : ImageVector,
    iconTint     : Color,
    subtitle     : String?  = null,
    showVipBadge : Boolean  = false,
    isDestructive: Boolean  = false,
    onClick      : () -> Unit,
    modifier     : Modifier = Modifier,
) = SettingsItemRow(
    title         = title,
    icon          = icon,
    iconTint      = iconTint,
    trailing      = SettingsTrailing.Chevron,
    subtitle      = subtitle,
    isDestructive = isDestructive,
    showVipBadge  = showVipBadge,
    onClick       = onClick,
    modifier      = modifier,
)

/** Value row: title + static trailing value string + chevron. */
@Composable
fun SettingsValueRow(
    title        : String,
    icon         : ImageVector,
    iconTint     : Color,
    value        : String,
    showVipBadge : Boolean  = false,
    onClick      : () -> Unit = {},
    modifier     : Modifier = Modifier,
) = SettingsItemRow(
    title        = title,
    icon         = icon,
    iconTint     = iconTint,
    trailing     = SettingsTrailing.Value(value),
    showVipBadge = showVipBadge,
    onClick      = onClick,
    modifier     = modifier,
)

/** Toggle row: title + switch. [onClick] is ignored. */
@Composable
fun SettingsToggleRow(
    title        : String,
    icon         : ImageVector,
    iconTint     : Color,
    subtitle     : String?          = null,
    checked      : Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier     : Modifier         = Modifier,
) = SettingsItemRow(
    title    = title,
    icon     = icon,
    iconTint = iconTint,
    trailing = SettingsTrailing.Toggle(checked, onCheckedChange),
    subtitle = subtitle,
    modifier = modifier,
)
