package com.aliimran.financialtracker.presentation.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.aliimran.financialtracker.presentation.navigation.AppNavGraph
import com.aliimran.financialtracker.presentation.navigation.BottomNavItem
import com.aliimran.financialtracker.presentation.navigation.Screen
import com.aliimran.financialtracker.presentation.theme.PrimaryYellow

/**
 * Root scaffold composable — wraps every screen with a bottom navigation bar
 * and a floating action button (+) for adding transactions.
 *
 * Layout:
 *  ┌─────────────────────────────────┐
 *  │         Screen Content          │
 *  │                           [+]   │  ← FAB
 *  ├────┬────┬────┬────┤
 *  │Riw │Grf │Lap │Sya │  ← 4-item navbar
 *  └────┴────┴────┴────┘
 */
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Routes where the bottom bar should be hidden
    val fullScreenRoutes = setOf(
        Screen.AddTransaction.route,
        Screen.EditTransaction.route,
        Screen.CategoryManagement.route,
        Screen.TransactionDetail.route,
    )
    val currentRoute = currentDestination?.route ?: ""
    val showBottomBar = fullScreenRoutes.none { currentRoute.startsWith(it.substringBefore("{")) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            if (showBottomBar) {
                FloatingActionButton(
                    onClick        = {
                        navController.navigate(Screen.AddTransaction.route) {
                            launchSingleTop = true
                        }
                    },
                    shape          = CircleShape,
                    containerColor = PrimaryYellow,
                    contentColor   = Color.White,
                ) {
                    Icon(
                        imageVector        = Icons.Default.Add,
                        contentDescription = "Tambah Transaksi",
                        modifier           = Modifier.size(28.dp),
                    )
                }
            }
        },
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomBar,
                enter   = slideInVertically(initialOffsetY = { it }),
                exit    = slideOutVertically(targetOffsetY = { it }),
            ) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 0.dp,
                ) {
                    BottomNavItem.entries.forEach { item ->
                        val isSelected = currentDestination
                            ?.hierarchy
                            ?.any { it.route == item.screen.route } == true

                        NavigationBarItem(
                            selected = isSelected,
                            onClick  = {
                                navController.navigate(item.screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState    = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector        = item.icon,
                                    contentDescription = item.label,
                                    modifier           = Modifier.size(22.dp),
                                )
                            },
                            label = {
                                Text(
                                    text       = item.label,
                                    fontSize   = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                )
                            },
                            alwaysShowLabel = true,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor   = PrimaryYellow,
                                selectedTextColor   = PrimaryYellow,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                indicatorColor      = PrimaryYellow.copy(alpha = 0.12f),
                            ),
                        )
                    }
                }
            }
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            AppNavGraph(navController = navController)
        }
    }
}
