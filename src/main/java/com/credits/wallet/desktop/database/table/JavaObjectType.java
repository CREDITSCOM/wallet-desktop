package com.credits.wallet.desktop.database.table;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@DatabaseTable(tableName = "java_object_type")
public class JavaObjectType {
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(uniqueIndex = true)
    private String name;
}
