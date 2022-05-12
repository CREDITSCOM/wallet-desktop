package com.credits.wallet.desktop.database.table;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@DatabaseTable(tableName = "smart_contract_call_has_argument_value")
public class SmartContractCallHasArgumentValue {
    @DatabaseField(generatedId = true)
    private long id;
    
    @DatabaseField(columnName = "smart_contract_call_id", foreign = true)
    private SmartContractCall smartContractCall;

    @DatabaseField(columnName = "argument_value", foreign = true)
    private ArgumentValue argumentValue;
}
