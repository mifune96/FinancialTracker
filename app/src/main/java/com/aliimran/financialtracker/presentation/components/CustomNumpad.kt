package com.aliimran.financialtracker.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Backspace
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aliimran.financialtracker.presentation.addtransaction.NumpadKey
import com.aliimran.financialtracker.presentation.theme.PrimaryYellow

/**
 * A custom persistent numeric keypad that replaces the system keyboard
 * for amount entry in the Add Transaction screen.
 *
 * Layout (4 rows × 3 columns + confirm button):
 * ┌─────────────────────────────────┐
 * │   7    │   8    │   9           │
 * │   4    │   5    │   6           │
 * │   1    │   2    │   3           │
 * │  000   │   0    │   ⌫           │
 * │  [   ✓  Simpan Transaksi   ]   │
 * └─────────────────────────────────┘
 *
 * @param onKeyPress       Called with the pressed [NumpadKey].
 * @param onConfirm        Called when the user taps the Save button.
 * @param isConfirmEnabled Whether the Save button is interactive (false while saving).
 * @param isSaving         Shows a loading indicator inside the Save button.
 * @param confirmLabel     Label on the save button — defaults to "Simpan Transaksi".
 * @param modifier         External layout modifier.
 */
@Composable
fun CustomNumpad(
    onKeyPress       : (NumpadKey) -> Unit,
    onConfirm        : () -> Unit,
    isConfirmEnabled : Boolean,
    isSaving         : Boolean  = false,
    confirmLabel     : String   = "Simpan Transaksi",
    modifier         : Modifier = Modifier,
) {
    // Key layout: each inner list is one row
    val numpadRows = listOf(
        listOf(NumpadRow.Digit(7), NumpadRow.Digit(8), NumpadRow.Digit(9)),
        listOf(NumpadRow.Digit(4), NumpadRow.Digit(5), NumpadRow.Digit(6)),
        listOf(NumpadRow.Digit(1), NumpadRow.Digit(2), NumpadRow.Digit(3)),
        listOf(NumpadRow.Special("000"), NumpadRow.Digit(0), NumpadRow.SpecialBackspace),
    )

    Column(
        modifier           = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
        Spacer(modifier = Modifier.height(4.dp))

        // ── Digit rows ────────────────────────────────────────
        numpadRows.forEach { row ->
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                row.forEach { key ->
                    NumpadButton(
                        key      = key,
                        onClick  = {
                            onKeyPress(
                                when (key) {
                                    is NumpadRow.Digit        -> NumpadKey.Digit(key.value)
                                    is NumpadRow.Special      -> NumpadKey.TripleZero
                                    NumpadRow.SpecialBackspace -> NumpadKey.Backspace
                                }
                            )
                        },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // ── Confirm / Save button ─────────────────────────────
        Button(
            onClick  = onConfirm,
            enabled  = isConfirmEnabled,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape    = RoundedCornerShape(16.dp),
            colors   = ButtonDefaults.buttonColors(
                containerColor         = PrimaryYellow,
                disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
            ),
        ) {
            if (isSaving) {
                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    color    = Color.White,
                    strokeWidth = 2.dp,
                )
            } else {
                Icon(
                    imageVector        = Icons.Outlined.CheckCircle,
                    contentDescription = null,
                    tint               = Color.White,
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text       = confirmLabel,
                    color      = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize   = 16.sp,
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))
    }
}

// ─────────────────────────────────────────────────────────────
// Individual Numpad Button
// ─────────────────────────────────────────────────────────────

@Composable
private fun NumpadButton(
    key      : NumpadRow,
    onClick  : () -> Unit,
    modifier : Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier          = modifier
            .height(54.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
            .clickable(
                interactionSource = interactionSource,
                indication        = rememberRipple(bounded = true),
                onClick           = onClick,
            ),
        contentAlignment  = Alignment.Center,
    ) {
        when (key) {
            is NumpadRow.Digit    -> Text(
                text       = key.value.toString(),
                fontSize   = 22.sp,
                fontWeight = FontWeight.SemiBold,
                color      = MaterialTheme.colorScheme.onSurface,
            )
            is NumpadRow.Special  -> Text(
                text       = key.label,
                fontSize   = 18.sp,
                fontWeight = FontWeight.Medium,
                color      = MaterialTheme.colorScheme.onSurface,
            )
            NumpadRow.SpecialBackspace -> Icon(
                imageVector        = Icons.AutoMirrored.Outlined.Backspace,
                contentDescription = "Hapus",
                tint               = MaterialTheme.colorScheme.onSurface,
                modifier           = Modifier.size(22.dp),
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────
// Internal sealed key representation (private to this file)
// ─────────────────────────────────────────────────────────────

private sealed interface NumpadRow {
    data class Digit(val value: Int)     : NumpadRow
    data class Special(val label: String): NumpadRow
    data object SpecialBackspace         : NumpadRow
}
