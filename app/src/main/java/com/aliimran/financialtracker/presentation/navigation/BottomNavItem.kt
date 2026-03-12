package com.aliimran.financialtracker.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Person
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Defines the four bottom navigation items (the FAB centre button is
 * handled separately in the scaffold and is NOT a nav item).
 *
 * @param screen      Corresponding [Screen] destination.
 * @param label       Indonesian label shown below the icon.
 * @param icon        Material icon for unselected state.
 */
enum class BottomNavItem(
    val screen: Screen,
    val label: String,
    val icon: ImageVector,
) {
    HISTORY(
        screen = Screen.History,
        label  = "Riwayat",
        icon   = Icons.Outlined.History,
    ),
    ANALYTICS(
        screen = Screen.Analytics,
        label  = "Grafik",
        icon   = Icons.Outlined.BarChart,
    ),
    REPORTS(
        screen = Screen.Reports,
        label  = "Laporan",
        icon   = Icons.Outlined.Description,
    ),
    PROFILE(
        screen = Screen.Profile,
        label  = "Saya",
        icon   = Icons.Outlined.Person,
    ),
}
