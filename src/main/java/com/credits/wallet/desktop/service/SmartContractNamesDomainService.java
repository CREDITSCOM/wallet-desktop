package com.credits.wallet.desktop.service;

import com.credits.client.node.exception.NodeClientException;
import com.credits.client.node.pojo.SmartContractData;
import com.credits.general.classload.ByteCodeContractClassLoader;
import com.credits.general.exception.CreditsException;
import com.credits.general.pojo.ByteCodeObjectData;
import com.credits.general.serialize.Serializer;
import com.credits.wallet.desktop.AppState;
import com.credits.wallet.desktop.AppStateInitializer;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * SmartContractNamesDomain smart-contract methods execution service
 */
public class SmartContractNamesDomainService {
    private final static Logger LOGGER = LoggerFactory.getLogger(AppStateInitializer.class);

    private byte[] scObject;
    private ByteCodeContractClassLoader classLoader;
    private Method methodGetContractAddress;
    private long scTransactionsCount = 0;
    private boolean initialized = false;
    private JsonParser jsonParser = new JsonParser();

    private static volatile SmartContractNamesDomainService instance;
    // Double Checked Locking & volatile.
    public static SmartContractNamesDomainService getInstance() {
        SmartContractNamesDomainService localInstance = instance;
        if (localInstance == null) {
            synchronized (SmartContractNamesDomainService.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new SmartContractNamesDomainService();
                    instance.init();
                }
            }
        }
        return localInstance;
    }

    private SmartContractNamesDomainService() {}

    private void init() {
        ScheduledExecutorService headerExecService = Executors.newScheduledThreadPool(1);
        try {
            SmartContractData smartContractData = getSmartContractData();
            List<ByteCodeObjectData> byteCodeObjects = smartContractData.getSmartContractDeployData().getByteCodeObjects();

            Class<?> contractClass = null;
            List<Class<?>> innerContractClasses = new ArrayList<>();
            ByteCodeContractClassLoader byteCodeContractClassLoader = new ByteCodeContractClassLoader();
            for (ByteCodeObjectData byteCodeObject : byteCodeObjects) {
                Class<?> clazz = byteCodeContractClassLoader.loadClass(byteCodeObject.getName(), byteCodeObject.getByteCode());
                if (clazz.getName().contains("$")) {
                    innerContractClasses.add(clazz);
                } else {
                    contractClass = clazz;
                }
            }
            classLoader = byteCodeContractClassLoader;
            methodGetContractAddress = contractClass.getMethod("getContractAddress", String.class);
            initialized = true;

            headerExecService.scheduleWithFixedDelay(() -> {
                long transactionsCount;
                try {
                    transactionsCount = AppState.getNodeApiService().getWalletTransactionsCount(AppState.getSmartContractNamesDomainAddress());
                    if (scTransactionsCount < transactionsCount) {
                        SmartContractData data = getSmartContractData();
                        if (data == null) {
                            throw new CreditsException("getSmartContractData() returns null");
                        }
                        scObject = data.getObjectState();
                        scTransactionsCount = transactionsCount;
                    }
                } catch (CreditsException e) {
                    LOGGER.error("Smart-contract SmartContractNamesDomain state receiving error: {}", e.getMessage());
                }
                LOGGER.info("Smart-contract SmartContractNamesDomain state received successfully");
            }, 0, 30, TimeUnit.SECONDS);
            LOGGER.info("SmartContractNamesDomainService initialized successfully");
        } catch (CreditsException | NoSuchMethodException e) {
            LOGGER.error("SmartContractNamesDomainService initializing error: SmartContractNamesDomain by address {} is not found", AppState.getSmartContractNamesDomainAddress());
        }
    }

    private SmartContractData getSmartContractData() throws NodeClientException{
        SmartContractData smartContractData = AppState.getNodeApiService().getSmartContract(AppState.getSmartContractNamesDomainAddress());
        return smartContractData;
    }

    public String executeGetContractAddress(String name) {
        String methodInvokeResult;
        try {
            methodInvokeResult = (String)methodGetContractAddress.invoke(Serializer.deserialize(scObject, classLoader), name);
            JsonObject jsonObject = jsonParser.parse(methodInvokeResult).getAsJsonObject();
            if (jsonObject.get("code").getAsString().equals("0")) {
                return jsonObject.get("result").getAsString();
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            LOGGER.error("Error on invoking getContractAddress() method: {}", e.getMessage());
        }
        return null;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }
}
