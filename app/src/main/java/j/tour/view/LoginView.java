package j.tour.view;

import j.tour.controller.database.TextDatabaseManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class LoginView {
    private boolean isRegisterMode = false; 

    public void show(Stage stage) {
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: #fcf9ee; -fx-font-family: 'Helvetica', 'Arial';"); 

        VBox authCard = new VBox(22);
        authCard.setPadding(new Insets(45, 40, 45, 40));
        authCard.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 18px; "
                + "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.08), 15, 0, 0, 5);");
        authCard.setMaxWidth(400);
        authCard.setMaxHeight(480);
        authCard.setAlignment(Pos.TOP_CENTER);

     
        Label lblTitle = new Label("J-TOUR PLANNER");
        lblTitle.setStyle("-fx-font-size: 24px; -fx-font-weight: 1000; -fx-text-fill: #f57c00; -fx-font-family: 'Helvetica', 'Arial'; -fx-letter-spacing: 0.5px;");

    
        VBox formBox = new VBox(15);
        formBox.setPadding(new Insets(10, 0, 5, 0));
        
        TextField txtEmail = new TextField();
        txtEmail.setPromptText("Email / Username");
        txtEmail.setPrefHeight(44);
        txtEmail.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #CBD5E1; -fx-border-radius: 6px; -fx-background-radius: 6px; -fx-padding: 0 12 0 12; -fx-font-size: 13px;");

        PasswordField txtPassword = new PasswordField();
        txtPassword.setPromptText("Password");
        txtPassword.setPrefHeight(44);
        txtPassword.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #CBD5E1; -fx-border-radius: 6px; -fx-background-radius: 6px; -fx-padding: 0 12 0 12; -fx-font-size: 13px;");
        
        formBox.getChildren().addAll(txtEmail, txtPassword);

     
        Button btnSubmit = new Button("SIGN IN");
        btnSubmit.setMaxWidth(Double.MAX_VALUE);
        btnSubmit.setPrefHeight(44);
        btnSubmit.setStyle("-fx-background-color: #0d47a1; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-background-radius: 6px; -fx-cursor: hand;");
        
        btnSubmit.setOnMouseEntered(e -> btnSubmit.setStyle("-fx-background-color: #1565c0; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-background-radius: 6px; -fx-cursor: hand;"));
        btnSubmit.setOnMouseExited(e -> btnSubmit.setStyle("-fx-background-color: #0d47a1; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-background-radius: 6px; -fx-cursor: hand;"));

        txtEmail.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                btnSubmit.fire();
            }
        });

        txtPassword.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                btnSubmit.fire();
            }
        });

        // --- FOOTER NOTE / TOGGLE ---
        Label lblHintAdmin = new Label("💡 Masuk dengan user 'admin' untuk ke panel Admin.");
        lblHintAdmin.setStyle("-fx-text-fill: #78909c; -fx-font-size: 11px; -fx-font-style: italic;");

        HBox footerBox = new HBox(5); 
        footerBox.setAlignment(Pos.CENTER);
        Label lblFooterPrompt = new Label("Don't have an account?");
        lblFooterPrompt.setStyle("-fx-text-fill: #718096; -fx-font-size: 13px;");
        Hyperlink linkToggle = new Hyperlink("Sign up");
        linkToggle.setStyle("-fx-text-fill: #0d47a1; -fx-font-weight: bold; -fx-font-size: 13px; -fx-underline: false;");
        footerBox.getChildren().addAll(lblFooterPrompt, linkToggle);

        // --- ACTION TOGGLE MODE (SIGN IN / SIGN UP) ---
        linkToggle.setOnAction(e -> {
            isRegisterMode = !isRegisterMode;
            if (isRegisterMode) {
                lblTitle.setText("CREATE ACCOUNT");
                lblTitle.setStyle("-fx-font-size: 24px; -fx-font-weight: 1000; -fx-text-fill: #0d47a1; -fx-font-family: 'Helvetica', 'Arial';");
                btnSubmit.setText("SIGN UP");
                lblFooterPrompt.setText("Already have an account?");
                linkToggle.setText("Log in");
                lblHintAdmin.setVisible(false);
            } else {
                lblTitle.setText("J-TOUR PLANNER");
                lblTitle.setStyle("-fx-font-size: 24px; -fx-font-weight: 1000; -fx-text-fill: #f57c00; -fx-font-family: 'Helvetica', 'Arial';");
                btnSubmit.setText("SIGN IN");
                lblFooterPrompt.setText("Don't have an account?");
                linkToggle.setText("Sign up");
                lblHintAdmin.setVisible(true);
            }
        });

        // --- ACTION BUTTON SUBMIT ---
        btnSubmit.setOnAction(e -> {
            String email = txtEmail.getText().trim();
            String pass = txtPassword.getText().trim();

            if (email.isEmpty() || pass.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Form input tidak boleh kosong!", ButtonType.OK);
                alert.showAndWait();
                return;
            }

            if (isRegisterMode) {
                boolean success = TextDatabaseManager.registerUser(email, pass);
                if (success) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Registrasi Berhasil! Silakan masuk.", ButtonType.OK);
                    alert.showAndWait();
                    linkToggle.fire(); 
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Email/Username sudah terdaftar!", ButtonType.OK);
                    alert.showAndWait();
                }
            } else {
                if (email.equalsIgnoreCase("admin") && pass.equals("admin")) {
                    new AdminDashboardView().show(stage);
                } 
                else if (TextDatabaseManager.loginUser(email, pass)) {
                    new MainDashboardView(email).show(stage);
                } 
                else {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Email atau Password salah!", ButtonType.OK);
                    alert.showAndWait();
                }
            }
        });

        authCard.getChildren().addAll(lblTitle, formBox, btnSubmit, lblHintAdmin, footerBox);
        root.getChildren().add(authCard);
        
        stage.setTitle("J-Tour - Login Gateway");
        
        // Perbaikan Utama: Cek jika Scene belum ada buat baru, jika sudah ada pakai setRoot()
        if (stage.getScene() == null) {
            Scene scene = new Scene(root, 1280, 720);
            stage.setScene(scene);
        } else {
            stage.getScene().setRoot(root);
        }

        if (!stage.isShowing()) {
            stage.setMaximized(true);
            stage.show();
        }
    }
}