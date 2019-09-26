package com.credits.wallet.desktop.controller;

import com.credits.client.node.exception.NodeClientException;
import com.credits.client.node.pojo.*;
import com.credits.general.classload.ByteCodeContractClassLoader;
import com.credits.general.exception.CreditsException;
import com.credits.general.pojo.ByteCodeObjectData;
import com.credits.general.thrift.generated.Variant;
import com.credits.general.util.Callback;
import com.credits.general.util.GeneralConverter;
import com.credits.general.util.variant.VariantConverter;
import com.credits.wallet.desktop.AppState;
import com.credits.wallet.desktop.VistaNavigator;
import com.credits.wallet.desktop.struct.MethodData;
import com.credits.wallet.desktop.struct.SmartContractTabRow;
import com.credits.wallet.desktop.struct.SmartContractTransactionTabRow;
import com.credits.wallet.desktop.utils.sourcecode.SourceCodeUtils;
import com.credits.wallet.desktop.utils.sourcecode.codeArea.CodeAreaUtils;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import org.apache.commons.lang3.tuple.Pair;
import org.fxmisc.richtext.CodeArea;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static com.credits.client.node.service.NodeApiServiceImpl.async;
import static com.credits.client.node.service.NodeApiServiceImpl.handleCallback;
import static com.credits.client.node.util.TransactionIdCalculateUtils.calcTransactionIdSourceTarget;
import static com.credits.general.thrift.generated.Variant._Fields.V_STRING;
import static com.credits.general.util.GeneralConverter.*;
import static com.credits.general.util.Utils.rethrowUnchecked;
import static com.credits.general.util.Utils.threadPool;
import static com.credits.general.util.variant.VariantConverter.toVariant;
import static com.credits.wallet.desktop.AppState.NODE_ERROR;
import static com.credits.wallet.desktop.utils.ApiUtils.createSmartContractTransaction;
import static com.credits.wallet.desktop.utils.FormUtils.*;
import static com.credits.wallet.desktop.utils.SmartContractsUtils.getSmartsListFromField;
import static org.apache.commons.io.FileUtils.deleteDirectory;


