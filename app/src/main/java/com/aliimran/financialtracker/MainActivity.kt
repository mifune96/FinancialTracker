package com.aliimran.financialtracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.aliimran.financialtracker.presentation.main.MainScreen
import com.aliimran.financialtracker.presentation.theme.FinancialTrackerTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Single-Activity host for the entire Compose UI tree.
 * All navigation is handled by NavHost inside [MainScreen].
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FinancialTrackerTheme {
                MainScreen()
            }
        }
    }
}
