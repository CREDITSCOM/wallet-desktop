package com.credits.wallet.desktop.utils;

import lombok.Getter;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Properties;

import static java.lang.Integer.parseInt;

public class ApplicationProperties {
    private static final String ERR_NO_PROPERTIES = "settings.properties not found";
    private static final String JDK_VERSION = "java-11-openjdk-11.0.3-1";

    @Getter
    private String apiAddress;
    @Getter
    private int apiPort;
    @Getter
    private String jdkPath;

    private ApplicationProperties() {
    }

    public static ApplicationProperties loadPropertyFile(String path) {
        final var instance = new ApplicationProperties();
        final var properties = instance.openPropertyFile(path);

        instance.apiAddress = properties.getProperty("node.api.host", "localhost");
        apiAddressCantBeEmpty(instance);
        instance.apiPort = parseInt(properties.getProperty("node.api.port", "9090"));

        final var defaultJdkPath = Paths.get("..", "ojdkbuild", JDK_VERSION).toString();
        instance.jdkPath = properties.getProperty("jdk.path", defaultJdkPath);

        return instance;
    }

    private static void apiAddressCantBeEmpty(ApplicationProperties instance) {
        if(instance.apiAddress.isEmpty()) throw new RuntimeException("\"node.api.host\" can't be empty");
    }

    private Properties openPropertyFile(String path) {
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream(path)) {
            properties.load(fis);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(ERR_NO_PROPERTIES, e);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return properties;
    }
}
