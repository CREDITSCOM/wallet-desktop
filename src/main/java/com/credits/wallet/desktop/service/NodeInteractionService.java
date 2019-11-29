package com.credits.wallet.desktop.service;

import com.credits.client.node.pojo.ModifiedInnerIdSenderReceiver;
import com.credits.client.node.pojo.TransactionFlowResultData;
import com.credits.client.node.service.NodeApiService;
import com.credits.general.pojo.ByteCodeObjectData;
import com.credits.general.util.Callback;
import org.apache.commons.lang3.ArrayUtils;

import java.math.BigDecimal;
import java.security.PrivateKey;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;

import static com.credits.general.crypto.Blake2S.generateHash;
import static com.credits.general.util.GeneralConverter.*;
import static com.credits.general.util.Utils.threadPool;
import static com.credits.general.util.variant.VariantConverter.toObject;
import static com.credits.general.util.variant.VariantConverter.toVariant;
import static java.util.Collections.emptyList;


public class NodeInteractionService {
    public static final String TRANSFER_METHOD = "transfer";
    public static final String BALANCE_OF_METHOD = "balanceOf";
    private final NodeApiService nodeApi;
    private final String account;
    private final PrivateKey privateKey;
    private final AtomicLong lastTransactionCount;

    public NodeInteractionService(NodeApiService nodeApiService, String account, PrivateKey privateKey) {
        this.nodeApi = nodeApiService;
        this.account = account;
        this.privateKey = privateKey;
        lastTransactionCount = new AtomicLong();
    }

    public long getActualTransactionsCount() {
        final var totalTransactionsByAddress = nodeApi.getWalletTransactionsCount(account);

        if (totalTransactionsByAddress > lastTransactionCount.get()) {
            lastTransactionCount.set(totalTransactionsByAddress);
        } else {
            lastTransactionCount.incrementAndGet();
        }

        return lastTransactionCount.get();
    }

    public void getBalanceOfToken(String tokenAddress, Callback<BigDecimal> callback) {
        CompletableFuture
                .supplyAsync(() -> nodeApi.getSmartContract(tokenAddress), threadPool)
                .thenApply(sc -> nodeApi.invokeContractGetterMethod(account,
                                                                    sc.getBase58Address(),
                                                                    BALANCE_OF_METHOD,
                                                                    List.of(account),
                                                                    emptyList()))
                .whenComplete((resultData, throwable) -> {
                    if (throwable != null) callback.onError(throwable);
                    else resultData.getContractResult().ifPresent(variant -> callback.onSuccess((BigDecimal) toObject(variant)));
                });
    }

    public void transferCsTo(String receiver,
                             BigDecimal amount,
                             float maxFee,
                             List<String> usedContracts,
                             byte[] userData,
                             BiConsumer<? super TransactionFlowResultData, ? super Throwable> handleResult) {
        CompletableFuture
                .supplyAsync(() -> getActualIdSenderReceiver(receiver), threadPool)
                .thenApply(it -> nodeApi.submitTransferTransaction(it.getTransactionInnerId(),
                                                                   it.getModifiedSenderAddress(),
                                                                   it.getModifiedReceiverAddress(),
                                                                   amount,
                                                                   maxFee,
                                                                   usedContracts,
                                                                   userData,
                                                                   privateKey))
                .whenComplete(handleResult);
    }

    public void transferTokensTo(String tokenAddress,
                                 String receiver,
                                 BigDecimal amount,
                                 float offeredMaxFee,
                                 BiConsumer<? super Boolean, ? super Throwable> handleResult) {
        CompletableFuture
                .supplyAsync(() -> nodeApi.getSmartContract(tokenAddress), threadPool)
                .thenApply((sc) -> {
                    final var actualData = getActualIdSenderReceiver(receiver);
                    return nodeApi.submitInvokeTransaction(actualData.getTransactionInnerId(),
                                                           actualData.getModifiedSenderAddress(),
                                                           actualData.getModifiedReceiverAddress(),
                                                           TRANSFER_METHOD,
                                                           List.of(receiver, amount),
                                                           offeredMaxFee,
                                                           emptyList(),
                                                           privateKey);
                })
                .whenComplete((resultData, throwable) -> {
                    if (throwable != null) handleResult.accept(false, throwable);
                    else resultData
                            .getContractResult()
                            .ifPresent(variant -> handleResult.accept((Boolean) toObject(variant), null));
                });
    }

