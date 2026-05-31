package j.tour.model;

public class Category {
    // Variabel/Fields utama untuk kategori destinasi
    private String categoryId;
    private String name;
    private String icon;

    /**
     * Constructor untuk membuat objek Kategori baru.
     * @param categoryId ID unik kategori (misal: "CAT001")
     * @param name Nama kategori (misal: "Budaya", "Alam")
     * @param icon Path atau kode icon (misal: "🏛️", "🌲")
     */
    public Category(String categoryId, String name, String icon) {
        this.categoryId = categoryId; //
        this.name = name;             //
        this.icon = icon;             //
    }

    // =========================================================================
    // GETTER DAN SETTER LENGKAP
    // (Membaca & menulis variabel agar linter VS Code tidak kuning lagi)
    // =========================================================================

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() { 
        return name; //
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}