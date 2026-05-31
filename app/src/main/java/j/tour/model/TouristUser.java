package j.tour.model;

import java.util.ArrayList;
import java.util.List;

public class TouristUser extends User {
    // Variabel spesifik milik TouristUser
    private List<TripPlan> savedTrips;
    private String preferences;

    /**
     * Constructor untuk membuat objek TouristUser baru.
     * Memanggil constructor superclass (User) untuk mengatur data akun dasar.
     */
    public TouristUser(String userId, String username, String passwordHash) {
        super(userId, username, passwordHash); // Mengoper data ke class User
        this.savedTrips = new ArrayList<>(); // Inisialisasi list kosong
    }

    // =========================================================================
    // GETTER DAN SETTER STANDARD
    // (Berfungsi membaca & menulis variabel agar linter VS Code tidak kuning lagi)
    // =========================================================================

    /**
     * Mengambil daftar rencana perjalanan yang telah disimpan oleh turis.
     * @return List objek TripPlan
     */
    public List<TripPlan> getSavedTrips() {
        return savedTrips;
    }

    /**
     * Mengatur atau memperbarui daftar rencana perjalanan turis.
     * @param savedTrips List TripPlan yang baru
     */
    public void setSavedTrips(List<TripPlan> savedTrips) {
        this.savedTrips = savedTrips;
    }

    /**
     * Mengambil preferensi wisata yang dipilih oleh turis (misal: "Alam", "Budaya").
     * @return String preferensi wisata
     */
    public String getPreferences() {
        return preferences;
    }

    /**
     * Mengatur preferensi wisata baru untuk mencocokkan rekomendasi tempat.
     * @param preferences Preferensi destinasi baru
     */
    public void setPreferences(String preferences) {
        this.preferences = preferences;
    }
}