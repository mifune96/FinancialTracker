# 💰 Catatan Keuangan (Financial Tracker)

Aplikasi pencatat keuangan pribadi berbasis Android yang dibuat dengan **Jetpack Compose** dan arsitektur **Clean Architecture + MVVM**. Catat pemasukan & pengeluaran, lihat grafik analitik, dan kelola kategori dengan mudah.

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android-green?logo=android" />
  <img src="https://img.shields.io/badge/Min%20SDK-26-blue" />
  <img src="https://img.shields.io/badge/Target%20SDK-35-blue" />
  <img src="https://img.shields.io/badge/Language-Kotlin-purple?logo=kotlin" />
  <img src="https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?logo=jetpackcompose" />
</p>

---

## ✨ Fitur Utama

| Fitur | Deskripsi |
|-------|-----------|
| 📝 **Catat Transaksi** | Tambah pemasukan & pengeluaran dengan kategori, catatan, tanggal, dan foto bukti |
| 📊 **Grafik Analitik** | Visualisasi pengeluaran per kategori dengan pie chart interaktif |
| 📅 **Riwayat Bulanan** | Lihat ringkasan saldo, pemasukan, dan pengeluaran per bulan |
| 📋 **Laporan** | Filter transaksi berdasarkan periode dan tipe |
| 🏷️ **Manajemen Kategori** | Tambah, edit, dan hapus kategori kustom dengan ikon & warna |
| 📸 **Foto Bukti** | Lampirkan foto dari kamera atau galeri, tersimpan permanen |
| ✏️ **Edit & Hapus** | Edit detail transaksi atau hapus dengan konfirmasi |
| 📤 **Ekspor CSV** | Ekspor semua transaksi ke file CSV untuk dianalisis di Excel |
| 🗑️ **Hapus Semua Data** | Reset data dengan dialog konfirmasi |
| 🔔 **Pengingat Harian** | Toggle pengingat untuk mencatat pengeluaran |

---

## 🏗️ Arsitektur

Aplikasi ini menggunakan **Clean Architecture** dengan pola **MVVM (Model-View-ViewModel)**:

```
app/
├── data/                       # Data Layer
│   ├── local/
│   │   ├── dao/                # Room DAO (TransactionDao, CategoryDao)
│   │   ├── entity/             # Room Entities
│   │   └── converter/          # Type Converters
│   ├── mapper/                 # Entity ↔ Domain mappers
│   └── repository/             # Repository implementations
│
├── domain/                     # Domain Layer
│   ├── model/                  # Domain models (Transaction, Category)
│   ├── repository/             # Repository interfaces
│   └── usecase/                # Use cases (AddTransaction, GetCategories)
│
├── presentation/               # Presentation Layer (UI)
│   ├── addtransaction/         # Add/Edit Transaction screen
│   ├── analytics/              # Analytics & Charts screen
│   ├── detail/                 # Transaction Detail screen
│   ├── history/                # History (home) screen
│   ├── main/                   # Main scaffold + bottom nav
│   ├── navigation/             # Navigation graph & routes
│   ├── profile/                # Profile/Settings screen
│   ├── settings/               # Settings detail screen
│   ├── components/             # Reusable UI components
│   └── theme/                  # Material 3 theme
│
├── di/                         # Hilt Dependency Injection modules
└── util/                       # Utility classes
```

---

## 🛠️ Tech Stack

| Kategori | Teknologi |
|----------|-----------|
| **Bahasa** | Kotlin |
| **UI Framework** | Jetpack Compose (Material 3) |
| **Arsitektur** | Clean Architecture + MVVM |
| **Database** | Room (SQLite) |
| **Dependency Injection** | Hilt (Dagger) |
| **Navigation** | Jetpack Navigation Compose |
| **Async** | Kotlin Coroutines + Flow |
| **Image Loading** | Coil |
| **Build System** | Gradle (Kotlin DSL) |
| **Min SDK** | 26 (Android 8.0 Oreo) |
| **Target SDK** | 35 (Android 15) |
| **Compile SDK** | 35 |

---

## 📦 Dependencies

