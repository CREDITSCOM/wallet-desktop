package com.credits.wallet.desktop.database.table;

import com.j256.ormlite.core.field.DatabaseField;
import com.j256.ormlite.core.table.DatabaseTable;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@DatabaseTable(tableName = "smart_contract_call_has_method")
public class SmartContractCallHasMethod {
   @DatabaseField(generatedId = true)
   private long id;

   @DatabaseField(columnName = "smart_contract_call", foreign = true)
   private SmartContractCall smartContractCall;

   @DatabaseField(foreign = true)
   private Method method;
}
