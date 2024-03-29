package com.credits.wallet.desktop;

import com.credits.client.node.service.NodeApiService;
import com.credits.client.node.service.NodeApiServiceImpl;
import com.credits.wallet.desktop.database.DatabaseHelper;
import com.credits.wallet.desktop.service.NodeInteractionService;
import com.credits.wallet.desktop.service.db.DatabaseService;
import com.credits.wallet.desktop.service.db.DatabaseServiceImpl;
import com.credits.wallet.desktop.service.db.UpdateDatabaseService;
import com.credits.wallet.desktop.utils.ApplicationProperties;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.credits.wallet.desktop.utils.GeneralUtils.createDirectoryIfNotExist;


@Slf4j
public class AppState {
    public static final String NODE_ERROR = "A problem connecting to the Node";
    public static final int CREDITS_DECIMAL = 18;
    public static final String CREDITS_TOKEN_NAME = "CS";
    public static final int DELAY_AFTER_FULL_SYNC = 5;
    public static final int DELAY_BEFORE_FULL_SYNC = 2;

    private static final Map<String, Session> sessionMap = new HashMap<>();
    public static final String URI_SCHEME_JDBC_SQLITE = "jdbc:sqlite:";
    private static NodeApiService nodeApiService;
    private static PrivateKey privateKey;
    private static PublicKey publicKey;
    private static String pwd;
    private static Stage primaryStage;
    private static String jdkPath;
    private static DatabaseService database;
    private static NodeInteractionService nodeInteractionService;

    public static UpdateDatabaseService getUpdateDatabaseService() {
        return updateDatabaseService;
    }

    private static UpdateDatabaseService updateDatabaseService;

    private AppState() {
    }

    public static void initialize(ApplicationProperties properties) {
        nodeApiService = NodeApiServiceImpl.getInstance(properties.getApiAddress(), properties.getApiPort());
        jdkPath = properties.getJdkPath();
        final var databasePath = properties.getDatabasePath();
        createDirectoryIfNotExist(databasePath);
        initializeDatabase(databasePath);
        updateDatabaseService = new UpdateDatabaseService(database);
    }

    private static void initializeDatabase(String databasePath) {
        final var dbHelper = new DatabaseHelper(URI_SCHEME_JDBC_SQLITE + databasePath);
        database = new DatabaseServiceImpl(nodeApiService, dbHelper);
    }

    public static void initializeNodeInteractionService(String account) {
        nodeInteractionService = new NodeInteractionService(nodeApiService, account, privateKey);
    }

    public static NodeInteractionService getNodeInteractionService() {
        return Objects.requireNonNull(nodeInteractionService, "NodeInteractionService not initialized");
    }

    public static NodeApiService getNodeApiService() {
        return nodeApiService;
    }

    public static PrivateKey getPrivateKey() {
        return privateKey;
    }

    public static void setPrivateKey(PrivateKey privateKey) {
        AppState.privateKey = privateKey;
    }

    public static PublicKey getPublicKey() {
        return publicKey;
    }

    public static void setPublicKey(PublicKey publicKey) {
        AppState.publicKey = publicKey;
    }

    public static String getPwd() {
        return pwd;
    }

    public static void setPwd(String pwd) {
        AppState.pwd = pwd;
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void setPrimaryStage(Stage primaryStage) {
        AppState.primaryStage = primaryStage;
    }

    public static Map<String, Session> getSessionMap() {
        return sessionMap;
    }

    public static String getJdkPath() {
        return jdkPath;
    }

    public static DatabaseService getDatabase() {
        return database;
    }
}
