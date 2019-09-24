package com.credits.wallet.desktop.database;

import org.junit.jupiter.api.TestInfo;

import static com.credits.general.util.Utils.rethrowUnchecked;

public class DatabaseTest {
    protected DatabaseHelper db;

    protected void processingTestAnnotations(TestInfo testInfo) {
        if (testInfo.getTags().contains(CreateTables.class.getSimpleName())) {
            testInfo.getTestMethod().ifPresent(m -> {
                final var createTables = m.getAnnotation(CreateTables.class);
                createTables(createTables);
            });
        }
    }

    private void createTables(CreateTables createTables) {
        for (Class<?> aClass : createTables.value()) {
            rethrowUnchecked(() -> db.createTable(aClass));
        }
    }
}
