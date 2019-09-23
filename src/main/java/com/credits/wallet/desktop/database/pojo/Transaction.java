package com.credits.wallet.desktop.database.pojo;

import com.j256.ormlite.core.table.DatabaseTable;
import lombok.Data;

@Data
@DatabaseTable(tableName = "transaction")
public class Transaction {
    long id;
    String sender;
    String receiver;
    String fee;
    String timestamp;
    String userData;
    SmartContract smartContract;
}
