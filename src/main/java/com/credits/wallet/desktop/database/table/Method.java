package com.credits.wallet.desktop.database.table;

import com.j256.ormlite.field.DatabaseField;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Method {
    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField
    private String name;

    @DatabaseField(columnName = "return_type", foreign = true, foreignColumnName = "name", foreignAutoCreate = true)
    private JavaObjectType returnType;

}
