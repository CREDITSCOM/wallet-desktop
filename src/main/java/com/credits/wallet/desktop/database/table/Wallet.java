package com.credits.wallet.desktop.database.table;

import com.j256.ormlite.field.DatabaseField;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
public class Wallet {
    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField(uniqueIndex = true)
    private String address;

    public Wallet(@NonNull String address) {
        this.address = address;
    }
}
