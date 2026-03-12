package com.aliimran.financialtracker.presentation.theme

import android.app.Activity
import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// ── Light Color Scheme (Yellow brand) ─────────────────────────
private val LightColorScheme = lightColorScheme(
    primary          = PrimaryYellow,
    onPrimary        = OnPrimary,
    primaryContainer = Color(0xFFFFF8E1),      // Very light yellow
    secondary        = Color(0xFFFFA000),       // Amber accent
    surface          = SurfaceLight,
    background       = Color(0xFFF0F2F5),
    error            = ExpenseRed,
)

// ── Dark Color Scheme ─────────────────────────────────────────
private val DarkColorScheme = darkColorScheme(
    primary          = PrimaryYellowDark,
    onPrimary        = OnPrimary,
    primaryContainer = Color(0xFF5C4700),       // Rich dark gold
    secondary        = Color(0xFFFFCC80),        // Light amber for dark mode
    surface          = SurfaceDark,
    background       = Color(0xFF1A1A1A),
    error            = Color(0xFFEF9A9A),
)

@Composable
fun FinancialTrackerTheme(
    darkTheme: Boolean = false,    // default light — change to isSystemInDarkTheme()
    dynamicColor: Boolean = false, // enable for Android 12+ dynamic colors
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else      -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content     = content,
    )
}
