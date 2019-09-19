package com.credits.wallet.desktop;

import com.credits.client.node.service.NodeApiServiceImpl;
import com.credits.wallet.desktop.utils.ApplicationProperties;

public class AppStateInitializer {
    public void init() {
        final var properties = ApplicationProperties.loadPropertyFile("settings.properties");
        AppState.setNodeApiService(NodeApiServiceImpl.getInstance(properties.getApiAddress(), properties.getApiPort()));
        AppState.setJdkPath(properties.getJdkPath());
    }
}
