package com.aliimran.financialtracker.presentation.navigation

/**
 * Sealed class representing every navigable destination in the app.
 * Using [route] strings ensures compatibility with Compose Navigation's
 * string-based back-stack and avoids magic-string duplication.
 *
 * Navigation tree:
 *   History   ← bottom nav
 *   Analytics ← bottom nav
 *   Reports   ← bottom nav
 *   Profile   ← bottom nav ("Saya" tab)
 *     └─ Settings            ← from "Pengaturan" menu item
 *          └─ CategoryManagement ← from "Pengaturan Kategori"
 *   AddTransaction  ← FAB
 *   TransactionDetail/{id} ← from History row tap
 */
sealed class Screen(val route: String) {

    // ── Bottom Nav destinations ───────────────────────────────
    data object History   : Screen("history")
    data object Analytics : Screen("analytics")
    data object Reports   : Screen("reports")
    data object Profile   : Screen("profile")

    // ── Full-screen destinations ──────────────────────────────
    data object AddTransaction : Screen("add_transaction")

    /**
     * Detail/Edit screen — accepts a transaction id argument.
     * Use [createRoute] to build the concrete navigation string.
     */
    data object TransactionDetail : Screen("transaction_detail/{transactionId}") {
        fun createRoute(transactionId: Long) = "transaction_detail/$transactionId"
    }

    /**
     * Edit an existing transaction — reuses the Add screen in edit mode.
     */
    data object EditTransaction : Screen("edit_transaction/{transactionId}") {
        fun createRoute(transactionId: Long) = "edit_transaction/$transactionId"
    }

    /**
     * Settings screen — navigated to from the "Pengaturan" menu item
     * on the Profile (Saya) tab.
     */
    data object Settings : Screen("settings")

    /**
     * Category CRUD screen — navigated to from Settings →
     * "Pengaturan Kategori".
     */
    data object CategoryManagement : Screen("category_management")

    /**
     * FAQ screen — navigated to from Settings → "Bantuan & FAQ".
     */
    data object Faq : Screen("faq")
}
