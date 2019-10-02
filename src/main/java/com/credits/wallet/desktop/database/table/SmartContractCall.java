package com.credits.wallet.desktop.database.table;

import com.j256.ormlite.core.field.DatabaseField;
import com.j256.ormlite.core.table.DatabaseTable;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@DatabaseTable(tableName = "smart_contract_call")
public class SmartContractCall {
    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField(columnName = "smart_contract_id", foreign = true)
    private SmartContract smartContract;

    @DatabaseField(foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true)
    private SmartContractCallHasMethod method;

    @DatabaseField(foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true)
    private SmartContractCallHasArgumentValue argumentValue;
}
