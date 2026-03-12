package com.aliimran.financialtracker.presentation.addtransaction

/**
 * Represents every possible key press on the custom numeric keypad.
 *
 * Using a sealed class instead of a String/Int enum makes exhaustive
 * `when` expressions compile-enforced — no "else" branch required.
 */
sealed class NumpadKey {

    /** A single digit 0–9 pressed by the user. */
    data class Digit(val value: Int) : NumpadKey()

    /**
     * The "000" shortcut key — appends three zeros at once.
     * Common in Indonesian financial apps for fast Rupiah entry
     * (e.g. quickly entering 1.000.000 with three taps: 1, 000, 000).
     */
    data object TripleZero : NumpadKey()

    /** Removes the last character from the amount input. */
    data object Backspace : NumpadKey()
}
