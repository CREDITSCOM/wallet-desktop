package com.credits.wallet.desktop.database;

import com.credits.wallet.desktop.database.pojo.SmartContract;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;

import static java.nio.file.Files.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DatabaseHelperTest {

    public static final Path dbDir = Paths.get("./testdb");
    public static final Path dbPath = Paths.get(dbDir.toString(), "wallet-cache-test.db");
    private DatabaseHelper db;

    @BeforeEach
    void setUp() throws SQLException, IOException {
        if (!exists(dbDir)) createDirectory(dbDir);
        db = new DatabaseHelper("jdbc:sqlite:" + dbPath.toString());
        db.connect();
    }

    @AfterEach
    void tearDown() throws IOException {
        deleteIfExists(dbPath);
        deleteIfExists(dbDir);
    }

    @Test
    void createTableThenAddAndGetSmartContract() throws SQLException {
        final var contract = new SmartContract("source", "target", "someCode", System.currentTimeMillis());

        db.createTable(contract.getClass());
        db.addSmartContract(contract);
        final var returnedContract = db.getSmartContract(contract.getAddress());

        assertEquals(contract, returnedContract);
    }
}