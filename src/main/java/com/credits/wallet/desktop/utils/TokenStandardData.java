package com.credits.wallet.desktop.utils;


import com.credits.scapi.v0.BasicStandard;
import com.credits.scapi.v0.ExtensionStandard;
import com.credits.scapi.v1.BasicTokenStandard;
import com.credits.scapi.v1.ExtensionTokenStandard;

public enum TokenStandardData {
    NOT_A_TOKEN(0, Void.TYPE),
    BASIC_STANDARD(1, BasicStandard.class),
    EXTENSION_STANDARD(2, ExtensionStandard.class),
    BASIC_TOKEN_STANDARD(3, BasicTokenStandard.class),
    EXTENSION_TOKEN_STANDARD(4, ExtensionTokenStandard.class);

    private int id;
    private Class<?> clazz;

    private TokenStandardData(int value, Class<?> clazz) {
        this.id = value;
        this.clazz = clazz;
    }

    public int getId() {
        return id;
    }

    public Class<?> getTokenStandardClass() {
        return clazz;
    }

    public static TokenStandardData findByValue(long value) {
        switch ((int) value) {
            case 0:
                return NOT_A_TOKEN;
            case 1:
                return BASIC_STANDARD;
            case 2:
                return EXTENSION_STANDARD;
            case 3:
                return BASIC_TOKEN_STANDARD;
            case 4:
                return EXTENSION_TOKEN_STANDARD;
            default:
                return null;
        }
    }
}
