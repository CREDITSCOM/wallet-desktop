package com.credits.wallet.desktop.controller;

import com.credits.client.node.crypto.Ed25519;
import com.credits.wallet.desktop.AppState;
import com.credits.wallet.desktop.VistaNavigator;
import com.credits.wallet.desktop.utils.FormUtils;
import com.credits.wallet.desktop.utils.crypto.sodium.SodiumLibrary;
import com.credits.wallet.desktop.utils.crypto.sodium.SodiumLibraryException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;

import java.util.Map;

import static com.credits.general.util.GeneralConverter.decodeFromBASE58;


public class CheckPrivateKeyPwdController extends AbstractController {

    private String nonce;
    private String salt;
    private String encryptedPrivKey;
    private String publicKeyBase58;

    @FXML
    Button btnShowPassword;

    @FXML
    PasswordField txPassword;

    @FXML
    Label labPassword;

    private boolean validateKeys(byte[] publicKey, byte[] privateKey) {
        if (privateKey.length <= 32) {
            return false;
        }
        for (int i = 0; i < publicKey.length && i < privateKey.length - 32; i++) {
            if (publicKey[i] != privateKey[i + 32]) {
                return false;
            }
        }
        return true;
    }

    @FXML
    private void handleBack() {
        VistaNavigator.reloadForm(VistaNavigator.FORM_5);
    }

    @FXML
    private void handleOpen() {

        String pwd = txPassword.getText();

        if (pwd == null) { pwd = ""; }

        byte[] decryptedPrivateKey;
        try {
            byte[] key = SodiumLibrary.cryptoPwhashArgon2i(pwd.getBytes(), decodeFromBASE58(salt));
            decryptedPrivateKey = SodiumLibrary.cryptoSecretBoxOpenEasy(
                    decodeFromBASE58(encryptedPrivKey),
                    decodeFromBASE58(nonce),
                    key
            );
        } catch (SodiumLibraryException e) {
            FormUtils.showError("Invalid password");
            return;
        }

        final var publicKey = Ed25519.bytesToPublicKey(decodeFromBASE58(publicKeyBase58));
        final var privateKey = Ed25519.bytesToPrivateKey(decryptedPrivateKey);
        setSession(publicKeyBase58);
        AppState.setPublicKey(publicKey);
        AppState.setPrivateKey(privateKey);
        AppState.initializeNodeInteractionService(publicKeyBase58);

        byte[] publicKeyByteArr;
        try {
            publicKeyByteArr = decodeFromBASE58(publicKeyBase58);
            AppState.setPublicKey(Ed25519.bytesToPublicKey(publicKeyByteArr));
        } catch (Exception e) {
            FormUtils.showError(e.getMessage());
            return;
        }

        if (validateKeys(publicKeyByteArr, decryptedPrivateKey)) {
            VistaNavigator.reloadForm(VistaNavigator.WALLET);
        }
    }

    @Override
    public void initialize(Map<String, ?> objects) {

        nonce = (String)objects.get(PutKeysController.NONCE_KEY);
        salt = (String)objects.get(PutKeysController.SALT_KEY);
        encryptedPrivKey = (String)objects.get(PutKeysController.ENCRYPTED_PRIVKEY_KEY);
        publicKeyBase58 = (String)objects.get(PutKeysController.PUBKEY_KEY);


        txPassword.setVisible(true);
        labPassword.setVisible(false);

        labPassword.setPrefWidth(txPassword.getPrefWidth());
        labPassword.setPrefHeight(txPassword.getPrefHeight());
        labPassword.setLayoutX(txPassword.getLayoutX());
        labPassword.setLayoutY(txPassword.getLayoutY());

        btnShowPassword.setOnMousePressed(event -> {
            labPassword.setText(txPassword.getText());
            txPassword.setVisible(false);
            labPassword.setVisible(true);
        });

        btnShowPassword.setOnMouseReleased(event -> {
            txPassword.setVisible(true);
            labPassword.setVisible(false);
        });

        btnShowPassword.setOnMouseExited(event -> {
            txPassword.setVisible(true);
            labPassword.setVisible(false);
        });

        SodiumLibrary.initSodium();
    }
}

