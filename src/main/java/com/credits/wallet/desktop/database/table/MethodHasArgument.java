package com.credits.wallet.desktop.database.table;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@DatabaseTable(tableName = "method_has_argument")
public class MethodHasArgument {
    @DatabaseField(foreign = true)
    private Method method;
    @DatabaseField(foreign = true)
    private Argument argument;
}
