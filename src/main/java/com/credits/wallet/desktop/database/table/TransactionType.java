package com.credits.wallet.desktop.database.table;

import com.j256.ormlite.core.field.DatabaseField;
import com.j256.ormlite.core.table.DatabaseTable;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
@DatabaseTable(tableName = "transaction_type")
public class TransactionType {
    @DatabaseField(generatedId = true)
    long  id;

    @DatabaseField(uniqueIndex = true,  indexName = "ix_transaction_type")
    String name = "unknown";

    public TransactionType(@NonNull String name) {
        this.name = name;
    }
}
