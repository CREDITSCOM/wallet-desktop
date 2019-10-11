package com.credits.wallet.desktop.controller;

import com.credits.client.node.crypto.Ed25519;
import com.credits.wallet.desktop.AppState;
import com.credits.wallet.desktop.VistaNavigator;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;

import java.security.KeyPair;
import java.util.Map;


public class GenerateKeysController extends AbstractController {
    @FXML
    Button btnShowPassword;

    @FXML
    PasswordField txPassword;

    @FXML
    Label labPassword;

    @FXML
    private void handleBack() {
        VistaNavigator.reloadForm(VistaNavigator.WELCOME);
    }

    @FXML
    private void handleGenerate() {
        KeyPair keyPair = Ed25519.generateKeyPair();
        AppState.setPwd(txPassword.getText());
        VistaNavigator.reloadForm(VistaNavigator.FORM_4);
    }

    @Override
    public void initialize(Map<String, ?> objects) {
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
    }
}

