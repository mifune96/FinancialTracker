package com.aliimran.financialtracker.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.aliimran.financialtracker.util.DateFormatter

/**
 * Dialog-based month/year picker shown when the user taps the month
 * label in the History screen top bar.
 *
 * @param currentMonth  Currently selected month (1-based).
 * @param currentYear   Currently selected year.
 * @param onConfirm     Called with (month, year) when the user confirms selection.
 * @param onDismiss     Called when the user dismisses without selecting.
 */
@Composable
fun MonthYearPickerDialog(
    currentMonth : Int,
    currentYear  : Int,
    onConfirm    : (month: Int, year: Int) -> Unit,
    onDismiss    : () -> Unit,
) {
    // Local state scoped to this dialog — only committed on Confirm tap.
    var pickerYear  by remember { mutableIntStateOf(currentYear) }
    var pickerMonth by remember { mutableIntStateOf(currentMonth) }

    val monthNames = remember { DateFormatter.allMonthNames() }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
        ) {
            Column(modifier = Modifier.padding(24.dp)) {

                // ── Title ─────────────────────────────────────
                Text(
                    text  = "Pilih Bulan & Tahun",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )

                Spacer(modifier = Modifier.height(16.dp))

                // ── Year selector row ─────────────────────────
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = { pickerYear-- }) {
                        Icon(Icons.Default.ChevronLeft, contentDescription = "Tahun sebelumnya")
                    }
                    Text(
                        text      = pickerYear.toString(),
                        style     = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                    IconButton(onClick = { pickerYear++ }) {
                        Icon(Icons.Default.ChevronRight, contentDescription = "Tahun berikutnya")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // ── Month grid (3 columns × 4 rows) ───────────
                LazyVerticalGrid(
                    columns             = GridCells.Fixed(3),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier            = Modifier.fillMaxWidth(),
                ) {
                    items(monthNames.indices.toList()) { index ->
                        val month     = index + 1
                        val isSelected = month == pickerMonth

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { pickerMonth = month },
                            shape  = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.surfaceVariant,
                            ),
                        ) {
                            Text(
                                text      = monthNames[index].take(3), // Abbreviated name
                                modifier  = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 10.dp),
                                textAlign = TextAlign.Center,
                                style     = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                color     = if (isSelected)
                                    MaterialTheme.colorScheme.onPrimary
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ── Action buttons ────────────────────────────
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Batal")
                    }
                    TextButton(
                        onClick = { onConfirm(pickerMonth, pickerYear) },
                    ) {
                        Text("Konfirmasi", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
