package com.credits.wallet.desktop.database.table;

import com.j256.ormlite.core.field.DatabaseField;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
public class Wallet {
    @DatabaseField(generatedId = true)
    long id;
    @DatabaseField(unique = true, indexName = "ix_wallet_address")
    String address;

    public Wallet(@NonNull String address) {
        this.address = address;
    }
}
