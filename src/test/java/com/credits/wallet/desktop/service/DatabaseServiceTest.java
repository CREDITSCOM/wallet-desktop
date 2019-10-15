package com.credits.wallet.desktop.service;

import com.credits.client.node.pojo.TransactionData;
import com.credits.client.node.pojo.TransactionListByAddressData;
import com.credits.client.node.pojo.TransactionTypeData;
import com.credits.client.node.service.NodeApiService;
import com.credits.wallet.desktop.database.DatabaseHelper;
import com.credits.wallet.desktop.database.table.ApplicationMetadata;
import com.credits.wallet.desktop.database.table.Wallet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.mockito.Mockito.*;

class DatabaseServiceTest {

    DatabaseService database;
    private NodeApiService mockNodeApi;
    private DatabaseHelper mockDBHelper;
    private TransactionData mockTransaction;
    private String address;

    @BeforeEach
    void setUp() {
        mockNodeApi = mock(NodeApiService.class);
        mockDBHelper = mock(DatabaseHelper.class);

        database = new DatabaseServiceImpl(mockNodeApi, mockDBHelper);
        mockTransaction = new TransactionData(0L, new byte[0], new byte[0], BigDecimal.ONE, TransactionTypeData.TT_Normal);
        mockTransaction.setBlockNumber(0);
        mockTransaction.setIndexIntoBlock(1);
        address = "address";

        Wallet wallet = new Wallet(address);
        when(mockDBHelper.getOrCreateApplicationMetadata(any())).thenReturn(new ApplicationMetadata(wallet, 0));
        when(mockDBHelper.getOrCreateWallet(any())).thenReturn(wallet);
    }

    @Test
    void updateTransactions_return_1_transaction() throws InterruptedException {
        configureNodeMockReturnTransactions(1, 0, 100);
        database.updateTransactionsOnAddress(address);
        Thread.sleep(100);
        verify(mockNodeApi).getTransactionList(address, 0, 100);
    }

    @Test
    void updateTransactions_return_100_transaction() throws InterruptedException {

        configureNodeMockReturnTransactions(100, 0, 100);

        database.updateTransactionsOnAddress(address);
        Thread.sleep(100);

        verify(mockNodeApi).getTransactionList(address, 0, 100);
    }

    @Test
    void updateTransactions_return_300_transaction() throws InterruptedException {
        configureNodeMockReturnTransactions(300, 0, 100);
        configureNodeMockReturnTransactions(300, 100, 100);
        configureNodeMockReturnTransactions(300, 200, 100);
        configureNodeMockReturnTransactions(300, 300, 100);

        database.updateTransactionsOnAddress(address);
        Thread.sleep(100);

        verify(mockNodeApi).getTransactionList(address, 0, 100);
        verify(mockNodeApi).getTransactionList(address, 100, 100);
        verify(mockNodeApi).getTransactionList(address, 200, 100);
    }



    private void configureNodeMockReturnTransactions(int amountTotalTransactions, int offset, int limit) {
        final var responseTransactions = new ArrayList<TransactionData>();
        for (int i = 0; i < limit; i++) {
            responseTransactions.add(mockTransaction);
        }
        final var transactionAndAmountData = new TransactionListByAddressData(amountTotalTransactions, responseTransactions);
        when(mockNodeApi.getTransactionList("address", offset, limit)).thenReturn(transactionAndAmountData);
    }

}