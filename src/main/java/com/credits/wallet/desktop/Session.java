package com.credits.wallet.desktop;

import com.credits.client.node.pojo.CompiledSmartContract;
import com.credits.client.node.pojo.SmartContractTransactionData;
import com.credits.client.node.util.ObjectKeeper;
import com.credits.wallet.desktop.service.ContractInteractionService;
import com.credits.wallet.desktop.service.SmartContractNamesDomainService;
import com.credits.wallet.desktop.struct.DeploySmartListItem;
import com.credits.wallet.desktop.struct.UnapprovedTransactionData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class Session {
    public int lastSmartIndex;
    public ObjectKeeper<ConcurrentHashMap<String, String>> coinsKeeper;
    public ObjectKeeper<HashMap<String, CompiledSmartContract>> favoriteContractsKeeper;
    public ObjectKeeper<ArrayList<DeploySmartListItem>> deployContractsKeeper;
    public ObjectKeeper<HashMap<String, List<SmartContractTransactionData>>> contractsTransactionsKeeper;
    public String account;
    public ConcurrentHashMap<Long, UnapprovedTransactionData> unapprovedTransactions = new ConcurrentHashMap<>();
    public ContractInteractionService contractInteractionService = initializeContractInteractionService();
    public SmartContractNamesDomainService smartContractNamesDomainService = SmartContractNamesDomainService.getInstance();

    public Session(String pubKey) {
        account = pubKey;
        favoriteContractsKeeper = new ObjectKeeper<>(this.account, "favorite");
        deployContractsKeeper = new ObjectKeeper<>(this.account,"deployedContracts");
        coinsKeeper = new ObjectKeeper<>(this.account, "coins");
        contractsTransactionsKeeper = new ObjectKeeper<>(this.account, "contractsTransactions");
        AppState.getSessionMap().put(pubKey,this);
    }

    public ContractInteractionService initializeContractInteractionService() {
        return new ContractInteractionService(this);
    }

    public void close() {
        if(favoriteContractsKeeper != null){
            favoriteContractsKeeper.flush();
        }
        if(coinsKeeper != null){
            coinsKeeper.flush();
        }
        if(deployContractsKeeper != null){
            deployContractsKeeper.flush();
        }
        if(contractsTransactionsKeeper != null){
            contractsTransactionsKeeper.flush();
        }
    }
}
