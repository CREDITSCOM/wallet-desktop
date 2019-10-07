package com.credits.wallet.desktop.service;

import com.credits.client.node.pojo.TransactionData;
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
        runAsync(writeLock(() -> {
            final var metadata = database.getOrCreateApplicationMetadata(address);
            var receivedTrx = metadata.getAmountTransactions();
            var totalTrx = metadata.getAmountTransactions();
            for (var diff = 10; diff > 0; diff = min(totalTrx - receivedTrx, 100)) {
                final var response = nodeApiService.getTransactionsAndAmount(address, receivedTrx, diff);
                receivedTrx += response.getTransactionsList().size();
                totalTrx = response.getAmountTotalTransactions();
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

                        final var bytecodeObjects = deployData.getByteCodeObjects();
                        bytecodeObjects.forEach(it -> {
                            final var bytecodeEntity = new Bytecode(it.getName(), it.getByteCode());
                            bytecodeRelation.add(bytecodeEntity);
                            smartContractHasByteCodeRelation.add(new SmartContractHasBytecode(smartContractEntity, bytecodeEntity));
                        });
                    }
                }
                database.keepTransactionsList(transactionRelation);
                database.keepBytecodeList(bytecodeRelation);
                database.keepSmartContractList(smartContractRelation);
                database.keepSmartContractHasByteCodeList(smartContractHasByteCodeRelation);
                database.keepWalletHasSmartContractList(walletHasSmartContractsRelation);
            }
            metadata.setAmountTransactions(receivedTrx);
            database.updateApplicationMetadata(metadata);
        })).exceptionally(exception -> {
            log.error("error occurred while update transactions table. Reason: {}", exception.getMessage());
            return null;
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

    private <R> Supplier<R> writeLock(Supplier<R> supplier) {
        return wrapLock(lock.writeLock(), supplier);
    }

    private <R> Supplier<R> readLock(Supplier<R> supplier) {
        return wrapLock(lock.readLock(), supplier);
    }

    private Runnable writeLock(Runnable runnable) {
        return wrapLock(lock.writeLock(), runnable);
    }

    private Runnable readLock(Runnable runnable) {
        return wrapLock(lock.readLock(), runnable);
    }

    private Runnable wrapLock(Lock lock, Runnable runnable) {
        return () -> {
            try {
                lock.lock();
                runnable.run();
            } finally {
                lock.unlock();
            }
        };
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
