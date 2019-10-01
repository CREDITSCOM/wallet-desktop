package com.credits.wallet.desktop.controller;

import com.credits.wallet.desktop.struct.TransactionTabRow;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

import java.util.Map;


public class TransactionController extends AbstractController{
    private static final String ERR_GETTING_TRANSACTION = "Error getting transaction details";
    public static final int MAX_HEIGHT = 300;

    final int ROW_HEIGHT = 24;
    @FXML
    public HBox listContainer;

    @FXML
    private TextField labTransactionId;
    @FXML
    private TextField labSource;
    @FXML
    private TextField labTarget;
    @FXML
    private TextField labAmount;
    @FXML
    private TextField labState;
    @FXML
    private TextField labMethod;
    @FXML
    private ListView listParams;

    @Override
    public void initializeForm(Map<String, Object> objects) {
        TransactionTabRow selectedTransactionRow = (TransactionTabRow) objects.get("selectedTransactionRow");

        labTransactionId.setText(selectedTransactionRow.getId());
        labSource.setText(selectedTransactionRow.getSender());
        labTarget.setText(selectedTransactionRow.getReceiver());
        labAmount.setText(selectedTransactionRow.getAmount());
        labState.setText(selectedTransactionRow.getType());
//        labMethod.setText(selectedTransactionRow.getMethod());
        ObservableList<String> items = FXCollections.observableArrayList();
//        List<Variant> params = selectedTransactionRow.getParams();
//        if (params != null) {
//            params.forEach(item -> items.add(VariantConverter.toObject(item).toString()));
//            listParams.setItems(items);
//        }
        int value = items.size() * ROW_HEIGHT + 2 > MAX_HEIGHT ? MAX_HEIGHT : items.size() * ROW_HEIGHT + 2;
        listContainer.setPrefHeight(value);

    }

    @Override
    public void formDeinitialize() {

    }
}
