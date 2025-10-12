// Nama   : Ali Muhammad Firdaus
// NIM    : H1D024102
// Shift  : D

public class Motor extends Kendaraan {
    private static int totalMotor = 0;                  // Hitung jumlah total objek Motor yang dibuat

    private String jenisMotor;                          // Jenis motor: Sport, Matic, atau Manual

    // Constructor: gunakan super() untuk parent, this untuk atribut sendiri
    public Motor(String merk, int tahun, double hargaSewa, String jenisMotor) {
        super(merk, tahun, hargaSewa);                  // Panggil constructor kelas Kendaraan
        this.jenisMotor = jenisMotor;                   // this.jenisMotor = atribut, jenisMotor = parameter
        totalMotor++;                                   // Tambah hitungan motor setiap objek dibuat
    }

    // Overload: hitung sewa dengan tambahan helm (Rp5.000 per helm per hari)
    public double hitungTotalSewa(int jumlahHari, int jumlahHelm) {
        double sewaDasar = hargaSewa * jumlahHari;      // Hitung sewa dasar tanpa akumulasi ganda
        double biayaHelm = jumlahHelm * 5000 * jumlahHari; // Hitung biaya helm tambahan
        double total = sewaDasar + biayaHelm;          // Jumlahkan sewa dan helm
        totalPendapatan += total;                       // Akumulasi ke total pendapatan sistem
        return total;                                   // Kembalikan total akhir
    }

    // Override: tampilkan info lengkap motor
    @Override
    public void tampilkanInfo() {
        System.out.println("\n=== MOTOR ===");         // Header kategori
        super.tampilkanInfo();                          // Tampilkan info dasar dari kelas induk
        System.out.println("Jenis Motor: " + jenisMotor); // Tambah info jenis motor
    }

    // Menampilkan informasi lengkap motor dalam format ringkas
    public void infoLengkap() {
        System.out.println("\nInformasi Motor " + merk + ":");
        System.out.println("- Jenis: " + jenisMotor);   // Akses atribut sendiri
        System.out.println("- Tahun: " + tahun);        // Akses atribut protected dari parent
        System.out.println("- Harga: Rp " + hargaSewa); // Akses harga dari parent
    }

    // Getter static: kembalikan jumlah total motor
    public static int getTotalMotor() {
        return totalMotor;                              // Mengembalikan nilai totalMotor
    }

    // Tampilkan statistik jumlah motor terdaftar
    public static void tampilkanInfoMotor() {
        System.out.println("Total Motor Terdaftar: " + totalMotor); // Cetak jumlah motor
    }
}