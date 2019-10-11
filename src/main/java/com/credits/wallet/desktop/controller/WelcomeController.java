package com.credits.wallet.desktop.controller;

import com.credits.wallet.desktop.VistaNavigator;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;

import java.util.Map;


public class WelcomeController extends AbstractController {

    public BorderPane bp;

    @FXML
    private void handleExistingAccount() {
        VistaNavigator.reloadForm(VistaNavigator.FORM_5);
    }

    @FXML
    private void handleNewAccount() {
        VistaNavigator.reloadForm(VistaNavigator.FORM_1);
    }

    @Override
    public void initialize(Map<String, ?> objects) {
    }

    @Override
    public void deinitialize() {

    }
}
