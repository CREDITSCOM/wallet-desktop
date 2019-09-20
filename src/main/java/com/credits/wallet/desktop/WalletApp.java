package com.credits.wallet.desktop;


import com.credits.wallet.desktop.controller.MainController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

import static com.credits.wallet.desktop.utils.ApplicationProperties.loadPropertyFile;
import static com.credits.wallet.desktop.utils.GeneralUtils.getResourceAsStream;


@Slf4j
public class WalletApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {

        final var properties = loadPropertyFile("settings.properties");
        final var gitProperties = properties.getGitProperties();
        log.info("---------------------------------------------------------------------------");
        log.info("Wallet Desktop {} build {} commit {}", gitProperties.getTag(), gitProperties.getBuild(), gitProperties.getCommit());
        log.info("---------------------------------------------------------------------------");
        AppState.initialize(properties);

        stage.getIcons().add(new Image(getResourceAsStream("/img/icon.png")));
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();

        stage.setX(bounds.getMinX());
        stage.setY(bounds.getMinY());
        stage.setWidth(bounds.getWidth());
        stage.setHeight(bounds.getHeight());
        stage.setMaximized(true);

        stage.setTitle("Credits");

        stage.setScene(createScene(loadMainPane()));
        loadFirstForm(VistaNavigator.WELCOME);
        stage.setOnCloseRequest(event -> {
            VistaNavigator.getCurrentVistaController().formDeinitialize();
            AppState.getSessionMap().forEach((account, session) -> session.close());
            Platform.exit();
            System.exit(0);
        });
        AppState.setPrimaryStage(stage);
        stage.show();
    }

    void loadFirstForm(String form) throws IOException {
        VistaNavigator.loadFirstForm(form);
    }

    private Pane loadMainPane() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        BorderPane mainPane = loader.load(getResourceAsStream(VistaNavigator.MAIN));
        MainController mainController = loader.getController();
        VistaNavigator.saveMainController(mainController);
        return mainPane;
    }


    private Scene createScene(Pane mainPane) {
        Scene scene = new Scene(mainPane);
        addFonts();
        scene.getStylesheets().setAll(WalletApp.class.getResource("/styles.css").toExternalForm());
        return scene;
    }


    private void addFonts() {
        Font.loadFont(WalletApp.class.getResource("/fonts/Montserrat-Black.otf").toExternalForm(), 14);
        Font.loadFont(WalletApp.class.getResource("/fonts/Montserrat-BlackItalic.otf").toExternalForm(), 14);
        Font.loadFont(WalletApp.class.getResource("/fonts/Montserrat-Bold.otf").toExternalForm(), 14);
        Font.loadFont(WalletApp.class.getResource("/fonts/Montserrat-BoldItalic.otf").toExternalForm(), 14);
        Font.loadFont(WalletApp.class.getResource("/fonts/Montserrat-ExtraBold.otf").toExternalForm(), 14);
        Font.loadFont(WalletApp.class.getResource("/fonts/Montserrat-ExtraBoldItalic.otf").toExternalForm(), 14);
        Font.loadFont(WalletApp.class.getResource("/fonts/Montserrat-ExtraLight.otf").toExternalForm(), 14);
        Font.loadFont(WalletApp.class.getResource("/fonts/Montserrat-ExtraLightItalic.otf").toExternalForm(), 14);
        Font.loadFont(WalletApp.class.getResource("/fonts/Montserrat-Italic.otf").toExternalForm(), 14);
        Font.loadFont(WalletApp.class.getResource("/fonts/Montserrat-Light.otf").toExternalForm(), 14);
        Font.loadFont(WalletApp.class.getResource("/fonts/Montserrat-LightItalic.otf").toExternalForm(), 14);
        Font.loadFont(WalletApp.class.getResource("/fonts/Montserrat-Medium.otf").toExternalForm(), 14);
        Font.loadFont(WalletApp.class.getResource("/fonts/Montserrat-MediumItalic.otf").toExternalForm(), 14);
        Font.loadFont(WalletApp.class.getResource("/fonts/Montserrat-Regular.otf").toExternalForm(), 14);
        Font.loadFont(WalletApp.class.getResource("/fonts/Montserrat-SemiBold.otf").toExternalForm(), 14);
        Font.loadFont(WalletApp.class.getResource("/fonts/Montserrat-SemiBoldItalic.otf").toExternalForm(), 14);
        Font.loadFont(WalletApp.class.getResource("/fonts/Montserrat-Thin.otf").toExternalForm(), 14);
        Font.loadFont(WalletApp.class.getResource("/fonts/Montserrat-ThinItalic.otf").toExternalForm(), 14);
    }

}
