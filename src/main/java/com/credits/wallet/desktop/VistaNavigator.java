package com.credits.wallet.desktop;

import com.credits.wallet.desktop.controller.AbstractController;
import com.credits.wallet.desktop.controller.HeaderController;
import com.credits.wallet.desktop.controller.MainController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

/**
 * Utility class for controlling navigation between vistas.
 * <p>
 * All methods on the navigator are static to facilitate
 * simple access from anywhere in the application.
 */
public class VistaNavigator {
    private final static Logger LOGGER = LoggerFactory.getLogger(VistaNavigator.class);

    public static final String WELCOME = "/fxml/welcome.fxml";
    public static final String FORM_1 = "/fxml/generate_keys.fxml";
    public static final String FORM_4 = "/fxml/save_keys.fxml";
    public static final String FORM_5 = "/fxml/put_keys.fxml";
    public static final String WALLET = "/fxml/wallet.fxml";
    public static final String FORM_7 = "/fxml/generate_transaction.fxml";
    public static final String HEADER = "/fxml/header.fxml";
    public static final String HISTORY = "/fxml/history.fxml";
    public static final String MAIN = "/fxml/main.fxml";
    public static final String NEW_COIN = "/fxml/new_coin.fxml";
    public static final String SMART_CONTRACT = "/fxml/smart_contract.fxml";
    public static final String SMART_CONTRACT_DEPLOY = "/fxml/smart_contract_deploy.fxml";
    public static final String TRANSACTION = "/fxml/transaction.fxml";
    public static final String SMART_CONTRACT_TRANSACTION = "/fxml/smart_contract_transaction.fxml";
    public static final String CHECK_PRIVATE_KEY = "/fxml/check_privkey_pwd.fxml";
    private static MainController mainController;
    private static HeaderController headerController;
    private static AbstractController currentVistaController;

    public static void setCurrentVistaController(AbstractController currentVistaController) {
        VistaNavigator.currentVistaController = currentVistaController;
    }

    public static AbstractController getCurrentVistaController() {
        return currentVistaController;
    }

    static void saveMainController(MainController mainController) {
        VistaNavigator.mainController = mainController;
    }

    public static void reloadForm(String fxml, Map<String, Object> params) {
        changeVista(fxml, params);
    }

    public static void reloadForm(String fxml) {
        changeVista(fxml, null);
    }

    public static void showModalForm(String fxml, Map<String, Object> params) {
        FXMLLoader loader = new FXMLLoader(VistaNavigator.class.getResource(fxml));
        Scene scene;
        try {
            scene = new Scene(loader.load());
        } catch (IOException ex) {
            // TODO: handle error
            return;
        }
        scene.getStylesheets().setAll(WalletApp.class.getResource("/styles.css").toExternalForm());
        AbstractController controller = loader.getController();
        controller.initialize(params);
        Stage stage = new Stage();
        stage.initOwner(AppState.getPrimaryStage());
        stage.initModality(Modality.WINDOW_MODAL);
        stage.getIcons().add(new Image(WalletApp.class.getResourceAsStream("/img/icon.png")));
        stage.setTitle("Credits");
        stage.setOnCloseRequest(event -> {
            controller.deinitialize();
        });
        stage.setScene(scene);
        stage.showAndWait();
    }

    static void loadFirstForm(String form) throws IOException {
        FXMLLoader headerLoader = new FXMLLoader(VistaNavigator.class.getResource(VistaNavigator.HEADER));
        BorderPane headerPane = headerLoader.load();
        mainController.setTopVista(headerPane);
        headerController = headerLoader.getController();
        VistaNavigator.reloadForm(form, null);
    }

    private static void changeVista(String fxml, Map<String, Object> params) {
        try {
            final var oldVista = currentVistaController;
            final var fxmlLoader = new FXMLLoader(VistaNavigator.class.getResource(fxml));
            final var pane = (Pane) fxmlLoader.load();
            currentVistaController = fxmlLoader.getController();
            resizeForm(pane);
            if (oldVista != null) {
                initializeNewController(oldVista, currentVistaController, params);
                deinitialize(oldVista);
            }
            mainController.setVista(pane);
        } catch (IOException e) {
            LOGGER.error("failed!", e);
        }
    }

    private static void initializeNewController(AbstractController oldVistaController,
                                                AbstractController newVistaController,
                                                Map<String, Object> params) {
        try {
            if (oldVistaController != null) {
                newVistaController.session = oldVistaController.session;
            }
            headerController.changeParentController(newVistaController);
            newVistaController.initialize(params);
        } catch (Exception e) {
            LOGGER.error("cannot initialize controller", e);
            throw e;
        }
    }

    private static void deinitialize(AbstractController oldVistaController) {
        try {
            if (oldVistaController != null) {
                oldVistaController.deinitialize();
            }
        } catch (Exception e) {
            LOGGER.error("Cannot deinitialize vista", e);
            throw e;
        }
    }

    public static void resizeForm(Pane pane) {
        pane.setPrefHeight(700D);
        pane.setPrefWidth(1300D);
    }
}