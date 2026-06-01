package j.tour.view;

import j.tour.controller.database.DataStore;
import j.tour.controller.database.TextDatabaseManager;
import j.tour.model.Destination;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class MainDashboardView {
    private final String currentUserEmail;
    private final List<SelectedDayTrip> selectedList;

    private int currentDurationDays = 0;
    private int selectedFilterDay = 0; 
    private String selectedCategoryFilter = "Semua"; 
    private String searchQuery = ""; 

    private VBox dayButtonContainer = new VBox(8);
    private VBox destinationCardsContainer = new VBox(15);
    private VBox itinerarySummaryContainer = new VBox(10);
    private HBox categoryFilterButtonsContainer = new HBox(8); 
    private VBox searchAndFilterWrapper = new VBox(8); 
    private TextField txtDurasi = new TextField();
    
    private Button btnLihatSemua = new Button("👁 Lihat Semua Hari");

    public MainDashboardView(String email) {
        this.currentUserEmail = email;
        this.selectedList = new ArrayList<>(TextDatabaseManager.loadItinerary(currentUserEmail, DataStore.getInstance().getAllDestinations()));
        
        int savedDuration = loadUserDurationConfig(currentUserEmail);
        if (savedDuration > 0) {
            this.currentDurationDays = savedDuration;
        } else {
            for (SelectedDayTrip trip : selectedList) {
                if (trip.getAssignedDay() > currentDurationDays) {
                    currentDurationDays = trip.getAssignedDay();
                }
            }
        }
    }

    private interface AppNotifier {
        void notifyUser(String message);
    }

    private static class SystemAlertNotifier implements AppNotifier {
        private final Alert.AlertType type;

        public SystemAlertNotifier(Alert.AlertType type) {
            this.type = type;
        }

        @Override
        public void notifyUser(String message) {
            Alert alert = new Alert(this.type, message, ButtonType.OK);
            alert.showAndWait();
        }
    }
    private void sendNotification(AppNotifier notifier, String message) {
        notifier.notifyUser(message); 
    }
    
    public void show(Stage stage) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #fcf9ee; -fx-font-family: 'Helvetica';"); 

        // =========================================================================
        // 1. SIDEBAR KIRI - PENGATURAN DURASI, PROFIL, & JADWAL
        // =========================================================================
        VBox leftSidebar = new VBox(15); 
        leftSidebar.setPadding(new Insets(25, 15, 25, 15));
        leftSidebar.setPrefWidth(260);
        leftSidebar.setMinWidth(260);
        leftSidebar.setMaxWidth(260);
        leftSidebar.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #eef2f5; -fx-border-width: 0 1px 0 0;");

        Label lblLogo = new Label("J-Tour");
        lblLogo.setStyle("-fx-font-size: 28px; -fx-font-weight: 900; -fx-text-fill: #0d47a1; -fx-font-family: 'Helvetica', 'Arial';");

        Button btnProfil = new Button("👤 Pengaturan Profil Akun");
        btnProfil.setMaxWidth(Double.MAX_VALUE);
        btnProfil.setPrefHeight(35);
        btnProfil.setStyle("-fx-background-color: #f4f6f9; -fx-text-fill: #37474f; -fx-font-weight: bold; -fx-background-radius: 6px; -fx-cursor: hand;");
        btnProfil.setOnAction(e -> showProfileManagementModal(stage));

        VBox durationInputBox = new VBox(8);
        Label lblDurasiTitle = new Label("DURASI WISATA (HARI):");
        lblDurasiTitle.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #0d47a1;");
        
        HBox durationRow = new HBox(10);
        txtDurasi.setPrefWidth(80);
        txtDurasi.setPrefHeight(32);
        if (currentDurationDays > 0) {
            txtDurasi.setText(String.valueOf(currentDurationDays));
        }
        
        Button btnTerapkan = new Button("Terapkan");
        btnTerapkan.setPrefHeight(32);
        btnTerapkan.setStyle("-fx-background-color: #1565c0; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        durationRow.getChildren().addAll(txtDurasi, btnTerapkan);
        durationInputBox.getChildren().addAll(lblDurasiTitle, durationRow);

        VBox dayFilterBox = new VBox(8);
        Label lblPilihHariTitle = new Label("PILIH HARI JADWAL:");
        lblPilihHariTitle.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #0d47a1;");
        
        dayButtonContainer.setPadding(new Insets(5, 2, 5, 2));
        
        ScrollPane scrollLeftDays = new ScrollPane(dayButtonContainer);
        scrollLeftDays.setFitToWidth(true);
        scrollLeftDays.setPrefHeight(3500); 
        scrollLeftDays.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: transparent;");
        VBox.setVgrow(scrollLeftDays, Priority.ALWAYS);
        
        dayFilterBox.getChildren().addAll(lblPilihHariTitle, scrollLeftDays);

        Region sidebarSpacer = new Region();
        VBox.setVgrow(sidebarSpacer, Priority.SOMETIMES);

        btnLihatSemua.setDisable(true); 
        btnLihatSemua.setStyle("-fx-background-color: transparent; -fx-text-fill: #b0bec5; -fx-font-size: 13px; -fx-cursor: default;");
        btnLihatSemua.setOnAction(e -> showAllDaysModalReport(stage));
        
        Button btnKeluar = new Button("🚪 Keluar Aplikasi");
        btnKeluar.setStyle("-fx-background-color: transparent; -fx-text-fill: #c62828; -fx-font-weight: bold; -fx-cursor: hand;");
        btnKeluar.setOnAction(e -> new LoginView().show(stage));

        leftSidebar.getChildren().addAll(lblLogo, btnProfil, new Separator(), durationInputBox, dayFilterBox, sidebarSpacer, btnLihatSemua, btnKeluar);
        root.setLeft(leftSidebar);

        // =========================================================================
        // 2. PANEL TENGAH - REKOMENDASI TEMPAT KUNJUNGAN + FILTER KATEGORI
        // =========================================================================
        VBox centerPanel = new VBox(15);
        centerPanel.setPadding(new Insets(35, 30, 35, 30));

        Label lblMainTitle = new Label("Rencana Liburan Jogja");
        lblMainTitle.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #0d47a1;");

        Label lblSubTitle = new Label("Pilih destinasi di bawah untuk dijadwalkan secara teratur");
        lblSubTitle.setStyle("-fx-font-size: 14px; -fx-text-fill: #1565c0;");

        HBox recommendationHeaderRow = new HBox(15);
        recommendationHeaderRow.setAlignment(Pos.BOTTOM_LEFT);
        VBox.setMargin(recommendationHeaderRow, new Insets(15, 0, 0, 0));

        Label lblSectionTitle = new Label("Rekomendasi Tempat Kunjungan:");
        lblSectionTitle.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #1a1a1a;");

        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);

        searchAndFilterWrapper.setAlignment(Pos.CENTER_RIGHT);
        categoryFilterButtonsContainer.setAlignment(Pos.CENTER_RIGHT);

        recommendationHeaderRow.getChildren().addAll(lblSectionTitle, headerSpacer, searchAndFilterWrapper);

        ScrollPane scrollCenter = new ScrollPane(destinationCardsContainer);
        scrollCenter.setFitToWidth(true);
        scrollCenter.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: transparent;");
        VBox.setVgrow(scrollCenter, Priority.ALWAYS);

        centerPanel.getChildren().addAll(lblMainTitle, lblSubTitle, recommendationHeaderRow, scrollCenter);
        root.setCenter(centerPanel);

        // =========================================================================
        // 3. PANEL KANAN - SUSUNAN ACARA PERJALANAN (ITINERARY SUMMARY)
        // =========================================================================
        VBox rightSidebar = new VBox(20);
        rightSidebar.setPadding(new Insets(35, 20, 35, 20));
        rightSidebar.setPrefWidth(320);
        rightSidebar.setMinWidth(320);
        rightSidebar.setStyle("-fx-background-color: #f4f6f9; -fx-border-color: #eef2f5; -fx-border-width: 0 0 0 1px;");

        Label lblRightTitle = new Label("Susunan Acara Perjalanan");
        lblRightTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #0d47a1;");

        ScrollPane scrollRight = new ScrollPane(itinerarySummaryContainer);
        scrollRight.setFitToWidth(true);
        scrollRight.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: transparent;");
        VBox.setVgrow(scrollRight, Priority.ALWAYS);

        Button btnSimpanItinerary = new Button("Simpan Struktur Itinerary");
        btnSimpanItinerary.setMaxWidth(Double.MAX_VALUE);
        btnSimpanItinerary.setPrefHeight(45);
        btnSimpanItinerary.setStyle("-fx-background-color: #f57c00; -fx-text-fill: white; -fx-font-weight: 1000; -fx-font-size: 14px; -fx-background-radius: 6px; -fx-cursor: hand;");
        
        btnSimpanItinerary.setOnAction(e -> {
            TextDatabaseManager.saveItinerary(currentUserEmail, selectedList);
            saveUserDurationConfig(currentUserEmail, currentDurationDays);
            
            btnLihatSemua.setDisable(false);
            btnLihatSemua.setStyle("-fx-background-color: transparent; -fx-text-fill: #78909c; -fx-font-size: 13px; -fx-cursor: hand;");
            
            refreshFilterDayButtons();
            refreshItinerarySummary();
            
            sendNotification(new SystemAlertNotifier(Alert.AlertType.INFORMATION), 
            "Struktur Rencana Perjalanan Wisata Anda Berhasil Disimpan!");
        });

        rightSidebar.getChildren().addAll(lblRightTitle, new Separator(), scrollRight, btnSimpanItinerary);
        root.setRight(rightSidebar);

        // =========================================================================
        // ACTION LISTENERS MAIN CONTAINER
        // =========================================================================
        btnTerapkan.setOnAction(e -> {
            String input = txtDurasi.getText().trim();
            try {
                int days = Integer.parseInt(input);
                if (days <= 0) throw new NumberFormatException();
                
                this.currentDurationDays = days;
                this.selectedFilterDay = 1; 
                
                selectedList.removeIf(trip -> trip.getAssignedDay() > currentDurationDays);

                btnLihatSemua.setDisable(true);
                btnLihatSemua.setStyle("-fx-background-color: transparent; -fx-text-fill: #b0bec5; -fx-font-size: 13px; -fx-cursor: default;");

                refreshFilterDayButtons();
                refreshCategoryFilterRow();
                refreshDestinationCards();
                refreshItinerarySummary();
            } catch (NumberFormatException ex) {
                sendNotification(new SystemAlertNotifier(Alert.AlertType.WARNING), "Masukkan jumlah durasi hari yang valid (Angka > 0)!");
            }
        });

        if (!selectedList.isEmpty()) {
            btnLihatSemua.setDisable(false);
            btnLihatSemua.setStyle("-fx-background-color: transparent; -fx-text-fill: #78909c; -fx-font-size: 13px; -fx-cursor: hand;");
            this.selectedFilterDay = 1;
        } else if (currentDurationDays > 0) {
            this.selectedFilterDay = 1;
        }

        refreshFilterDayButtons();
        refreshCategoryFilterRow();
        refreshDestinationCards();
        refreshItinerarySummary();

        stage.setTitle("J-Tour - Perencana Perjalanan Wisata");

        // Perbaikan Utama: Gunakan setRoot() untuk menjamin responsivitas tata letak di layar penuh
        if (stage.getScene() == null) {
            Scene scene = new Scene(root);
            stage.setScene(scene);
        } else {
            stage.getScene().setRoot(root);
        }
    }

    private void showDetailPopup(Destination destination) {
       
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Detail Tempat Wisata - J-Tour");
        popupStage.setResizable(false);

        // 2. Kontainer Utama 
        VBox root = new VBox(12);
        root.setPadding(new Insets(25));
        root.setStyle("-fx-background-color: #FAF8F2;"); 

        // ✨ TAMBAHKAN 2 BARIS INI UNTUK MENGUNCI LEBAR KANAN-KIRI:
        root.setPrefWidth(460); // Lebar ideal yang pas dan kompak
        root.setMaxWidth(460);

        
        Label headerLabel = new Label("DETAIL INFORMASI DESTINASI");
        headerLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #0A4B93;");

     
        Label subtitleLabel = new Label("Menampilkan informasi lengkap mengenai tempat kunjungan terpilih:");
        subtitleLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #666666;");

     
        VBox cardInfo = new VBox(10);
        cardInfo.setPadding(new Insets(15));
        cardInfo.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #E2E0D9; -fx-border-width: 1px; -fx-border-radius: 5px; -fx-background-radius: 5px;");
        
        Label infoHeader = new Label( destination.getName().toUpperCase());
        infoHeader.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #0A4B93;"); 
        
        Label categoryLabel = new Label("Kategori Tempat: " + destination.getCategory());
        categoryLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #333333;");
        
        cardInfo.getChildren().addAll(infoHeader, categoryLabel);

  
        VBox cardDesc = new VBox(6);
        cardDesc.setPadding(new Insets(15));
        cardDesc.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #E2E0D9; -fx-border-width: 1px; -fx-border-radius: 5px; -fx-background-radius: 5px;");
        
        Label descHeader = new Label("DESKRIPSI DESTINASI");
        descHeader.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #0A4B93;");
        
        Label descContent = new Label(destination.getDescription());
        descContent.setWrapText(true);

     
        descContent.setMaxWidth(360); 

     
        descContent.setStyle("-fx-font-size: 13px; -fx-text-fill: #4F4F4F; -fx-line-spacing: 1.2; -fx-padding: 0 10 0 0;");

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(descContent);
        scrollPane.setFitToWidth(true);


        scrollPane.setPrefHeight(150);   
        scrollPane.setMinHeight(150); 
        
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: #FFFFFF; -fx-border-color: transparent; -fx-padding: 0;");

        cardDesc.getChildren().addAll(descHeader, scrollPane);


        VBox cardLoc = new VBox(6);
        cardLoc.setPadding(new Insets(15));
        cardLoc.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #E2E0D9; -fx-border-width: 1px; -fx-border-radius: 5px; -fx-background-radius: 5px;");
        
        Label locHeader = new Label("📍 LOKASI WILAYAH");
        locHeader.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #0A4B93;");
        
        Label locContent = new Label(destination.getLocation());
        locContent.setStyle("-fx-font-size: 13px; -fx-text-fill: #4F4F4F;");
        
        cardLoc.getChildren().addAll(locHeader, locContent);

        // ==================== TOMBOL AKSI (KANAN BAWAH) ====================
        
        Button closeButton = new Button("Tutup Detail");
        closeButton.setStyle("-fx-background-color: #0A4B93; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8px 20px; -fx-background-radius: 5px; -fx-cursor: hand;");
        closeButton.setOnAction(e -> popupStage.close());

        HBox btnContainer = new HBox();
        btnContainer.setAlignment(Pos.BOTTOM_RIGHT);
        btnContainer.setPadding(new Insets(50, 0, 0, 0));
        btnContainer.getChildren().add(closeButton);

        // 5. Gabungkan Semua Komponen ke Root Layout
        root.getChildren().addAll(headerLabel, subtitleLabel, cardInfo, cardDesc, cardLoc, btnContainer);

        // 6. Set Scene & Tampilkan Window
      // Sediakan scene tanpa ukuran hardcode agar tingginya tetap otomatis (auto-height)
        Scene scene = new Scene(root); 
        popupStage.setScene(scene);
        popupStage.sizeToScene(); // Mengikuti tinggi konten secara alami
        popupStage.showAndWait();
        
    }
    private void refreshCategoryFilterRow() {
        categoryFilterButtonsContainer.getChildren().clear();
        searchAndFilterWrapper.getChildren().clear(); 

        Set<String> categories = new HashSet<>();
        for (Destination dest : DataStore.getInstance().getAllDestinations()) {
            if (dest.getCategory() != null && !dest.getCategory().trim().isEmpty()) {
                categories.add(dest.getCategory().trim());
            }
        }

        List<String> sortedCategories = new ArrayList<>();
        sortedCategories.add("Semua"); 
        sortedCategories.addAll(categories);

        for (String cat : sortedCategories) {
            Button btnFilter = new Button(cat);
            btnFilter.setPrefHeight(26);
            btnFilter.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-background-radius: 20px; -fx-padding: 4px 12px 4px 12px; -fx-cursor: hand;");

            if (selectedCategoryFilter.equalsIgnoreCase(cat)) {
                btnFilter.setStyle(btnFilter.getStyle() + "-fx-background-color: #0d47a1; -fx-text-fill: white;");
            } else {
                btnFilter.setStyle(btnFilter.getStyle() + "-fx-background-color: #e0e0e0; -fx-text-fill: #424242;");
            }

            btnFilter.setOnAction(e -> {
                selectedCategoryFilter = cat;
                refreshCategoryFilterRow(); 
                refreshDestinationCards();   
            });

            categoryFilterButtonsContainer.getChildren().add(btnFilter);
        }

        TextField txtSearch = new TextField(searchQuery);
        txtSearch.setPromptText("🔍 Cari nama destinasi...");
        txtSearch.setPrefHeight(28);
        txtSearch.setMaxWidth(200); 
        txtSearch.setPrefWidth(200);
        txtSearch.setStyle("-fx-font-size: 11px; -fx-background-radius: 15px; -fx-border-radius: 15px; -fx-border-color: #cfd8dc; -fx-background-color: white;");

        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            searchQuery = newValue;
            refreshDestinationCards(); 
        });

        txtSearch.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                javafx.application.Platform.runLater(txtSearch::end);
            }
        });

        searchAndFilterWrapper.getChildren().addAll(txtSearch, categoryFilterButtonsContainer);
    }

    private void refreshDestinationCards() {
        destinationCardsContainer.getChildren().clear();

        for (Destination dest : DataStore.getInstance().getAllDestinations()) {
            if (!selectedCategoryFilter.equalsIgnoreCase("Semua") && !dest.getCategory().equalsIgnoreCase(selectedCategoryFilter)) {
                continue;
            }

            if (searchQuery != null && !searchQuery.trim().isEmpty() && 
                !dest.getName().toLowerCase().contains(searchQuery.toLowerCase().trim())) {
                continue;
            }

            HBox card = new HBox(20);
            card.setPadding(new Insets(18, 20, 18, 20));
            card.setAlignment(Pos.CENTER_LEFT);
            card.setStyle("-fx-background-color: white; -fx-background-radius: 8px; "
                    + "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.04), 8, 0, 0, 2); "
                    + "-fx-border-color: #eef2f5; -fx-border-radius: 8px;");

            VBox textCol = new VBox(6);
            card.setCursor(Cursor.HAND);
            
            HBox.setHgrow(textCol, Priority.ALWAYS);

            Label lblTitleCard = new Label(dest.getName() + " [" + dest.getCategory() + "]");
            lblTitleCard.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #0d47a1;");

            Label lblDescCard = new Label(dest.getDescription() + " (Lokasi: " + dest.getLocation() + ")");
            lblDescCard.setStyle("-fx-text-fill: #546e7a; -fx-font-size: 13px;");
            lblDescCard.setWrapText(true);
            
            textCol.getChildren().addAll(lblTitleCard);
            card.setOnMouseClicked(event -> {
                showDetailPopup(dest);
            });

            int targetDay = selectedFilterDay;
            Button btnAddTrip = new Button("+ Hari " + targetDay);
            btnAddTrip.setMinWidth(100); 
            btnAddTrip.setStyle("-fx-background-color: #f57c00; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 4px; -fx-padding: 6px 14px 6px 14px; -fx-cursor: hand;");
            
            if (currentDurationDays <= 0) {
                btnAddTrip.setDisable(true);
                btnAddTrip.setStyle("-fx-background-color: #b0bec5; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 4px;");
            }

            btnAddTrip.setOnAction(e -> {
                boolean matchesExisting = selectedList.stream()
                        .anyMatch(t -> t.getDestination().getDestinationId().equals(dest.getDestinationId()) && t.getAssignedDay() == targetDay);

                if (!matchesExisting) {
                    selectedList.add(new SelectedDayTrip(dest, targetDay));
                    refreshFilterDayButtons(); 
                    refreshItinerarySummary();
                    
                    btnLihatSemua.setDisable(true);
                    btnLihatSemua.setStyle("-fx-background-color: transparent; -fx-text-fill: #b0bec5; -fx-font-size: 13px; -fx-cursor: default;");
                }
            });

            card.getChildren().addAll(textCol, btnAddTrip);
            destinationCardsContainer.getChildren().add(card);
        }
    }

    private void showProfileManagementModal(Stage ownerStage) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(ownerStage);
        dialog.setTitle("Pengaturan Akun Pengguna - J-Tour");

        VBox dialogRoot = new VBox(15);
        dialogRoot.setPadding(new Insets(25));
        dialogRoot.setStyle("-fx-background-color: #FFFFFF;");

        Label lblTitle = new Label("⚙️ MANAJEMEN PROFIL AKUN");
        lblTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #0d47a1;");

        VBox emailBox = new VBox(5);
        Label lblEmail = new Label("Email Akun (Tidak dapat diubah):");
        lblEmail.setStyle("-fx-font-size: 12px; -fx-text-fill: #78909c; -fx-font-weight: bold;");
        TextField txtEmail = new TextField(currentUserEmail);
        txtEmail.setEditable(false);
        txtEmail.setDisable(true);
        txtEmail.setStyle("-fx-background-color: #f4f6f9;");
        emailBox.getChildren().addAll(lblEmail, txtEmail);

        VBox passwordBox = new VBox(5);
        Label lblPassword = new Label("Ubah Kata Sandi Baru:");
        lblPassword.setStyle("-fx-font-size: 12px; -fx-text-fill: #37474f; -fx-font-weight: bold;");
        PasswordField txtPassword = new PasswordField();
        txtPassword.setPromptText("Masukkan kata sandi baru jika ingin diubah");
        passwordBox.getChildren().addAll(lblPassword, txtPassword);

        Button btnUpdate = new Button("Simpan Perubahan");
        btnUpdate.setStyle("-fx-background-color: #1565c0; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        btnUpdate.setPrefHeight(35);
        btnUpdate.setMaxWidth(Double.MAX_VALUE);

        btnUpdate.setOnAction(e -> {
            String newPassword = txtPassword.getText().trim();
            if (newPassword.isEmpty()) {
                sendNotification(new SystemAlertNotifier(Alert.AlertType.WARNING),
                 "Silakan isi kata sandi baru terlebih dahulu!");
                return;
            }

            boolean success = TextDatabaseManager.updateUserPassword(currentUserEmail, newPassword);
            if (success) {
                sendNotification(new SystemAlertNotifier(Alert.AlertType.INFORMATION), 
                "Profil akun Anda berhasil diperbarui!");
                dialog.close();
            } else {
                sendNotification(new SystemAlertNotifier(Alert.AlertType.ERROR), 
                "Gagal memperbarui data akun. Coba beberapa saat lagi.");
            }
        });

        VBox dangerZoneBox = new VBox(10);
        dangerZoneBox.setPadding(new Insets(15));
        dangerZoneBox.setStyle("-fx-border-color: #ffcdd2; -fx-border-width: 1px; -fx-background-color: #ffebee; -fx-border-radius: 6px; -fx-background-radius: 6px;");
        VBox.setMargin(dangerZoneBox, new Insets(10, 0, 0, 0));

        Label lblDangerTitle = new Label("Zona Bahaya!");
        lblDangerTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #c62828; -fx-font-size: 13px;");
        
        Label lblDangerDesc = new Label("Menghapus akun akan menghilangkan seluruh data itinerary yang telah Anda buat secara permanen dari sistem database.");
        lblDangerDesc.setStyle("-fx-text-fill: #546e7a; -fx-font-size: 12px;");
        lblDangerDesc.setWrapText(true);

        Button btnDeleteAccount = new Button("🗑️ Hapus Akun Saya Permanen");
        btnDeleteAccount.setStyle("-fx-background-color: #c62828; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        btnDeleteAccount.setMaxWidth(Double.MAX_VALUE);
        btnDeleteAccount.setPrefHeight(35);

        btnDeleteAccount.setOnAction(e -> {
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Konfirmasi Hapus Akun");
            confirmAlert.setHeaderText("Apakah Anda benar-benar yakin ingin menghapus akun ini?");
            confirmAlert.setContentText("Tindakan ini tidak dapat dibatalkan dan Anda akan langsung diarahkan keluar aplikasi.");

            Optional<ButtonType> result = confirmAlert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                boolean accountDeleted = TextDatabaseManager.deleteUserAccount(currentUserEmail);
                if (accountDeleted) {
                    dialog.close();
                    new LoginView().show(ownerStage);
                    
                    sendNotification(new SystemAlertNotifier(Alert.AlertType.INFORMATION),
                     "Akun Anda telah berhasil dihapus dari sistem J-Tour.");
                } else {
                    sendNotification(new SystemAlertNotifier(Alert.AlertType.ERROR), 
                    "Gagal menghapus akun. Coba beberapa saat lagi.");
                }
            }
        });

        dangerZoneBox.getChildren().addAll(lblDangerTitle, lblDangerDesc, btnDeleteAccount);

        Button btnCancel = new Button("Batal");
        btnCancel.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #37474f; -fx-cursor: hand;");
        btnCancel.setPrefWidth(100);
        btnCancel.setOnAction(e -> dialog.close());

        HBox bottomRow = new HBox(btnCancel);
        bottomRow.setAlignment(Pos.CENTER_RIGHT);

        dialogRoot.getChildren().addAll(lblTitle, new Separator(), emailBox, passwordBox, btnUpdate, dangerZoneBox, bottomRow);

        Scene dialogScene = new Scene(dialogRoot, 420, 520);
        dialog.setScene(dialogScene);
        dialog.setResizable(false);
        dialog.showAndWait();
    }

    private void showAllDaysModalReport(Stage ownerStage) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(ownerStage);
        dialog.setTitle("Pratinjau Seluruh Jadwal Liburan - J-Tour");

        VBox dialogRoot = new VBox(20);
        dialogRoot.setPadding(new Insets(25));
        dialogRoot.setStyle("-fx-background-color: #fcf9ee;"); 

        Label lblModalTitle = new Label("📋 RANGKUMAN STRUKTUR ITINERARY GLOBAL");
        lblModalTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #0d47a1;");
        
        Label lblModalDesc = new Label("Menampilkan susunan destinasi perjalanan dari rentang waktu hari ke-1 hingga hari terakhir:");
        lblModalDesc.setStyle("-fx-font-size: 13px; -fx-text-fill: #546e7a;");

        VBox reportContainer = new VBox(15);
        reportContainer.setPadding(new Insets(10, 5, 10, 5));

        for (int day = 1; day <= currentDurationDays; day++) {
            final int activeDay = day;
            List<SelectedDayTrip> tripsForDay = selectedList.stream()
                    .filter(t -> t.getAssignedDay() == activeDay)
                    .toList();

            VBox dayBlock = new VBox(8);
            dayBlock.setPadding(new Insets(12, 15, 12, 15));
            dayBlock.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 8px; -fx-border-color: #cfd8dc; -fx-border-width: 1px;");

            String icon = tripsForDay.isEmpty() ? "⚪ " : "🔵 ";
            Label lblDayHeader = new Label(icon + "JADWAL HARI " + activeDay);
            lblDayHeader.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: #f57c00;");
            dayBlock.getChildren().add(lblDayHeader);

            if (tripsForDay.isEmpty()) {
                Label lblEmpty = new Label("  ( Belum ada destinasi yang dijadwalkan pada hari ini )");
                lblEmpty.setStyle("-fx-text-fill: #90a4ae; -fx-font-style: italic; -fx-font-size: 12px;");
                dayBlock.getChildren().add(lblEmpty);
            } else {
                int index = 1;
                for (SelectedDayTrip trip : tripsForDay) {
                    Label lblItem = new Label(index + ". " + trip.getDestination().getName() 
                        + " [" + trip.getDestination().getCategory() + "] - " + trip.getDestination().getLocation());
                    lblItem.setStyle("-fx-font-size: 13px; -fx-text-fill: #263238;");
                    dayBlock.getChildren().add(lblItem);
                    index++;
                }
            }
            reportContainer.getChildren().add(dayBlock);
        }

        ScrollPane modalScroll = new ScrollPane(reportContainer);
        modalScroll.setFitToWidth(true);
        modalScroll.setPrefHeight(450);
        modalScroll.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: transparent;");

        Button btnClose = new Button("Tutup Pratinjau");
        btnClose.setPrefWidth(140);
        btnClose.setPrefHeight(35);
        btnClose.setStyle("-fx-background-color: #0d47a1; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 4px; -fx-cursor: hand;");
        btnClose.setOnAction(e -> dialog.close());

        HBox btnRow = new HBox(btnClose);
        btnRow.setAlignment(Pos.CENTER_RIGHT);

        dialogRoot.getChildren().addAll(lblModalTitle, lblModalDesc, modalScroll, new Separator(), btnRow);

        Scene dialogScene = new Scene(dialogRoot, 650, 620);
        dialog.setScene(dialogScene);
        dialog.setResizable(false);
        dialog.showAndWait();
    }

    private void refreshFilterDayButtons() {
        dayButtonContainer.getChildren().clear();

        if (currentDurationDays <= 0) {
            Label lblPlaceholder = new Label("(Tentukan durasi dulu)");
            lblPlaceholder.setStyle("-fx-text-fill: #90a4ae; -fx-font-style: italic; -fx-font-size: 13px;");
            dayButtonContainer.getChildren().add(lblPlaceholder);
            return;
        }

        for (int i = 1; i <= currentDurationDays; i++) {
            final int dayNum = i;
            
            boolean isDayEmpty = selectedList.stream().noneMatch(t -> t.getAssignedDay() == dayNum);
            
            String circleIcon = isDayEmpty ? "⚪ " : "🔵 ";
            String buttonText = circleIcon + "Hari " + dayNum;
            
            Button btnDay = new Button(buttonText);
            btnDay.setMaxWidth(Double.MAX_VALUE);
            btnDay.setPrefHeight(35);
            btnDay.setAlignment(Pos.CENTER_LEFT);

            if (selectedFilterDay == dayNum) {
                btnDay.setStyle("-fx-background-color: #e3f2fd; -fx-text-fill: #0d47a1; -fx-font-weight: bold; -fx-border-color: #0d47a1; -fx-border-width: 0 0 0 4px; -fx-cursor: hand;");
            } else {
                if (isDayEmpty) {
                    btnDay.setStyle("-fx-background-color: transparent; -fx-text-fill: #90a4ae; -fx-font-weight: normal; -fx-cursor: hand;");
                } else {
                    btnDay.setStyle("-fx-background-color: transparent; -fx-text-fill: #37474f; -fx-font-weight: bold; -fx-cursor: hand;");
                }
            }

            btnDay.setOnAction(e -> {
                selectedFilterDay = dayNum;
                refreshFilterDayButtons();
                refreshDestinationCards();
                refreshItinerarySummary();
            });

            dayButtonContainer.getChildren().add(btnDay);
        }
    }

    private void refreshItinerarySummary() {
        itinerarySummaryContainer.getChildren().clear();

        if (currentDurationDays <= 0) {
            Label lblWarn = new Label("Silakan tentukan durasi wisata Anda terlebih dahulu di sidebar kiri.");
            lblWarn.setStyle("-fx-text-fill: #78909c; -fx-font-size: 13px;");
            lblWarn.setWrapText(true);
            itinerarySummaryContainer.getChildren().add(lblWarn);
            return;
        }

        List<SelectedDayTrip> tripsForDay = selectedList.stream()
                .filter(t -> t.getAssignedDay() == selectedFilterDay)
                .toList();

        if (tripsForDay.isEmpty()) {
            Label lblEmpty = new Label("Belum ada destinasi yang dipilih untuk Hari " + selectedFilterDay + ". Klik tombol '+ Hari' pada rekomendasi tempat.");
            lblEmpty.setStyle("-fx-text-fill: #78909c; -fx-font-size: 13px; -fx-font-style: italic;");
            lblEmpty.setWrapText(true);
            itinerarySummaryContainer.getChildren().add(lblEmpty);
            return;
        }

        VBox dayGroupNode = new VBox(8);
        dayGroupNode.setPadding(new Insets(5, 0, 10, 0));

        Label lblDayHeader = new Label("📅 JADWAL HARI " + selectedFilterDay);
        lblDayHeader.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: #1565c0;");
        dayGroupNode.getChildren().add(lblDayHeader);

        for (SelectedDayTrip trip : tripsForDay) {
            HBox summaryCard = new HBox(10);
            summaryCard.setAlignment(Pos.CENTER_LEFT);
            summaryCard.setPadding(new Insets(8, 12, 8, 12));
            summaryCard.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 5px; -fx-border-color: #cfd8dc; -fx-border-width: 1px;");

            Label lblDestName = new Label(trip.getDestination().getName());
            lblDestName.setStyle("-fx-font-size: 13px; -fx-text-fill: #263238;");
            HBox.setHgrow(lblDestName, Priority.ALWAYS);
            lblDestName.setMaxWidth(180);

            Button btnRemoveItem = new Button("×");
            btnRemoveItem.setStyle("-fx-background-color: transparent; -fx-text-fill: #c62828; -fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 0; -fx-cursor: hand;");
            btnRemoveItem.setOnAction(e -> {
                selectedList.remove(trip);
                refreshFilterDayButtons(); 
                refreshItinerarySummary();
                
                btnLihatSemua.setDisable(true);
                btnLihatSemua.setStyle("-fx-background-color: transparent; -fx-text-fill: #b0bec5; -fx-font-size: 13px; -fx-cursor: default;");
            });

            summaryCard.getChildren().addAll(lblDestName, new Region(), btnRemoveItem);
            HBox.setHgrow(summaryCard.getChildren().get(1), Priority.ALWAYS); 
            
            dayGroupNode.getChildren().add(summaryCard);
        }

        itinerarySummaryContainer.getChildren().add(dayGroupNode);
    }

    private void saveUserDurationConfig(String email, int duration) {
        try {
            File dir = new File("data/durations/");
            if (!dir.exists()) dir.mkdirs();
            
            String safeName = email.replace("@", "_").replace(".", "_") + "_durasi.txt";
            File configFile = new File(dir, safeName);
            
            try (PrintWriter pw = new PrintWriter(new FileWriter(configFile))) {
                pw.println(duration);
            }
        } catch (IOException e) {
            System.err.println("Gagal menyimpan data durasi: " + e.getMessage());
        }
    }

    private int loadUserDurationConfig(String email) {
        String safeName = email.replace("@", "_").replace(".", "_") + "_durasi.txt";
        File configFile = new File("data/durations/", safeName);
        
        if (!configFile.exists()) return -1;
        
        try (BufferedReader br = new BufferedReader(new FileReader(configFile))) {
            String line = br.readLine();
            if (line != null && !line.trim().isEmpty()) {
                return Integer.parseInt(line.trim());
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Gagal memuat konfigurasi durasi: " + e.getMessage());
        }
        return -1;
    }

    public static class SelectedDayTrip {
        private final Destination destination;
        private final int assignedDay;

        public SelectedDayTrip(Destination destination, int assignedDay) {
            this.destination = destination;
            this.assignedDay = assignedDay;
        }

        public Destination getDestination() {
            return destination;
        }

        public int getAssignedDay() {
            return assignedDay;
        }
    }
}