package com.credits.service.db.leveldb;

import com.credits.leveldb.client.data.PoolData;
import com.credits.leveldb.client.data.SmartContractData;
import com.credits.leveldb.client.data.TransactionData;
import com.credits.leveldb.client.data.TransactionFlowData;

import java.math.BigDecimal;
import java.util.List;

public interface LevelDbInteractionService {

    BigDecimal getBalance(String address, String currency) throws Exception;

    TransactionData getTransaction(String transactionId) throws Exception;

    List<TransactionData> getTransactions(String address, long offset, long limit) throws Exception;

    List<PoolData> getPoolList(long offset, long limit) throws Exception;

    PoolData getPoolInfo(byte[] hash, long index) throws Exception;

    void transactionFlow(String innerId,
                         String source,
                         String target,
                         BigDecimal amount,
                         String currency,
                         String signature,
                         SmartContractData smartContractData) throws Exception;

    void transactionFlowWithFee(TransactionFlowData transactionFlowData, TransactionFlowData transactionFlowDataFee, boolean checkBalance) throws Exception;
}
