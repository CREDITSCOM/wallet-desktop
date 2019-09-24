package com.credits.wallet.desktop.database.table;


import com.j256.ormlite.core.field.DatabaseField;
import com.j256.ormlite.core.table.DatabaseTable;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@DatabaseTable(tableName = "smart_contract")
public class SmartContract {
    @DatabaseField(generatedId = true)
    long id;
    @DatabaseField(foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true)
    Wallet wallet;
    @DatabaseField(columnName = "source_code")
    String sourceCode;
    @DatabaseField(columnName = "time_stamp")
    long timeStamp;

    public SmartContract(Wallet wallet, String sourceCode, long timeStamp) {
        this.wallet = wallet;
        this.sourceCode = sourceCode;
        this.timeStamp = timeStamp;
    }
}
