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
    private int id;
    @DatabaseField(foreign = true, foreignColumnName = "address", foreignAutoCreate = true)
    private Wallet wallet;
    @DatabaseField(columnName = "source_code")
    private String sourceCode;
    @DatabaseField(columnName = "time_creation")
    private long timeCreation;

    public SmartContract(Wallet wallet, String sourceCode, long timeCreation) {
        this.wallet = wallet;
        this.sourceCode = sourceCode;
        this.timeCreation = timeCreation;
    }
}
