package com.credits.wallet.desktop.database.table;

import com.j256.ormlite.core.field.DataType;
import com.j256.ormlite.core.field.DatabaseField;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Bytecode {
    @DatabaseField(generatedId = true)
    int id;

    @DatabaseField
    String className;

    @DatabaseField(dataType = DataType.BYTE_ARRAY)
    byte[] bytecode;

    public Bytecode(String className, byte[] bytecode) {
        this.className = className;
        this.bytecode = bytecode;
    }
}
