// Root build.gradle.kts — only plugin declarations, no code here.
// All version numbers live in gradle/libs.versions.toml.
plugins {
    alias(libs.plugins.android.application)    apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose)         apply false
    alias(libs.plugins.hilt.android)           apply false
    alias(libs.plugins.kotlin.ksp)             apply false
}
