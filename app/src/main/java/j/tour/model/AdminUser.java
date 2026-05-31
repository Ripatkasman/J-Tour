package j.tour.model;

import java.util.ArrayList;
import java.util.List;

public class AdminUser extends User {
    // Variabel spesifik milik AdminUser
    private int adminLevel;
    private List<Destination> managedItems;

    /**
     * Constructor untuk membuat objek AdminUser baru.
     * Memanggil constructor superclass (User) untuk mengatur akun dasar.
     */
    public AdminUser(String userId, String username, String passwordHash) {
        super(userId, username, passwordHash); // Mengoper data ke kelas User
        this.managedItems = new ArrayList<>(); // Inisialisasi list kosong
    }

    // =========================================================================
    // GETTER DAN SETTER STANDARD
    // (Membaca & menulis variabel agar linter VS Code tidak kuning lagi)
    // =========================================================================

    /**
     * Mengambil level otoritas admin (misal: Level 1 untuk super admin).
     * @return int level admin
     */
    public int getAdminLevel() {
        return adminLevel;
    }

    /**
     * Mengatur level otoritas baru bagi admin ini.
     * @param adminLevel Level tingkatan admin yang baru
     */
    public void setAdminLevel(int adminLevel) {
        this.adminLevel = adminLevel;
    }

    /**
     * Mengambil daftar destinasi wisata yang dikelola oleh admin ini.
     * @return List objek Destination
     */
    public List<Destination> getManagedItems() {
        return managedItems;
    }

    /**
     * Menetapkan atau memperbarui daftar destinasi yang diurus oleh admin.
     * @param managedItems List destinasi baru
     */
    public void setManagedItems(List<Destination> managedItems) {
        this.managedItems = managedItems;
    }
}