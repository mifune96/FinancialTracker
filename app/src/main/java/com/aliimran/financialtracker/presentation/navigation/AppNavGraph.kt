package com.aliimran.financialtracker.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.aliimran.financialtracker.presentation.addtransaction.AddTransactionScreen
import com.aliimran.financialtracker.presentation.analytics.AnalyticsScreen
import com.aliimran.financialtracker.presentation.history.HistoryScreen
import com.aliimran.financialtracker.presentation.profile.ProfileScreen
import com.aliimran.financialtracker.presentation.settings.CategoryManagementScreen
import com.aliimran.financialtracker.presentation.detail.TransactionDetailScreen
import com.aliimran.financialtracker.presentation.settings.ReportsScreen
import com.aliimran.financialtracker.presentation.settings.SettingsScreen

/**
 * Root navigation graph for the entire application.
 *
 * Full route tree:
 *   history                        ← HistoryScreen      (bottom nav)
 *   analytics                      ← AnalyticsScreen    (bottom nav)
 *   reports                        ← ReportsScreen      (bottom nav)
 *   profile                        ← ProfileScreen      (bottom nav / "Saya")
 *     └─ settings                  ← SettingsScreen     (from "Pengaturan" menu)
 *          └─ category_management  ← CategoryManagementScreen
 *   add_transaction                ← AddTransactionScreen (FAB)
 *   transaction_detail/{id}        ← TransactionDetailScreen
 */
@Composable
fun AppNavGraph(
    navController    : NavHostController,
    startDestination : String = Screen.History.route,
) {
    NavHost(
        navController    = navController,
        startDestination = startDestination,
        enterTransition  = { androidx.compose.animation.EnterTransition.None },
        exitTransition   = { androidx.compose.animation.ExitTransition.None },
        popEnterTransition = { androidx.compose.animation.EnterTransition.None },
        popExitTransition  = { androidx.compose.animation.ExitTransition.None },
    ) {

        // ── Bottom Navigation destinations ────────────────────

        composable(Screen.History.route) {
            HistoryScreen(
                onTransactionClick = { id ->
                    navController.navigate(Screen.TransactionDetail.createRoute(id)) {
                        launchSingleTop = true
                    }
                },
            )
        }

        composable(Screen.Analytics.route) { AnalyticsScreen() }

        composable(Screen.Reports.route) { ReportsScreen() }

        composable(Screen.Profile.route) {
            ProfileScreen(
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                },
            )
        }

        // ── Settings tree ─────────────────────────────────────

        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack                 = { navController.popBackStack() },
                onNavigateToCategoryManagement = {
                    navController.navigate(Screen.CategoryManagement.route)
                },
            )
        }

        composable(Screen.CategoryManagement.route) {
            CategoryManagementScreen(
                onNavigateBack = { navController.popBackStack() },
            )
        }

        // ── Full-screen destinations ──────────────────────────

        composable(Screen.AddTransaction.route) {
            AddTransactionScreen(
                onNavigateBack = { navController.popBackStack() },
            )
        }

        composable(
            route     = Screen.EditTransaction.route,
            arguments = listOf(
                navArgument("transactionId") { type = NavType.LongType },
            ),
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("transactionId") ?: -1L
            AddTransactionScreen(
                onNavigateBack   = { navController.popBackStack() },
                editTransactionId = id,
            )
        }

        composable(
            route     = Screen.TransactionDetail.route,
            arguments = listOf(
                navArgument("transactionId") { type = NavType.LongType },
            ),
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("transactionId") ?: -1L
            TransactionDetailScreen(
                transactionId  = id,
                onNavigateBack = { navController.popBackStack() },
                onEdit         = { txId ->
                    navController.navigate(Screen.EditTransaction.createRoute(txId))
                },
            )
        }
    }
}
