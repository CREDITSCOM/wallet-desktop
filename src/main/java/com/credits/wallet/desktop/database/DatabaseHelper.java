package com.credits.wallet.desktop.database;

import com.credits.general.exception.CreditsException;
import com.credits.wallet.desktop.database.table.*;
import com.j256.ormlite.core.dao.Dao;
import com.j256.ormlite.core.support.ConnectionSource;
import com.j256.ormlite.core.table.TableUtils;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Callable;

import static com.credits.general.util.Utils.CheckedRunnable;
import static com.credits.wallet.desktop.utils.GeneralUtils.getResourceAsStream;
import static com.j256.ormlite.core.dao.DaoManager.createDao;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.lang3.exception.ExceptionUtils.getRootCauseMessage;

@Slf4j
public class DatabaseHelper {

    private final String databaseUrl;
    private ConnectionSource connectionSource;
    private Dao<SmartContract, Long> smartContractDao;
    private Dao<Wallet, Long> walletDao;
    private Dao<Transaction, Long> transactionDao;
    private Dao<ApplicationMetadata, Wallet> applicationMetadataDao;
    private Dao<Argument, ?> argumentDao;
    private Dao<SmartContractHasBytecode, ?> smartContractHasBytecodeDao;
    private Dao<Bytecode, ?> bytecodeDao;

    public DatabaseHelper(String databaseUrl) {
        this.databaseUrl = databaseUrl;
    }

    public void connectAndInitialize() {
        try {
            connectionSource = new JdbcConnectionSource(databaseUrl);
            argumentDao = createDao(connectionSource, Argument.class);
            smartContractDao = createDao(connectionSource, SmartContract.class);
            walletDao = createDao(connectionSource, Wallet.class);
            transactionDao = createDao(connectionSource, Transaction.class);
            applicationMetadataDao = createDao(connectionSource, ApplicationMetadata.class);
            argumentDao = createDao(connectionSource, Argument.class);
            smartContractHasBytecodeDao = createDao(connectionSource, SmartContractHasBytecode.class);
            bytecodeDao = createDao(connectionSource, Bytecode.class);

        } catch (SQLException e) {
            log.error("can't connect to database. Reason {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void createTable(Class<?> clazz) throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, clazz);
    }

    public void keepSmartContract(SmartContract smartContract) throws SQLException {
        smartContractDao.createIfNotExists(smartContract);
    }

    public void keepSmartContractList(List<SmartContract> smartContractList) {
        rethrowWithDetailMessage(() -> smartContractDao.create(smartContractList));
    }

    public void keepSmartContractHasByteCodeList(List<SmartContractHasBytecode> smartContractHasBytecodeList) {
        rethrowWithDetailMessage(() -> smartContractHasBytecodeDao.create(smartContractHasBytecodeList));
    }

    public SmartContract getSmartContract(String address) throws SQLException {
        return smartContractDao.queryBuilder().where().eq("wallet_address", address).queryForFirst();
    }

    public void createIfNotExistsTransaction(Transaction transaction) {
        rethrowWithDetailMessage(() -> transactionDao.createIfNotExists(transaction));
    }

    public void keepTransactionsList(List<Transaction> transactionList) {
        rethrowWithDetailMessage(() -> transactionDao.create(transactionList));
    }

    public Wallet getOrCreateWallet(String address) {
        return rethrowWithDetailMessage(() -> {
            var wallet = findWalletByAddress(address);
            if (wallet == null) {
                wallet = new Wallet(address);
                walletDao.create(wallet);
            }
            return wallet;
        });
    }

    public void updateApplicationMetadata(ApplicationMetadata metadata) {
        rethrowWithDetailMessage(() -> applicationMetadataDao.update(metadata));
    }

    public ApplicationMetadata getOrCreateApplicationMetadata(String address) {
        return rethrowWithDetailMessage(() -> {
            final var wallet = getOrCreateWallet(address);
            var metadata = applicationMetadataDao.queryBuilder().where().eq("account_id", wallet.getId()).queryForFirst();
            if (metadata == null) {
                metadata = new ApplicationMetadata(wallet, 0);
                applicationMetadataDao.create(metadata);
            }
            return metadata;
        });
    }

    private Wallet findWalletByAddress(String address) throws SQLException {
        return walletDao.queryBuilder().where().eq("address", address).queryForFirst();
    }

    public List<Transaction> getTransactionsByAddress(String address) {
        return rethrowWithDetailMessage(() -> getTransactionsByAddress(address, -1L));
    }

    public List<Transaction> getTransactionsByAddress(String address, long limit) {
        return rethrowWithDetailMessage(() -> transactionDao.queryBuilder()
                .limit(limit > 0 ? limit : null)
                .where().eq("sender_address", address)
                .query());
    }

    public List<Transaction> getLastTransactions(String address, long blockNumber, long limit) {
        return rethrowWithDetailMessage(() -> transactionDao.queryBuilder()
                .limit(limit > 0 ? limit : null)
                .orderBy("block_number", false)
                .where().eq("sender_address", address).and().ge("block_number", blockNumber)
                .query());
    }

    public void keepBytecodeList(List<Bytecode> bytecodeList) {
        rethrowWithDetailMessage(() -> bytecodeDao.create(bytecodeList));
    }

    public void createTablesIfNotExist() {
        rethrowWithDetailMessage(() -> {
            createTable(Wallet.class);
            createTable(Transaction.class);
            createTable(ApplicationMetadata.class);
            createTable(Argument.class);
            createTable(ArgumentValue.class);
            createTable(Method.class);
            createTable(SmartContract.class);
            createTable(SmartContractCall.class);
            createTable(SmartContractHasBytecode.class);
            createTable(Bytecode.class);
        });
    }

    public void createSchemeFromResourceFile(String path) {
        try (var connection = DriverManager.getConnection(databaseUrl);
             var statement = connection.createStatement()) {
            var sqlFileStream = IOUtils.toString(getResourceAsStream(path), UTF_8);
            final var sqlRequests = sqlFileStream.split(";");
            for (var sql : sqlRequests) {
                statement.executeUpdate(sql);
            }

        } catch (SQLException | IOException e) {
            log.error("can't create database scheme. Reason {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private static void rethrowWithDetailMessage(CheckedRunnable runnable) {
        try {
            runnable.run();
        } catch (Exception e) {
            throw new DatabaseHelperException(getRootCauseMessage(e));
        }
    }

    private static <R> R rethrowWithDetailMessage(Callable<R> callable) {
        try {
            return callable.call();
        } catch (Exception e) {
            throw new DatabaseHelperException(getRootCauseMessage(e));
        }
    }

    public static class DatabaseHelperException extends CreditsException {

        private static final long serialVersionUID = 372937201016271310L;

        public DatabaseHelperException(String errorMessage) {
            super(errorMessage);
        }
    }
}
