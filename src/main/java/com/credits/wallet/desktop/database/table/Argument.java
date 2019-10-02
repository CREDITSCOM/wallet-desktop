package com.credits.wallet.desktop.database.table;

import com.j256.ormlite.core.field.DatabaseField;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Argument {
    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField(foreign = true, foreignColumnName = "name", foreignAutoCreate = true)
    private JavaObjectType type;

    @DatabaseField
    private String name;
}
