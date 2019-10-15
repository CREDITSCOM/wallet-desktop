package com.credits.wallet.desktop.service;

import com.credits.client.node.pojo.ModifiedInnerIdSenderReceiver;
import com.credits.client.node.pojo.TransactionFlowResultData;
import com.credits.client.node.service.NodeApiService;
import com.credits.general.util.Callback;

import java.math.BigDecimal;
import java.security.PrivateKey;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;

import static com.credits.general.util.Utils.threadPool;
import static com.credits.general.util.variant.VariantConverter.toObject;
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
                    final ModifiedInnerIdSenderReceiver actualData = getActualIdSenderReceiver(receiver);
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

    public void invokeContractGetter(String contractAddress,
                                     String methodName,
                                     List<Object> params,
                                     List<String> usedContracts,
                                     BiConsumer<? super String, ? super Throwable> handleResult) {
        CompletableFuture
                .supplyAsync(() -> nodeApi.invokeContractGetterMethod(account, contractAddress, methodName, params, usedContracts), threadPool)
                .whenComplete((result, throwable) -> {
                    if (throwable != null) handleResult.accept(throwable.getMessage(), throwable);
                    else result
                            .getContractResult()
                            .ifPresent(variant -> handleResult.accept("" + toObject(variant), null));
                });
    }

    public ModifiedInnerIdSenderReceiver getActualIdSenderReceiver(String receiver) {
        final var transactionsCount = getActualTransactionsCount();
        return nodeApi.modifyInnerIdSenderReceiver(transactionsCount, account, receiver);
    }
}