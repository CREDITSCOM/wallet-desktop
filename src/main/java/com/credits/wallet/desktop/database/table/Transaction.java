package com.credits.wallet.desktop.database.table;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.Date;

@Data
@NoArgsConstructor
@DatabaseTable(tableName = "transaction")
public class Transaction {
    @DatabaseField(generatedId = true)
    private long id;
    @DatabaseField(foreign = true, foreignColumnName = "address", foreignAutoCreate = true)
    private Wallet sender;
    @DatabaseField(foreign = true, foreignColumnName = "address", foreignAutoCreate = true)
    private Wallet receiver;
    @DatabaseField
    private String amount;
    @DatabaseField(columnName = "max_fee")
    private double maxFee;
    @DatabaseField(columnName = "time_creation")
    private Date timeCreation;
    @DatabaseField(columnName = "user_data")
    private String userData;
    @DatabaseField
    private String type;
    @DatabaseField(columnName = "block_number", uniqueCombo = true, index = true)
    private long blockNumber;
    @DatabaseField(columnName = "index_into_block", uniqueCombo = true)
    private int indexIntoBlock;

    public Transaction(@NonNull Wallet sender,
                       @NonNull Wallet receiver,
                       @NonNull String amount,
                       double maxFee,
                       @NonNull Date timeCreation,
                       @NonNull String userData,
                       @NonNull String type,
                       long blockNumber,
                       int indexIntoBlock) {
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
        this.maxFee = maxFee;
        this.timeCreation = timeCreation;
        this.userData = userData;
        this.type = type;
        this.blockNumber = blockNumber;
        this.indexIntoBlock = indexIntoBlock;
    }

}
