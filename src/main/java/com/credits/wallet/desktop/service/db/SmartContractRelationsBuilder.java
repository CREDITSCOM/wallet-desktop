package com.credits.wallet.desktop.service.db;

import com.credits.client.node.pojo.SmartContractData;
import com.credits.general.pojo.ByteCodeObjectData;
import com.credits.wallet.desktop.database.DatabaseHelper;
import com.credits.wallet.desktop.database.table.*;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static com.credits.general.util.GeneralConverter.encodeToBASE58;

@RequiredArgsConstructor
public class SmartContractRelationsBuilder {
    final DatabaseHelper database;
    final List<SmartContract> smartContractRelation = new ArrayList<SmartContract>();
    final List<Bytecode> bytecodeRelation = new ArrayList<>();
    final List<WalletHasSmartContract> walletHasSmartContractsRelation = new ArrayList<>();
    final List<SmartContractHasBytecode> smartContractHasByteCodeRelation = new ArrayList<>();

    void parse(List<SmartContractData> smartContractDataList) {
        smartContractDataList.forEach(this::parse);
    }

    void parse(SmartContractData smartContractData) {
        final var smartContractEntity = createSmartContractEntity(smartContractData);
        final var byteCodeObjectDataList = smartContractData.getSmartContractDeployData().getByteCodeObjectDataList();
        for (ByteCodeObjectData byteCodeObject : byteCodeObjectDataList) {
            final var bytecodeEntity = new Bytecode(byteCodeObject.getName(), byteCodeObject.getByteCode());
            bytecodeRelation.add(bytecodeEntity);
            smartContractHasByteCodeRelation.add(new SmartContractHasBytecode(smartContractEntity, bytecodeEntity));
        }
        smartContractRelation.add(smartContractEntity);

        final var deployerAddress = encodeToBASE58(smartContractData.getDeployer());
        walletHasSmartContractsRelation.add(createWalletHasSmartContractEntity(deployerAddress, smartContractEntity));
    }

    private SmartContract createSmartContractEntity(SmartContractData smartContract) {
        final var deployData = smartContract.getSmartContractDeployData();
        final var contractAddress = new Wallet(encodeToBASE58(smartContract.getAddress()));
        final var sourceCode = deployData.getSourceCode();
        final var timeCreation = smartContract.getTimeCreation();
        final var contractState = smartContract.getObjectState();
        return new SmartContract(contractAddress, sourceCode, contractState, timeCreation);
    }

    private WalletHasSmartContract createWalletHasSmartContractEntity(String deployerAddress, SmartContract smartContractEntity) {
        final var deployerWallet = database.getOrCreateWallet(deployerAddress);
        return new WalletHasSmartContract(deployerWallet, smartContractEntity);
    }

    void updateDatabase() {
        database.keepBytecodeList(bytecodeRelation);
        database.keepSmartContractList(smartContractRelation);
        database.keepSmartContractHasByteCodeList(smartContractHasByteCodeRelation);
        database.keepWalletHasSmartContractList(walletHasSmartContractsRelation);
    }
}