package com.aliimran.financialtracker.presentation.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
 * Laporan (Reports) screen — stub.
 * Will contain monthly/yearly PDF/CSV export functionality.
 */
@Composable
fun ReportsScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Laporan — Coming Soon",
            style = MaterialTheme.typography.titleMedium)
    }
}
