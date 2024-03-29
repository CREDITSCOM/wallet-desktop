package com.credits.wallet.desktop.database.table;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Data;
import lombok.NoArgsConstructor;

@DatabaseTable(tableName = "application_metadata")
@Data
@NoArgsConstructor
public class ApplicationMetadata {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private Wallet account;

    @DatabaseField(columnName = "amount_transactions")
    private int amountTransactions;

    public ApplicationMetadata(Wallet account, int amountTransactions) {
        this.account = account;
        this.amountTransactions = amountTransactions;
    }
}
