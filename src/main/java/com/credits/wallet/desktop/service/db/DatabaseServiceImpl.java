package com.credits.wallet.desktop.service.db;

import com.credits.client.node.pojo.SmartContractData;
import com.credits.client.node.service.NodeApiService;
import com.credits.general.util.Callback;
import com.credits.wallet.desktop.database.DatabaseHelper;
import com.credits.wallet.desktop.database.table.ApplicationMetadata;
import com.credits.wallet.desktop.database.table.SmartContract;
import com.credits.wallet.desktop.database.table.Transaction;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.function.Supplier;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.util.concurrent.CompletableFuture.runAsync;
import static java.util.concurrent.CompletableFuture.supplyAsync;
import static java.util.stream.Collectors.toUnmodifiableList;
import static lombok.AccessLevel.PACKAGE;

@Slf4j
public class DatabaseServiceImpl implements DatabaseService {
    @Getter(PACKAGE)
    private final NodeApiService nodeApi;
    @Getter(PACKAGE)
    private final DatabaseHelper database;
    private final ConcurrentAccessor concurrentAccessor;

    public DatabaseServiceImpl(NodeApiService nodeApi, DatabaseHelper database) {
        this.nodeApi = nodeApi;
        this.database = database;
        database.connectAndInitialize();
        database.createTablesIfNotExist();
        concurrentAccessor = new ConcurrentAccessor();
    }

    public void keepLogin(String address) {
        runAsync(() -> concurrentAccessor.write(() -> database.getOrCreateApplicationMetadata(address)));
    }

    @Override
    public void syncUpdateAccountDatabase(String address) {
        final var metadata = database.getOrCreateApplicationMetadata(address);
        var stored = metadata.getAmountTransactions();
        var limit = 100;
        var added = stored;
        var total = nodeApi.getTransactionList(address, 0, 1).getAmountTotalTransactions();
        var diff = 0;
        added = resetDatabaseIfNotActual(total, added, metadata);
        while ((diff = total - added) > 0) {
            final var response = nodeApi.getTransactionList(address, max(diff - limit, 0), min(limit, diff));
            total = response.getAmountTotalTransactions();
            final var amountRequestTransactions = response.getTransactionsList().size();

            if (total != stored) {
                final var addedTransactions = added += amountRequestTransactions;
                final var transactionRelationBuilder = new TransactionRelationBuilder(database);
                transactionRelationBuilder.parse(response);

                final var calledSmartContractList = transactionRelationBuilder.getCalledContractAddressList();
                final var contractRelationsBuilder = calledSmartContractList.size() > 0 ? new SmartContractRelationsBuilder(database) : null;
                if (contractRelationsBuilder != null) {
                    final var smartContractDataList = calledSmartContractList.stream().map(nodeApi::getSmartContract).collect(toUnmodifiableList());
                    contractRelationsBuilder.parse(smartContractDataList);
                }

                concurrentAccessor.write(() -> {
                    transactionRelationBuilder.updateDatabase();
                    if (contractRelationsBuilder != null)
                        contractRelationsBuilder.updateDatabase();
                    updateAmountTransactions(addedTransactions, metadata);
                });
            }
        }
    }

    private int resetDatabaseIfNotActual(int total, int added, ApplicationMetadata metadata) {
        if (total - added < 0) {
            concurrentAccessor.write(() -> {
                database.clearAllTables();
                updateAmountTransactions(0, metadata);
            });
            added = 0;
        }
        return added;
    }

    private void updateAmountTransactions(int amount, ApplicationMetadata metadata) {
        metadata.setAmountTransactions(amount);
        database.updateApplicationMetadata(metadata);
    }

    @Override
    public void getLastTransactions(String address, long limit, Callback<List<Transaction>> handleResult) {
        asyncRead(() -> database.getLastTransactions(address, 0, limit), handleResult);
    }

    @Override
    public void getLastTransactions(String address, long blockNumber, long limit, Callback<List<Transaction>> handleResult) {
        asyncRead(() -> database.getLastTransactions(address, blockNumber, limit), handleResult);
    }

    @Override
    public void getDeployerContractsAddressList(String deployer, Callback<List<String>> handleResult) {
        asyncRead(() -> database.getSmartContractsAddressList(deployer), handleResult);
    }

    @Override
    public void getSmartContract(String address, Callback<SmartContract> handleResult) {
        asyncRead(() -> database.getSmartContract(address), handleResult);
    }

    @Override
    public void getFavoriteContractsList(String address, Callback<List<String>> handleResult) {
        asyncRead(() -> database.getFavoriteContractsList(address), handleResult);
    }

    @Override
    public void deleteFavoriteContract(String account, String contractAddress) {
        asyncWrite(() -> database.deleteFavoriteContract(account, contractAddress));
    }

    @Override
    public void keepFavoriteContract(String accountAddress, String contractAddress) {
        asyncWrite(() -> database.keepFavoriteContract(accountAddress, contractAddress));
    }

    @Override
    public void keepSmartContract(SmartContractData smartContractData) {
        final var contractRelationsBuilder = new SmartContractRelationsBuilder(database);
        contractRelationsBuilder.parse(smartContractData);
        asyncWrite(contractRelationsBuilder::updateDatabase);
    }

    @Override
    public void getTransactions(String address, Callback<List<Transaction>> handleResult) {
        asyncRead(() -> database.getTransactionsByAddress(address), handleResult);
    }

    private <R> void asyncRead(Supplier<R> supplier, Callback<R> handleResult) {
        supplyAsync(concurrentAccessor.read(supplier)).whenComplete(Callback.handleCallback(handleResult));
    }

    private <R> void asyncWrite(Supplier<R> supplier, Callback<R> handleResult) {
        supplyAsync(concurrentAccessor.write(supplier)).whenComplete(Callback.handleCallback(handleResult));
    }

    private void asyncWrite(Runnable runnable) {
        runAsync(() -> concurrentAccessor.write(runnable));
    }
}
