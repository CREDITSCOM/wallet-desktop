package com.credits.wallet.desktop.controller;

import java.util.Map;

public interface Initializable {
    void initialize(Map<String, ?> objects);

    default void deinitialize() {
    }
}