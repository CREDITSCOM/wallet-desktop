package com.credits.wallet.desktop.database.table;

import com.j256.ormlite.core.field.DatabaseField;
import com.j256.ormlite.core.table.DatabaseTable;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@DatabaseTable(tableName = "transaction")
public class Transaction {
    @DatabaseField(generatedId = true)
    long id;
    @DatabaseField(columnName = "sender_id", foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true)
    Wallet sender;
    @DatabaseField(columnName = "receiver_id", foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true)
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
    String blockNumber;

    public Transaction(Wallet sender,
                       Wallet receiver,
                       String amount,
                       String fee,
                       long timestamp,
                       String userData,
                       TransactionType type,
                       String blockNumber) {
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
        this.fee = fee;
        this.timestamp = timestamp;
        this.userData = userData;
        this.type = type;
        this.blockNumber = blockNumber;
    }
}
