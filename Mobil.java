// Nama   : Ali Muhammad Firdaus
// NIM    : H1D024102
// Shift  : D

public class Mobil extends Kendaraan {
    private static int totalMobil = 0;                  // Hitung jumlah total objek Mobil yang dibuat

    private int jumlahPintu;                            // Jumlah pintu mobil (2 atau 4)
    private String jenisTransmisi;                      // Jenis transmisi: Manual atau Automatic

    // Constructor: gunakan super() untuk inisialisasi parent, this untuk atribut sendiri
    public Mobil(String merk, int tahun, double hargaSewa, int jumlahPintu, String jenisTransmisi) {
        super(merk, tahun, hargaSewa);                  // Panggil constructor kelas induk (Kendaraan)
        this.jumlahPintu = jumlahPintu;                 // this.jumlahPintu = atribut, jumlahPintu = parameter
        this.jenisTransmisi = jenisTransmisi;           // Inisialisasi atribut transmisi
        totalMobil++;                                   // Tambah hitungan mobil setiap objek dibuat
    }

    // Override: hitung sewa dengan tambahan 10% markup khusus mobil
    @Override
    public double hitungTotalSewa(int jumlahHari) {
        double totalDenganMarkup = hargaSewa * jumlahHari * 1.1; // Tambah 10% dari harga dasar
        totalPendapatan += totalDenganMarkup;           // Akumulasi ke total pendapatan sistem
        return totalDenganMarkup;                       // Kembalikan total setelah markup
    }

    // Overload khusus mobil: sewa dengan opsi driver
    public double hitungTotalSewa(int jumlahHari, boolean denganDriver) {
        double total = hitungTotalSewa(jumlahHari);     // Panggil versi override (sudah termasuk 10%)
        if (denganDriver) {
            total += 100000 * jumlahHari;               // Tambah Rp100.000/hari jika pakai driver
        }
        return total;                                   // Kembalikan total akhir
    }

    // Hitung sewa tanpa markup (gunakan method parent langsung)
    public double hitungSewaStandard(int jumlahHari) {
        return super.hitungTotalSewa(jumlahHari);       // super = akses method asli dari Kendaraan
    }

    // Override: tampilkan info lengkap mobil
    @Override
    public void tampilkanInfo() {
        System.out.println("\n=== MOBIL ===");         // Header kategori
        super.tampilkanInfo();                          // Tampilkan info dasar dari parent
        System.out.println("Jumlah Pintu: " + jumlahPintu); // Tambah info jumlah pintu
        System.out.println("Jenis Transmisi: " + jenisTransmisi); // Tambah info transmisi
    }

    // Bandingkan harga antara versi parent dan child
    public void bandingkanHarga(int jumlahHari) {
        double hargaParent = super.hitungTotalSewa(jumlahHari); // Harga tanpa markup
        double hargaChild = this.hitungTotalSewa(jumlahHari);   // Harga dengan markup (+10%)
        System.out.println("\n--- Perbandingan Harga " + merk + " ---");
        System.out.println("Harga Standard (parent): Rp " + hargaParent);
        System.out.println("Harga Mobil (+10%): Rp " + hargaChild);
    }

    // Getter static: kembalikan jumlah total mobil
    public static int getTotalMobil() {
        return totalMobil;                              // Mengembalikan nilai totalMobil
    }

    // Tampilkan info statistik khusus mobil
    public static void tampilkanInfoMobil() {
        System.out.println("Total Mobil Terdaftar: " + totalMobil); // Cetak jumlah mobil
    }
}