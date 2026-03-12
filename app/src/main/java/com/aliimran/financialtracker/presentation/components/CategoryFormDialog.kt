package com.aliimran.financialtracker.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aliimran.financialtracker.presentation.settings.CATEGORY_COLOR_PALETTE
import com.aliimran.financialtracker.presentation.settings.CategoryFormState
import com.aliimran.financialtracker.util.Extensions.toComposeColor
import com.aliimran.financialtracker.util.IconMapper

/**
 * Add / Edit Category modal dialog.
 *
 * Layout (top → bottom):
 * ┌─────────────────────────────────────────┐
 * │  Tambah / Edit Kategori                 │ Title
 * ├─────────────────────────────────────────┤
 * │  [ Nama Kategori...                   ] │ Name TextField
 * │                                         │
 * │  Pilih Ikon                             │ Section header
 * │  [🍔][🚗][🛒][🎮][💊][📚][🏠][✈]…    │ Icon grid
 * │                                         │
 * │  Pilih Warna                            │ Section header
 * │  [●][●][●][●][●][●][●][●]…             │ Color swatches
 * ├─────────────────────────────────────────┤
 * │              [Batal] [Simpan]           │ Action buttons
 * └─────────────────────────────────────────┘
 *
 * @param formState   Current [CategoryFormState] (drives pre-fill for edit mode).
 * @param onNameChange     Called on each keystroke in the name field.
 * @param onIconSelected   Called when an icon chip is tapped.
 * @param onColorSelected  Called when a color swatch is tapped.
 * @param onConfirm        Called when "Simpan" is tapped (only if form is valid).
 * @param onDismiss        Called when dialog is dismissed or "Batal" is tapped.
 * @param isSaving         Shows disabled state on the confirm button while saving.
 */
@Composable
fun CategoryFormDialog(
    formState        : CategoryFormState,
    onNameChange     : (String) -> Unit,
    onIconSelected   : (String) -> Unit,
    onColorSelected  : (Int) -> Unit,
    onConfirm        : () -> Unit,
    onDismiss        : () -> Unit,
    isSaving         : Boolean = false,
) {
    val selectedColor = formState.color.toComposeColor()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text       = if (formState.isEditMode) "Edit Kategori" else "Tambah Kategori",
                fontWeight = FontWeight.Bold,
                fontSize   = 18.sp,
            )
        },
        text = {
            Column {

                // ── Name input ────────────────────────────────
                OutlinedTextField(
                    value         = formState.name,
                    onValueChange = { if (it.length <= 30) onNameChange(it) },
                    label         = { Text("Nama Kategori") },
                    placeholder   = { Text("contoh: Makan Siang") },
                    singleLine    = true,
                    isError       = formState.name.isNotEmpty() && !formState.isNameValid,
                    modifier      = Modifier.fillMaxWidth(),
                    shape         = RoundedCornerShape(12.dp),
                    colors        = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor   = selectedColor,
                        focusedLabelColor    = selectedColor,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    ),
                    supportingText = {
                        Text(
                            text  = "${formState.name.length}/30",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    },
                )

                Spacer(Modifier.height(16.dp))

                // ── Icon picker ───────────────────────────────
                Text(
                    text       = "Pilih Ikon",
                    style      = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(Modifier.height(8.dp))

                LazyVerticalGrid(
                    columns              = GridCells.Fixed(6),
                    modifier             = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    contentPadding       = PaddingValues(2.dp),
                    verticalArrangement  = Arrangement.spacedBy(6.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    items(
                        items = IconMapper.ALL_ICONS,
                        key   = { it.first },
                    ) { (resName, vector) ->
                        val isSelected = resName == formState.iconResName

                        Box(
                            modifier         = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (isSelected) selectedColor.copy(alpha = 0.20f)
                                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                )
                                .then(
                                    if (isSelected) Modifier.border(
                                        1.5.dp, selectedColor, RoundedCornerShape(8.dp)
                                    ) else Modifier
                                )
                                .clickable { onIconSelected(resName) },
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector        = vector,
                                contentDescription = resName,
                                modifier           = Modifier.size(20.dp),
                                tint               = if (isSelected) selectedColor
                                                     else MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // ── Color picker ──────────────────────────────
                Text(
                    text       = "Pilih Warna",
                    style      = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(Modifier.height(8.dp))

                // 2 rows of 8 swatches
                CATEGORY_COLOR_PALETTE.chunked(8).forEach { row ->
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        row.forEach { argb ->
                            val swatchColor = argb.toComposeColor()
                            val isSelected  = argb == formState.color

                            Box(
                                modifier         = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(swatchColor)
                                    .then(
                                        if (isSelected) Modifier.border(
                                            2.dp, MaterialTheme.colorScheme.onSurface, CircleShape
                                        ) else Modifier
                                    )
                                    .clickable { onColorSelected(argb) },
                                contentAlignment = Alignment.Center,
                            ) {
                                if (isSelected) {
                                    Icon(
                                        imageVector        = Icons.Outlined.Check,
                                        contentDescription = "Dipilih",
                                        tint               = Color.White,
                                        modifier           = Modifier.size(16.dp),
                                    )
                                }
                            }
                        }
                        // Fill remaining spaces in the last row
                        repeat(8 - row.size) { Spacer(Modifier.size(28.dp)) }
                    }
                    Spacer(Modifier.height(6.dp))
                }

                // ── Preview ───────────────────────────────────
                Spacer(Modifier.height(8.dp))
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier              = Modifier.fillMaxWidth(),
                ) {
                    CategoryChip(
                        category   = com.aliimran.financialtracker.domain.model.Category(
                            name        = formState.name.ifBlank { "Pratinjau" },
                            iconResName = formState.iconResName,
                            type        = formState.type,
                            color       = formState.color,
                        ),
                        isSelected = false,
                        onClick    = {},
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick  = onConfirm,
                enabled  = formState.isNameValid && !isSaving,
            ) {
                Text(
                    text       = if (isSaving) "Menyimpan…" else "Simpan",
                    fontWeight = FontWeight.Bold,
                    color      = if (formState.isNameValid) selectedColor
                                 else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        },
        shape         = RoundedCornerShape(20.dp),
        containerColor = MaterialTheme.colorScheme.surface,
    )
}
