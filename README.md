# Asmaul Husna: Memory Practice

[![Get it on Google Play](https://img.shields.io/badge/Google_Play-414141?style=for-the-badge&logo=google-play&logoColor=white)](https://play.google.com/store/apps/details?id=com.henrisusanto.asmaulhusnamemorypractice)

Repositori ini berisi *source code* Android (Kotlin) untuk aplikasi **[Asmaul Husna: Memory Practice](https://play.google.com/store/apps/details?id=com.henrisusanto.asmaulhusnamemorypractice)**. 

Aplikasi ini dirancang khusus untuk mempermudah **hafalan asmaul husna** menggunakan metode *active recall* yang terbukti efektif. Pengguna tidak hanya diajak membaca secara pasif, melainkan aktif mengingat urutan dan makna dari setiap nama Allah.

## 🌟 Fitur Utama

- **Mode Hafalan Berurutan (*Active Recall*)**: Latih dan uji ingatan Anda dengan menebak urutan **asmaul husna 99** (Ar-Rahman, Ar-Rahim, Al-Malik, dst) melalui sistem kartu interaktif.
- **Informasi Komprehensif & Offline**: Setiap kartu dilengkapi dengan kaligrafi Arab, transliterasi cara baca, serta **asmaul husna beserta artinya**. Anda bisa mempelajari **asmaul husna dan artinya** bahkan saat offline.
- **UI Bersih & Mudah Diakses**: Menggunakan desain warna pastel yang nyaman di mata dengan font tingkat keterbacaan tinggi. Sangat cocok digunakan oleh segala usia, dari anak-anak hingga lansia.
- **Dukungan *Dark Mode***: Tampilan otomatis menyesuaikan dengan tema terang/gelap pada pengaturan sistem perangkat Android Anda.

## 🚀 Teknologi yang Digunakan

Aplikasi ini dikembangkan sepenuhnya menggunakan ekosistem Android modern:
- **Bahasa Pemrograman**: 100% Kotlin
- **Build System**: Gradle (Kotlin DSL / `build.gradle.kts`)
- **Arsitektur**: Native Android

## 📱 Unduh Aplikasi

Dukung pengembangan aplikasi ini dengan mengunduh versi resminya secara gratis di Google Play Store:

👉 **[Unduh Asmaul Husna: Memory Practice di Google Play](https://play.google.com/store/apps/details?id=com.henrisusanto.asmaulhusnamemorypractice)**

## 🔗 Referensi & Sumber Daya

- **[Landing Page](https://asmaul-husna-memory-practice.github.io/hafalan-asmaul-husna/)** - Halaman utama untuk melihat informasi produk dan pengalaman penggunaan aplikasi.
- **[Demo Video](https://www.youtube.com/shorts/5d4aLgjN0hs)** - Demo singkat aplikasi yang menampilkan fitur utama dalam bentuk video pendek.

## 💻 Cara Menjalankan Proyek secara Lokal

Jika Anda ingin melihat kode sumber atau menjalankan proyek ini di *environment* lokal Anda:

1. *Clone* repositori ini:
   ```bash
   git clone https://github.com/susantohenri/asmaulhusna-memorypractice.git
   ```

## 📊 Firebase & Google Play Instrumentation

### 1. Identitas Konfigurasi Firebase
- **Firebase Project Name**: `tempat-sambung`
- **Firebase Project Number**: `215024871022`
- **Application ID**: `com.henrisusanto.asmaulhusnamemorypractice`
- **Config File**: `app/google-services.json`

### 2. Firebase Remote Config Parameters
Aplikasi mengonfigurasi Remote Config untuk manajemen penayangan iklan dinamis dan fallback id unit:
| Parameter Key | Tipe | Nilai Default In-App | Deskripsi |
|---|---|---|---|
| `isAdsEnabled` | Boolean | `true` | Toggle global penayangan iklan di seluruh aplikasi |
| `appOpenAdUnitId` | String | `""` | Ad Unit ID untuk iklan App Open |
| `bannerAdUnitId` | String | `""` | Ad Unit ID untuk banner iklan |
| `interstitialAdUnitId` | String | `""` | Ad Unit ID untuk interstitial iklan |
| `rewardedAdUnitId` | String | `""` | Ad Unit ID untuk rewarded iklan sertifikat |
| `nativeAdUnitId` | String | `""` | Ad Unit ID untuk native advanced iklan |

*Catatan:* Pada build `DEBUG`, minimum fetch interval diatur ke `0 detik` untuk mempermudah testing. Pada build `RELEASE`, interval fetch kembali ke standar `3600 detik (1 jam)`.

### 3. Panduan Google Play - Firebase Linking
Untuk melacak konversi akuisisi instal, subscription/IAP, serta analisis funnel install Google Play di Firebase Console, hubungkan Google Play Console ke Firebase Project:

#### Langkah Penautan (Link Steps):
1. Masuk ke [Firebase Console](https://console.firebase.google.com/) menggunakan akun pemilik/admin.
2. Pilih project **tempat-sambung**.
3. Klik ikon gerigi ⚙️ (**Project settings**) di sidebar kiri atas, lalu buka tab **Integrations**.
4. Cari kartu **Google Play**, lalu klik tombol **Link** (Tautkan).
5. Pastikan akun Google yang sedang aktif memiliki akses admin pada akun Google Play Console pengembang.
6. Pilih akun developer Google Play yang memuat aplikasi **Asmaul Husna: Memory Practice** (`com.henrisusanto.asmaulhusnamemorypractice`).
7. Aktifkan opsi sinkronisasi data yang dibutuhkan:
   - *Financial data / In-App Purchases reporting*
   - *Google Play install conversion tracking*
8. Klik **Confirm and link** untuk menyelesaikan penautan.

#### Verifikasi Status Penautan:
- Status pada kartu Google Play di tab **Integrations** akan berubah menjadi **Linked** hijau.
- Event otomatis dari Google Play (`in_app_purchase`, funnel install Google Play) akan mulai masuk ke dashboard Google Analytics 4 dan AdMob mediation reports.