public class SmartContractController extends AbstractController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SmartContractController.class);
    private static final String ERR_FEE = "Fee must be greater than 0";
    private static final String ERR_GETTING_TRANSACTION_HISTORY = "Error getting transaction history";
    private static final Set<String> OBJECT_METHODS = Set.of("getClass", "hashCode", "equals", "toString", "notify", "notifyAll", "wait", "finalize");

    private CodeArea codeArea;
    private Method currentMethod;
    private HashMap<String, CompiledSmartContract> favoriteContracts;

    private final List<SmartContractTransactionTabRow> unapprovedList = new ArrayList<>();
    private final List<SmartContractTransactionTabRow> approvedList = new ArrayList<>();
    private CompiledSmartContract selectedContract;
    private SimpleBooleanProperty storeContractState = new SimpleBooleanProperty();

    @FXML
    private HBox hbFeeLayout;
    @FXML
    private CheckBox cbStoreContractState;
    @FXML
    public TextField usdSmart;
    @FXML
    public Tab myContractsTab;
    @FXML
    public Tab favoriteContractsTab;
    @FXML
    public ToggleButton tbFavorite;
    @FXML
    private Pane pControls;
    @FXML
    private TextField tfAddress;
    @FXML
    private ComboBox<MethodData> cbMethods;
    @FXML
    private TableView<SmartContractTabRow> smartContractTableView;
    @FXML
    private TableView<SmartContractTabRow> favoriteContractTableView;
    @FXML
    private TextField tfSearchAddress;
    @FXML
    private StackPane pCodePanel;
    @FXML
    private AnchorPane pParams;
    @FXML
    private AnchorPane pParamsContainer;
    @FXML
    private TextField feeField;
    @FXML
    private Label actualOfferedMaxFeeLabel;
    @FXML
    private TableView<SmartContractTransactionTabRow> approvedTableView;
    @FXML
    private TableView<SmartContractTransactionTabRow> unapprovedTableView;


    @FXML
    private void handleBack() {
        VistaNavigator.loadVista(VistaNavigator.WALLET);
    }

    @FXML
    private void handleCreate() {
        VistaNavigator.loadVista(VistaNavigator.SMART_CONTRACT_DEPLOY);
    }

    @FXML
    private void handleSearch() {
        String address = tfSearchAddress.getText();
        async(() -> AppState.getNodeApiService().getSmartContract(address), handleGetSmartContractResult());
    }

    @FXML
    private void handleExecute() {
        final short fee;
        try {
            if (storeContractState.getValue()) {
                if (checkFeeFieldNotValid()) return;
                fee = getActualOfferedMaxFee16Bits(feeField);
                selectedContract.setGetterMethod(false);
            } else {
                fee = 0;
                selectedContract.setGetterMethod(true);
            }

            final var usedContracts = getSmartsListFromField(usdSmart.getText());
            final var params = getVariantsFromParamsFields();

            selectedContract.setMethod(currentMethod.getName());
            selectedContract.setParams(params);

            CompletableFuture
                    .supplyAsync(
                            () -> calcTransactionIdSourceTarget(
                                    AppState.getNodeApiService(),
                                    session.account,
                                    selectedContract.getBase58Address(),
                                    true),
                            threadPool)
                    .thenApply(
                            (transactionData) -> createSmartContractTransaction(
                                    transactionData,
                                    fee,
                                    selectedContract,
                                    usedContracts,
                                    session))
                    .whenComplete(handleCallback(handleExecuteResult()));
        } catch (CreditsException e) {
            LOGGER.error("failed!", e);
            showError(e.getMessage());
        }
    }

    @FXML
    private void handleRefreshTransactions() {
        fillTransactionsTables(selectedContract.getBase58Address());
    }

    @FXML
    private void cbMethodsOnAction() {
        this.pParams.setVisible(false);
        final var selectedItem = this.cbMethods.getSelectionModel().getSelectedItem();
        this.currentMethod = selectedItem != null ? selectedItem.getMethod() : null;
        if (this.currentMethod == null) {
            return;
        }
        Parameter[] params = this.currentMethod.getParameters();
        this.pParamsContainer.getChildren().clear();
        if (params.length > 0) {
            this.pParams.setVisible(true);
            double layoutY = 10;
            for (Parameter param : params) {
                TextField paramValueTextField = new TextField();
                paramValueTextField.setLayoutX(250);
                paramValueTextField.setLayoutY(layoutY);
                paramValueTextField.setStyle(
                        "-fx-background-color:  #fff; -fx-border-radius:15; -fx-border-width: 1; -fx-border-color:  #000; -fx-font-size: 16px");
                paramValueTextField.setPrefSize(500, 30);
                Label paramNameLabel = new Label(param.getType().getSimpleName() + " " + param.getName());
                paramNameLabel.setLayoutX(10);
                paramNameLabel.setLayoutY(layoutY + 5);
                paramNameLabel.setStyle("-fx-font-size: 18px");
                paramNameLabel.setLabelFor(paramValueTextField);
                this.pParamsContainer.getChildren().addAll(paramNameLabel, paramValueTextField);
                layoutY += 40;
            }
            this.pParamsContainer.setPrefHeight(layoutY);
        }
    }

    @FXML
    private void handleRefreshSmarts() {
        updateSelectedTab();
    }

    @FXML
    private void handleStoreContractState(ActionEvent actionEvent) {
        hbFeeLayout.setVisible(storeContractState.getValue());
    }

    @FXML
    private void updateSelectedTab() {
        if (myContractsTab != null && myContractsTab.isSelected()) {
            refreshContractsTab();
        } else if (favoriteContractsTab != null && favoriteContractsTab.isSelected()) {
            refreshFavoriteContractsTab();
        }
    }

    @Override
    public void initializeForm(Map<String, Object> objects) {
        tbFavorite.setVisible(false);
        pControls.setVisible(false);
        pCodePanel.setVisible(false);
        hbFeeLayout.setVisible(false);
        codeArea = CodeAreaUtils.initCodeArea(this.pCodePanel, true);
        codeArea.setEditable(false);
        codeArea.copy();
        favoriteContracts = session.favoriteContractsKeeper.getKeptObject().orElseGet(HashMap::new);
        favoriteContracts.forEach((key, value) -> {
            SmartContractClass contractClass;
            try {
                contractClass = compileContractClass(value.getSmartContractDeployData().getByteCodeObjects());
            } catch (Throwable e) {
                rethrowUnchecked(() -> deleteDirectory(session.favoriteContractsKeeper.getAccountDirectory().toFile()));
                return;
            }
            value.setContractClass(contractClass);
        });
        initializeContractsTable(smartContractTableView);
        initializeContractsTable(favoriteContractTableView);
        refreshFavoriteContractsTab();
        initFeeField(feeField, actualOfferedMaxFeeLabel);
        initTransactionHistoryTable(approvedTableView);
        initTransactionHistoryTable(unapprovedTableView);
        cbStoreContractState.selectedProperty().bindBidirectional(storeContractState);
    }

    @Override
    public void formDeinitialize() {

    }

    private Callback<SmartContractData> handleGetSmartContractResult() {
        return new Callback<>() {
            @Override
            public void onSuccess(SmartContractData contractData) throws CreditsException {
                refreshSmartContractForm(compileSmartContract(contractData));
            }

            @Override
            public void onError(Throwable e) {
                LOGGER.error("failed!", e);
                showError("Can't get smart-contract from the node. Reason: " + e.getMessage());
            }
        };
    }

    private void refreshSmartContractForm(CompiledSmartContract compiledSmartContract) {
        if (compiledSmartContract == null || compiledSmartContract.getSmartContractDeployData().getByteCodeObjects().size() == 0 ||
                compiledSmartContract.getAddress().length == 0) {
            tbFavorite.setVisible(false);
            pControls.setVisible(false);
            pCodePanel.setVisible(false);
        } else {
            selectedContract = compiledSmartContract;

            pControls.setVisible(true);
            pCodePanel.setVisible(true);

            tbFavorite.setOnAction(handleFavoriteButtonEvent(tbFavorite, selectedContract));
            tbFavorite.setVisible(true);
            findInFavoriteThenSelect(compiledSmartContract, tbFavorite);

            String sourceCode = compiledSmartContract.getSmartContractDeployData().getSourceCode();
            tfAddress.setText(compiledSmartContract.getBase58Address());
            MethodData[] methods = Arrays.stream(compiledSmartContract.getContractClass().getRootClass().getMethods())
                    .filter(m -> !OBJECT_METHODS.contains(m.getName()))
                    .map(MethodData::new)
                    .toArray(MethodData[]::new);
            cbMethods.getItems().clear();
            cbMethods.getItems().addAll(methods);

            codeArea.clear();
            codeArea.replaceText(0, 0, SourceCodeUtils.formatSourceCode(sourceCode));

            fillTransactionsTables(compiledSmartContract.getBase58Address());
        }
    }

    private HashMap<String, List<SmartContractTransactionData>> getKeptContractsTransactions() {
        return session.contractsTransactionsKeeper.getKeptObject().orElseGet(HashMap::new);
    }

    private void fillTransactionsTables(String base58Address) {
        approvedTableView.getItems().clear();
        approvedList.clear();
        unapprovedTableView.getItems().clear();
        unapprovedList.clear();

        List<SmartContractTransactionData> contractTransactions = getKeptContractsTransactions().getOrDefault(base58Address, new ArrayList<>());
        async(
                () -> {
                    SmartContractData smartContractData = AppState.getNodeApiService().getSmartContract(base58Address);
                    long transactionCount = smartContractData.getTransactionsCount();
                    return AppState.getNodeApiService().getSmartContractTransactions(base58Address,
                                                                                     0,
                                                                                     transactionCount - contractTransactions.size());
                },
                handleGetSmartContractTransactionsResult()
        );
    }

    private void keepTransactions(String base58Address, List<SmartContractTransactionData> transactionList) {
        if (transactionList.size() == 0) {
            return;
        }
        HashMap<String, List<SmartContractTransactionData>> contractsTransactions = getKeptContractsTransactions();
        List<SmartContractTransactionData> transactions = contractsTransactions.getOrDefault(base58Address, new ArrayList<>());
        transactions.addAll(transactionList);
        contractsTransactions.put(base58Address, transactions);
        session.contractsTransactionsKeeper.keepObject(contractsTransactions);
    }

    private Callback<List<SmartContractTransactionData>> handleGetSmartContractTransactionsResult() {
        return new Callback<>() {

            @Override
            public void onSuccess(List<SmartContractTransactionData> transactionList) throws CreditsException {

                keepTransactions(selectedContract.getBase58Address(), transactionList);
                List<SmartContractTransactionData> smartContractTransactionDataList =
                        getKeptContractsTransactions().getOrDefault(selectedContract.getBase58Address(), new ArrayList<>());

                smartContractTransactionDataList.forEach(transactionData -> {

                    SmartTransInfoData smartInfo = transactionData.getSmartInfo();
                    SmartContractTransactionTabRow tableRow = new SmartContractTransactionTabRow();

                    tableRow.setId(transactionData.getInnerId());
                    tableRow.setAmount(GeneralConverter.toString(transactionData.getAmount()));
                    tableRow.setSource(encodeToBASE58(transactionData.getSource()));
                    tableRow.setTarget(encodeToBASE58(transactionData.getTarget()));
                    tableRow.setBlockId(transactionData.getBlockNumber() + "." + transactionData.getIndexIntoBlock());
                    tableRow.setType(getTransactionDescType(transactionData));
                    tableRow.setMethod(transactionData.getMethod());
                    tableRow.setParams(transactionData.getParams());
                    tableRow.setSmartInfo(smartInfo);

                    if (smartInfo == null) {
                        approvedList.add(tableRow);
                    } else if (smartInfo.isSmartDeploy()) {
                        SmartDeployTransInfoData smartDeployTransInfoData = smartInfo.getSmartDeployTransInfoData();
                        if (smartDeployTransInfoData.getState() == SmartOperationStateData.SOS_Success) {
                            approvedList.add(tableRow);
                        } else {
                            unapprovedList.add(tableRow);
                        }
                    } else if (smartInfo.isSmartExecution()) {
                        SmartExecutionTransInfoData smartExecutionTransInfoData = smartInfo.getSmartExecutionTransInfoData();
                        if (smartExecutionTransInfoData.getState() == SmartOperationStateData.SOS_Success) {
                            approvedList.add(tableRow);
                        } else {
                            unapprovedList.add(tableRow);
                        }
                    } else if (smartInfo.isSmartState()) {
                        SmartStateTransInfoData smartStateTransInfoData = smartInfo.getSmartStateTransInfoData();
                        if (smartStateTransInfoData.success) {
                            approvedList.add(tableRow);
                        } else {
                            unapprovedList.add(tableRow);
                        }
                    }
                });

                session.unapprovedTransactions.forEach((id, value) -> {

                    if (smartContractTransactionDataList.stream().anyMatch(smartContractTransactionData -> {
                        SmartTransInfoData smartInfo = smartContractTransactionData.getSmartInfo();
                        if (smartInfo == null) { return true; }
                        return smartContractTransactionData.getInnerId() == id && !smartInfo.isSmartState();
                    })) {
                        session.unapprovedTransactions.remove(id);
                    } else {
                        SmartContractTransactionTabRow tableRow = new SmartContractTransactionTabRow();
                        tableRow.setId(id);
                        tableRow.setAmount(value.getAmount());
                        tableRow.setCurrency(value.getCurrency());
                        tableRow.setSource(value.getSource());
                        tableRow.setTarget(value.getTarget());
                        tableRow.setType("unknown");
                        tableRow.setMethod(value.getScMethod());
                        tableRow.setParams(value.getScParams());
                        unapprovedList.add(tableRow);
                    }
                });

                Platform.runLater(() -> {
                    approvedTableView.getItems().addAll(approvedList);
                });
                Platform.runLater(() -> {
                    unapprovedTableView.getItems().addAll(unapprovedList);
                });
            }

            @Override
            public void onError(Throwable e) {
                LOGGER.error(e.getMessage());
                if (e instanceof NodeClientException) {
                    showError(NODE_ERROR);
                } else {
                    showError(ERR_GETTING_TRANSACTION_HISTORY + "\n" + e.getMessage());
                }
            }
        };
    }

    private void initTransactionHistoryTable(TableView<SmartContractTransactionTabRow> tableView) {
        tableView.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("id"));
        tableView.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("blockId"));
        tableView.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("source"));
        tableView.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("target"));
        tableView.getColumns().get(4).setCellValueFactory(new PropertyValueFactory<>("amount"));
        tableView.getColumns().get(5).setCellValueFactory(new PropertyValueFactory<>("type"));
        tableView.setOnMousePressed(event -> {
            if ((event.isPrimaryButtonDown() || event.getButton() == MouseButton.PRIMARY) && event.getClickCount() == 2) {
                SmartContractTransactionTabRow tabRow = tableView.getSelectionModel().getSelectedItem();
                if (tabRow != null) {
                    HashMap<String, Object> params = new HashMap<>();
                    params.put("selectedTransactionRow", tabRow);
                    VistaNavigator.showFormModal(VistaNavigator.SMART_CONTRACT_TRANSACTION, params);
                }
            }
        });
    }

    private void initializeContractsTable(TableView<SmartContractTabRow> tableView) {
        initializeRowFactory(tableView);
        initializeColumns(tableView);
    }

    private void initializeColumns(TableView<SmartContractTabRow> tableView) {
        TableColumn<SmartContractTabRow, String> idColumn = new TableColumn<>();
        idColumn.setPrefWidth(tableView.getPrefWidth() * 0.85);
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        addTooltipToColumnCells(idColumn);

        TableColumn<SmartContractTabRow, String> favColumn = new TableColumn<>();
        favColumn.setPrefWidth(tableView.getPrefWidth() * 0.1);
        favColumn.setCellValueFactory(new PropertyValueFactory<>("fav"));

        tableView.getColumns().add(idColumn);
        tableView.getColumns().add(favColumn);
    }

    private void initializeRowFactory(TableView<SmartContractTabRow> tableView) {
        tableView.setRowFactory(tv -> {
            TableRow<SmartContractTabRow> row = new TableRow<>();
            row.setOnMouseClicked(getMouseEventRowHandler(row));
            return row;
        });
    }

    private EventHandler<MouseEvent> getMouseEventRowHandler(TableRow<SmartContractTabRow> row) {
        return event -> {
            if (event.getClickCount() == 2 && (!row.isEmpty())) {
                refreshSmartContractForm(row.getItem().getCompiledSmartContract());
            }
        };
    }

    private EventHandler<ActionEvent> handleFavoriteButtonEvent(ToggleButton pressedButton, CompiledSmartContract compiledSmartContract) {
        return event -> switchFavoriteState(pressedButton, compiledSmartContract);
    }

    private void switchFavoriteState(ToggleButton favoriteButton, CompiledSmartContract compiledSmartContract) {
        if (favoriteContracts.containsKey(compiledSmartContract.getBase58Address())) {
            favoriteContracts.remove(compiledSmartContract.getBase58Address());
            favoriteButton.setSelected(false);
            setFavoriteCurrentContract(compiledSmartContract, false);
            changeFavoriteStateIntoTab(smartContractTableView, compiledSmartContract, false);
        } else {
            favoriteContracts.put(compiledSmartContract.getBase58Address(), compiledSmartContract);
            favoriteButton.setSelected(true);
            tbFavorite.setSelected(true);
            setFavoriteCurrentContract(compiledSmartContract, true);
            changeFavoriteStateIntoTab(smartContractTableView, compiledSmartContract, true);
        }
        refreshFavoriteContractsTab();
    }

    private void changeFavoriteStateIntoTab(TableView<SmartContractTabRow> table, SmartContractData smartContractData, boolean isSelected) {
        table.getItems()
                .stream()
                .filter(row -> row.getCompiledSmartContract().equals(smartContractData))
                .findFirst()
                .ifPresent(row -> row.getFav().setSelected(isSelected));
        table.refresh();
    }

    private void setFavoriteCurrentContract(SmartContractData smartContractData, boolean isSelected) {
        if (selectedContract != null &&
                smartContractData.getBase58Address().equals(selectedContract.getBase58Address())) {
            tbFavorite.setSelected(isSelected);
        } else {
            tbFavorite.setSelected(false);
        }
    }

    private CompiledSmartContract compileSmartContract(SmartContractData smartContractData) {
        return new CompiledSmartContract(
                smartContractData,
                compileContractClass(smartContractData.getSmartContractDeployData().getByteCodeObjects()),
                smartContractData.getSmartContractDeployData().getByteCodeObjects());
    }

    private SmartContractClass compileContractClass(List<ByteCodeObjectData> byteCodeObjects) {
        Class<?> contractClass = null;
        List<Class<?>> innerContractClasses = new ArrayList<>();
        ByteCodeContractClassLoader byteCodeContractClassLoader = new ByteCodeContractClassLoader();
        for (ByteCodeObjectData byteCodeObject : byteCodeObjects) {
            Class<?> clazz = byteCodeContractClassLoader.loadClass(byteCodeObject.getName(), byteCodeObject.getByteCode());
            if (clazz.getName().contains("$")) {
                innerContractClasses.add(clazz);
            } else {
                contractClass = clazz;
            }
        }
        return new SmartContractClass(contractClass, innerContractClasses);
    }

    private void findInFavoriteThenSelect(SmartContractData smartContractData, ToggleButton tbFavorite) {
        if (favoriteContracts.containsKey(smartContractData.getBase58Address())) {
            tbFavorite.setSelected(true);
        } else {
            tbFavorite.setSelected(false);
        }
    }

    private void refreshContractsTab() {
        async(() -> AppState.getNodeApiService().getSmartContracts(session.account), handleGetSmartContractsResult());
    }

    private void refreshFavoriteContractsTab() {
        if (favoriteContractTableView != null && favoriteContracts != null) {
            favoriteContractTableView.getItems().clear();
            favoriteContracts.forEach(
                    (contractName, contractData) -> addContractToTable(favoriteContractTableView, contractData));
        }
        if (session != null) {
            session.favoriteContractsKeeper.keepObject(favoriteContracts);
        }
    }

    private Callback<List<SmartContractData>> handleGetSmartContractsResult() {
        return new Callback<>() {
            @Override
            public void onSuccess(List<SmartContractData> smartContracts) throws CreditsException {
                Platform.runLater(() -> {
                    smartContractTableView.getItems().clear();
                    smartContracts.forEach(smartContract -> {
                        try {
                            addContractToTable(smartContractTableView, compileSmartContract(smartContract));
                        } catch (Throwable e) {
                            LOGGER.error("can't compile smart contract {}. Reason: {}", smartContract.getBase58Address(), e.getMessage());
                        }
                    });

                    if (selectedContract != null) {
                        ObservableList<SmartContractTabRow> items = smartContractTableView.getItems();
                        items.forEach(smartContractTabRow -> {
                            if (smartContractTabRow.getCompiledSmartContract().equals(selectedContract)) {
                                smartContractTableView.requestFocus();
                                smartContractTableView.getSelectionModel().select(smartContractTabRow);
                                refreshSmartContractForm(selectedContract);
                            }
                        });
                    }
                });
            }

            @Override
            public void onError(Throwable e) {
                LOGGER.error("Can't get smart-contracts from the node. Reason: {}", e.getMessage());
                showError("Can't get smart-contracts from the node. Reason: " + e.getMessage());
            }
        };
    }

    private boolean checkFeeFieldNotValid() {
        if (feeField.getText().isEmpty() || (toBigDecimal(feeField.getText()).compareTo(BigDecimal.ZERO) <= 0)) {
            feeField.clear();
            feeField.setPromptText(ERR_FEE);
            feeField.setStyle(feeField.getStyle() + " -fx-prompt-text-fill: red;");
            feeField.setStyle(feeField.getStyle() + "-fx-border-color: red;");
            return true;
        }
        return false;
    }

    private ArrayList<Variant> getVariantsFromParamsFields() {
        final var params = currentMethod.getParameters();
        final var paramsValuesContainer = pParamsContainer.getChildren();
        final var paramsAsVariant = new ArrayList<Variant>();
        for (int i = 0, j = 0; i < paramsValuesContainer.size(); i++) {
            Node node = paramsValuesContainer.get(i);
            if (node instanceof TextField) {
                String paramValue = ((TextField) node).getText();
                Parameter parameter = params[j++];
                paramsAsVariant.add(toVariant(parameter.getType().getTypeName(), createObjectFromString(paramValue, parameter.getType())));
            }
        }
        return paramsAsVariant;
    }

    private Callback<Pair<Long, TransactionFlowResultData>> handleExecuteResult() {
        return new Callback<>() {
            @Override
            public void onSuccess(Pair<Long, TransactionFlowResultData> resultData) {
                TransactionFlowResultData transactionFlowResultData = resultData.getRight();
                Variant contractResult = transactionFlowResultData.getContractResult().orElseGet(() -> new Variant(V_STRING, "unknown result"));
                String message = transactionFlowResultData.getMessage();

                if (!contractResult.isSetV_void()) {
                    Object resultObj = VariantConverter.toObject(contractResult);
                    message = "Result: " + resultObj.toString() + "\n\n" + message;
                }

                showPlatformInfo(message);
            }

            @Override
            public void onError(Throwable e) {
                LOGGER.error("failed!", e);
                showPlatformError(e.getMessage());
            }
        };
    }

    private void addContractToTable(TableView<SmartContractTabRow> table, CompiledSmartContract smartContractData) {
        ToggleButton favoriteButton = new ToggleButton();
        favoriteButton.setOnAction(handleFavoriteButtonEvent(favoriteButton, smartContractData));
        SmartContractTabRow row =
                new SmartContractTabRow(smartContractData.getBase58Address(), favoriteButton, smartContractData);
        row.setFav(favoriteButton);
        findInFavoriteThenSelect(smartContractData, favoriteButton);
        table.getItems().add(row);
    }
}