package com.credits.service.db.leveldb;

import com.credits.common.exception.CreditsCommonException;
import com.credits.common.utils.Base58;
import com.credits.ioc.Injector;
import com.credits.leveldb.client.data.CreateTransactionData;
import com.credits.leveldb.client.data.PoolData;
import com.credits.leveldb.client.data.TransactionData;
import com.credits.leveldb.client.data.TransactionIdData;
import com.credits.leveldb.client.exception.CreditsNodeException;
import com.credits.leveldb.client.exception.LevelDbClientException;
import com.credits.leveldb.client.service.LevelDbService;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.List;

public class LevelDbInteractionServiceThriftImpl implements LevelDbInteractionService {

    @Inject
    LevelDbService service;

    public LevelDbInteractionServiceThriftImpl(){
        Injector.INJECTOR.component.inject(this);
    }

    @Override
    public BigDecimal getBalance(String address, byte currency)
        throws LevelDbClientException, CreditsNodeException, CreditsCommonException {
        return service.getBalance(address);
    }

    @Override
    public TransactionData getTransaction(String transactionId) throws LevelDbClientException, CreditsNodeException {
        //TODO need add adapter from String to TransactionIdData
        return service.getTransaction(new TransactionIdData());
    }

    @Override
    public List<TransactionData> getTransactions(String address, long offset, long limit) throws LevelDbClientException, CreditsNodeException, CreditsCommonException {
        return service.getTransactions(address, offset, limit);
    }

    @Override
    public List<PoolData> getPoolList(long offset, long limit) throws LevelDbClientException, CreditsNodeException {
        return service.getPoolList(offset, limit);
    }

    @Override
    public PoolData getPoolInfo(byte[] hash, long index) throws LevelDbClientException, CreditsNodeException {
        return service.getPoolInfo(hash, index);
    }

    @Override
    public void transactionFlow(Long innerId, String source, String target, BigDecimal amount, byte currency, byte[] signature, BigDecimal fee)
        throws LevelDbClientException, CreditsNodeException, CreditsCommonException {
        short maxFee = 0x6648; //TODO need add fee converter from BigDecimal to short
        CreateTransactionData CreateTransactionData =
            new CreateTransactionData(System.currentTimeMillis(), Base58.decode(source), Base58.decode(target), amount, currency, maxFee, signature);
        service.createTransaction(CreateTransactionData, true);
    }
}