    public void submitInvokeTransaction(String contractAddress,
                                        float maxFee,
                                        String method,
                                        List<Object> params,
                                        List<String> usedContacts,
                                        BiConsumer<? super String, ? super Throwable> handleResult) {
        CompletableFuture
                .supplyAsync(() -> {
                    final var actualData = getActualIdSenderReceiver(contractAddress);
                    return nodeApi.submitInvokeTransaction(actualData.getTransactionInnerId(),
                                                           actualData.getModifiedSenderAddress(),
                                                           actualData.getModifiedReceiverAddress(),
                                                           method,
                                                           params,
                                                           maxFee,
                                                           usedContacts,
                                                           privateKey);
                })
                .whenComplete(handleContractResult(handleResult));
    }

    public void submitDeployTransaction(String contractAddress,
                                        String sourceCode, List<ByteCodeObjectData> byteCodeObjects, float maxFee,
                                        int tokenStandardId, List<String> usedContacts,
                                        BiConsumer<? super String, ? super Throwable> handleResult) {
        CompletableFuture
                .supplyAsync(() -> {
                    final var actualData = getActualIdSenderReceiver(contractAddress);
                    return nodeApi.submitDeployTransaction(actualData.getTransactionInnerId(),
                                                           actualData.getModifiedSenderAddress(),
                                                           actualData.getModifiedReceiverAddress(),
                                                           sourceCode,
                                                           byteCodeObjects,
                                                           tokenStandardId,
                                                           maxFee,
                                                           usedContacts,
                                                           privateKey);
                })
                .whenComplete(handleContractResult(handleResult));
    }

    public void invokeContractGetter(String contractAddress,
                                     String methodName,
                                     List<Object> params,
                                     List<String> usedContracts,
                                     BiConsumer<? super String, ? super Throwable> handleResult) {
        CompletableFuture
                .supplyAsync(() -> nodeApi.invokeContractGetterMethod(account, contractAddress, methodName, params, usedContracts), threadPool)
                .whenComplete(handleContractResult(handleResult));
    }

    public String generateSmartContractAddress(List<ByteCodeObjectData> byteCodeObjects) {

        final var innerId = getActualTransactionsCount() + 1;
        final var innerIdBytes = toByteArray(innerId);

        final var sliceId = Arrays.copyOfRange(innerIdBytes, 2, 8);
        ArrayUtils.reverse(sliceId);

        final var accountBytes = decodeFromBASE58(account);
        var seed = ArrayUtils.addAll(accountBytes, toByteArrayLittleEndian(sliceId, sliceId.length));

        for (final var unit : byteCodeObjects) {
            seed = ArrayUtils.addAll(seed, unit.getByteCode());
        }
        seed = toByteArrayLittleEndian(seed, seed.length);

        return encodeToBASE58(generateHash(seed));
    }

    private BiConsumer<TransactionFlowResultData, Throwable> handleContractResult(BiConsumer<? super String, ? super Throwable> handleResult) {
        return (result, throwable) -> {
            if (throwable != null) handleResult.accept(throwable.getMessage(), throwable);
            else handleResult.accept("" + toObject(result.getContractResult().orElse(toVariant(null))), null);
        };
    }

    public ModifiedInnerIdSenderReceiver getActualIdSenderReceiver(String receiver) {
        final var transactionsCount = getActualTransactionsCount();
        return nodeApi.modifyInnerIdSenderReceiver(transactionsCount, account, receiver);
    }
}