plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.kotlin.ksp)
}

android {
    namespace   = "com.aliimran.financialtracker"
    compileSdk  = 35

    defaultConfig {
        applicationId = "com.aliimran.financialtracker"
        minSdk        = 26          // Required for java.time.* without desugaring
        targetSdk     = 35
        versionCode   = 1
        versionName   = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        create("release") {
            storeFile     = file("release-key.jks")
            storePassword = project.findProperty("RELEASE_STORE_PASSWORD") as String? ?: ""
            keyAlias      = project.findProperty("RELEASE_KEY_ALIAS") as String? ?: "financialtracker"
            keyPassword   = project.findProperty("RELEASE_KEY_PASSWORD") as String? ?: ""
        }
    }

    buildTypes {
        debug {
            isMinifyEnabled   = false
            isDebuggable      = true
        }
        release {
            isMinifyEnabled   = true
            isShrinkResources = true
            signingConfig     = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
        // Enable experimental Compose APIs used in the codebase
        freeCompilerArgs += listOf(
            "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
            "-opt-in=androidx.compose.foundation.ExperimentalFoundationApi",
            "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
        )
    }

    buildFeatures {
        compose     = true
        buildConfig = true          // Enables BuildConfig generation
    }

    // Room: export schema JSON to schemas/ directory for migration tracking.
    // Commit the generated JSON to version control.
    ksp {
        arg("room.schemaLocation",   "$projectDir/schemas")
        arg("room.incremental",      "true")
        arg("room.expandProjection", "true")
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // ── AndroidX Core ─────────────────────────────────────────
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtime)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.splashscreen)

    // ── Compose BOM (pins all Compose library versions together) ──
    val composeBom = platform(libs.androidx.compose.bom)
    implementation(composeBom)
    androidTestImplementation(composeBom)

    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.navigation.compose)

    // ── Room ──────────────────────────────────────────────────
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // ── Hilt ──────────────────────────────────────────────────
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    // ── Coroutines ────────────────────────────────────────────
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.core)

    // ── Coil (image loading for receipt thumbnails) ────────────
    implementation(libs.coil.compose)


    // ── Debug tooling ─────────────────────────────────────────
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // ── Unit Tests ────────────────────────────────────────────
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.core)

    // ── Instrumented Tests ────────────────────────────────────
    androidTestImplementation(libs.androidx.junit.ext)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
}
