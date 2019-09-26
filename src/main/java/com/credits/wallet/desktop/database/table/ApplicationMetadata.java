package com.credits.wallet.desktop.database.table;

import com.j256.ormlite.core.field.DatabaseField;
import com.j256.ormlite.core.table.DatabaseTable;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@DatabaseTable(tableName = "application_metadata")
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationMetadata {

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    public Wallet account;

    @DatabaseField(columnName = "amount_transactions")
    public int amountTransactions;
}
