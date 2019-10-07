package com.credits.wallet.desktop.database.table;

import com.j256.ormlite.core.field.DatabaseField;
import com.j256.ormlite.core.table.DatabaseTable;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@DatabaseTable(tableName = "smart_contract_has_bytecode")
public class SmartContractHasBytecode {
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(columnName = "smart_contract_id", foreign = true)
    private SmartContract smartContract;

    @DatabaseField(foreign = true)
    private Bytecode bytecode;

    public SmartContractHasBytecode(SmartContract smartContract, Bytecode bytecode) {
        this.smartContract = smartContract;
        this.bytecode = bytecode;
    }
}

