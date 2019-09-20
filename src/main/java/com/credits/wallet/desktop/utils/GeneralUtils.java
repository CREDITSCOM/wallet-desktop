package com.credits.wallet.desktop.utils;

import java.io.InputStream;

public interface GeneralUtils {

    static InputStream getResourceAsStream(String path) {
        return GeneralUtils.class.getResourceAsStream(path);
    }
}
