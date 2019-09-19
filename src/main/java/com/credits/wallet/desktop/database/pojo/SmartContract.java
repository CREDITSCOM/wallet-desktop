package com.credits.wallet.desktop.database.pojo;


import com.j256.ormlite.core.field.DatabaseField;
import com.j256.ormlite.core.table.DatabaseTable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@DatabaseTable(tableName = "smart_contract")
public class SmartContract {
    @DatabaseField(id = true)
    String address;
    @DatabaseField
    String deployer;
    @DatabaseField(columnName = "source_code")
    String sourceCode;
    @DatabaseField(columnName = "time_stamp_creation")
    long timeStampCreation;
}
