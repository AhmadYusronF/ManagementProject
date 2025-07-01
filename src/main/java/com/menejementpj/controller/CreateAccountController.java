package com.menejementpj.controller;

import com.menejementpj.db.DatabaseManager;
import com.menejementpj.utils.Utils;

import java.io.IOException;

import com.menejementpj.components.PopUpAlert;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class CreateAccountController {

    @FXML
    private Rectangle clipRect;

    @FXML
    private Button togglePasswordButton;

    @FXML
    private Button cancelBtn;

    @FXML
    private Button confirmBtn;

    @FXML
    private TextField emailField;

    @FXML
    private TextField namaField;

    @FXML
    private PasswordField passwordField;
    
    @FXML
    private TextField visiblePasswordField;

    private boolean isPasswordVisible = false;

    @FXML
    void handleCancelAction(ActionEvent event) {
        Utils.closeWindow(event);
    }

    @FXML
    void handleConfirmAction(ActionEvent event) {
        System.out.println("Tombol 'Buat Akun' ditekan. Memproses data...");

        String nama = namaField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();

        if (nama.isEmpty() || email.isEmpty() || password.isEmpty()) {
            PopUpAlert.popupWarn("Kesalahan", "Semua field harus diisi!", "");
            return;
        }

        System.out.println("Data yang akan dikirim ke database:");
        System.out.println("  - Nama: " + nama);
        System.out.println("  - Email: " + email);
        System.out.println("  - Password: " + "[DISEMBUNYIKAN]");
        boolean isSuccess = DatabaseManager.daftarAkunDanPengguna(email, password, nama);

        if (isSuccess) {
            PopUpAlert.popupInfo("Sukses! Akun berhasil dibuat", "Account dengan email " + email + " berhasil dibuat",
                    "");
        } else {
            PopUpAlert.popupErr("Gagal membuat akun", "Silakan cek log untuk detailnya.", "");
        }
        Utils.closeWindow(event);
    }

    @FXML
    void handleRegisterButtonAction(ActionEvent event) {
        showPopup(event, "/com/menejementpj/components/accountCreate.fxml", "Create An Account");
    }

    private void showPopup(ActionEvent event, String fxmlFile, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();

            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.initOwner(((Node) event.getSource()).getScene().getWindow());
            popupStage.setTitle(title);
            popupStage.setScene(new Scene(root));
            popupStage.showAndWait();

        } catch (IOException e) {
            System.err.println("⚠️ Failed to load FXML file: " + fxmlFile);
            e.printStackTrace();
        }
    }

    @FXML
    private void initialize() {
        passwordField.textProperty().bindBidirectional(visiblePasswordField.textProperty());
    }

    @FXML
    private void togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible;

        if (isPasswordVisible) {
            visiblePasswordField.setVisible(true);
            visiblePasswordField.setManaged(true);
            passwordField.setVisible(false);
            passwordField.setManaged(false);
            togglePasswordButton.setText("HIDE");
        } else {
            visiblePasswordField.setVisible(false);
            visiblePasswordField.setManaged(false);
            passwordField.setVisible(true);
            passwordField.setManaged(true);
            togglePasswordButton.setText("SHOW");
        }
    }
}
