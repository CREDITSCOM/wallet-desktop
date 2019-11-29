package com.credits.wallet.desktop.utils;

import com.credits.general.util.Utils;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public interface GeneralUtils {

    static InputStream getResourceAsStream(String path) {
        return GeneralUtils.class.getResourceAsStream(path);
    }

    static void createDirectoryIfNotExist(String path) {
        final var directoryPath = Paths.get(path).getParent();
        if (directoryPath != null && !Files.exists(directoryPath)) {
            Utils.rethrowUnchecked(() -> Files.createDirectory(directoryPath));
        }
    }
}
