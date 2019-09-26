package com.credits.wallet.desktop.service;

import com.credits.client.node.pojo.TransactionData;
import com.credits.client.node.service.NodeApiService;
import com.credits.wallet.desktop.database.DatabaseHelper;
import com.credits.wallet.desktop.database.table.Transaction;
import com.credits.wallet.desktop.database.table.TransactionType;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

import static com.credits.general.util.GeneralConverter.encodeToBASE58;
import static java.lang.Math.min;
import static java.util.concurrent.CompletableFuture.runAsync;
import static java.util.stream.Collectors.toList;

@Slf4j
public class DatabaseServiceImpl implements DatabaseService {
    final NodeApiService nodeApiService;
    final DatabaseHelper database;

    public DatabaseServiceImpl(NodeApiService nodeApiService, DatabaseHelper database) {
        this.nodeApiService = nodeApiService;
        this.database = database;
        database.connectAndInitialize();
        database.createTablesIfNotExist();
    }

    @Override
    public void updateTransactionsOnAddress(String address) {
        runAsync(() -> {
            var receivedTrx = 0;
            var totalTrx = 0;
            for (var diff = 10; diff > 0; diff = min(totalTrx - receivedTrx, 100)) {
                final var response = nodeApiService.getTransactionsAndAmount(address, receivedTrx, diff);
                receivedTrx += response.getTransactionsList().size();
                totalTrx = response.getAmountTotalTransactions();
                final var transactions = response.getTransactionsList();
                final var entities = transactions.stream().map(this::createTransactionDBEntity).collect(toList());
                database.keepTransactionsList(entities);
            }
        }).exceptionally(exception -> {
            log.error("error occurred while update transactions table. Reason: {}", exception.getMessage());
            return null;
        });
    }

    private Transaction createTransactionDBEntity(TransactionData transactionData) {
        final var sender = database.createWalletIfNotExist(encodeToBASE58(transactionData.getSource()));
        final var receiver = database.createWalletIfNotExist(encodeToBASE58(transactionData.getTarget()));
        final var amount = transactionData.getAmount().toString();
        final var fee = "";
        final var timeCreation = 0L;
        final var transactionType = new TransactionType(transactionData.getType().toString());
        final var blockNumber = transactionData.getBlockId();
        final var userData = transactionData.getCommentBytes() != null
                             ? new String(transactionData.getCommentBytes(), StandardCharsets.UTF_8)
                             : "";

        return new Transaction(sender, receiver, amount, fee, timeCreation, userData, transactionType, blockNumber);
    }
}
