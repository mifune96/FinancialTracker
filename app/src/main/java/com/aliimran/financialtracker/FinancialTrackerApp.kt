package com.aliimran.financialtracker

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class — entry point for Dagger Hilt's component hierarchy.
 * Must be declared in AndroidManifest.xml:
 *   android:name=".FinancialTrackerApp"
 */
@HiltAndroidApp
class FinancialTrackerApp : Application()
