package com.credits.wallet.desktop.database;

import com.credits.wallet.desktop.database.table.ApplicationMetadata;
import com.credits.wallet.desktop.database.table.SmartContract;
import com.credits.wallet.desktop.database.table.Transaction;
import com.credits.wallet.desktop.database.table.Wallet;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Date;

import static java.nio.file.Files.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DatabaseHelperTest extends DatabaseTest {

    public static final Path dbDir = Paths.get("./testdb");
    public static final Path dbPath = Paths.get(dbDir.toString(), "wallet-cache-test.db");
    private Wallet address1;
    private Wallet address2;
    private Transaction transaction1;
    private Transaction transaction2;
    private Transaction transaction3;
    private Wallet wallet;
    private SmartContract contract;

    @BeforeEach
    void setUp(TestInfo testInfo) throws IOException {
        if (!exists(dbDir)) createDirectory(dbDir);
        db = new DatabaseHelper("jdbc:sqlite:" + dbPath.toString());
        db.connectAndInitialize();
        processingTestAnnotations(testInfo);
        address1 = db.getOrCreateWallet("address1");
        address2 = db.getOrCreateWallet("address2");
        final var currentTime = Date.from(Instant.now());
        transaction1 = new Transaction(address1,
                                       address2,
                                       "1.00",
                                       0.1,
                                       currentTime,
                                                 "from address1 to address2",
                                                 "TT_NORMAL",
                                                 0,
                                                 0);
        transaction2 = new Transaction(address2,
                                       address1,
                                       "1.00",
                                       0.1,
                                       currentTime,
                                       "from address2 to address1",
                                       "TT_NORMAL",
                                       0,
                                       1);
        transaction3 = new Transaction(address2,
                                       address1,
                                       "2.00",
                                       0.1,
                                       currentTime,
                                       "from address2 to address1",
                                       "TT_NORMAL",
                                       0,
                                       2);
        wallet = new Wallet("someAddress");
        contract = new SmartContract(wallet, "someCode", new byte[0], System.currentTimeMillis());
    }

    @AfterEach
    void tearDown() throws IOException {
        deleteIfExists(dbPath);
        deleteIfExists(dbDir);
    }

    @Test
    @CreateTables({ApplicationMetadata.class, Wallet.class, SmartContract.class})
    void createTableThenAddAndGetSmartContract() throws SQLException {
        db.keepSmartContract(contract);

        final var returnedContract = db.getSmartContract(contract.getWallet().getAddress());

        assertEquals(contract, returnedContract);
    }

    @Test
    @CreateTables({Wallet.class, Transaction.class})
    void testingTransactionManipulationMethods() {
        db.createIfNotExistsTransaction(transaction1);
        db.createIfNotExistsTransaction(transaction2);
        db.createIfNotExistsTransaction(transaction3);

        final var transactionsFromAddress1 = db.getTransactionsByAddress(address1.getAddress());
        assertEquals(1, transactionsFromAddress1.size());
        assertEquals(transaction1, transactionsFromAddress1.get(0));

        final var transactionsFromAddress2 = db.getTransactionsByAddress(address2.getAddress());
        assertEquals(2, transactionsFromAddress2.size());
        assertEquals(transaction2, transactionsFromAddress2.get(0));
        assertEquals(transaction3, transactionsFromAddress2.get(1));
    }

    @Test
    @CreateTables({Wallet.class, Transaction.class})
    void keepSameTransactionTwice() {
        db.createIfNotExistsTransaction(transaction1);
        db.createIfNotExistsTransaction(transaction1);
    }

}