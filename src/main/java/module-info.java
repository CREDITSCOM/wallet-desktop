module wallet.desktop {
    requires javafx.graphics;
    requires javafx.fxml;
    requires slf4j.api;
    requires com.google.common;
    requires org.apache.commons.lang3;
    requires com.credits.general;
    requires node.client;
    requires javafx.controls;
    requires com.credits.sc.api;
    requires wellbehavedfx;
    requires flowless;
    requires gson;
    requires org.apache.commons.io;
    requires reactfx;
    requires junit;
    requires java.compiler;
    requires java.datatransfer;
    requires org.eclipse.text;
    requires org.eclipse.jdt.core;
    requires java.desktop;

    requires transitive javafx.base;

    requires transitive java.management;
    requires transitive java.instrument;

    requires transitive richtextfx;
    requires com.sun.jna;
    requires java.sql;
    requires sqlite.jdbc;
    requires ormlite.jdbc;
    requires ormlite.core;
    requires lombok;

    opens com.credits.wallet.desktop;
}