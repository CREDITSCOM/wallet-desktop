package com.credits.wallet.desktop.database.pojo;

import com.j256.ormlite.core.field.DatabaseField;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Wallet {
    @DatabaseField(generatedId = true)
    long id;
    @DatabaseField(unique = true, indexName = "ix_wallet_address")
    String address;

    public Wallet(String address) {
        this.address = address;
    }
}
