package com.credits.wallet.desktop.service;

import com.credits.wallet.desktop.database.table.Transaction;

import java.util.List;
import java.util.function.BiConsumer;

public interface DatabaseService {

    void keepLogin(String address);

    void updateTransactionsOnAddress(String address);

    void getTransactions(String address, long limit,
                         BiConsumer<? super List<Transaction>, ? super Throwable> handleTransactionsResult);
}
