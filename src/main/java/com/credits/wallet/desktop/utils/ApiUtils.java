package com.credits.wallet.desktop.utils;

import com.credits.client.node.pojo.ModifiedInnerIdSenderReceiver;
import com.credits.general.thrift.generated.Variant;
import com.credits.wallet.desktop.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.credits.wallet.desktop.utils.TokenStandardData.NOT_A_TOKEN;
import static java.util.Arrays.stream;


public class ApiUtils {

    private final static Logger LOGGER = LoggerFactory.getLogger(ApiUtils.class);


//    public static Pair<Long, TransactionFlowResultData> createTransaction(
//            CalcTransactionIdSourceTargetResult transactionData, BigDecimal amount, short offeredMaxFee16Bits, String text, List<ByteBuffer> usedSmartContracts, Session session)
//        throws NodeClientException, ConverterException {
//        return Pair.of(
//            transactionData.getTransactionId(),
//            AppState.getNodeApiService().transactionFlow(getTransactionFlowData(transactionData, amount, offeredMaxFee16Bits, null, text, usedSmartContracts, session,
//                                                                                transactionData.getByteSource(),
//                                                                                transactionData.getByteTarget(),
//                                                                                transactionData.getTransactionId())));
//    }
//
//    public static TransactionFlowData createInvokeContractTransaction(long transactionId,
//                                                                      String initiator,
//                                                                      String contractAddress,
//                                                                      String method,
//                                                                      List<Variant> params,
//                                                                      boolean isGetter,
//                                                                      List<ByteBuffer> usedContracts) {
//        final var contractInvocationData = new SmartContractInvocationData(null, method, params, usedContracts, isGetter);
//        final var transactionFlowData = createSmartContractTransaction(
//
//    }

//    public static Pair<Long, TransactionFlowResultData> createSmartContractTransaction(
//        CalcTransactionIdSourceTargetResult transactionData,
//        short offeredMaxFee,
//        SmartContractData smartContractData,
//        List<ByteBuffer> usedSmartContracts,
//        Session session)
//        throws NodeClientException, ConverterException {
//
//        final var transactionId = transactionData.getTransactionId();
//        var contractAddress = smartContractData.getAddress();
//
//        if (contractAddress == null || contractAddress.length == 0) {
//            final var initiatorAddress = transactionData.getByteSource();
//            final var byteCodeObjects = smartContractData.getSmartContractDeployData().getByteCodeObjects();
//            contractAddress = generateSmartContractAddress(initiatorAddress, transactionId, byteCodeObjects);
//            smartContractData.setAddress(contractAddress);
//        }
//
//        final var contractInvocationData = new SmartContractInvocationData(smartContractData.getSmartContractDeployData(),
//                                                                           smartContractData.getMethod(),
//                                                                           smartContractData.getParams(),
//                                                                           usedSmartContracts,
//                                                                           smartContractData.isGetterMethod());
//
//        final var transactionFlowData = getTransactionFlowData(transactionData,
//                                                               ZERO,
//                                                               offeredMaxFee,
//                                                               contractInvocationData,
//                                                               null,
//                                                               usedSmartContracts,
//                                                               session,
//                                                               transactionData.getByteSource(),
//                                                               transactionData.getByteTarget(),
//                                                               transactionData.getTransactionId());
//
//        final var contractFlowData = new SmartContractTransactionFlowData(transactionFlowData, contractInvocationData);
//
//        return Pair.of(transactionId, AppState.getNodeApiService().smartContractTransactionFlow(contractFlowData));
//    }

//    private static TransactionFlowData getTransactionFlowData(
//            CalcTransactionIdSourceTargetResult transactionData,
//            BigDecimal amount,
//            short offeredMaxFee16Bits,
//            SmartContractInvocationData smartContractInvocationData,
//            String text,
//            List<ByteBuffer> usedSmartContracts,
//            Session session, byte[] source, byte[] target, long transactionId) {
//
//        final var currency = 1;
//        final var textBytes = text != null
//                              ? text.getBytes(StandardCharsets.UTF_8)
//                              : null;
//
//        String method = null;
//        List<Variant> params = null;
//        byte[] smartContractBytes = null;
//        if(smartContractInvocationData != null) {
//            method = smartContractInvocationData.getMethod();
//            params = smartContractInvocationData.getParams();
//            smartContractBytes = serializeByThrift(smartContractInvocationData);
//        }
//
//        saveTransactionIntoMap(transactionData, amount.toString(), String.valueOf(currency), session , method, params);
//
//        final var  transactionFlowData = new TransactionFlowData(transactionId, source, target, amount, offeredMaxFee16Bits, smartContractBytes, textBytes, usedSmartContracts);
//        SignUtils.signTransaction(transactionFlowData, AppState.getPrivateKey());
//        return transactionFlowData;
//    }

    public static int getTokenStandard(Class<?> contractClass) {
        final var contractInterfaces = contractClass.getInterfaces();
        return stream(TokenStandardData.values())
                .filter(ts -> stream(contractInterfaces).anyMatch(ci -> ts.getTokenStandardClass().equals(ci)))
                .findFirst()
                .orElse(NOT_A_TOKEN).getId();
    }

    private static void saveTransactionIntoMap(
            ModifiedInnerIdSenderReceiver transactionData,
            String amount,
            String currency,
            Session session,
            String scMethod,
            List<Variant> csParams) {
//        if (session.unapprovedTransactions == null) {
//            session.unapprovedTransactions = new ConcurrentHashMap<>();
//        }
//        long shortTransactionId = NodePojoConverter.getShortTransactionId(transactionData.getTransactionId());
//        UnapprovedTransactionData unapprovedTransactionData =
//                new UnapprovedTransactionData(String.valueOf(shortTransactionId), transactionData.getWideSource(),
//                                              transactionData.getWideTarget(), amount, currency, scMethod, csParams);
//        session.unapprovedTransactions.put(shortTransactionId, unapprovedTransactionData);
    }
}