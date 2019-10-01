package com.credits.wallet.desktop.controller;

import com.credits.client.node.exception.NodeClientException;
import com.credits.general.exception.CreditsException;
import com.credits.general.util.Callback;
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

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.credits.wallet.desktop.AppState.NODE_ERROR;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME;


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
        final var columns = tableView.getColumns();
        columns.get(0).setCellValueFactory(new PropertyValueFactory<>("id"));
        columns.get(1).setCellValueFactory(new PropertyValueFactory<>("time"));
        columns.get(2).setCellValueFactory(new PropertyValueFactory<>("sender"));
        columns.get(3).setCellValueFactory(new PropertyValueFactory<>("receiver"));
        columns.get(4).setCellValueFactory(new PropertyValueFactory<>("amount"));
        columns.get(5).setCellValueFactory(new PropertyValueFactory<>("type"));
        tableView.setOnMousePressed(event -> {
            if ((event.isPrimaryButtonDown() || event.getButton() == MouseButton.PRIMARY) && event.getClickCount() == 2) {
                TransactionTabRow tabRow = tableView.getSelectionModel().getSelectedItem();
                if (tabRow != null) {
                    HashMap<String, Object> params = new HashMap<>();
                    params.put("selectedTransactionRow", tabRow);
                    VistaNavigator.showFormModal(VistaNavigator.TRANSACTION, params);
                }
            }
        });
    }

    private void fillApprovedTable() {
        AppState.getDatabase().getLastTransactions(session.account, lastBlockNumber, INIT_PAGE_SIZE, handleApprovedTransactions());
    }

    private Callback<List<Transaction>> handleApprovedTransactions() {
        return new Callback<>() {
            @Override
            public void onSuccess(List<Transaction> transactionList) throws CreditsException {

                List<TransactionTabRow> approvedList = new ArrayList<>();
                transactionList.forEach(transaction -> {
                    TransactionTabRow tableRow = new TransactionTabRow();
                    tableRow.setId(transaction.getBlockNumber() + "." + transaction.getIndexIntoBlock());
                    final String time = formatDateToString(transaction.getTimeCreation());
                    tableRow.setTime(time);
                    tableRow.setSender(transaction.getSender().getAddress());
                    tableRow.setReceiver(transaction.getReceiver().getAddress());
                    tableRow.setAmount(transaction.getAmount());
                    tableRow.setType(transaction.getType().getName());
                    approvedList.add(tableRow);

                });
                Platform.runLater(() -> {
                    approvedTableView.getItems().clear();
                    approvedTableView.getItems().addAll(approvedList);
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

    public String formatDateToString(Date date) {
        final var localDate = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
        return ISO_LOCAL_DATE_TIME.format(localDate).replace('T', ' ');
    }


    @FXML
    private void handleBack() {
        VistaNavigator.loadVista(VistaNavigator.WALLET);
    }

    @FXML
    private void handleRefresh() {
        fillApprovedTable();
    }

    @Override
    public void formDeinitialize() {

    }
}

