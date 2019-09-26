package com.credits.wallet.desktop.database.table;

import com.j256.ormlite.core.field.DatabaseField;
import com.j256.ormlite.core.table.DatabaseTable;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
@DatabaseTable(tableName = "transaction")
public class Transaction {
    @DatabaseField(generatedId = true)
    long id;
    @DatabaseField(columnName = "sender_id", foreign = true, foreignAutoRefresh = true)
    Wallet sender;
    @DatabaseField(columnName = "receiver_id", foreign = true, foreignAutoRefresh = true)
    Wallet receiver;
    @DatabaseField
    String amount;
    @DatabaseField
    String fee;
    @DatabaseField(columnName = "time_stamp")
    long timestamp;
    @DatabaseField(columnName = "user_data")
    String userData;
    @DatabaseField(foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true, columnName = "type")
    TransactionType type;
    @DatabaseField
    long blockNumber;
    @DatabaseField
    int indexIntoBlock;

    public Transaction(@NonNull Wallet sender,
                       @NonNull Wallet receiver,
                       @NonNull String amount,
                       @NonNull String fee,
                       long timestamp,
                       @NonNull String userData,
                       @NonNull TransactionType type,
                       long blockNumber,
                       int indexIntoBlock) {
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
        this.fee = fee;
        this.timestamp = timestamp;
        this.userData = userData;
        this.type = type;
        this.blockNumber = blockNumber;
        this.indexIntoBlock = indexIntoBlock;
    }
}
