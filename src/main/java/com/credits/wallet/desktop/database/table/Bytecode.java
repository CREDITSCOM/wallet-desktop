package com.credits.wallet.desktop.database.table;

import com.j256.ormlite.core.dao.DatabaseResultsMapper;
import com.j256.ormlite.core.field.DataType;
import com.j256.ormlite.core.field.DatabaseField;
import com.j256.ormlite.core.support.DatabaseResults;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.SQLException;

@Data
@NoArgsConstructor
public class Bytecode {
    @DatabaseField(generatedId = true)
    int id;

    @DatabaseField(columnName = "class_name")
    String className;

    @DatabaseField(dataType = DataType.BYTE_ARRAY)
    byte[] bytecode;

    public static DatabaseResultsMapper<Bytecode> rowMapper = new BytecodeRowMapper();

    public Bytecode(String className, byte[] bytecode) {
        this.className = className;
        this.bytecode = bytecode;
    }

    private static class BytecodeRowMapper implements DatabaseResultsMapper<Bytecode> {
        @Override
        public Bytecode mapRow(DatabaseResults databaseResults) throws SQLException {
            final var className = databaseResults.getString(1);
            final var bytecode = databaseResults.getBytes(2);
            return new Bytecode(className, bytecode);
        }
    }
}
