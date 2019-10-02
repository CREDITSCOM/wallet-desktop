package com.credits.wallet.desktop.database.table;

import com.j256.ormlite.core.field.DatabaseField;
import com.j256.ormlite.core.table.DatabaseTable;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@DatabaseTable(tableName = "smart_contract_has_method")
public class SmartContractHasMethod {
    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField(columnName = "smart_contract", foreign = true)
    private SmartContract smartContract;

    @DatabaseField(foreign = true)
    private Method method;
}
