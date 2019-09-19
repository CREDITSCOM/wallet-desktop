package com.credits.wallet.desktop.database;

import java.sql.Connection;
import java.sql.DriverManager;

import static com.credits.general.util.Utils.rethrowUnchecked;

public class DBHelper {
    private final String url;

    public DBHelper(String url) {
        this.url = url;
        rethrowUnchecked(() -> Class.forName("org.sqlite.JDBC"));
    }

    public Connection connect() {
        return rethrowUnchecked(() -> DriverManager.getConnection(url));
    }

}