```kotlin
// Core
androidx.core:core-ktx
androidx.activity:activity-compose
androidx.lifecycle:lifecycle-runtime-ktx
androidx.lifecycle:lifecycle-viewmodel-compose
androidx.core:core-splashscreen

// Compose (BOM)
androidx.compose:compose-bom
  ├── ui
  ├── ui-graphics
  ├── ui-tooling-preview
  ├── material3
  └── material-icons-extended

// Navigation
androidx.navigation:navigation-compose

// Room Database
androidx.room:room-runtime
androidx.room:room-ktx
androidx.room:room-compiler (KSP)

// Hilt
dagger:hilt-android
dagger:hilt-compiler (KSP)
androidx.hilt:hilt-navigation-compose

// Coroutines
kotlinx-coroutines-android
kotlinx-coroutines-core

// Image Loading
coil-compose
```

---

## 🚀 Cara Menjalankan

### Prasyarat
- Android Studio Hedgehog (2023.1.1) atau lebih baru
- JDK 17
- Android SDK 35
- Device/Emulator dengan min API 26

### Langkah

1. **Clone repository**
   ```bash
   git clone https://github.com/mifune96/FinancialTracker.git
   cd FinancialTracker
   ```

2. **Buka di Android Studio**
   ```
   File → Open → pilih folder FinancialTracker
   ```

3. **Sync Gradle**
   ```
   Android Studio akan otomatis sync dependencies
   ```

4. **Jalankan**
   ```bash
   ./gradlew installDebug
   # atau klik tombol ▶️ Run di Android Studio
   ```

### Build Release (AAB untuk Play Store)

1. Buat keystore (jika belum ada):
   ```bash
   keytool -genkey -v -keystore app/release-key.jks \
     -keyalg RSA -keysize 2048 -validity 10000 \
     -alias financialtracker
   ```

2. Tambahkan ke `gradle.properties`:
   ```properties
   RELEASE_STORE_PASSWORD=your_password
   RELEASE_KEY_ALIAS=financialtracker
   RELEASE_KEY_PASSWORD=your_password
   ```

3. Build AAB:
   ```bash
   ./gradlew bundleRelease
   ```

4. Output: `app/build/outputs/bundle/release/app-release.aab`

---

## 📱 Screenshots

| Riwayat | Tambah Transaksi | Detail | Grafik |
|---------|------------------|--------|--------|
| Halaman utama dengan ringkasan bulanan | Form input dengan numpad kustom | Detail transaksi lengkap | Pie chart pengeluaran per kategori |

---

## 📂 Struktur Database

### Tabel `categories`
| Kolom | Tipe | Keterangan |
|-------|------|------------|
| `id` | INTEGER (PK) | Auto-increment |
| `name` | TEXT | Nama kategori |
| `icon_res_name` | TEXT | Nama resource ikon |
| `type` | TEXT | EXPENSE / INCOME |
| `color` | INTEGER | Warna ARGB |

### Tabel `transactions`
| Kolom | Tipe | Keterangan |
|-------|------|------------|
| `id` | INTEGER (PK) | Auto-increment |
| `type` | TEXT | EXPENSE / INCOME |
| `amount` | REAL | Nominal transaksi |
| `category_id` | INTEGER (FK) | Referensi ke categories |
| `timestamp` | INTEGER | Unix timestamp (ms) |
| `note` | TEXT | Catatan opsional |
| `image_uri` | TEXT | URI foto bukti (nullable) |

---

## 🎨 Kategori Default

### Pengeluaran
🍔 Makanan & Minuman • 🚗 Transportasi • 🛍️ Belanja • 🎬 Hiburan • 🏥 Kesehatan • 📚 Pendidikan • 💡 Tagihan & Utilitas • 🏠 Tempat Tinggal • ✈️ Perjalanan • 📦 Umum

### Pemasukan
💼 Gaji • 🏢 Bisnis • 💻 Freelance • 📈 Investasi • 🎁 Hadiah • 💵 Pendapatan Lain

---

## 👨‍💻 Developer

**Ali Imran**

---

## 📄 Lisensi

```
Copyright © 2026 Ali Imran. All rights reserved.
```
