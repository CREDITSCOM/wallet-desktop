package com.credits.wallet.desktop.controller;

import com.credits.client.node.crypto.Ed25519;
import com.credits.general.util.GeneralConverter;
import com.credits.wallet.desktop.AppState;
import com.credits.wallet.desktop.VistaNavigator;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.util.HashMap;
import java.util.Map;


public class SaveKeysController extends AbstractController {
    @FXML
    private TextField txKey;

    @FXML
    private void handleBack() {
        VistaNavigator.reloadForm(VistaNavigator.FORM_1);
    }

    @FXML
    private void handleContinue() {
        HashMap<String, Object> params = new HashMap<>();
        params.put("isNewAccount","true");
        VistaNavigator.reloadForm(VistaNavigator.FORM_5, params);
    }

    @Override
    public void initialize(Map<String, ?> objects) {
        txKey.setText(GeneralConverter.encodeToBASE58(Ed25519.privateKeyToBytes(AppState.getPrivateKey())));
    }

    @Override
    public void deinitialize() {

    }
}
