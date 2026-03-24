# Script Video YouTube - CPMK-01 Pemrograman Web II
## Proyek: Aksara Lokal (E-Commerce Produk Lokal) - Paket 2

---

## BAGIAN 1: OPENING (Durasi: ~30 detik)

**[TAMPILKAN: Browser - Halaman Home Aksara Lokal]**

Assalamualaikum warahmatullahi wabarakatuh.

Halo Pak Irham, perkenalkan saya [NAMA], NIM [NIM], dari kelas [KELAS]. Di video ini saya akan menjelaskan proyek e-commerce Aksara Lokal yang sudah saya buat untuk ujian CPMK-01 Pemrograman Web II Paket 2.

Jadi, Aksara Lokal ini adalah platform e-commerce untuk produk-produk UMKM lokal Indonesia, mulai dari kerajinan kayu Bali, batik Jogja, keramik, perhiasan tradisional, sampai makanan khas daerah. Sistemnya punya tiga role: Buyer, Seller, dan Admin.

Langsung aja kita masuk ke penjelasan teknisnya.

---

## BAGIAN 2: SINGLETON PATTERN - Koneksi Database (Durasi: ~1.5 menit)

**[TAMPILKAN: VS Code - Buka file [config/database.php](file:///c:/xampp/htdocs/pemweb/config/database.php)]**

Oke, yang pertama saya mau tunjukin adalah Singleton Pattern di koneksi database. Buka file [config/database.php](file:///c:/xampp/htdocs/pemweb/config/database.php).

**[TAMPILKAN: VS Code - Highlight line 5]**

Di line 5 ada deklarasi property static:

```php
private static ?Database $instance = null;
```

Property ini bertipe nullable [Database](file:///c:/xampp/htdocs/pemweb/config/database.php#3-58), artinya dia bisa berisi objek [Database](file:///c:/xampp/htdocs/pemweb/config/database.php#3-58) atau `null`. Awalnya diset `null` karena belum ada koneksi yang dibuat.

**[TAMPILKAN: VS Code - Highlight line 6]**

Lalu di line 6:

```php
private PDO $pdo;
```

Ini adalah property yang menyimpan objek PDO, yaitu koneksi database-nya sendiri.

**[TAMPILKAN: VS Code - Highlight line 16-30]**

Sekarang lihat constructornya di line 16-30:

```php
private function __construct()
{
    $dsn = "mysql:host={$this->host};dbname={$this->dbname};charset={$this->charset}";
    $options = [
        PDO::ATTR_ERRMODE            => PDO::ERRMODE_EXCEPTION,
        PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
        PDO::ATTR_EMULATE_PREPARES   => false,
    ];

    try {
        $this->pdo = new PDO($dsn, $this->username, $this->password, $options);
    } catch (PDOException $e) {
        die('Database connection failed: ' . $e->getMessage());
    }
}
```

Perhatikan, constructornya **private**. Ini kunci dari Singleton. Kenapa private? Supaya tidak ada kode lain di manapun yang bisa bikin `new Database()` langsung. Kalau constructor-nya public, bisa aja ada 10 koneksi dibuat sekaligus, itu boros resource.

Ada tiga opsi PDO penting di sini:
- `ERRMODE_EXCEPTION` artinya kalau ada error SQL, PHP langsung throw exception, bukan diem aja.
- `FETCH_ASSOC` artinya hasil query langsung jadi array asosiatif, jadi akses datanya pake nama kolom, bukan indeks angka.
- `EMULATE_PREPARES => false` ini penting banget, artinya prepared statement-nya BENERAN dikirim ke MySQL, bukan cuma di-emulasi di PHP. Ini bikin aplikasi jauh lebih aman dari SQL Injection.

**[TAMPILKAN: VS Code - Highlight line 33]**

Di line 33:

```php
private function __clone() {}
```

Method [__clone()](file:///c:/xampp/htdocs/pemweb/config/database.php#33-34) juga private supaya objek ini tidak bisa di-clone. Kalau bisa di-clone, berarti bisa ada dua instance, Singleton-nya jadi rusak.

**[TAMPILKAN: VS Code - Highlight line 36-39]**

Di line 36-39:

```php
public function __wakeup()
{
    throw new \Exception("Cannot unserialize singleton");
}
```

[__wakeup()](file:///c:/xampp/htdocs/pemweb/config/database.php#36-40) akan di-trigger kalau ada yang coba `unserialize()` objek ini. Kalau bisa di-unserialize, artinya orang bisa bikin instance kedua lewat serialisasi, jadi kita block juga.

**[TAMPILKAN: VS Code - Highlight line 43-49]**

Nah, inti Singleton-nya ada di line 43-49, yaitu method [getInstance()](file:///c:/xampp/htdocs/pemweb/config/database.php#43-50):

```php
public static function getInstance(): self
{
    if (self::$instance === null) {
        self::$instance = new self();
    }
    return self::$instance;
}
```

Jadi alurnya: pertama kali [getInstance()](file:///c:/xampp/htdocs/pemweb/config/database.php#43-50) dipanggil, `$instance` masih `null`, maka `new self()` dijalankan -- ini yang memanggil constructor private tadi. Setelah itu, setiap kali dipanggil lagi, dia langsung return objek yang sama. Satu aplikasi, satu koneksi. Itu konsep Singleton.

**[TAMPILKAN: VS Code - Highlight line 53-56]**

Terakhir, line 53-56:

```php
public function getConnection(): PDO
{
    return $this->pdo;
}
```

Method ini yang mengembalikan objek PDO-nya untuk dipakai query. Jadi model-model nanti cukup panggil `Database::getInstance()->getConnection()` untuk dapat koneksi.

---

## BAGIAN 3: INHERITANCE (Durasi: ~1.5 menit)

**[TAMPILKAN: VS Code - Buka file [classes/BaseModel.php](file:///c:/xampp/htdocs/pemweb/classes/BaseModel.php)]**

Sekarang masuk ke konsep Inheritance. Buka file [classes/BaseModel.php](file:///c:/xampp/htdocs/pemweb/classes/BaseModel.php).

**[TAMPILKAN: VS Code - Highlight line 3-11]**

Di line 3 kita lihat:

```php
abstract class BaseModel
{
    protected PDO $db;
    protected string $table;

    public function __construct()
    {
        $this->db = Database::getInstance()->getConnection();
    }
```

[BaseModel](file:///c:/xampp/htdocs/pemweb/classes/BaseModel.php#3-84) ini adalah abstract class, artinya dia tidak bisa di-instantiate langsung, tapi harus di-extend oleh class lain. Di constructornya, dia memanggil Singleton `Database::getInstance()->getConnection()` yang tadi kita bahas.

Ada dua property protected di sini. `$db` untuk simpan koneksi PDO, `$table` untuk nama tabel. Kenapa protected? Supaya anak class-nya bisa akses, tapi dari luar class tidak bisa.

**[TAMPILKAN: VS Code - Highlight line 15-21, 36-44, 48-59, 63-67]**

BaseModel juga menyediakan method-method CRUD universal, yaitu:

```php
// line 15-21
public function findById(int $id): ?array { ... }

// line 36-44 
public function create(array $data): int { ... }

// line 48-59
public function update(int $id, array $data): bool { ... }

// line 63-67
public function delete(int $id): bool { ... }
```

Semua method ini general-purpose. Jadi setiap child class yang extend BaseModel langsung punya kemampuan CRUD tanpa nulis ulang.

**[TAMPILKAN: VS Code - Buka file [classes/AdminModel.php](file:///c:/xampp/htdocs/pemweb/classes/AdminModel.php)]**

Sekarang buka [classes/AdminModel.php](file:///c:/xampp/htdocs/pemweb/classes/AdminModel.php) di line 4:

```php
class AdminModel extends BaseModel
```

[AdminModel](file:///c:/xampp/htdocs/pemweb/classes/AdminModel.php#4-137) ini extend [BaseModel](file:///c:/xampp/htdocs/pemweb/classes/BaseModel.php#3-84). Otomatis dia mewarisi `$db`, `$table`, semua method CRUD tadi. Tapi dia juga nambah method spesifik untuk admin, seperti [getGlobalRevenue()](file:///c:/xampp/htdocs/pemweb/classes/AdminModel.php#10-23), [getUsersPaginated()](file:///c:/xampp/htdocs/pemweb/classes/AdminModel.php#40-48), [toggleProductStatus()](file:///c:/xampp/htdocs/pemweb/classes/AdminModel.php#79-92), dan lain-lain.

**[TAMPILKAN: VS Code - Buka file [classes/Product.php](file:///c:/xampp/htdocs/pemweb/classes/Product.php) line 4-6]**

Hal yang sama berlaku untuk [Product.php](file:///c:/xampp/htdocs/pemweb/classes/Product.php):

```php
class Product extends BaseModel
{
    protected string $table = 'products';
```

Product extend BaseModel, dan men-set `$table = 'products'`. Ini penting, karena method-method BaseModel seperti [findById()](file:///c:/xampp/htdocs/pemweb/classes/BaseModel.php#15-22) atau [create()](file:///c:/xampp/htdocs/pemweb/classes/BaseModel.php#36-45) pakai `$this->table` di query-nya. Jadi Product tinggal override value-nya, semua method CRUD langsung tahu harus query ke tabel `products`.

Ini juga berlaku untuk [User](file:///c:/xampp/htdocs/pemweb/classes/User.php#4-124), [Order](file:///c:/xampp/htdocs/pemweb/classes/Order.php#4-225), [Voucher](file:///c:/xampp/htdocs/pemweb/classes/Voucher.php#4-69), dan [Category](file:///c:/xampp/htdocs/pemweb/classes/Category.php#4-36). Semua extend BaseModel.

---

## BAGIAN 4: ENCAPSULATION (Durasi: ~1 menit)

**[TAMPILKAN: VS Code - Buka file [classes/Product.php](file:///c:/xampp/htdocs/pemweb/classes/Product.php) line 8-18]**

Konsep yang ketiga adalah Encapsulation. Buka [classes/Product.php](file:///c:/xampp/htdocs/pemweb/classes/Product.php) line 8-18:

```php
private ?int $id = null;
private int $sellerId = 0;
private string $name = '';
private string $description = '';
private float $price = 0;
private int $stock = 0;
private int $categoryId = 0;
private string $image = 'default.jpg';
private bool $isActive = true;
private ?float $flashSalePrice = null;
private ?string $flashSaleEnd = null;
```

Semua property ini **private**. Artinya dari luar class Product, tidak ada yang bisa langsung mengubah `$price` atau `$stock`. Ini mencegah manipulasi data yang tidak diinginkan.

**[TAMPILKAN: VS Code - Highlight line 21-31]**

Untuk mengakses data, kita sediakan getter di line 21-31:

```php
public function getId(): ?int { return $this->id; }
public function getSellerId(): int { return $this->sellerId; }
public function getName(): string { return $this->name; }
public function getDescription(): string { return $this->description; }
public function getPrice(): float { return $this->price; }
public function getStock(): int { return $this->stock; }
public function getCategoryId(): int { return $this->categoryId; }
public function getImage(): string { return $this->image; }
public function getIsActive(): bool { return $this->isActive; }
public function getFlashSalePrice(): ?float { return $this->flashSalePrice; }
public function getFlashSaleEnd(): ?string { return $this->flashSaleEnd; }
```

**[TAMPILKAN: VS Code - Highlight line 34-39]**

Dan setter di line 34-39:

```php
public function setName(string $name): void { $this->name = $name; }
public function setDescription(string $desc): void { $this->description = $desc; }
public function setPrice(float $price): void { $this->price = $price; }
public function setStock(int $stock): void { $this->stock = $stock; }
public function setCategoryId(int $id): void { $this->categoryId = $id; }
public function setImage(string $image): void { $this->image = $image; }
```

Perhatikan, tidak ada `setId()`. Kenapa? Karena ID itu auto-increment dari database, tidak boleh diubah manual dari kode.

**[TAMPILKAN: VS Code - Buka file [classes/User.php](file:///c:/xampp/htdocs/pemweb/classes/User.php) line 8-13]**

Pola yang sama ada di [User.php](file:///c:/xampp/htdocs/pemweb/classes/User.php):

```php
private ?int $id = null;
private string $username = '';
private string $email = '';
private string $role = 'buyer';
private ?string $address = null;
private ?string $phone = null;
```

Dan juga class [Order](file:///c:/xampp/htdocs/pemweb/classes/Order.php#4-225), [Voucher](file:///c:/xampp/htdocs/pemweb/classes/Voucher.php#4-69), dan [Category](file:///c:/xampp/htdocs/pemweb/classes/Category.php#4-36) -- semuanya menerapkan encapsulation yang sama.

---

## BAGIAN 5: PDO PREPARED STATEMENTS (Durasi: ~1.5 menit)

**[TAMPILKAN: VS Code - Buka file [classes/BaseModel.php](file:///c:/xampp/htdocs/pemweb/classes/BaseModel.php) line 15-21]**

Sekarang kita buktikan bahwa semua interaksi database pakai PDO Prepared Statements.

Mulai dari [BaseModel.php](file:///c:/xampp/htdocs/pemweb/classes/BaseModel.php) method [findById()](file:///c:/xampp/htdocs/pemweb/classes/BaseModel.php#15-22) di line 15-21:

```php
public function findById(int $id): ?array
{
    $stmt = $this->db->prepare("SELECT * FROM {$this->table} WHERE id = :id LIMIT 1");
    $stmt->execute(['id' => $id]);
    $result = $stmt->fetch();
    return $result ?: null;
}
```

Polanya selalu: `prepare()` dulu, lalu `execute()` dengan parameter array. Parameter `:id` di-bind lewat array, bukan langsung ditempel ke string SQL. Ini yang mencegah SQL Injection.

**[TAMPILKAN: VS Code - [classes/BaseModel.php](file:///c:/xampp/htdocs/pemweb/classes/BaseModel.php) line 36-44]**

Method [create()](file:///c:/xampp/htdocs/pemweb/classes/BaseModel.php#36-45):

```php
public function create(array $data): int
{
    $columns = implode(', ', array_keys($data));
    $placeholders = ':' . implode(', :', array_keys($data));
    $sql = "INSERT INTO {$this->table} ({$columns}) VALUES ({$placeholders})";
    $stmt = $this->db->prepare($sql);
    $stmt->execute($data);
    return (int) $this->db->lastInsertId();
}
```

Dynamic prepared statement. Kolom dan placeholder dibuat otomatis dari key array `$data`, tapi value-nya tetap di-bind lewat `execute($data)`.

**[TAMPILKAN: VS Code - [classes/Product.php](file:///c:/xampp/htdocs/pemweb/classes/Product.php) line 95-112]**

Method [search()](file:///c:/xampp/htdocs/pemweb/classes/Product.php#95-113) di Product:

```php
public function search(string $keyword, int $limit = 20): array
{
    $keyword = '%' . $keyword . '%';
    $stmt = $this->db->prepare(
        "SELECT p.*, c.name as category_name, u.username as seller_name
         FROM {$this->table} p
         JOIN categories c ON p.category_id = c.id
         JOIN users u ON p.seller_id = u.id
         WHERE p.is_active = 1 AND (p.name LIKE :kw OR p.description LIKE :kw2)
         ORDER BY p.created_at DESC
         LIMIT :lim"
    );
    $stmt->bindValue(':kw', $keyword);
    $stmt->bindValue(':kw2', $keyword);
    $stmt->bindValue(':lim', $limit, PDO::PARAM_INT);
    $stmt->execute();
    return $stmt->fetchAll();
}
```

Bahkan untuk LIKE clause yang biasanya rawan injection, keyword di-bind lewat `bindValue()`, bukan concatenation.

**[TAMPILKAN: VS Code - [classes/User.php](file:///c:/xampp/htdocs/pemweb/classes/User.php) line 78-84]**

Method [findByUsername()](file:///c:/xampp/htdocs/pemweb/classes/User.php#78-85):

```php
public function findByUsername(string $username): ?array
{
    $stmt = $this->db->prepare("SELECT * FROM {$this->table} WHERE username = :username LIMIT 1");
    $stmt->execute(['username' => $username]);
    $result = $stmt->fetch();
    return $result ?: null;
}
```

Semua file tanpa terkecuali memakai `prepare()` dan `execute()` dengan binding parameter, tidak ada satupun raw query yang langsung menempelkan input user ke string SQL.

---

## BAGIAN 6: KEAMANAN - CSRF TOKEN dan PASSWORD HASH (Durasi: ~1.5 menit)

**[TAMPILKAN: VS Code - Buka file [config/app.php](file:///c:/xampp/htdocs/pemweb/config/app.php) line 15-38]**

Masuk ke aspek keamanan. Buka [config/app.php](file:///c:/xampp/htdocs/pemweb/config/app.php). Di line 15-21:

```php
function generateCsrfToken(): string
{
    if (empty($_SESSION['csrf_token'])) {
        $_SESSION['csrf_token'] = bin2hex(random_bytes(32));
    }
    return $_SESSION['csrf_token'];
}
```

Fungsi ini generate token random 32 bytes yang dikonversi ke hex. `random_bytes()` ini cryptographically secure, jadi token-nya tidak bisa ditebak.

Di line 23-27, fungsi [csrfField()](file:///c:/xampp/htdocs/pemweb/config/app.php#23-28):

```php
function csrfField(): string
{
    $token = generateCsrfToken();
    return '<input type="hidden" name="csrf_token" value="' . htmlspecialchars($token) . '">';
}
```

Ini yang dipanggil di setiap form POST untuk menyisipkan hidden input berisi token.

Di line 29-38, validasinya:

```php
function validateCsrfToken(): bool
{
    if (empty($_POST['csrf_token']) || empty($_SESSION['csrf_token'])) {
        return false;
    }
    $valid = hash_equals($_SESSION['csrf_token'], $_POST['csrf_token']);
    unset($_SESSION['csrf_token']);
    return $valid;
}
```

Poin penting di sini: pakai `hash_equals()` bukan `===`. Kenapa? Karena `hash_equals()` itu constant-time comparison, tidak rentan timing attack. Dan setelah divalidasi, token langsung di-`unset()`, jadi tidak bisa dipakai dua kali. Ini mencegah CSRF replay attack.

**[TAMPILKAN: VS Code - Buka file [public/login.php](file:///c:/xampp/htdocs/pemweb/public/login.php) line 12-15]**

Buktinya, di [login.php](file:///c:/xampp/htdocs/pemweb/public/login.php):

```php
if ($_SERVER['REQUEST_METHOD'] === 'POST' && isset($_POST['login'])) {
    if (!validateCsrfToken()) {
        $error = 'Invalid request. Please try again.';
    }
```

Setiap form POST pasti dicek CSRF token-nya dulu. Ini ada di login, register, checkout, seller products, seller orders, dan semua halaman admin.

**[TAMPILKAN: VS Code - Buka file [classes/User.php](file:///c:/xampp/htdocs/pemweb/classes/User.php) line 109-113]**

Untuk password, lihat method [register()](file:///c:/xampp/htdocs/pemweb/classes/User.php#109-114) di [User.php](file:///c:/xampp/htdocs/pemweb/classes/User.php) line 109-113:

```php
public function register(array $data): int
{
    $data['password'] = password_hash($data['password'], PASSWORD_BCRYPT);
    return $this->create($data);
}
```

Password di-hash pakai `password_hash()` dengan algoritma BCRYPT sebelum disimpan ke database. Jadi yang tersimpan di database bukan plain text, tapi hash yang tidak bisa di-reverse.

**[TAMPILKAN: VS Code - Buka file [classes/User.php](file:///c:/xampp/htdocs/pemweb/classes/User.php) line 98-105]**

Di method [authenticate()](file:///c:/xampp/htdocs/pemweb/classes/User.php#98-106):

```php
public function authenticate(string $username, string $password): ?array
{
    $user = $this->findByUsername($username);
    if ($user && password_verify($password, $user['password'])) {
        return $user;
    }
    return null;
}
```

Saat login, `password_verify()` membandingkan password plain text yang diketik user dengan hash di database. Ini pasangannya `password_hash()`.

---

## BAGIAN 7: KERANJANG BELANJA BERBASIS SESSION (Durasi: ~2 menit)

**[TAMPILKAN: VS Code - Buka file [classes/Cart.php](file:///c:/xampp/htdocs/pemweb/classes/Cart.php)]**

Sekarang kita bahas keranjang belanja. Buka [classes/Cart.php](file:///c:/xampp/htdocs/pemweb/classes/Cart.php).

**[TAMPILKAN: VS Code - Highlight line 7-12]**

Method [init()](file:///c:/xampp/htdocs/pemweb/classes/Cart.php#7-13) di line 7-12:

```php
public static function init(): void
{
    if (!isset($_SESSION['cart'])) {
        $_SESSION['cart'] = [];
    }
}
```

Cart disimpan di `$_SESSION['cart']` sebagai array. Kalau belum ada, diinisialisasi kosong. Semua method Cart bersifat `static`, jadi dipanggil lewat `Cart::add()`, `Cart::remove()`, dan sebagainya.

**[TAMPILKAN: VS Code - Highlight line 16-37]**

Method [add()](file:///c:/xampp/htdocs/pemweb/classes/Cart.php#16-38) di line 16-37:

```php
public static function add(int $productId, string $name, float $price, int $quantity = 1, string $image = 'default.jpg'): void
{
    self::init();
    foreach ($_SESSION['cart'] as &$item) {
        if ($item['product_id'] === $productId) {
            $item['quantity'] += $quantity;
            return;
        }
    }
    unset($item);
    $_SESSION['cart'][] = [
        'product_id' => $productId,
        'name'       => $name,
        'price'      => $price,
        'quantity'   => $quantity,
        'image'      => $image,
    ];
}
```

Alurnya: pertama cek apakah produk sudah ada di keranjang. Kalau sudah ada, cuma tambah quantity-nya (pakai reference `&$item`). Kalau belum ada, push item baru ke array. Perhatikan `unset($item)` setelah loop -- ini penting untuk menghapus reference agar tidak ada side effect.

**[TAMPILKAN: VS Code - Highlight line 69-99]**

Method [getItems()](file:///c:/xampp/htdocs/pemweb/classes/Cart.php#69-100) di line 69-99:

```php
public static function getItems(): array
{
    self::init();
    require_once BASE_PATH . '/classes/Product.php';
    $productModel = new Product();
    
    $validItems = [];
    foreach ($_SESSION['cart'] as $item) {
        $product = $productModel->findById($item['product_id']);
        if (!$product || !$product['is_active']) {
            continue;
        }
        $price = ($product['flash_sale_price'] && strtotime($product['flash_sale_end']) > time())
            ? (float)$product['flash_sale_price']
            : (float)$product['price'];
        $item['price'] = $price;
        $item['name']  = $product['name'];
        $item['image'] = $product['image'];
        $validItems[] = $item;
    }
    $_SESSION['cart'] = $validItems;
    return $_SESSION['cart'];
}
```

Yang menarik di sini: setiap kali [getItems()](file:///c:/xampp/htdocs/pemweb/classes/Cart.php#69-100) dipanggil, dia revalidasi semua item. Dia cek ke database apakah produk masih aktif. Kalau produk sudah dihapus atau di-nonaktifkan, otomatis dibuang dari session. Harga juga diperbarui, termasuk kalau ada flash sale.

**[TAMPILKAN: Browser - Demo tambah produk ke cart, lihat cart, ubah quantity, hapus item]**

Sekarang saya tunjukkan langsung di browser. Saya buka halaman produk, klik Add to Bag, maka di belakang layar itu memanggil `Cart::add()` lewat AJAX. Counter cart di header berubah. Kalau saya buka halaman cart, bisa ubah quantity atau hapus item.

---

## BAGIAN 8: UPLOAD BUKTI PEMBAYARAN (Durasi: ~1.5 menit)

**[TAMPILKAN: VS Code - Buka file [public/checkout.php](file:///c:/xampp/htdocs/pemweb/public/checkout.php) line 40-69]**

Sekarang kita bahas upload bukti pembayaran. Buka [public/checkout.php](file:///c:/xampp/htdocs/pemweb/public/checkout.php) line 40-69:

```php
$paymentProofFile = null;
if (isset($_FILES['payment_proof']) && $_FILES['payment_proof']['error'] === UPLOAD_ERR_OK) {
    $file = $_FILES['payment_proof'];
    
    $allowedTypes = ['image/jpeg', 'image/png'];
    $finfo = finfo_open(FILEINFO_MIME_TYPE);
    $mimeType = finfo_file($finfo, $file['tmp_name']);
    finfo_close($finfo);

    if (!in_array($mimeType, $allowedTypes)) {
        setFlash('error', 'Payment proof must be JPG or PNG.');
        redirect('/checkout.php');
    }

    if ($file['size'] > 2 * 1024 * 1024) {
        setFlash('error', 'Payment proof must be under 2MB.');
        redirect('/checkout.php');
    }

    $ext = $mimeType === 'image/png' ? 'png' : 'jpg';
    $paymentProofFile = 'proof_' . time() . '_' . uniqid() . '.' . $ext;
    
    if (!is_dir(UPLOAD_PATH)) {
        mkdir(UPLOAD_PATH, 0755, true);
    }
    move_uploaded_file($file['tmp_name'], UPLOAD_PATH . $paymentProofFile);
}
```

Ada tiga validasi di sini:

1. **Validasi MIME Type** -- Pakai `finfo_file()` untuk cek MIME type asli file, bukan dari ekstensi. Jadi kalau ada orang rename file `.exe` jadi `.jpg`, tetap ketahuan. Hanya `image/jpeg` dan `image/png` yang diizinkan.

2. **Validasi ukuran file** -- Line 56: `$file['size'] > 2 * 1024 * 1024` artinya maksimal 2MB.

3. **Nama file unik** -- Line 63: `'proof_' . time() . '_' . uniqid() . '.' . $ext`. Tidak pakai nama file asli dari user untuk mencegah path traversal attack.

**[TAMPILKAN: Browser - Demo checkout dan upload file bukti pembayaran]**

Saya tunjukkan: saat checkout ada form upload bukti pembayaran, pilih file gambar, kalau formatnya bener dan ukurannya di bawah 2MB, berhasil diupload.

---

## BAGIAN 9: VOUCHER/DISKON (Durasi: ~1.5 menit)

**[TAMPILKAN: VS Code - Buka file [classes/Voucher.php](file:///c:/xampp/htdocs/pemweb/classes/Voucher.php)]**

Masuk ke sistem voucher diskon. Buka [classes/Voucher.php](file:///c:/xampp/htdocs/pemweb/classes/Voucher.php).

**[TAMPILKAN: VS Code - Highlight line 26-32]**

Method [findByCode()](file:///c:/xampp/htdocs/pemweb/classes/Voucher.php#26-33) line 26-32:

```php
public function findByCode(string $code): ?array
{
    $stmt = $this->db->prepare("SELECT * FROM {$this->table} WHERE code = :code LIMIT 1");
    $stmt->execute(['code' => strtoupper($code)]);
    $result = $stmt->fetch();
    return $result ?: null;
}
```

Kode voucher dikonversi ke uppercase dulu pakai `strtoupper()`. Jadi user ketik "aksara10" atau "AKSARA10" hasilnya sama.

**[TAMPILKAN: VS Code - Highlight line 36-56]**

Method [isValid()](file:///c:/xampp/htdocs/pemweb/classes/Voucher.php#36-57) line 36-56:

```php
public function isValid(string $code): array
{
    $voucher = $this->findByCode($code);

    if (!$voucher) {
        return ['valid' => false, 'message' => 'Voucher code not found.'];
    }
    if (strtotime($voucher['expired_at']) < time()) {
        return ['valid' => false, 'message' => 'Voucher has expired.'];
    }
    if ($voucher['used_count'] >= $voucher['max_use']) {
        return ['valid' => false, 'message' => 'Voucher usage limit reached.'];
    }

    return [
        'valid'            => true,
        'message'          => 'Voucher applied!',
        'discount_percent' => $voucher['discount_percent'],
        'voucher_id'       => $voucher['id'],
    ];
}
```

Ada tiga lapisan validasi: voucher harus ada di database, belum expired, dan `used_count` belum melebihi `max_use`.

**[TAMPILKAN: VS Code - Buka file [public/checkout.php](file:///c:/xampp/htdocs/pemweb/public/checkout.php) line 11-26]**

Di file checkout, voucher diapply via AJAX:

```php
if ($_SERVER['REQUEST_METHOD'] === 'POST' && ($_POST['action'] ?? '') === 'apply_voucher') {
    header('Content-Type: application/json');
    $code = strtoupper(trim($_POST['voucher_code'] ?? ''));
    
    $voucherModel = new Voucher();
    $result = $voucherModel->isValid($code);
    
    if ($result['valid']) {
        $_SESSION['voucher_code']     = $code;
        $_SESSION['voucher_discount'] = $result['discount_percent'];
        $_SESSION['voucher_id']       = $result['voucher_id'];
    }
    
    echo json_encode($result);
    exit;
}
```

Kalau valid, diskon disimpan di session. Nanti saat order di-submit, diskonnya dihitung:

**[TAMPILKAN: VS Code - Highlight line 77-81]**

```php
if (isset($_SESSION['voucher_discount'])) {
    $discount = round($subtotal * $_SESSION['voucher_discount'] / 100);
}
$totalAmount = $subtotal + $shipping + $tax - $discount;
```

Dan setelah order berhasil, penggunaan voucher dicatat:

**[TAMPILKAN: VS Code - Highlight line 104-107]**

```php
if (isset($_SESSION['voucher_id'])) {
    $voucherModel = new Voucher();
    $voucherModel->incrementUsage($_SESSION['voucher_id']);
}
```

**[TAMPILKAN: VS Code - [classes/Voucher.php](file:///c:/xampp/htdocs/pemweb/classes/Voucher.php) line 60-67]**

```php
public function incrementUsage(int $id): bool
{
    $stmt = $this->db->prepare(
        "UPDATE {$this->table} SET used_count = used_count + 1 WHERE id = :id AND used_count < max_use"
    );
    $stmt->execute(['id' => $id]);
    return $stmt->rowCount() > 0;
}
```

Increment `used_count` tapi dengan kondisi `used_count < max_use`, jadi walaupun ada race condition, voucher tidak akan over-used.

**[TAMPILKAN: Browser - Demo ketik kode voucher di checkout]**

Saya tunjukkan di browser: masukkan kode voucher AKSARA10, klik apply, harga langsung terpotong.

---

## BAGIAN 10: ASCII CHART di Dashboard Seller (Durasi: ~1.5 menit)

**[TAMPILKAN: VS Code - Buka file [public/seller_dashboard.php](file:///c:/xampp/htdocs/pemweb/public/seller_dashboard.php) line 22-67]**

Sekarang fitur yang cukup unik: ASCII Chart di Dashboard Seller. Buka [public/seller_dashboard.php](file:///c:/xampp/htdocs/pemweb/public/seller_dashboard.php).

**[TAMPILKAN: VS Code - Highlight line 22-33]**

Pertama, data penjualan diambil 7 hari terakhir:

```php
function generateAsciiChart(array $salesData): string
{
    $days = [];
    for ($i = 6; $i >= 0; $i--) {
        $date = date('Y-m-d', strtotime("-$i days"));
        $days[$date] = 0;
    }
    foreach ($salesData as $row) {
        if (isset($days[$row['sale_date']])) {
            $days[$row['sale_date']] = (float)$row['daily_total'];
        }
    }
```

Array `$days` diinisialisasi dengan 7 tanggal terakhir, semua bernilai 0. Lalu diisi dari data database.

**[TAMPILKAN: VS Code - Highlight line 35-49]**

```php
    $maxVal = max(1, max($days));
    $chartHeight = 10;
    $chart = '';
    $chart .= "\n";

    for ($row = $chartHeight; $row >= 1; $row--) {
        $threshold = ($row / $chartHeight) * $maxVal;
        $label = ($row === $chartHeight) ? formatShort($maxVal) : ...;
        $chart .= str_pad($label, 8, ' ', STR_PAD_LEFT) . ' |';
        foreach ($days as $val) {
            $chart .= $val >= $threshold ? '  ##  ' : '      ';
        }
        $chart .= "\n";
    }
```

Ini pure PHP, tidak pakai library chart apapun. Logikanya: tentuin nilai maksimum, lalu dari baris paling atas ke bawah, kalau nilai hari itu lebih besar atau sama dengan threshold baris tersebut, cetak blok (unicode block character), kalau tidak, cetak spasi.

**[TAMPILKAN: VS Code - Highlight line 69-74]**

Fungsi pembantu [formatShort()](file:///c:/xampp/htdocs/pemweb/public/seller_dashboard.php#69-75):

```php
function formatShort(float $val): string
{
    if ($val >= 1000000) return round($val / 1000000, 1) . 'M';
    if ($val >= 1000) return round($val / 1000, 0) . 'K';
    return (string)(int)$val;
}
```

Supaya label Y-axis tidak terlalu panjang: 1 juta jadi "1M", 500 ribu jadi "500K".

**[TAMPILKAN: Browser - Buka seller dashboard, tunjukkan ASCII chart]**

Ini hasilnya di browser. Grafik batang sederhana tapi informatif, menampilkan tren penjualan 7 hari terakhir, total revenue, rata-rata harian, dan peak day. Semuanya murni server-side rendering PHP, tanpa JavaScript chart library.

---

## BAGIAN 11: PENCARIAN, FILTER KATEGORI, dan SORTING (Durasi: ~1.5 menit)

**[TAMPILKAN: VS Code - Buka file [public/products.php](file:///c:/xampp/htdocs/pemweb/public/products.php)]**

Sekarang fitur pencarian dan filter. Buka [public/products.php](file:///c:/xampp/htdocs/pemweb/public/products.php).

**[TAMPILKAN: VS Code - Highlight line 10-28]**

```php
$currentCategory = isset($_GET['category']) ? (int)$_GET['category'] : null;
$searchQuery = isset($_GET['q']) ? trim($_GET['q']) : '';
$minPrice = isset($_GET['min_price']) && $_GET['min_price'] !== '' ? (float)$_GET['min_price'] : null;
$maxPrice = isset($_GET['max_price']) && $_GET['max_price'] !== '' ? (float)$_GET['max_price'] : null;
$sort = isset($_GET['sort']) ? $_GET['sort'] : 'newest';

$page = isset($_GET['page']) && (int)$_GET['page'] > 0 ? (int)$_GET['page'] : 1;
$limit = 12;
$offset = ($page - 1) * $limit;

$filters = [
    'keyword'   => $searchQuery,
    'category'  => $currentCategory,
    'min_price' => $minPrice,
    'max_price' => $maxPrice,
    'sort'      => $sort
];
```

Semua filter dibaca dari query string GET dan dikumpulkan jadi satu array `$filters`.

**[TAMPILKAN: VS Code - Buka file [classes/Product.php](file:///c:/xampp/htdocs/pemweb/classes/Product.php) line 116-171]**

Filter ini diteruskan ke method [searchAdvanced()](file:///c:/xampp/htdocs/pemweb/classes/Product.php#116-172) di [Product.php](file:///c:/xampp/htdocs/pemweb/classes/Product.php):

```php
public function searchAdvanced(array $filters, int $limit = 12, int $offset = 0): array
{
    $sql = "SELECT p.*, c.name as category_name, u.username as seller_name
            FROM {$this->table} p
            JOIN categories c ON p.category_id = c.id
            JOIN users u ON p.seller_id = u.id
            WHERE p.is_active = 1";
    
    $params = [];
    
    if (!empty($filters['keyword'])) {
        $sql .= " AND (p.name LIKE :kw1 OR p.description LIKE :kw2)";
        $params['kw1'] = '%' . $filters['keyword'] . '%';
        $params['kw2'] = '%' . $filters['keyword'] . '%';
    }
    if (!empty($filters['category'])) {
        $sql .= " AND p.category_id = :cat_id";
        $params['cat_id'] = $filters['category'];
    }
    if (!empty($filters['min_price'])) {
        $sql .= " AND (COALESCE(...) >= :min_price)";
        $params['min_price'] = $filters['min_price'];
    }
    ...
```

Query-nya dibangun secara dynamic: setiap filter yang tidak kosong ditambahkan sebagai WHERE condition. Tapi semua value tetap di-bind lewat prepared statement, bukan concatenation.

Untuk sorting, ada tiga opsi: `newest` (berdasarkan tanggal), `price_asc` (harga termurah), dan `price_desc` (harga termahal). Sorting juga memperhitungkan flash sale price lewat COALESCE.

**[TAMPILKAN: Browser - Demo search "batik", filter kategori, sorting harga]**

Saya tunjukkan di browser: ketik "batik" di search, hasilnya terfilter. Klik kategori Tekstil, hasilnya berubah. Ubah sorting ke "Price: Low to High", urutannya berubah sesuai harga.

---

## BAGIAN 12: PAGINATION (Durasi: ~1 menit)

**[TAMPILKAN: VS Code - Buka file [public/products.php](file:///c:/xampp/htdocs/pemweb/public/products.php) line 16-18, 30-33]**

Pagination ada di halaman produk. Line 16-18:

```php
$page = isset($_GET['page']) && (int)$_GET['page'] > 0 ? (int)$_GET['page'] : 1;
$limit = 12;
$offset = ($page - 1) * $limit;
```

Limit 12 produk per halaman. Offset dihitung dari nomor halaman.

Line 30-33:

```php
$totalData = $productModel->countAdvanced($filters);
$totalPages = ceil($totalData / $limit);
if ($totalPages < 1) $totalPages = 1;
$products = $productModel->searchAdvanced($filters, $limit, $offset);
```

Total data dihitung dulu oleh [countAdvanced()](file:///c:/xampp/htdocs/pemweb/classes/Product.php#175-209) untuk menentukan jumlah halaman. Lalu data halaman saat ini diambil oleh [searchAdvanced()](file:///c:/xampp/htdocs/pemweb/classes/Product.php#116-172) dengan LIMIT dan OFFSET.

**[TAMPILKAN: VS Code - Buka file [public/admin_users.php](file:///c:/xampp/htdocs/pemweb/public/admin_users.php) line 46-53]**

Pagination juga ada di admin panel, misalnya di [admin_users.php](file:///c:/xampp/htdocs/pemweb/public/admin_users.php):

```php
$limit = 10;
$page = max(1, (int)($_GET['page'] ?? 1));
$offset = ($page - 1) * $limit;

$totalUsers = $adminModel->countEntity('users');
$totalPages = ceil($totalUsers / $limit);

$users = $adminModel->getUsersPaginated($limit, $offset);
```

Ini limit 10 per halaman, sudah memenuhi syarat minimum 10 data per halaman yang diminta di soal.

**[TAMPILKAN: Browser - Scroll ke bawah halaman produk, tunjukkan navigasi pagination]**

Di browser, kalau datanya lebih dari 12, navigasi halaman muncul di bawah. Tombol Previous, nomor halaman, dan Next.

---

## BAGIAN 13: PENGHAPUSAN DATA VIA POST (Durasi: ~1 menit)

**[TAMPILKAN: VS Code - Buka file [public/seller_products.php](file:///c:/xampp/htdocs/pemweb/public/seller_products.php) line 92-97]**

Sesuai syarat soal, penghapusan data harus lewat method POST, bukan GET. Di [seller_products.php](file:///c:/xampp/htdocs/pemweb/public/seller_products.php):

```php
case 'delete':
    $productId = (int)($_POST['product_id'] ?? 0);
    $productModel->deleteProduct($productId, $sellerId);
    setFlash('success', 'Product deleted.');
    redirect('/seller_products.php');
    break;
```

Data product_id dikirim lewat POST dan CSRF token divalidasi di line 14.

**[TAMPILKAN: VS Code - Buka file [public/admin_users.php](file:///c:/xampp/htdocs/pemweb/public/admin_users.php) line 27-34]**

Sama halnya di admin delete user:

```php
if ($action === 'delete') {
    try {
        $adminModel->deleteUser($userId);
        setFlash('success', 'User completely deleted from system.');
    } catch (PDOException $e) {
        setFlash('error', 'Cannot delete user because they have existing orders or products.');
    }
}
```

Penghapusan user juga lewat POST, plus ada error handling kalau user punya relasi ke order atau produk.

---

## BAGIAN 14: FITUR BONUS - PDO TRANSACTION dan SELECT FOR UPDATE (Durasi: ~2 menit)

**[TAMPILKAN: VS Code - Buka file [classes/Order.php](file:///c:/xampp/htdocs/pemweb/classes/Order.php) line 29-80]**

Sekarang masuk ke fitur bonus yang cukup advanced. Buka [classes/Order.php](file:///c:/xampp/htdocs/pemweb/classes/Order.php), method [createWithItems()](file:///c:/xampp/htdocs/pemweb/classes/Order.php#29-81):

```php
public function createWithItems(int $buyerId, array $cartItems, float $totalAmount, ?string $paymentProof = null): int
{
    $this->db->beginTransaction();

    try {
        $productModel = new Product();
        foreach ($cartItems as $item) {
            $product = $productModel->lockForUpdate($item['product_id']);
            if (!$product) {
                throw new Exception("Product #{$item['product_id']} not found.");
            }
            if ($product['stock'] < $item['quantity']) {
                throw new Exception("Insufficient stock for '{$product['name']}'. Available: {$product['stock']}");
            }
        }

        $orderId = $this->create([
            'buyer_id'       => $buyerId,
            'total_amount'   => $totalAmount,
            'status'         => 'pending',
            'payment_method' => 'bank_transfer',
            'payment_proof'  => $paymentProof,
        ]);

        foreach ($cartItems as $item) {
            $stmtItem = $this->db->prepare(
                "INSERT INTO order_items (order_id, product_id, quantity, unit_price)
                 VALUES (:oid, :pid, :qty, :price)"
            );
            $stmtItem->execute([...]);

            if (!$productModel->decrementStock($item['product_id'], $item['quantity'])) {
                throw new Exception("Failed to update stock for product #{$item['product_id']}.");
            }
        }

        $this->db->commit();
        return $orderId;

    } catch (Exception $e) {
        $this->db->rollBack();
        throw $e;
    }
}
```

Ini transaction lengkap. Alurnya:

1. `beginTransaction()` -- mulai transaksi, semua operasi di dalam try block akan atomic.
2. Loop semua item cart, untuk setiap item panggil [lockForUpdate()](file:///c:/xampp/htdocs/pemweb/classes/Product.php#212-221).
3. Cek stok, kalau kurang langsung throw exception.
4. Insert order ke tabel `orders`.
5. Insert setiap item ke tabel `order_items`.
6. Kurangi stok produk lewat [decrementStock()](file:///c:/xampp/htdocs/pemweb/classes/Product.php#224-232).
7. Kalau semua sukses, `commit()`.
8. Kalau ada yang gagal di mana pun, masuk `catch`, lalu `rollBack()` -- semua perubahan dibatalkan.

**[TAMPILKAN: VS Code - Buka file [classes/Product.php](file:///c:/xampp/htdocs/pemweb/classes/Product.php) line 212-220]**

Sekarang lihat [lockForUpdate()](file:///c:/xampp/htdocs/pemweb/classes/Product.php#212-221) di Product:

```php
public function lockForUpdate(int $id): ?array
{
    $stmt = $this->db->prepare(
        "SELECT * FROM {$this->table} WHERE id = :id FOR UPDATE"
    );
    $stmt->execute(['id' => $id]);
    $result = $stmt->fetch();
    return $result ?: null;
}
```

`SELECT ... FOR UPDATE` ini row-level lock di MySQL. Artinya, kalau dua orang checkout produk yang sama secara bersamaan, yang kedua harus menunggu sampai transaksi pertama selesai. Ini mencegah keadaan balapan (race condition) di mana stok bisa jadi negatif.

**[TAMPILKAN: VS Code - Highlight line 224-231]**

```php
public function decrementStock(int $id, int $qty): bool
{
    $stmt = $this->db->prepare(
        "UPDATE {$this->table} SET stock = stock - :qty WHERE id = :id AND stock >= :qty2"
    );
    $stmt->execute(['qty' => $qty, 'id' => $id, 'qty2' => $qty]);
    return $stmt->rowCount() > 0;
}
```

Pengurangan stok juga ada kondisi `stock >= :qty2`. Jadi walaupun lolos lock, kalau stok sudah habis, update-nya tidak jalan, dan `rowCount()` return 0, yang artinya gagal.

---

## BAGIAN 15: FITUR BONUS - FLASH SALE dan COUNTDOWN TIMER (Durasi: ~1.5 menit)

**[TAMPILKAN: VS Code - Buka file [public/admin_flash_sale.php](file:///c:/xampp/htdocs/pemweb/public/admin_flash_sale.php) line 20-49]**

Flash Sale diset oleh Admin. Di [admin_flash_sale.php](file:///c:/xampp/htdocs/pemweb/public/admin_flash_sale.php):

```php
case 'set_flash_sale':
    $productId  = (int)($_POST['product_id'] ?? 0);
    $flashPrice = abs((float)($_POST['flash_sale_price'] ?? 0));
    $flashEnd   = trim($_POST['flash_sale_end'] ?? '');

    if ($productId <= 0 || $flashPrice <= 0 || empty($flashEnd)) {
        setFlash('error', 'Please fill in all flash sale fields.');
        redirect('/admin_flash_sale.php');
        exit;
    }

    $endTimestamp = strtotime($flashEnd);
    if (!$endTimestamp || $endTimestamp <= time()) {
        setFlash('error', 'Flash sale end date must be in the future.');
        redirect('/admin_flash_sale.php');
        exit;
    }

    $endDate = date('Y-m-d H:i:s', $endTimestamp);
    $result = $adminModel->setFlashSaleAdmin($productId, $flashPrice, $endDate);
```

Ada validasi: harga flash sale harus diisi, tanggal berakhir harus di masa depan.

**[TAMPILKAN: VS Code - Buka file [classes/AdminModel.php](file:///c:/xampp/htdocs/pemweb/classes/AdminModel.php) line 110-127]**

Di [AdminModel](file:///c:/xampp/htdocs/pemweb/classes/AdminModel.php#4-137):

```php
public function setFlashSaleAdmin(int $productId, float $price, string $endDate): bool
{
    $stmt = $this->db->prepare("SELECT price FROM products WHERE id = :id");
    $stmt->execute(['id' => $productId]);
    $normalPrice = $stmt->fetchColumn();
    
    if ($normalPrice === false || $price >= $normalPrice) {
        return false;
    }
    
    $update = $this->db->prepare("UPDATE products SET flash_sale_price = :fprice, flash_sale_end = :fend WHERE id = :id");
    return $update->execute([...]);
}
```

Harga flash sale harus **lebih murah** dari harga normal. Kalau lebih tinggi atau sama, return false.

**[TAMPILKAN: VS Code - Buka file [views/product_detail.php](file:///c:/xampp/htdocs/pemweb/views/product_detail.php) line 136-156]**

Countdown timer ada di [views/product_detail.php](file:///c:/xampp/htdocs/pemweb/views/product_detail.php):

```javascript
(function() {
    const el = document.getElementById('countdown');
    if (!el) return;
    const endStr = el.dataset.end;
    const endTime = new Date(endStr.replace(' ', 'T') + '+07:00').getTime();

    function tick() {
        const now = Date.now();
        let diff = Math.max(0, Math.floor((endTime - now) / 1000));

        document.getElementById('cd-hours').textContent = String(Math.floor(diff / 3600)).padStart(2, '0');
        document.getElementById('cd-minutes').textContent = String(Math.floor((diff % 3600) / 60)).padStart(2, '0');
        document.getElementById('cd-seconds').textContent = String(diff % 60).padStart(2, '0');

        if (diff > 0) setTimeout(tick, 1000);
    }
    tick();
})();
```

Waktu berakhir flash sale di-pass dari PHP ke HTML lewat `data-end` attribute. JavaScript membacanya, mengkonversi ke timestamp, lalu setiap detik menghitung selisih waktu dan update tampilan. Pakai `+07:00` untuk timezone WIB, jadi akurat.

**[TAMPILKAN: Browser - Buka produk yang sedang Flash Sale, tunjukkan countdown berjalan]**

Di browser, saat buka produk yang sedang flash sale, countdown timer berjalan secara real-time: jam, menit, detik, mundur terus sampai flash sale selesai.

---

## BAGIAN 16: CLOSING (Durasi: ~30 detik)

**[TAMPILKAN: Browser - Halaman Home Aksara Lokal]**

Oke, jadi itu tadi penjelasan lengkap proyek Aksara Lokal saya. Untuk merangkum:

- **OOP Concepts**: Singleton Pattern di koneksi database, Inheritance dari BaseModel ke semua child class, dan Encapsulation dengan private property plus getter/setter.
- **Keamanan**: CSRF Token di semua form POST, password hashing dengan BCRYPT, dan semua query memakai PDO Prepared Statements.
- **Fitur Utama**: Keranjang berbasis session, upload bukti pembayaran dengan validasi file, voucher diskon dari database, ASCII chart tanpa library, pencarian, filter kategori, sorting, dan pagination.
- **Fitur Bonus**: PDO Transaction dengan SELECT FOR UPDATE untuk mencegah race condition saat checkout, dan sistem Flash Sale dengan countdown timer JavaScript.

Terima kasih Pak Irham, Wassalamualaikum warahmatullahi wabarakatuh.
