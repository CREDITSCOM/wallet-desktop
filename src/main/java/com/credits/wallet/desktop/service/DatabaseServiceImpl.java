package com.credits.wallet.desktop.service;

import com.credits.client.node.pojo.TransactionData;
import com.credits.client.node.pojo.TransactionListByAddressData;
import com.credits.client.node.service.NodeApiService;
import com.credits.general.util.Callback;
import com.credits.general.util.GeneralConverter;
import com.credits.wallet.desktop.database.DatabaseHelper;
import com.credits.wallet.desktop.database.table.*;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;

import static com.credits.general.util.GeneralConverter.encodeToBASE58;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.util.concurrent.CompletableFuture.runAsync;
import static java.util.concurrent.CompletableFuture.supplyAsync;

@Slf4j
public class DatabaseServiceImpl implements DatabaseService {
    final NodeApiService nodeApiService;
    final DatabaseHelper database;
    private final ReadWriteLock lock;

    public DatabaseServiceImpl(NodeApiService nodeApiService, DatabaseHelper database) {
        this.nodeApiService = nodeApiService;
        this.database = database;
        database.connectAndInitialize();
        database.createTablesIfNotExist();
        lock = new ReentrantReadWriteLock(true);
    }

    @Override
    public void keepLogin(String address) {
        runAsync(() -> writeLock(() -> database.getOrCreateApplicationMetadata(address)));
    }

    @Override
    public void updateTransactionsOnAddress(String address) {
        final var metadata = database.getOrCreateApplicationMetadata(address);
        var stored = metadata.getAmountTransactions();
        var limit = 100;
        var added = stored;
        var total = nodeApiService.getTransactionList(address, 0, 1).getAmountTotalTransactions();
        var diff = 0;
        added = resetDatabaseIfNotActual(total, added, metadata);
        while ((diff = total - added) > 0) {
            final var response = nodeApiService.getTransactionList(address, max(diff - limit, 0), min(limit, diff));
            total = response.getAmountTotalTransactions();
            final var amountRequestTransactions = response.getTransactionsList().size();

            if (total != stored) {
                parseTransactionsThenUpdateDB(response);
                added += amountRequestTransactions;
                updateAmountTransactions(added, metadata);
            }
        }
    }

    private int resetDatabaseIfNotActual(int total, int added, ApplicationMetadata metadata) {
        if (total - added < 0) {
            writeLock(() -> {
                database.clearAllTables();
                updateAmountTransactions(0, metadata);
            });
            added = 0;
        }
        return added;
    }

    private void updateAmountTransactions(int amount, ApplicationMetadata metadata) {
        metadata.setAmountTransactions(amount);
        writeLock(() -> database.updateApplicationMetadata(metadata));
    }

    private void parseTransactionsThenUpdateDB(TransactionListByAddressData response) {
        final var transactions = response.getTransactionsList();
        final var transactionRelation = new ArrayList<Transaction>();
        final var smartContractRelation = new ArrayList<SmartContract>();
        final var bytecodeRelation = new ArrayList<Bytecode>();
        final var walletHasSmartContractsRelation = new ArrayList<WalletHasSmartContract>();
        final var smartContractHasByteCodeRelation = new ArrayList<SmartContractHasBytecode>();
        for (final var transaction : transactions) {
            final var transactionEntity = createTransactionDBEntity(transaction);
            transactionRelation.add(transactionEntity);
            final var smartInfo = transaction.getSmartInfo();
            if (smartInfo != null) {
                final var wallet = transactionEntity.getReceiver();
                final var smartContract = nodeApiService.getSmartContract(wallet.getAddress());
                final var deployData = smartContract.getSmartContractDeployData();
                final var sourceCode = deployData.getSourceCode();
                final var timeCreation = smartContract.getTimeCreation();
                final var contractState = smartContract.getObjectState();
                final var smartContractEntity = new SmartContract(wallet, sourceCode, contractState, timeCreation);
                smartContractRelation.add(smartContractEntity);
                walletHasSmartContractsRelation.add(new WalletHasSmartContract(transactionEntity.getSender(), smartContractEntity));

                final var bytecodeObjects = deployData.getByteCodeObjectDataList();
                bytecodeObjects.forEach(it -> {
                    final var bytecodeEntity = new Bytecode(it.getName(), it.getByteCode());
                    bytecodeRelation.add(bytecodeEntity);
                    smartContractHasByteCodeRelation.add(new SmartContractHasBytecode(smartContractEntity, bytecodeEntity));
                });
            }
        }
        writeLock(() -> {
            database.keepTransactionsList(transactionRelation);
            database.keepBytecodeList(bytecodeRelation);
            database.keepSmartContractList(smartContractRelation);
            database.keepSmartContractHasByteCodeList(smartContractHasByteCodeRelation);
            database.keepWalletHasSmartContractList(walletHasSmartContractsRelation);
        });
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
        database.deleteFavoriteContract(account, contractAddress);
    }

    @Override
    public void keepFavoriteContract(String accountAddress, String contractAddress) {
        asyncWrite(() -> database.keepFavoriteContract(accountAddress, contractAddress));
    }

    @Override
    public void getTransactions(String address, Callback<List<Transaction>> handleResult) {
        asyncRead(() -> database.getTransactionsByAddress(address), handleResult);
    }

    private Transaction createTransactionDBEntity(TransactionData transactionData) {
        final var sender = database.getOrCreateWallet(encodeToBASE58(transactionData.getSource()));
        final var receiver = database.getOrCreateWallet(encodeToBASE58(transactionData.getTarget()));
        final var amount = GeneralConverter.toString(transactionData.getAmount());
        final var maxFee = transactionData.getMaxFee();
        final var timeCreation = new Date(transactionData.getTimeCreation());
        final var transactionType = transactionData.getType().toString();
        final var blockNumber = transactionData.getBlockNumber();
        final var trxIndex = transactionData.getIndexIntoBlock();
        final var userData = transactionData.getCommentBytes() != null ? new String(transactionData.getCommentBytes(), StandardCharsets.UTF_8) : "";

        return new Transaction(sender, receiver, amount, maxFee, timeCreation, userData, transactionType, blockNumber, trxIndex);
    }

    private <R> void asyncRead(Supplier<R> supplier, Callback<R> handleResult) {
        supplyAsync(readLock(supplier)).whenComplete(Callback.handleCallback(handleResult));
    }

    private <R> void asyncWrite(Supplier<R> supplier, Callback<R> handleResult) {
        supplyAsync(writeLock(supplier)).whenComplete(Callback.handleCallback(handleResult));
    }

    private void asyncWrite(Runnable runnable) {
        runAsync(() -> writeLock(runnable));
    }

    private <R> Supplier<R> writeLock(Supplier<R> supplier) {
        return wrapLock(lock.writeLock(), supplier);
    }

    private <R> Supplier<R> readLock(Supplier<R> supplier) {
        return wrapLock(lock.readLock(), supplier);
    }

    private void writeLock(Runnable runnable) {
        wrapLock(lock.writeLock(), runnable);
    }

    private void readLock(Runnable runnable) {
        wrapLock(lock.readLock(), runnable);
    }

    private void wrapLock(Lock lock, Runnable runnable) {
        try {
            lock.lock();
            runnable.run();
        } finally {
            lock.unlock();
        }
    }

    private <R> Supplier<R> wrapLock(Lock lock, Supplier<R> supplier) {
        return () -> {
            try {
                lock.lock();
                return supplier.get();
            } finally {
                lock.unlock();
            }
        };
    }
}
