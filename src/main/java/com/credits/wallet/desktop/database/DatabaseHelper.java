package com.credits.wallet.desktop.database;

import com.credits.general.exception.CreditsException;
import com.credits.general.pojo.ByteCodeObjectData;
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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import static com.credits.general.util.Utils.CheckedRunnable;
import static com.credits.wallet.desktop.utils.GeneralUtils.getResourceAsStream;
import static com.j256.ormlite.core.dao.DaoManager.createDao;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.exception.ExceptionUtils.getRootCauseMessage;

@Slf4j
public class DatabaseHelper {

    private final String databaseUrl;
    private ConnectionSource connectionSource;
    private Dao<SmartContract, Long> smartContractDao;
    private Dao<Wallet, Long> walletDao;
    private Dao<Transaction, String> transactionDao;
    private Dao<ApplicationMetadata, Wallet> applicationMetadataDao;
    private Dao<Argument, Long> argumentDao;
    private Dao<SmartContractHasBytecode, Integer> smartContractHasBytecodeDao;
    private Dao<Bytecode, Integer> bytecodeDao;
    private Dao<WalletHasSmartContract, Long> walletHasSmartContractDao;
    private List<Dao<?, ?>> daoCollection;

    public DatabaseHelper(String databaseUrl) {
        this.databaseUrl = databaseUrl;
        daoCollection = new ArrayList<>();
    }

    public void connectAndInitialize() {
        try {
            connectionSource = new JdbcConnectionSource(databaseUrl);

            smartContractDao = createDaoAndAddToCollection(SmartContract.class);
            walletDao = createDaoAndAddToCollection(Wallet.class);
            transactionDao = createDaoAndAddToCollection(Transaction.class);
            applicationMetadataDao = createDaoAndAddToCollection(ApplicationMetadata.class);
            argumentDao = createDaoAndAddToCollection(Argument.class);
            smartContractHasBytecodeDao = createDaoAndAddToCollection(SmartContractHasBytecode.class);
            bytecodeDao = createDaoAndAddToCollection(Bytecode.class);
            walletHasSmartContractDao = createDaoAndAddToCollection(WalletHasSmartContract.class);

        } catch (SQLException e) {
            log.error("can't connect to database. Reason {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private <T, ID> Dao<T, ID> createDaoAndAddToCollection(Class<T> table) throws SQLException {
        final Dao<T, ID> dao = createDao(connectionSource, table);
        daoCollection.add(dao);
        return dao;
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

    public SmartContract getSmartContract(String address) {
        return rethrowWithDetailMessage(() -> {
            final var smartContract = smartContractDao.queryBuilder().where().eq("wallet_address", address).queryForFirst();
            final var bytecodeObjects = getSmartContractBytecodeObjects(address);
            smartContract.setByteCodeObjectList(bytecodeObjects);
            return smartContract;
        });
    }

    public void createIfNotExistsTransaction(Transaction transaction) {
        rethrowWithDetailMessage(() -> transactionDao.createIfNotExists(transaction));
    }

    public void keepTransactionsList(List<Transaction> transactionList) {
        rethrowWithDetailMessage(() -> transactionDao.create(transactionList));
    }

    public void keepWalletHasSmartContractList(List<WalletHasSmartContract> walletHasSmartContractList) {
        rethrowWithDetailMessage(() -> walletHasSmartContractDao.create(walletHasSmartContractList));
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

    public List<String> getSmartContractsAddressList(String address) {
        final var query = "select smart_contract.wallet_address as address " +
                "from wallet_has_smart_contract " +
                "join wallet on wallet_has_smart_contract.wallet_id = wallet.id " +
                "join smart_contract on smart_contract.id = wallet_has_smart_contract.smart_contract_id " +
                "where address = ?";
        return rethrowWithDetailMessage(() -> walletDao.queryRaw(query, walletDao.getRawRowMapper(), address)
                .getResults()
                .stream()
                .map(Wallet::getAddress)
                .collect(toList()));
    }

    public List<ByteCodeObjectData> getSmartContractBytecodeObjects(String address) {
        final var query = "select bytecode.* " +
                "from bytecode " +
                "join smart_contract_has_bytecode on bytecode.id = smart_contract_has_bytecode.bytecode_id " +
                "join smart_contract on smart_contract_has_bytecode.smart_contract_id = smart_contract.id " +
                "where smart_contract.wallet_address = ?";

        return rethrowWithDetailMessage(() -> bytecodeDao.queryRaw(query, Bytecode.rowMapper, address)
                .getResults()
                .stream()
                .map(it -> new ByteCodeObjectData(it.getClassName(), it.getBytecode()))
                .collect(toList()));
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
                .where().eq("sender_address", address).or().eq("receiver_address", address).and().ge("block_number", blockNumber)
                .query());
    }

    public void keepBytecodeList(List<Bytecode> bytecodeList) {
        rethrowWithDetailMessage(() -> bytecodeDao.create(bytecodeList));
    }

    public void createTablesIfNotExist() {
        daoCollection.forEach(dao -> rethrowWithDetailMessage(() -> createTable(dao.getDataClass())));
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

    public void clearAllTables() {
        daoCollection.forEach(dao -> rethrowWithDetailMessage(() -> TableUtils.clearTable(connectionSource, dao.getDataClass())));
    }

    public static class DatabaseHelperException extends CreditsException {

        private static final long serialVersionUID = 372937201016271310L;

        public DatabaseHelperException(String errorMessage) {
            super(errorMessage);
        }
    }
}
