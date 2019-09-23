package com.credits.wallet.desktop.database;

import com.credits.wallet.desktop.database.pojo.SmartContract;
import com.j256.ormlite.core.dao.Dao;
import com.j256.ormlite.core.dao.DaoManager;
import com.j256.ormlite.core.support.ConnectionSource;
import com.j256.ormlite.core.table.TableUtils;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;

import static com.credits.wallet.desktop.utils.GeneralUtils.getResourceAsStream;
import static java.nio.charset.StandardCharsets.UTF_8;

@Slf4j
public class DatabaseHelper {

    private final String databaseUrl;
    private ConnectionSource connectionSource;
    private Dao<SmartContract, String> smartContracts;

    public DatabaseHelper(String databaseUrl) {
        this.databaseUrl = databaseUrl;
    }

    public void connect() {
        try {
            connectionSource = new JdbcConnectionSource(databaseUrl);
        } catch (SQLException e) {
            log.error("can't connect to database. Reason {}", e.getMessage());
            throw new RuntimeException(e);
        }
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

    public void createDatabaseSchemeIfNotExist() {
        try (var connection = DriverManager.getConnection(databaseUrl);
             var statement = connection.createStatement()) {
            var sqlFileStream = IOUtils.toString(getResourceAsStream("/sql/create_db.sql"), UTF_8);
            final var sqlRequests = sqlFileStream.split(";");
            for (var sql : sqlRequests) {
                statement.executeUpdate(sql);
            }

        } catch (SQLException | IOException e) {
            log.error("can't create database scheme. Reason {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
