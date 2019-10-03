package com.credits.wallet.desktop;

import com.credits.client.node.service.NodeApiService;
import com.credits.client.node.service.NodeApiServiceImpl;
import com.credits.wallet.desktop.utils.FormUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class AppStateInitializer {
    private final static Logger LOGGER = LoggerFactory.getLogger(AppStateInitializer.class);
    public final int DEFAULT_NODE_API_PORT = 9090;

    public Properties properties;

    public void init() {
        properties = loadProperties();
        AppState.setNodeApiService(initializeNodeApiService());
        AppState.setJdkPath(properties.getProperty("jdk.path"));
        String smartContractNamesDomainAddress = properties.getProperty("smartContractNamesDomain.address");
        if (smartContractNamesDomainAddress == null || smartContractNamesDomainAddress.isEmpty()) {
            LOGGER.info("SmartContractNamesDomain address is not exists or is empty. Please add smartContractNamesDomain.address to property file");
        }
        AppState.setSmartContractNamesDomainAddress(smartContractNamesDomainAddress);
    }

    public Properties loadProperties() {
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream("settings.properties")){
            properties.load(fis);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("File settings.properties not found", e);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return properties;
    }


    public NodeApiService initializeNodeApiService() {
        String apiAddress = properties.getProperty("node.api.host");
        String apiPort = properties.getProperty("node.api.port");

        if (apiAddress == null || apiAddress.isEmpty() || apiPort == null || apiPort.isEmpty()) {
            FormUtils.showError("Node API address not found. Please add node.internal.host and node.internal.port to property file.");
        }
        return NodeApiServiceImpl.getInstance(apiAddress, apiPort == null ? DEFAULT_NODE_API_PORT : Integer.parseInt(apiPort));
    }

}
