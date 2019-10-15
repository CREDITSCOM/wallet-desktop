package com.credits.wallet.desktop.service;

import com.credits.general.util.Callback;
import com.credits.wallet.desktop.database.table.SmartContract;
import com.credits.wallet.desktop.database.table.Transaction;

import java.util.List;

public interface DatabaseService {

    void keepLogin(String address);

    void updateTransactionsOnAddress(String address);

    void getTransactions(String address, Callback<List<Transaction>> handleResult);

    void getLastTransactions(String address, long limit, Callback<List<Transaction>> handleResult);

    void getLastTransactions(String address, long beginBlock, long limit, Callback<List<Transaction>> handleResult);

    void getDeployerContractsAddressList(String deployerAddress, Callback<List<String>> handleResult);

    void getSmartContract(String address, Callback<SmartContract> handleResult);
}
