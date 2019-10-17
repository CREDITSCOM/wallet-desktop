package com.credits.wallet.desktop.controller;

import com.credits.client.node.pojo.SyncStateInfoData;
import com.credits.general.thrift.ThriftClientPool;
import com.credits.wallet.desktop.VistaNavigator;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.ProgressBar;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.credits.wallet.desktop.AppState.*;


public class HeaderController implements Initializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(HeaderController.class);

    public AbstractController parentController;

    @FXML
    public ProgressBar sync;

    @FXML
    public Label syncPercent;

    @FXML
    public Button btnWallet;
    @FXML
    public Button btnTransaction;
    @FXML
    public Button btnSmartExecute;
    @FXML
    public Button btnSmartDeploy;
    @FXML
    public Button btnLogout;
    @FXML
    public MenuButton btnSmartMenu;
    @FXML
    public Label lastRoundLabel;

    private ScheduledExecutorService headerExecService = Executors.newScheduledThreadPool(1);
    private Runnable runnable;
    private Future future;
    private boolean flag = false;
    private boolean connect = true;

    @FXML
    private void handleLogout() {
        parentController.closeSession();
        VistaNavigator.reloadForm(VistaNavigator.WELCOME);
    }

    public void handleWallet() {
        VistaNavigator.reloadForm(VistaNavigator.WALLET);
    }

    public void handleTransaction() {
        VistaNavigator.reloadForm(VistaNavigator.HISTORY);
    }

    public void handleSmartExecute() {
        VistaNavigator.reloadForm(VistaNavigator.SMART_CONTRACT);
    }

    public void handleSmartDeploy() {
        VistaNavigator.reloadForm(VistaNavigator.SMART_CONTRACT_DEPLOY);
    }

    public void initializeSynchronize() {
        headerExecService = Executors.newScheduledThreadPool(1);
        runnable = () -> {
            try {
                final var syncStateInfo = getNodeApiService().getSyncStateInfo();
                final var blockAndSyncPercent = calculateBlockAndSyncPercent(syncStateInfo);
                Long lastRound = blockAndSyncPercent.getLeft();
                int synchronizePercent = blockAndSyncPercent.getRight();
                Platform.runLater(() -> {
                    sync.setProgress((double) synchronizePercent / 100);
                    syncPercent.setText(synchronizePercent + "%");
                    lastRoundLabel.setText(String.valueOf(lastRound));
                });
                if (synchronizePercent == 100 && !flag) {
                    changeDelay(DELAY_AFTER_FULL_SYNC);
                }
                connect = true;
            } catch (ThriftClientPool.ThriftClientException te) {
                if (connect) {
                    changeDelay(30);
                }
                connect = false;
                LOGGER.error("Connection was refused");
            } catch (Exception e) {
                Platform.runLater(() -> {
                    sync.setProgress(0);
                    syncPercent.setText("0%");
                    lastRoundLabel.setText(String.valueOf(0));
                });
            }
        };
        future = headerExecService.scheduleWithFixedDelay(runnable, 0, DELAY_BEFORE_FULL_SYNC, TimeUnit.SECONDS);
    }

    private Pair<Long, Integer> calculateBlockAndSyncPercent(SyncStateInfoData syncStateInfo) {
        final var blockchainCurrentBlockNumber = syncStateInfo.getBlockchainCurrentBlockNumber();
        final var nodeCurrentBlockNumber = syncStateInfo.getNodeCurrentBlockNumber();
        final int percent = (int) (blockchainCurrentBlockNumber / nodeCurrentBlockNumber * 100);
        return Pair.of(blockchainCurrentBlockNumber, percent);

    }

    private void changeDelay(int delay) {
        flag = future.cancel(false);
        future = headerExecService.scheduleWithFixedDelay(runnable, delay, delay, TimeUnit.SECONDS);
    }


    public void changeElementVisible() {
        if (parentController.session == null) {
            setMainElementsVisible(false);
        } else {
            setMainElementsVisible(true);
        }
    }

    private void setMainElementsVisible(boolean b) {
        btnLogout.setVisible(b);
        btnSmartMenu.setVisible(b);
        btnSmartDeploy.setVisible(b);
        btnSmartExecute.setVisible(b);
        btnTransaction.setVisible(b);
        btnWallet.setVisible(b);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeSynchronize();
    }

    public void changeParentController(AbstractController newVistaController) {
        parentController = newVistaController;
        changeElementVisible();
    }
}
