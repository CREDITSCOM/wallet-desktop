package com.credits.wallet.desktop.database;

import com.credits.wallet.desktop.database.pojo.SmartContract;
import com.j256.ormlite.core.dao.Dao;
import com.j256.ormlite.core.dao.DaoManager;
import com.j256.ormlite.core.support.ConnectionSource;
import com.j256.ormlite.core.table.TableUtils;
import com.j256.ormlite.jdbc.JdbcConnectionSource;

import java.sql.SQLException;
import java.util.Objects;

public class DatabaseHelper {

    private final String databaseUrl;
    private ConnectionSource connectionSource;
    private Dao<SmartContract, String> smartContracts;

    public DatabaseHelper(String databaseUrl) {
        this.databaseUrl = databaseUrl;
    }

    public void connect() throws SQLException {
        connectionSource = new JdbcConnectionSource(databaseUrl);
    }

    public void createTable(Class<?> clazz) throws SQLException {
        checkConnection();
        TableUtils.createTable(connectionSource, clazz);
    }

    public void addSmartContract(SmartContract smartContract) throws SQLException {
        getSmartContractsDao().create(smartContract);
    }

    public SmartContract getSmartContract(String address) throws SQLException {
        return getSmartContractsDao().queryForId(address);
    }

    private void checkConnection() {
        Objects.requireNonNull(connectionSource, "database is not connected");
    }

    private Dao<SmartContract, String> getSmartContractsDao() throws SQLException {
        if (smartContracts == null) {
            checkConnection();
            smartContracts = DaoManager.createDao(connectionSource, SmartContract.class);
        }
        return smartContracts;
    }
}
