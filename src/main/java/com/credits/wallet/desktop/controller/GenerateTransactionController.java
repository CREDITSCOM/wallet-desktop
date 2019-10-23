package com.credits.wallet.desktop.controller;


import com.credits.client.node.pojo.TransactionFlowResultData;
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

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static com.credits.wallet.desktop.AppState.CREDITS_TOKEN_NAME;
import static com.credits.wallet.desktop.AppState.NODE_ERROR;
import static java.util.Optional.ofNullable;


public class GenerateTransactionController extends AbstractController {
    private final static Logger LOGGER = LoggerFactory.getLogger(GenerateTransactionController.class);

    @FXML
    public Label coinType;

    @FXML
    private TextField transactionToAddress;

    @FXML
    TextField transactionText;

    @FXML
    private TextField transactionAmount;

    @FXML
    private TextField transactionFeeValue;

    @FXML
    private TextField usedSmartContracts;

    private short actualOfferedMaxFee16Bits;

    @FXML
    private void handleBack() {
        Map<String, Object> params = new HashMap<>();
        params.put("transactionFee", transactionFeeValue.getText());
        params.put("transactionToAddress", transactionToAddress.getText());
        params.put("transactionAmount", transactionAmount.getText());
        params.put("transactionText", transactionText.getText());
        params.put("coinType", coinType.getText());
        params.put("usedSmartContracts", usedSmartContracts.getText());
        VistaNavigator.reloadForm(VistaNavigator.WALLET, params);
    }

    @FXML
    private void handleGenerate() {
        final var nodeService = AppState.getNodeInteractionService();
        final var receiver = transactionToAddress.getText();
        final var amount = GeneralConverter.toBigDecimal(transactionAmount.getText());
        final var maxFee = GeneralConverter.toFloat(transactionFeeValue.getText());
        final var usedContracts = FormUtils.parseUsedContractsField(usedSmartContracts.getText());
        final var userData = transactionAmount.getText().getBytes(StandardCharsets.UTF_8);
        try {
            if (coinType.getText().equals(CREDITS_TOKEN_NAME)) {
                nodeService.transferCsTo(receiver, amount, maxFee, usedContracts, userData, (result, error) -> {
                    if (error == null) {
                        FormUtils.showPlatformInfo("Transaction creation result",
                                                   "Transaction created successfully",
                                                   "cs moved to address " + receiver);
                    } else {
                        FormUtils.showPlatformError("Transaction to address \"" + receiver + "\" failed. Reason:\n" + error.getMessage());
                    }
                });
            } else {
                final var coinTypeText = coinType.getText();
                final var coinsMap = session.coinsKeeper
                        .getKeptObject()
                        .orElseThrow(() -> new CreditsException("internal error. Can't get kept coins map object"));
                final var tokenAddress = ofNullable(coinsMap.get(coinTypeText))
                        .orElseThrow(() -> new CreditsException("internal error. Can't get \"" + coinTypeText + "\" from coins map"));

                nodeService.transferTokensTo(tokenAddress, receiver, amount, maxFee, ((booleanResult, error) -> {
                    if (error == null) {
                        FormUtils.showPlatformInfo("Transfer token \"" + coinTypeText + "\" to address \"" + receiver + "\" created successfully");
                    } else {
                        FormUtils.showPlatformError("Transfer token \"" + coinTypeText + "\" to address \"" + receiver + "\" failed. Reason:\n" + error.getMessage());
                    }
                }));
            }
        } catch (CreditsException e) {
            LOGGER.error(NODE_ERROR + ": " + e.getMessage(), e);
            FormUtils.showError(NODE_ERROR);
            return;
        }

        VistaNavigator.reloadForm(VistaNavigator.WALLET);
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
    public void initialize(Map<String, ?> objects) {
        transactionToAddress.setText(objects.get("transactionToAddress").toString());
        transactionFeeValue.setText(objects.get("transactionFee").toString());
        transactionAmount.setText(objects.get("transactionAmount").toString());
        transactionText.setText(objects.get("transactionText").toString());
        coinType.setText(objects.get("coinType").toString());
        actualOfferedMaxFee16Bits = (Short) objects.get("actualOfferedMaxFee16Bits");
        usedSmartContracts.setText(objects.get("usedSmartContracts").toString());
    }

    @Override
    public void deinitialize() {

    }
}
