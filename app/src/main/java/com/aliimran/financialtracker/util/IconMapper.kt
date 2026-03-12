package com.aliimran.financialtracker.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.Apartment
import androidx.compose.material.icons.outlined.Attractions
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.BusinessCenter
import androidx.compose.material.icons.outlined.CardGiftcard
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Coffee
import androidx.compose.material.icons.outlined.ConfirmationNumber
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.DirectionsBus
import androidx.compose.material.icons.outlined.ElectricBolt
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Flight
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Laptop
import androidx.compose.material.icons.outlined.LocalGroceryStore
import androidx.compose.material.icons.outlined.LocalHospital
import androidx.compose.material.icons.outlined.LocalPharmacy
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.Paid
import androidx.compose.material.icons.outlined.PedalBike
import androidx.compose.material.icons.outlined.Pets
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.SportsEsports
import androidx.compose.material.icons.outlined.Subscriptions
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material.icons.outlined.Work
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Central mapping from category icon resource-name strings (stored in Room)
 * to Compose [ImageVector] objects.
 *
 * Why strings? Category icons are persisted in the database, so we can't
 * store an [ImageVector] directly.  The string key is stable across app
 * updates; the corresponding icon can be swapped without a DB migration.
 *
 * Usage:
 *   val icon = IconMapper.resolve("ic_cat_food")
 *   Icon(imageVector = icon, contentDescription = null)
 */
object IconMapper {

    /**
     * Ordered list of all pickable icons shown in the Category Form Dialog.
     * Each entry is a (resName, imageVector) pair.
     */
    val ALL_ICONS: List<Pair<String, ImageVector>> = listOf(
        // ── Food & Drink ──────────────────────────────────────
        "ic_cat_food"          to Icons.Outlined.Restaurant,
        "ic_cat_coffee"        to Icons.Outlined.Coffee,
        "ic_cat_grocery"       to Icons.Outlined.LocalGroceryStore,
        // ── Transport ────────────────────────────────────────
        "ic_cat_transport"     to Icons.Outlined.DirectionsBus,
        "ic_cat_bike"          to Icons.Outlined.PedalBike,
        "ic_cat_travel"        to Icons.Outlined.Flight,
        // ── Shopping ─────────────────────────────────────────
        "ic_cat_shopping"      to Icons.Outlined.CreditCard,
        "ic_cat_subscription"  to Icons.Outlined.Subscriptions,
        // ── Entertainment ────────────────────────────────────
        "ic_cat_entertain"     to Icons.Outlined.SportsEsports,
        "ic_cat_music"         to Icons.Outlined.MusicNote,
        "ic_cat_attraction"    to Icons.Outlined.Attractions,
        "ic_cat_ticket"        to Icons.Outlined.ConfirmationNumber,
        // ── Health ────────────────────────────────────────────
        "ic_cat_health"        to Icons.Outlined.LocalHospital,
        "ic_cat_pharmacy"      to Icons.Outlined.LocalPharmacy,
        "ic_cat_fitness"       to Icons.Outlined.FitnessCenter,
        "ic_cat_pet"           to Icons.Outlined.Pets,
        // ── Education ────────────────────────────────────────
        "ic_cat_education"     to Icons.Outlined.School,
        "ic_cat_book"          to Icons.Outlined.MenuBook,
        // ── Bills & Utilities ─────────────────────────────────
        "ic_cat_bills"         to Icons.Outlined.Receipt,
        "ic_cat_electric"      to Icons.Outlined.ElectricBolt,
        "ic_cat_phone"         to Icons.Outlined.Phone,
        // ── Housing ───────────────────────────────────────────
        "ic_cat_housing"       to Icons.Outlined.Home,
        "ic_cat_apartment"     to Icons.Outlined.Apartment,
        // ── Income ────────────────────────────────────────────
        "ic_cat_salary"        to Icons.Outlined.Work,
        "ic_cat_business"      to Icons.Outlined.BusinessCenter,
        "ic_cat_freelance"     to Icons.Outlined.Laptop,
        "ic_cat_investment"    to Icons.Outlined.TrendingUp,
        "ic_cat_gift"          to Icons.Outlined.CardGiftcard,
        "ic_cat_other_income"  to Icons.Outlined.Paid,
        "ic_cat_bank"          to Icons.Outlined.AccountBalance,
        "ic_cat_stocks"        to Icons.Outlined.BarChart,
        // ── General / Fallback ───────────────────────────────
        "ic_cat_general"       to Icons.Outlined.Category,
        "ic_cat_other"         to Icons.Outlined.MoreHoriz,
    )

    /** Fast lookup map built once at class-load time. */
    private val iconMap: Map<String, ImageVector> = ALL_ICONS.toMap()

    /**
     * Resolves a stored resource-name string to an [ImageVector].
     * Falls back to [Icons.Outlined.Category] for unknown/legacy keys.
     */
    fun resolve(resName: String): ImageVector =
        iconMap[resName] ?: Icons.Outlined.Category

    /** Convenience for composable call-sites. */
    operator fun get(resName: String): ImageVector = resolve(resName)
}
