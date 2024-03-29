package com.credits.wallet.desktop.database.table;


import com.credits.general.pojo.ByteCodeObjectData;
import com.j256.ormlite.dao.DatabaseResultsMapper;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.support.DatabaseResults;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.SQLException;
import java.util.List;

@Data
@NoArgsConstructor
@DatabaseTable(tableName = "smart_contract")
public class SmartContract {
    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(foreign = true, foreignColumnName = "address", foreignAutoRefresh = true, uniqueIndex = true)
    private Wallet wallet;
    @DatabaseField(columnName = "source_code")
    private String sourceCode;
    @DatabaseField(columnName = "time_creation")
    private long timeCreation;
    private List<ByteCodeObjectData> byteCodeObjectList;
    public static SmartContractIdRowMapper smartContractIdRowMapper = new SmartContractIdRowMapper();

    public SmartContract(Wallet wallet, String sourceCode, long timeCreation) {
        this.wallet = wallet;
        this.sourceCode = sourceCode;
        this.timeCreation = timeCreation;
    }

    public static class SmartContractIdRowMapper implements DatabaseResultsMapper<Integer> {

        @Override
        public Integer mapRow(DatabaseResults databaseResults) throws SQLException {
            return databaseResults.getInt(0);
        }
    }
}
