// Nama   : Ali Muhammad Firdaus
// NIM    : H1D024102
// Shift  : D

public class Kendaraan {
    protected static int totalKendaraan = 0;            // Total kendaraan yang pernah dibuat
    protected static double totalPendapatan = 0;        // Akumulasi pendapatan dari semua transaksi sewa

    protected String merk;                              // Merk kendaraan (contoh: Toyota, Honda)
    protected int tahun;                                // Tahun produksi kendaraan
    protected double hargaSewa;                         // Harga sewa per hari dalam Rupiah

    // Constructor: inisialisasi atribut dan tambah hitungan kendaraan
    public Kendaraan(String merk, int tahun, double hargaSewa) {
        this.merk = merk;                               // this.merk = atribut kelas, merk = parameter
        this.tahun = tahun;                             // Gunakan 'this' untuk bedakan atribut dan parameter
        this.hargaSewa = hargaSewa;                     // Set harga sewa awal
        totalKendaraan++;                               // Tambah jumlah kendaraan global
    }

    // Hitung sewa standar dan akumulasi ke total pendapatan
    public double hitungTotalSewa(int jumlahHari) {
        double total = hargaSewa * jumlahHari;          // Hitung total sewa dasar
        totalPendapatan += total;                       // Tambahkan ke total pendapatan sistem
        return total;                                   // Kembalikan hasil perhitungan
    }

    // Hitung sewa dengan diskon (dalam persen), tanpa akumulasi ulang
    public double hitungTotalSewa(int jumlahHari, double diskon) {
        double totalAwal = hargaSewa * jumlahHari;      // Hitung total sebelum diskon
        double diskonNominal = totalAwal * (diskon / 100); // Hitung nilai potongan diskon
        return totalAwal - diskonNominal;               // Kembalikan total setelah diskon
    }

    // Hitung sewa dengan diskon dan tambahan biaya asuransi
    public double hitungTotalSewa(int jumlahHari, double diskon, double biayaAsuransi) {
        double setelahDiskon = hitungTotalSewa(jumlahHari, diskon); // Dapatkan total setelah diskon
        return setelahDiskon + biayaAsuransi;           // Tambahkan biaya asuransi
    }

    // Tampilkan informasi dasar kendaraan ke konsol
    public void tampilkanInfo() {
        System.out.println("Merk: " + merk);            // Cetak merk kendaraan
        System.out.println("Tahun: " + tahun);          // Cetak tahun produksi
        System.out.println("Harga Sewa per Hari: Rp " + hargaSewa); // Cetak harga sewa per hari
    }

    // Perbarui harga sewa kendaraan ini
    public void updateHarga(double hargaBaru) {
        this.hargaSewa = hargaBaru;                     // Ganti harga sewa dengan nilai baru
        System.out.println("Harga sewa " + merk + " diupdate menjadi: Rp " + hargaBaru); // Tampilkan konfirmasi
    }

    // Getter static: kembalikan total kendaraan terdaftar
    public static int getTotalKendaraan() {
        return totalKendaraan;                          // Mengembalikan nilai totalKendaraan
    }

    // Getter static: kembalikan total pendapatan sistem
    public static double getTotalPendapatan() {
        return totalPendapatan;                         // Mengembalikan akumulasi pendapatan
    }

    // Tampilkan laporan statistik rental
    public static void tampilkanStatistik() {
        System.out.println("\n========== STATISTIK RENTAL =========="); // Header laporan
        System.out.println("Total Kendaraan Terdaftar: " + totalKendaraan); // Tampilkan jumlah kendaraan
        System.out.println("Total Pendapatan: Rp " + totalPendapatan); // Tampilkan total pendapatan
        System.out.println("======================================"); // Penutup laporan
    }

    // Reset semua statistik ke nilai awal
    public static void resetStatistik() {
        totalKendaraan = 0;                             // Set ulang jumlah kendaraan
        totalPendapatan = 0;                            // Set ulang total pendapatan
        System.out.println("Statistik telah direset!"); // Konfirmasi reset
    }
}