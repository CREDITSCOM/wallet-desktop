package com.credits.wallet.desktop.controller;

import com.credits.client.node.exception.NodeClientException;
import com.credits.client.node.pojo.TransactionData;
import com.credits.general.exception.CreditsException;
import com.credits.general.util.Callback;
import com.credits.general.util.GeneralConverter;
import com.credits.wallet.desktop.AppState;
import com.credits.wallet.desktop.VistaNavigator;
import com.credits.wallet.desktop.database.table.Transaction;
import com.credits.wallet.desktop.struct.TransactionTabRow;
import com.credits.wallet.desktop.utils.FormUtils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.credits.client.node.service.NodeApiServiceImpl.async;
import static com.credits.wallet.desktop.AppState.NODE_ERROR;


public class HistoryController extends AbstractController {
    private final String ERR_GETTING_TRANSACTION_HISTORY = "Error getting transaction history";
    private final int INIT_PAGE_SIZE = 100;
    private final int FIRST_TRANSACTION_NUMBER = 0;
    private final static Logger LOGGER = LoggerFactory.getLogger(HistoryController.class);
    private long lastBlockNumber;

    @FXML
    private TableView<TransactionTabRow> approvedTableView;
    @FXML
    private TableView<TransactionTabRow> unapprovedTableView;


    @Override
    public void initializeForm(Map<String, Object> objects) {
        initTable(approvedTableView);
        initTable(unapprovedTableView);

        fillApprovedTable();
//        fillUnapprovedTable();
    }

    private void initTable(TableView<TransactionTabRow> tableView) {
        tableView.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("id"));
        tableView.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("source"));
        tableView.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("target"));
        tableView.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("amount"));
        tableView.getColumns().get(4).setCellValueFactory(new PropertyValueFactory<>("type"));
        tableView.setOnMousePressed(event -> {
            if ((event.isPrimaryButtonDown()|| event.getButton() == MouseButton.PRIMARY) && event.getClickCount() == 2) {
                TransactionTabRow tabRow = tableView.getSelectionModel().getSelectedItem();
                if (tabRow != null) {
                    HashMap<String, Object> params = new HashMap<>();
                    params.put("selectedTransactionRow",tabRow);
                    VistaNavigator.showFormModal(VistaNavigator.TRANSACTION, params);
                }
            }
        });
    }

    private void fillUnapprovedTable() {
        async(() -> AppState.getNodeApiService().getTransactions(session.account, FIRST_TRANSACTION_NUMBER, INIT_PAGE_SIZE),
                handleUnapprovedTransactions());
    }

    private void fillApprovedTable() {
//        async(() -> AppState.getNodeApiService().getTransactions(session.account, FIRST_TRANSACTION_NUMBER, INIT_PAGE_SIZE),
//            handleApprovedTransactions());
        AppState.getDatabase().getLastTransactions(session.account, lastBlockNumber, INIT_PAGE_SIZE, handleApprovedTransactions());
    }

    private Callback<List<Transaction>> handleApprovedTransactions() {
        return new Callback<>() {
            @Override
            public void onSuccess(List<Transaction> transactionList) throws CreditsException {

                List<TransactionTabRow> approvedList = new ArrayList<>();
                TransactionTabRow tableRow = new TransactionTabRow();
                transactionList.forEach(transactionTable -> {
                    tableRow.setId(transactionTable.getId());
                    tableRow.setAmount(GeneralConverter.toString(transactionTable.getAmount()));
                    tableRow.setSource(transactionTable.getSender().getAddress());
                    tableRow.setTarget(transactionTable.getReceiver().getAddress());
                    tableRow.setBlockId(transactionTable.getBlockNumber() + "." + transactionTable.getIndexIntoBlock());
                    tableRow.setType(transactionTable.getType().getName());
                    approvedList.add(tableRow);

                });
                Platform.runLater(() -> {
                    approvedTableView.getItems().clear();
                    approvedTableView.getItems().addAll(approvedList);
                });
            }

            @Override
            public void onError(Throwable e) {

            }
        };
    }

//    private Callback<List<TransactionData>> handleApprovedTransactions() {
//        return new Callback<List<TransactionData>>() {
//
//            @Override
//            public void onSuccess(List<TransactionData> transactionsList) throws CreditsException {
//
//                List<TransactionTabRow> approvedList = new ArrayList<>();
//                transactionsList.forEach(transactionData -> {
//                    TransactionTabRow tableRow = new TransactionTabRow();
//                    tableRow.setId(transactionData.getInnerId());
//                    tableRow.setAmount(GeneralConverter.toString(transactionData.getAmount()));
//                    tableRow.setSource(GeneralConverter.encodeToBASE58(transactionData.getSource()));
//                    tableRow.setTarget(GeneralConverter.encodeToBASE58(transactionData.getTarget()));
//                    tableRow.setBlockId(transactionData.getBlockNumber() + "." + transactionData.getIndexIntoBlock());
//                    tableRow.setType(getTransactionDescType(transactionData));
//                    tableRow.setMethod(transactionData.getMethod());
//                    tableRow.setParams(transactionData.getParams());
//                    approvedList.add(tableRow);
//
//                    session.unapprovedTransactions.remove(transactionData.getInnerId());
//                });
//                refreshTableViewItems(approvedTableView, approvedList);
//            }
//
//            private void refreshTableViewItems(TableView<TransactionTabRow> tableView, List<TransactionTabRow> itemList) {
//                Platform.runLater(() -> {
//                    tableView.getItems().clear();
//                    tableView.getItems().addAll(itemList);
//                });
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                LOGGER.error(e.getMessage());
//                if (e instanceof NodeClientException) {
//                    FormUtils.showError(NODE_ERROR);
//                } else {
//                    FormUtils.showError(ERR_GETTING_TRANSACTION_HISTORY);
//                }
//            }
//        };
//    }

    private Callback<List<TransactionData>> handleUnapprovedTransactions() {
        return new Callback<>() {

            @Override
            public void onSuccess(List<TransactionData> transactionsList) throws CreditsException {

                List<TransactionTabRow> unapprovedList = new ArrayList<>();

                session.unapprovedTransactions.forEach((id, value) -> {

                    if (transactionsList.stream().anyMatch(transactionData ->
                                                                   transactionData.getInnerId() == id)) {
                        session.unapprovedTransactions.remove(id);
                    } else {
                        TransactionTabRow tableRow = new TransactionTabRow();
                        tableRow.setId(id);
                        tableRow.setAmount(value.getAmount());
                        tableRow.setCurrency(value.getCurrency());
                        tableRow.setSource(value.getSource());
                        tableRow.setTarget(value.getTarget());
                        tableRow.setType("unknown");
                        unapprovedList.add(tableRow);
                    }
                });
                refreshTableViewItems(unapprovedTableView, unapprovedList);
            }

            private void refreshTableViewItems(TableView<TransactionTabRow> tableView, List<TransactionTabRow> itemList) {
                Platform.runLater(() -> {
                    tableView.getItems().clear();
                    tableView.getItems().addAll(itemList);
                });
            }

            @Override
            public void onError(Throwable e) {
                LOGGER.error(e.getMessage());
                if (e instanceof NodeClientException) {
                    FormUtils.showError(NODE_ERROR);
                } else {
                    FormUtils.showError(ERR_GETTING_TRANSACTION_HISTORY);
                }
            }
        };
    }


    @FXML
    private void handleBack() {
        VistaNavigator.loadVista(VistaNavigator.WALLET);
    }

    @FXML
    private void handleRefresh() {
        fillApprovedTable();
//        fillUnapprovedTable();
    }

    @Override
    public void formDeinitialize() {

    }
}

