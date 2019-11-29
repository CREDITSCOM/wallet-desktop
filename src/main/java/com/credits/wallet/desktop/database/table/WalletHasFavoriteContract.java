package com.credits.wallet.desktop.database.table;

import com.j256.ormlite.core.field.DatabaseField;
import com.j256.ormlite.core.table.DatabaseTable;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@DatabaseTable(tableName = "wallet_has_favorite_contract")
@NoArgsConstructor
public class WalletHasFavoriteContract {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, uniqueCombo = true)
    private Wallet wallet;

    @DatabaseField(columnName = "smart_contract_id", foreign = true, foreignAutoRefresh = true, uniqueCombo = true)
    private SmartContract smartContract;
}
