package j.tour.view;

import j.tour.controller.database.DataStore;
import j.tour.model.Destination;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class AdminDashboardView {

    private VBox containerDestinasi = new VBox(15);
    private TextField txtNama = new TextField();
    private ComboBox<String> cbKategori = new ComboBox<>();
    private ComboBox<String> cbWilayah = new ComboBox<>();
    private TextArea txtDeskripsi = new TextArea();
    private Button btnSubmit = new Button("Tambah Ke Katalog");
    private Button btnBatalEdit = new Button("Batal Edit");
    
    private Destination selectedDestinationForEdit = null;

    public void show(Stage stage) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f4f6f9; -fx-font-family: 'Helvetica', 'Arial';");

        HBox topBar = new HBox();
        topBar.setPadding(new Insets(15, 25, 15, 25));
        topBar.setStyle("-fx-background-color: #0d47a1;");
        topBar.setAlignment(Pos.CENTER_LEFT);

        Label lblTitle = new Label("PANEL MANAJEMEN ADMIN J-TOUR");
        lblTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");
        
        Region topSpacer = new Region();
        HBox.setHgrow(topSpacer, Priority.ALWAYS);

        Button btnLogout = new Button("Logout");
        btnLogout.setStyle("-fx-background-color: #d32f2f; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        btnLogout.setOnAction(e -> new LoginView().show(stage));

        topBar.getChildren().addAll(lblTitle, topSpacer, btnLogout);
        root.setTop(topBar);

        VBox formSidebar = new VBox(15);
        formSidebar.setPadding(new Insets(25, 20, 25, 20));
        formSidebar.setStyle("-fx-background-color: #fffde7; -fx-border-color: #cfd8dc; -fx-border-width: 0 1px 0 0;");
        formSidebar.setPrefWidth(280);
        formSidebar.setMinWidth(280);
        formSidebar.setMaxWidth(280);

        Label lblFormHeader = new Label("Form Kelola Destinasi");
        lblFormHeader.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #0d47a1;");

        txtNama.setPromptText("Nama Destinasi");
        cbKategori.getItems().addAll("Budaya", "Alam", "Kuliner", "Belanja", "Hiburan");
        cbKategori.setPromptText("Pilih Kategori");
        cbKategori.setMaxWidth(Double.MAX_VALUE);
        cbKategori.setEditable(true);

        cbWilayah.getItems().addAll("Kota Jogja", "Sleman", "Bantul", "Kulon Progo", "Gunungkidul");
        cbWilayah.setPromptText("Pilih Wilayah");
        cbWilayah.setMaxWidth(Double.MAX_VALUE);
        cbWilayah.setEditable(true);

        txtDeskripsi.setPromptText("Deskripsi...");
        txtDeskripsi.setPrefHeight(120);
        txtDeskripsi.setWrapText(true);

        btnSubmit.setMaxWidth(Double.MAX_VALUE);
        btnSubmit.setStyle("-fx-background-color: #0d47a1; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10px; -fx-background-radius: 5px; -fx-cursor: hand;");
        
        btnBatalEdit.setMaxWidth(Double.MAX_VALUE);
        btnBatalEdit.setStyle("-fx-background-color: #78909c; -fx-text-fill: white; -fx-cursor: hand;");
        btnBatalEdit.setVisible(false);

        formSidebar.getChildren().addAll(lblFormHeader, new Separator(), txtNama, cbKategori, cbWilayah, txtDeskripsi, btnSubmit, btnBatalEdit);
        root.setLeft(formSidebar);

        VBox centerPanel = new VBox(15);
        centerPanel.setPadding(new Insets(25));
        
        Label lblDaftarHeader = new Label("Daftar Destinasi di Memori Sistem");
        lblDaftarHeader.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #37474f;");

        ScrollPane scrollCenter = new ScrollPane(containerDestinasi);
        scrollCenter.setFitToWidth(true);
        scrollCenter.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        VBox.setVgrow(scrollCenter, Priority.ALWAYS);

        centerPanel.getChildren().addAll(lblDaftarHeader, scrollCenter);
        root.setCenter(centerPanel);

        btnSubmit.setOnAction(e -> {
            String nama = txtNama.getText().trim();
            String kategori = cbKategori.getValue();
            String wilayah = cbWilayah.getValue();
            String deskripsi = txtDeskripsi.getText().trim();

            if (nama.isEmpty() || kategori == null || wilayah == null || deskripsi.isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "Semua data form wajib diisi!").showAndWait();
                return;
            }

            if (selectedDestinationForEdit == null) {
                int nextIdNum = 1;
                var currentList = DataStore.getInstance().getAllDestinations();
                if (!currentList.isEmpty()) {
                    String lastId = currentList.get(currentList.size() - 1).getDestinationId();
                    try {
                        nextIdNum = Integer.parseInt(lastId.replaceAll("[^0-9]", "")) + 1;
                    } catch (NumberFormatException ex) {
                        nextIdNum = currentList.size() + 1;
                    }
                }
                String customId = String.format("DEST%03d", nextIdNum);

                Destination newDest = new Destination(customId, nama, kategori, wilayah, deskripsi);
                DataStore.getInstance().getAllDestinations().add(newDest);
                DataStore.getInstance().saveDestinationsToFile();
                
                new Alert(Alert.AlertType.INFORMATION, "Destinasi Berhasil Ditambahkan!").showAndWait();
            } else {
                selectedDestinationForEdit.setName(nama);
                selectedDestinationForEdit.setCategory(kategori);
                selectedDestinationForEdit.setLocation(wilayah);
                selectedDestinationForEdit.setDescription(deskripsi);

                DataStore.getInstance().saveDestinationsToFile();

                selectedDestinationForEdit = null;
                btnSubmit.setText("Tambah Ke Katalog");
                btnSubmit.setStyle("-fx-background-color: #0d47a1; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10px;");
                btnBatalEdit.setVisible(false);

                new Alert(Alert.AlertType.INFORMATION, "Destinasi Berhasil Diperbarui!").showAndWait();
            }

            clearFormFields();
            refreshDestinationList();
        });

        btnBatalEdit.setOnAction(e -> {
            selectedDestinationForEdit = null;
            btnSubmit.setText("Tambah Ke Katalog");
            btnSubmit.setStyle("-fx-background-color: #0d47a1; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10px;");
            btnBatalEdit.setVisible(false);
            clearFormFields();
            refreshDestinationList();
        });

        refreshDestinationList();

        stage.setTitle("J-Tour - Admin Console");

        // Perbaikan Utama: Gunakan setRoot() untuk mencegah rusaknya layout fullscreen
        if (stage.getScene() == null) {
            Scene scene = new Scene(root);
            stage.setScene(scene);
        } else {
            stage.getScene().setRoot(root);
        }
    }

    private void clearFormFields() {
        txtNama.clear();
        cbKategori.setValue(null);
        cbWilayah.setValue(null);
        txtDeskripsi.clear();
    }

    private void refreshDestinationList() {
        containerDestinasi.getChildren().clear();

        for (Destination dest : DataStore.getInstance().getAllDestinations()) {
            HBox itemCard = new HBox(15);
            itemCard.setPadding(new Insets(15));
            itemCard.setAlignment(Pos.CENTER_LEFT);
            itemCard.setStyle("-fx-background-color: white; -fx-background-radius: 8px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 8, 0, 0, 1);");

            VBox infoColumn = new VBox(6);
            HBox.setHgrow(infoColumn, Priority.ALWAYS);

            Label lblHeaderCard = new Label("ID: " + dest.getDestinationId() + " | " + dest.getName() + " [" + dest.getCategory() + "]");
            lblHeaderCard.setStyle("-fx-font-weight: bold; -fx-text-fill: #0d47a1;");

            Label lblWilayahCard = new Label("📍 Wilayah: " + dest.getLocation());
            lblWilayahCard.setStyle("-fx-text-fill: #78909c; -fx-font-size: 12px;");

            Label lblDescCard = new Label(dest.getDescription());
            lblDescCard.setStyle("-fx-text-fill: #455a64; -fx-font-size: 13px;");
            lblDescCard.setWrapText(true);
            
            // ✨ TAMBAHKAN INI: Memaksa label deskripsi agar lebarnya fleksibel mengalah pada layout 
            // dan tidak mendorong tombol keluar dari layar
            lblDescCard.setMaxWidth(Double.MAX_VALUE);

            infoColumn.getChildren().addAll(lblHeaderCard, lblWilayahCard, lblDescCard);

            VBox actionBox = new VBox(8);
            actionBox.setAlignment(Pos.CENTER);
            
            // ✨ TAMBAHKAN INI: Kunci lebar minimum actionBox agar tidak bisa tergencet oleh teks deskripsi
            actionBox.setMinWidth(80); 

            Button btnEdit = new Button("Edit");
            btnEdit.setStyle("-fx-background-color: #37474f; -fx-text-fill: white; -fx-padding: 6px 12px; -fx-cursor: hand;");
            // ✨ TAMBAHKAN INI: Kunci lebar minimum tombol Edit agar teksnya tidak menjadi "..."
            btnEdit.setMinWidth(70); 
            
            btnEdit.setOnAction(e -> {
                selectedDestinationForEdit = dest;
                txtNama.setText(dest.getName());
                cbKategori.setValue(dest.getCategory());
                cbWilayah.setValue(dest.getLocation());
                txtDeskripsi.setText(dest.getDescription());
                
                btnSubmit.setText("Perbarui Destinasi");
                btnSubmit.setStyle("-fx-background-color: #2e7d32; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10px;");
                btnBatalEdit.setVisible(true);
                refreshDestinationList();
            });

            Button btnHapus = new Button("Hapus");
            btnHapus.setStyle("-fx-background-color: #c62828; -fx-text-fill: white; -fx-padding: 6px 12px; -fx-cursor: hand;");
            // ✨ TAMBAHKAN INI: Kunci lebar minimum tombol Hapus agar teksnya tidak terpotong
            btnHapus.setMinWidth(70); 
            
            btnHapus.setOnAction(e -> {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Hapus " + dest.getName() + "?", ButtonType.YES, ButtonType.NO);
                confirm.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.YES) {
                        DataStore.getInstance().getAllDestinations().remove(dest);
                        DataStore.getInstance().saveDestinationsToFile();
                        refreshDestinationList();
                    }
                });
            });

            actionBox.getChildren().addAll(btnEdit, btnHapus);
            itemCard.getChildren().addAll(infoColumn, actionBox);
            containerDestinasi.getChildren().add(itemCard);
        }
    }
}