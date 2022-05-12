package com.credits.wallet.desktop.database.table;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@DatabaseTable(tableName = "wallet_has_smart_contract")
public class WalletHasSmartContract {
    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField(foreign = true)
    private Wallet wallet;

    @DatabaseField(columnName = "smart_contract_id", foreign = true)
    private SmartContract smartContract;

    public WalletHasSmartContract(Wallet wallet, SmartContract smartContract) {
        this.wallet = wallet;
        this.smartContract = smartContract;
    }
}
