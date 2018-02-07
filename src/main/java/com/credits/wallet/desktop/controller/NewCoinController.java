package com.credits.wallet.desktop.controller;

import com.credits.wallet.desktop.App;
import com.credits.wallet.desktop.AppState;
import com.credits.wallet.desktop.Utils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by goncharov-eg on 07.02.2018.
 */
public class NewCoinController extends Controller implements Initializable {
    @FXML
    private TextField txToken;
    @FXML
    private TextField txCoin;
    @FXML
    private TextField txBalance;

    @FXML
    private void handleBack() {
        App.showForm("/fxml/form6.fxml", "Wallet");
    }

    @FXML
    private void handleSave() {
        String coin=txCoin.getText().replace(";","");
        String token=txToken.getText().replace(";", "");

        if (coin.isEmpty()) {
            Utils.showError("You must enter coin mnemonic");
            return;
        }

        if (token.isEmpty()) {
            Utils.showError("You must enter token");
            return;
        }

        if (AppState.coins.contains(coin)) {
            Utils.showError("Coin already exists");
            return;
        }

        String strToWrite=coin+";"+token+";"+txBalance.getText();

        // Save to csv
        BufferedWriter bw = null;
        FileWriter fw = null;
        try {
            fw = new FileWriter(new File("coins.csv"), true);
            bw = new BufferedWriter(fw);
            bw.write(strToWrite+"\n");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (bw != null)
                    bw.close();
                if (fw != null)
                    fw.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        App.showForm("/fxml/form6.fxml", "Wallet");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        try {
            String token = (String)clipboard.getData(DataFlavor.stringFlavor);
            txToken.setText(token);
        } catch (Exception e) {
            e.printStackTrace();
        }

        txBalance.setOnKeyReleased(event -> {
            String s1=txBalance.getText();
            String s2= Utils.correctNum(s1);
            if (!s1.equals(s2)) {
                txBalance.setText(s2);
                txBalance.positionCaret(s2.length());
            }
        });
    }
}
