package com.credits.wallet.desktop.database.table;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@DatabaseTable(tableName = "smart_contract_call")
public class SmartContractCall {
    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField(columnName = "smart_contract", foreign = true)
    private SmartContract smartContract;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private Method method;

    @DatabaseField(columnName = "operation_state")
    private String operationState;

    @DatabaseField(columnName = "return_value")
    private String returnValue;

    public SmartContractCall(SmartContract smartContract, Method method, String returnValue, String operationState) {
        this.smartContract = smartContract;
        this.method = method;
        this.operationState = operationState;
        this.returnValue = returnValue;
    }
}

