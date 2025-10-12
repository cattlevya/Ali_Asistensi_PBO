// Nama   : Ali Muhammad Firdaus
// NIM    : H1D024102
// Shift  : D

public class Main {
    public static void main(String[] args) {
        System.out.println("====================================");
        System.out.println("   SISTEM RENTAL KENDARAAN");
        System.out.println("====================================");

        // Tampilkan statistik awal (belum ada kendaraan)
        System.out.println("\n--- SEBELUM MEMBUAT OBJEK ---");
        System.out.println("Total Kendaraan: " + Kendaraan.getTotalKendaraan());
        Mobil.tampilkanInfoMobil();
        Motor.tampilkanInfoMotor();

        // Buat 3 objek Mobil dengan spesifikasi berbeda
        Mobil avanza = new Mobil("Toyota Avanza", 2022, 300000, 4, "Manual");
        Mobil jazz = new Mobil("Honda Jazz", 2023, 380000, 4, "Automatic");
        Mobil fortuner = new Mobil("Toyota Fortuner", 2024, 750000, 4, "Automatic");

        // Buat 3 objek Motor dengan jenis dan harga berbeda
        Motor beat = new Motor("Honda Beat", 2021, 70000, "Matic");
        Motor mio = new Motor("Yamaha Mio", 2022, 80000, "Matic");
        Motor r15 = new Motor("Yamaha R15", 2023, 160000, "Sport");

        // Tampilkan statistik setelah pembuatan objek
        System.out.println("\n--- SETELAH MEMBUAT 6 OBJEK ---");
        System.out.println("Total Kendaraan: " + Kendaraan.getTotalKendaraan());
        System.out.println("Total Mobil: " + Mobil.getTotalMobil());
        System.out.println("Total Motor: " + Motor.getTotalMotor());

        // === DEMO FITUR MOBIL ===
        avanza.tampilkanInfo();
        System.out.println("Sewa 2 hari: Rp " + avanza.hitungTotalSewa(2));
        System.out.println("Sewa 2 hari + driver: Rp " + avanza.hitungTotalSewa(2, true));

        fortuner.tampilkanInfo();
        System.out.println("Sewa 1 hari: Rp " + fortuner.hitungTotalSewa(1));

        // === DEMO FITUR MOTOR ===
        r15.tampilkanInfo();
        System.out.println("Sewa 4 hari: Rp " + r15.hitungTotalSewa(4));
        System.out.println("Sewa 4 hari + 3 helm: Rp " + r15.hitungTotalSewa(4, 3));

        beat.infoLengkap(); // Tampilkan info lengkap motor

        // === DEMO PERBANDINGAN HARGA (parent vs child) ===
        jazz.bandingkanHarga(3);

        // === DEMO UPDATE HARGA ===
        System.out.println("\nHarga Jazz sebelum update: Rp " + jazz.hargaSewa);
        jazz.updateHarga(400000);
        System.out.println("Harga Jazz setelah update: Rp " + jazz.hargaSewa);

        // === DEMO OVERLOADING DENGAN DISKON & ASURANSI (dari kelas Kendaraan) ===
        System.out.println("\n--- DEMO DISKON & ASURANSI ---");
        double sewaDiskon = fortuner.hitungTotalSewa(5, 10.0); // 10% diskon
        System.out.println("Fortuner 5 hari, diskon 10%: Rp " + sewaDiskon);

        double sewaAsuransi = beat.hitungTotalSewa(3, 5.0, 25000); // 5% diskon + asuransi
        System.out.println("Beat 3 hari, diskon 5% + asuransi: Rp " + sewaAsuransi);

        // === SIMULASI TRANSAKSI NYATA (akan update totalPendapatan) ===
        System.out.println("\n--- SIMULASI TRANSAKSI NYATA ---");
        avanza.hitungTotalSewa(3);                      // Transaksi 1: Avanza 3 hari (+10%)
        r15.hitungTotalSewa(2, 2);                      // Transaksi 2: R15 + 2 helm
        jazz.hitungTotalSewa(4, true);                  // Transaksi 3: Jazz + driver
        mio.hitungTotalSewa(5);                         // Transaksi 4: Mio standar

        // Tampilkan laporan akhir
        Kendaraan.tampilkanStatistik();

        System.out.println("\n--- STATISTIK PER KATEGORI ---");
        Mobil.tampilkanInfoMobil();
        Motor.tampilkanInfoMotor();

        System.out.println("\n====================================");
        System.out.println("         PROGRAM SELESAI");
        System.out.println("====================================");
    }
}