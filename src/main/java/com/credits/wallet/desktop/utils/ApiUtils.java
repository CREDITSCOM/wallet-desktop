package com.credits.wallet.desktop.utils;

import com.credits.client.node.exception.NodeClientException;
import com.credits.client.node.pojo.*;
import com.credits.client.node.util.NodePojoConverter;
import com.credits.client.node.util.SignUtils;
import com.credits.general.thrift.generated.Variant;
import com.credits.general.util.exception.ConverterException;
import com.credits.wallet.desktop.AppState;
import com.credits.wallet.desktop.Session;
import com.credits.wallet.desktop.struct.UnapprovedTransactionData;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static com.credits.client.node.util.NodeClientUtils.serializeByThrift;
import static com.credits.client.node.util.TransactionIdCalculateUtils.CalcTransactionIdSourceTargetResult;
import static com.credits.wallet.desktop.utils.SmartContractsUtils.generateSmartContractAddress;
import static java.math.BigDecimal.ZERO;


public class ApiUtils {

    private final static Logger LOGGER = LoggerFactory.getLogger(ApiUtils.class);


    public static Pair<Long, TransactionFlowResultData> createTransaction(
            CalcTransactionIdSourceTargetResult transactionData, BigDecimal amount, short offeredMaxFee16Bits, String text, List<ByteBuffer> usedSmartContracts, Session session)
        throws NodeClientException, ConverterException {
        return Pair.of(
            transactionData.getTransactionId(),
            AppState.getNodeApiService().transactionFlow(getTransactionFlowData(transactionData, amount, offeredMaxFee16Bits, null, text, usedSmartContracts, session)));
    }

    public static Pair<Long, TransactionFlowResultData> createSmartContractTransaction(
        CalcTransactionIdSourceTargetResult transactionData,
        short offeredMaxFee,
        SmartContractData smartContractData,
        List<ByteBuffer> usedSmartContracts,
        Session session)
        throws NodeClientException, ConverterException {

        smartContractData.setAddress(
            generateSmartContractAddress(transactionData.getByteSource(), transactionData.getTransactionId(),
                                         smartContractData.getSmartContractDeployData().getByteCodeObjects()));

        SmartContractInvocationData smartContractInvocationData =
            new SmartContractInvocationData(smartContractData.getSmartContractDeployData(),
                                            smartContractData.getMethod(),
                                            smartContractData.getParams(),
                                            usedSmartContracts,
                                            smartContractData.isGetterMethod());

        SmartContractTransactionFlowData scData = new SmartContractTransactionFlowData(
            getTransactionFlowData(transactionData, ZERO, offeredMaxFee, smartContractInvocationData, null,
                            usedSmartContracts, session), smartContractInvocationData);

        return Pair.of(transactionData.getTransactionId(), AppState.getNodeApiService().smartContractTransactionFlow(scData));
    }

    private static TransactionFlowData getTransactionFlowData(
        CalcTransactionIdSourceTargetResult transactionData,
        BigDecimal amount, short offeredMaxFee16Bits, SmartContractInvocationData smartContractInvocationData, String text, List<ByteBuffer> usedSmartContracts, Session session) {
        long id = transactionData.getTransactionId();
        byte[] source = transactionData.getByteSource();
        byte[] target = transactionData.getByteTarget();
        byte currency = 1;
        byte[] textBytes = null;
        if (text != null) {
            textBytes = text.getBytes(StandardCharsets.UTF_8);
        }

        saveTransactionIntoMap(transactionData, amount.toString(), String.valueOf(currency), session,
                smartContractInvocationData == null ? null : smartContractInvocationData.getMethod(),
                smartContractInvocationData == null ? null : smartContractInvocationData.getParams()
        );

        byte[] smartContractBytes = smartContractInvocationData == null ? null : serializeByThrift(smartContractInvocationData);

        TransactionFlowData transactionFlowData =
            new TransactionFlowData(id, source, target, amount, offeredMaxFee16Bits, smartContractBytes, textBytes, usedSmartContracts);
        SignUtils.signTransaction(transactionFlowData, AppState.getPrivateKey());
        return transactionFlowData;
    }


    private static void saveTransactionIntoMap(
        CalcTransactionIdSourceTargetResult transactionData, String amount,
        String currency, Session session, String scMethod, List<Variant> csParams) {
        if (session.unapprovedTransactions == null) {
            session.unapprovedTransactions = new ConcurrentHashMap<>();
        }
        long shortTransactionId = NodePojoConverter.getShortTransactionId(transactionData.getTransactionId());
        UnapprovedTransactionData unapprovedTransactionData =
            new UnapprovedTransactionData(String.valueOf(shortTransactionId), transactionData.getWideSource(),
                                     transactionData.getWideTarget(), amount, currency, scMethod, csParams);
        session.unapprovedTransactions.put(shortTransactionId, unapprovedTransactionData);
    }
}