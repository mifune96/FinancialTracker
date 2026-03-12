# 📋 Checklist Upload ke Google Play Store

## ✅ Persiapan Teknis (Sudah Selesai)

- [x] `applicationId` unik (`com.aliimran.financialtracker`)
- [x] `namespace` sudah diganti
- [x] Release keystore (`release-key.jks`) sudah dibuat
- [x] Signing config di `build.gradle.kts`
- [x] ProGuard / R8 aktif (`isMinifyEnabled = true`)
- [x] Resource shrinking aktif (`isShrinkResources = true`)
- [x] `targetSdk = 35` (sesuai requirement terbaru)
- [x] AAB berhasil di-build (`app-release.aab`)
- [x] Tidak ada debug log di source code
- [x] Tidak ada hardcoded secrets/API keys

---

## 📱 Google Play Console

### 1. Developer Account
- [ ] Punya **Google Play Developer Account** ($25 one-time fee)
  - Daftar di: https://play.google.com/console/signup

### 2. Buat Aplikasi Baru
- [ ] Buka Play Console → **"Create app"**
- [ ] Isi nama app: **Catatan Keuangan**
- [ ] Pilih bahasa default: **Bahasa Indonesia**
- [ ] Pilih tipe: **App** (bukan Game)
- [ ] Pilih: **Free** (gratis)

---

## 🖼️ Store Listing (Halaman Toko)

### Info Dasar
- [ ] **Nama App**: Catatan Keuangan (maks 30 karakter)
- [ ] **Deskripsi Singkat** (maks 80 karakter):
  > Catat pemasukan & pengeluaran harian dengan mudah dan gratis
- [ ] **Deskripsi Lengkap** (maks 4000 karakter):
  > Catatan Keuangan adalah aplikasi pencatat keuangan pribadi yang membantu Anda mengelola pemasukan dan pengeluaran sehari-hari. Dengan tampilan modern dan mudah digunakan, Anda bisa:
  >
  > 📝 Mencatat transaksi pemasukan & pengeluaran
  > 📊 Melihat grafik analitik pengeluaran per kategori
  > 📅 Melihat riwayat transaksi per bulan
  > 📋 Mengekspor data ke file CSV
  > 📸 Melampirkan foto bukti transaksi
  > 🏷️ Membuat kategori kustom
  > ✏️ Mengedit dan menghapus transaksi
  >
  > Semua data tersimpan di perangkat Anda — tidak perlu internet, tidak perlu akun.

### Aset Visual
- [ ] **Ikon App** (512 x 512 px, PNG, 32-bit, maks 1 MB)
- [ ] **Feature Graphic** (1024 x 500 px, JPG/PNG) — banner header di Play Store
- [ ] **Screenshot HP** (minimal 2 buah, maks 8)
  - Ukuran: 320–3840 px, rasio 16:9 atau 9:16
  - Rekomendasi: **ambil 4-5 screenshot** dari halaman utama app
    1. Halaman Riwayat (ringkasan bulanan)
    2. Tambah Transaksi (form input)
    3. Grafik Analitik (pie chart)
    4. Detail Transaksi
    5. Pengaturan Kategori
- [ ] **Screenshot Tablet** (opsional, tapi direkomendasikan)

---

## 🔒 Privacy Policy

- [ ] Buat halaman **Privacy Policy** (wajib)
  - Bisa pakai generator gratis: https://app-privacy-policy-generator.firebaseapp.com
  - Atau buat sendiri dan host di GitHub Pages / Google Sites
- [ ] Masukkan URL Privacy Policy di Play Console

> [!IMPORTANT]
> Play Store **WAJIB** punya Privacy Policy karena app menggunakan permission Camera dan Storage.

---

## 📊 Content Rating

- [ ] Isi kuesioner **IARC** di Play Console
  - Untuk app keuangan sederhana, biasanya dapat rating **Semua Umur (Everyone)**
  - Jawab: Tidak ada kekerasan, tidak ada perjudian, tidak ada konten dewasa

---

## 🛡️ Data Safety

Isi formulir Data Safety di Play Console:

| Pertanyaan | Jawaban |
|---|---|
| Apakah app mengumpulkan data pengguna? | **Tidak** (semua data lokal) |
| Apakah app membagikan data ke pihak ketiga? | **Tidak** |
| Apakah data dienkripsi saat transit? | **Tidak berlaku** (offline app) |
| Apakah pengguna bisa meminta penghapusan data? | **Ya** (fitur Hapus Semua Data) |

---

## 💰 Pricing & Distribution

- [ ] Pilih **Gratis** (free)
- [ ] Pilih negara distribusi (minimal: **Indonesia**)
- [ ] Tandai: **Tidak mengandung iklan**

---

## 📤 Upload AAB

- [ ] Buka **Production** → **Create new release**
- [ ] Upload file `app/build/outputs/bundle/release/app-release.aab`
- [ ] Isi **Release name**: `1.0.0`
- [ ] Isi **Release notes**:
  > Rilis pertama Catatan Keuangan! Fitur: catat pemasukan & pengeluaran, grafik analitik, ekspor CSV, foto bukti, manajemen kategori.

---

## 🚀 Submit untuk Review

- [ ] Pastikan semua bagian di dashboard berstatus ✅ (centang hijau)
- [ ] Klik **"Send for review"**
- [ ] Tunggu review Google (biasanya 1-7 hari kerja)

---

## 📌 Setelah Publish

- [ ] Test install dari Play Store di device lain
- [ ] Simpan `release-key.jks` dan password di tempat aman!
- [ ] Backup keystore (kalau hilang, tidak bisa update app lagi)
