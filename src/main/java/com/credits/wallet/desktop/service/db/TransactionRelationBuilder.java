package com.credits.wallet.desktop.service.db;

import com.credits.client.node.pojo.TransactionData;
import com.credits.client.node.pojo.TransactionListByAddressData;
import com.credits.general.util.GeneralConverter;
import com.credits.wallet.desktop.database.DatabaseHelper;
import com.credits.wallet.desktop.database.table.Transaction;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.*;

import static com.credits.general.util.GeneralConverter.encodeToBASE58;

@Slf4j
class TransactionRelationBuilder {
    private final DatabaseHelper database;
    private final List<Transaction> transactionRelation;
    @Getter
    private final Set<String> calledContractAddressList;

    TransactionRelationBuilder(DatabaseHelper database) {
        this.database = database;
        transactionRelation = new ArrayList<>();
        calledContractAddressList = new HashSet<>();
    }

    public void parse(TransactionListByAddressData response) {
        for (final var transaction : response.getTransactionsList()) {
            final var transactionEntity = createTransactionEntity(transaction);
            transactionRelation.add(transactionEntity);
            if (transaction.getSmartInfo() != null) {
                final var contractAddress = transactionEntity.getReceiver().getAddress();
                calledContractAddressList.add(contractAddress);
            }
        }
    }

    private Transaction createTransactionEntity(TransactionData transactionData) {
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

    public void updateDatabase() {
        database.keepTransactionsList(transactionRelation);
    }
}
