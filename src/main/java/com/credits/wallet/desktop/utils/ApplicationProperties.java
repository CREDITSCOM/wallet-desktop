package com.credits.wallet.desktop.utils;

import lombok.Getter;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Properties;

import static com.credits.wallet.desktop.utils.GeneralUtils.getResourceAsStream;
import static java.lang.Integer.parseInt;

@Slf4j
public class ApplicationProperties {
    private static final String JDK_VERSION = "java-11-openjdk-11.0.3-1";

    @Getter
    private String apiAddress;
    @Getter
    private int apiPort;
    @Getter
    private String jdkPath;
    @Getter
    private GitProperties gitProperties;

    private ApplicationProperties() {
    }

    public static ApplicationProperties loadPropertyFile(String path) {
        final var instance = new ApplicationProperties();
        final var properties = openPropertyFile(path);

        instance.apiAddress = properties.getProperty("node.api.host", "localhost");
        apiAddressCantBeEmpty(instance.apiAddress);
        instance.apiPort = parseInt(properties.getProperty("node.api.port", "9090"));

        final var defaultJdkPath = Paths.get("..", "ojdkbuild", JDK_VERSION).toString();
        instance.jdkPath = properties.getProperty("jdk.path", defaultJdkPath);

        instance.gitProperties = loadGitProperties();
        return instance;
    }

    private static void apiAddressCantBeEmpty(String apiAddress) {
        if (apiAddress.isEmpty()) throw new RuntimeException("\"node.api.host\" can't be empty");
    }



    private static GitProperties loadGitProperties() {
        try {
            final var properties = openPropertyFile(getResourceAsStream("/git.properties"));
            final var tag = (String) properties.get("git.closest.tag.name");
            final var build = (String) properties.get("git.closest.tag.commit.count");
            final var commit = (String) properties.get("git.commit.id");
            return new GitProperties(tag, build, commit);
        } catch (Throwable e) {
            log.warn("git properties not loaded. Reason: {}", e.getMessage());
        }
        return new GitProperties("unknown", "unknown", "unknown");
    }


    private static Properties openPropertyFile(InputStream inputStream) {
        final var properties = new Properties();
        try (inputStream) {
            properties.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return properties;
    }

    private static Properties openPropertyFile(String path) {
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream(path)) {
            properties.load(fis);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return properties;
    }

    @Value
    public static class GitProperties {
        String tag;
        String build;
        String commit;
    }
}
