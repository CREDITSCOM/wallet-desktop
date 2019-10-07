package com.credits.wallet.desktop.controller;


import com.credits.client.node.pojo.TransactionFlowResultData;
import com.credits.client.node.util.TransactionIdCalculateUtils;
import com.credits.general.exception.CreditsException;
import com.credits.general.util.Callback;
import com.credits.general.util.GeneralConverter;
import com.credits.wallet.desktop.AppState;
import com.credits.wallet.desktop.VistaNavigator;
import com.credits.wallet.desktop.utils.FormUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static com.credits.client.node.service.NodeApiServiceImpl.handleCallback;
import static com.credits.general.util.Utils.threadPool;
import static com.credits.wallet.desktop.AppState.CREDITS_TOKEN_NAME;
import static com.credits.wallet.desktop.AppState.NODE_ERROR;
import static com.credits.wallet.desktop.utils.ApiUtils.createTransaction;
import static com.credits.wallet.desktop.utils.SmartContractsUtils.getSmartsListFromField;


public class GenerateTransactionController extends AbstractController {
    private final static Logger LOGGER = LoggerFactory.getLogger(GenerateTransactionController.class);

    @FXML
    public Label coinType;

    @FXML
    private TextField tranToAddress;

    @FXML TextField tranText;

    @FXML
    private TextField tranAmount;

    @FXML
    private TextField tranFee;

    @FXML
    private TextField usedSmartContracts;

    @FXML
    private TextField tranToAddressDomainName;

    private short actualOfferedMaxFee16Bits;

    @FXML
    private void handleBack() {
        Map<String, Object> params = new HashMap<>();
        params.put("tranFee", tranFee.getText());
        params.put("tranToAddress", tranToAddress.getText());
        String tranToAddressDomainNameText = tranToAddressDomainName.getText();
        params.put("tranToAddressDomainName", tranToAddressDomainNameText.isEmpty() ? null : tranToAddressDomainNameText);
        params.put("tranAmount", tranAmount.getText());
        params.put("tranText", tranText.getText());
        params.put("coinType", coinType.getText());
        params.put("usedSmartContracts",usedSmartContracts.getText());
        VistaNavigator.loadVista(VistaNavigator.WALLET, params);
    }

    @FXML
    private void handleGenerate() {
        String toAddress = tranToAddress.getText();
        final var usedContracts = getSmartsListFromField(usedSmartContracts.getText());
        try {
            if(coinType.getText().equals(CREDITS_TOKEN_NAME)) {
                CompletableFuture
                    .supplyAsync(() -> TransactionIdCalculateUtils.calcTransactionIdSourceTarget(AppState.getNodeApiService(),session.account,
                                                                                                                             toAddress,
                        true),threadPool)
                    .thenApply((transactionData) -> createTransaction(transactionData, GeneralConverter.toBigDecimal(
                        tranAmount.getText()), actualOfferedMaxFee16Bits, tranText.getText(),usedContracts, session))
                    .whenComplete(handleCallback(handleTransactionResult()));
            } else {
                session.coinsKeeper.getKeptObject().ifPresent(coinsMap ->
                    Optional.ofNullable(coinsMap.get(coinType.getText())).ifPresent(
                        coin -> session.contractInteractionService.transferTo(coin, toAddress, GeneralConverter.toBigDecimal(
                            tranAmount.getText()), actualOfferedMaxFee16Bits, handleTransferTokenResult())));
            }
        } catch (CreditsException e) {
            LOGGER.error(NODE_ERROR + ": " + e.getMessage(), e);
            FormUtils.showError(NODE_ERROR);
            return;
        }

        VistaNavigator.loadVista(VistaNavigator.WALLET);
    }

    private Callback<String> handleTransferTokenResult() {
        return new Callback<String>() {
            @Override
            public void onSuccess(String message) throws CreditsException {
                FormUtils.showPlatformInfo(message);
            }

            @Override
            public void onError(Throwable e) {
                FormUtils.showPlatformError(e.getLocalizedMessage());
            }
        };
    }

    private Callback<Pair<Long, TransactionFlowResultData>> handleTransactionResult() {
        return new Callback<Pair<Long, TransactionFlowResultData>>() {
            @Override
            public void onSuccess(Pair<Long, TransactionFlowResultData> resultData) {
                FormUtils.showPlatformInfo("Transaction created");
            }

            @Override
            public void onError(Throwable e) {
                LOGGER.error("Failed!", e);
                FormUtils.showPlatformError(e.getMessage());
            }
        };
    }

    @Override
    public void initializeForm(Map<String,Object> objects) {

        Optional.ofNullable(objects.get("tranToAddressDomainName")).ifPresent(o -> {
            tranToAddressDomainName.setText("@" + o.toString());
            tranToAddressDomainName.setPrefWidth(tranToAddressDomainName.getText().length() * 70); // autosizing
        });
        tranToAddress.setText(objects.get("tranToAddress").toString());
        tranFee.setText(objects.get("tranFee").toString());
        tranAmount.setText(objects.get("tranAmount").toString());
        tranText.setText(objects.get("tranText").toString());
        coinType.setText(objects.get("coinType").toString());
        actualOfferedMaxFee16Bits = (Short)objects.get("actualOfferedMaxFee16Bits");
        usedSmartContracts.setText(objects.get("usedSmartContracts").toString());
    }

    @Override
    public void formDeinitialize() {

    }
}
