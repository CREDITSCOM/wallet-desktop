package com.credits.wallet.desktop.controller;

import com.credits.client.node.pojo.SmartContractClass;
import com.credits.client.node.pojo.SmartContractData;
import com.credits.client.node.pojo.SmartContractTransactionData;
import com.credits.client.node.pojo.TransactionFlowResultData;
import com.credits.general.classload.ByteCodeContractClassLoader;
import com.credits.general.exception.CreditsException;
import com.credits.general.pojo.ByteCodeObjectData;
import com.credits.general.thrift.generated.Variant;
import com.credits.general.util.Callback;
import com.credits.general.util.compiler.CompilationException;
import com.credits.general.util.variant.VariantConverter;
import com.credits.wallet.desktop.AppState;
import com.credits.wallet.desktop.VistaNavigator;
import com.credits.wallet.desktop.database.table.SmartContract;
import com.credits.wallet.desktop.pojo.CompiledSmartContract;
import com.credits.wallet.desktop.service.DatabaseService;
import com.credits.wallet.desktop.struct.MethodData;
import com.credits.wallet.desktop.struct.SmartContractTabRow;
import com.credits.wallet.desktop.struct.SmartContractTransactionTabRow;
import com.credits.wallet.desktop.utils.sourcecode.codeArea.CodeAreaUtils;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
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
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.fxmisc.richtext.CodeArea;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;

import static com.credits.client.node.service.NodeApiServiceImpl.async;
import static com.credits.general.thrift.generated.Variant._Fields.V_STRING;
import static com.credits.general.util.GeneralConverter.createObjectFromString;
import static com.credits.general.util.GeneralConverter.toBigDecimal;
import static com.credits.wallet.desktop.AppState.getDatabase;
import static com.credits.wallet.desktop.AppState.getNodeInteractionService;
import static com.credits.wallet.desktop.utils.FormUtils.*;
import static com.credits.wallet.desktop.utils.sourcecode.SourceCodeUtils.formatSourceCode;
import static java.lang.Float.parseFloat;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;


public class SmartContractController extends AbstractController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SmartContractController.class);
    private static final String ERR_FEE = "Fee must be greater than 0";
    private static final String ERR_GETTING_TRANSACTION_HISTORY = "Error getting transaction history";
    private static final Set<String> OBJECT_METHODS = Set.of("getClass", "hashCode", "equals", "toString", "notify", "notifyAll", "wait", "finalize");
    private final DatabaseService database = getDatabase();

    private CodeArea codeArea;
    private Method selectedMethod;
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
    public TextField tfUsedContracts;
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
        VistaNavigator.reloadForm(VistaNavigator.WALLET);
    }

    @FXML
    private void handleCreate() {
        VistaNavigator.reloadForm(VistaNavigator.SMART_CONTRACT_DEPLOY);
    }

    @FXML
    private void handleSearch() {
        String address = tfSearchAddress.getText();
        async(() -> AppState.getNodeApiService().getSmartContract(address), handleGetSmartContractResult());
    }

    @FXML
    private void handleExecute() {
        var fee = 0f;
        final boolean isGetterMethod;
        if (storeContractState.getValue()) {
            if (checkFeeFieldNotValid()) return;
            fee = parseFloat(feeField.getText());
            isGetterMethod = false;
        } else {
            fee = 0;
            isGetterMethod = true;
        }

        final var contractAddress = selectedContract.getAddress();
        final var methodName = selectedMethod.getName();
        final var params = getObjectsFromParamsField();
        final var usedContracts = parseUsedContractsField(tfUsedContracts.getText());
        final var nodeCall = getNodeInteractionService();

        if (isGetterMethod) {
            nodeCall.invokeContractGetter(contractAddress, methodName, params, usedContracts,
                                                             handleContractMethodResult(contractAddress, methodName, params));
        } else {
            nodeCall.submitInvokeTransaction(contractAddress, fee, methodName, params, usedContracts,
                                                                handleContractMethodResult(contractAddress, methodName, params));
        }
    }

    private BiConsumer<String, Throwable> handleContractMethodResult(String contractAddress, String methodName, ArrayList<Object> params) {
        return (result, error) -> {
            if (error == null) {
                showPlatformInfo("Smart-contract invocation response",
                                 "Response from \"" + contractAddress + "\"",
                                 "method: " + methodName + "\n"
                                         + "params: " + params.toString() + "\n\n"
                                         + "return value:" + "\n" + result);
            } else {
                LOGGER.error("smart contract invocation error. Reason {}", error.getMessage());
                showError("Smart-contract invocation", "Error invocation", error.getMessage());
            }

        };
    }

    @FXML
    private void handleRefreshTransactions() {
//        fillTransactionsTables(selectedContract.getBase58Address());
    }

    @FXML
    private void handleMethodsCheckBoxAction() {
        if (methodIsNotSelected()) return;

        final var params = selectedMethod.getParameters();
        final var layoutY = new AtomicInteger(10);
        final var paramRow = new ArrayList<Node>();
        buildParamsLayout(params, layoutY, paramRow);
        Platform.runLater(() -> refreshParametersPane(paramRow, layoutY.get()));
    }

    private boolean methodIsNotSelected() {
        final var selectedItem = cbMethods.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            selectedMethod = null;
            return true;
        } else {
            selectedMethod = selectedItem.getMethod();
        }
        return false;
    }

    private void buildParamsLayout(Parameter[] params, AtomicInteger layoutY, ArrayList<Node> nodes) {
        if (params.length > 0) {
            for (Parameter param : params) {
                final var paramValueTextField = initParameterTextField(layoutY.get());
                final var paramNameLabel = initParameterLabel(layoutY.get(), param, paramValueTextField);
                nodes.add(paramValueTextField);
                nodes.add(paramNameLabel);
                layoutY.getAndAdd(40);
            }

        }
    }

    private void refreshParametersPane(List<Node> nodes, int layoutMaxHeight) {
        clearParametersContainer();
        pParamsContainer.getChildren().addAll(nodes);
        pParamsContainer.setPrefHeight(layoutMaxHeight);
        pParams.setVisible(true);
    }

    private void clearParametersContainer() {
        pParamsContainer.getChildren().clear();
    }

    private Label initParameterLabel(double layoutY, Parameter param, TextField paramValueTextField) {
        final var paramNameLabel = new Label(param.getType().getSimpleName() + " " + param.getName());
        paramNameLabel.setLayoutX(10);
        paramNameLabel.setLayoutY(layoutY + 5);
        paramNameLabel.setStyle("-fx-font-size: 18px");
        paramNameLabel.setLabelFor(paramValueTextField);
        return paramNameLabel;
    }

    private TextField initParameterTextField(double layoutY) {
        final var textField = new TextField();
        textField.setLayoutX(250);
        textField.setLayoutY(layoutY);
        textField.setStyle("-fx-background-color:  #fff; -fx-border-radius:15; -fx-border-width: 1; -fx-border-color:  #000; -fx-font-size: 16px");
        textField.setPrefSize(500, 30);
        return textField;
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
    public void initialize(Map<String, ?> objects) {
        tbFavorite.setVisible(false);
        showContractExecutionsControls(false);
        hbFeeLayout.setVisible(false);
        codeArea = CodeAreaUtils.initCodeArea(pCodePanel, true);
        codeArea.setEditable(false);
        codeArea.copy();
        favoriteContracts = session.favoriteContractsKeeper.getKeptObject().orElseGet(HashMap::new);
        favoriteContracts.forEach((key, value) -> {
//            SmartContractClass contractClass;
//            try {
//                contractClass = compileContractClass(value.getSmartContractDeployData().getByteCodeObjects());
//            } catch (Throwable e) {
//                rethrowUnchecked(() -> deleteDirectory(session.favoriteContractsKeeper.getAccountDirectory().toFile()));
//                return;
//            }
//            value.setContractClass(contractClass);
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
    public void deinitialize() {

    }

    private Callback<SmartContractData> handleGetSmartContractResult() {
        return new Callback<>() {
            @Override
            public void onSuccess(SmartContractData contractData) throws CreditsException {
//                refreshSmartContractForm(compileSmartContract(contractData));
            }

            @Override
            public void onError(Throwable e) {
                LOGGER.error("failed!", e);
                showError("Can't get smart-contract from the node. Reason: " + e.getMessage());
            }
        };
    }

    private void refreshSmartContractForm() {
        final var methods = Arrays
                .stream(selectedContract.getContractClass().getMethods())
                .filter(m -> !OBJECT_METHODS.contains(m.getName()))
                .map(MethodData::new)
                .toArray(MethodData[]::new);

        Platform.runLater(() -> {
            showContractExecutionsControls(true);


            tfAddress.setText(selectedContract.getAddress());

            tbFavorite.setOnAction(handleFavoriteButtonEvent(tbFavorite, selectedContract));
            tbFavorite.setVisible(true);

            refreshParametersPane(emptyList(), 0);
            fillMethodsCheckBox(methods);
            refreshCodeArea();

            findInFavoriteThenSelect(selectedContract.getAddress(), tbFavorite);
        });

//        fillTransactionsTables(compiledSmartContract.getBase58Address());
    }

    private void fillMethodsCheckBox(MethodData[] methods) {
        cbMethods.getItems().clear();
        cbMethods.getItems().addAll(methods);
        cbMethods.getSelectionModel().selectFirst();
    }

    private void refreshCodeArea() {
        codeArea.clear();
        final var formattedSourceCode = formatSourceCode(selectedContract.getSourcecode());
        codeArea.replaceText(0, 0, formattedSourceCode);
    }

    private void showContractExecutionsControls(boolean isShow) {
        pControls.setVisible(isShow);
        pCodePanel.setVisible(isShow);
    }

    private HashMap<String, List<SmartContractTransactionData>> getKeptContractsTransactions() {
        return session.contractsTransactionsKeeper.getKeptObject().orElseGet(HashMap::new);
    }

    private void fillTransactionsTables(String base58Address) {
//        approvedTableView.getItems().clear();
//        approvedList.clear();
//        unapprovedTableView.getItems().clear();
//        unapprovedList.clear();
//
//        List<SmartContractTransactionData> contractTransactions = getKeptContractsTransactions().getOrDefault(base58Address, new ArrayList<>());
//        async(() -> {
//                  SmartContractData smartContractData = AppState.getNodeApiService().getSmartContract(base58Address);
//                  long transactionCount = smartContractData.getTransactionsCount();
//                  return AppState.getNodeApiService().getSmartContractTransactionList(base58Address,
//                                                                                      0,
//                                                                                      transactionCount - contractTransactions.size());
//              },
//              handleGetSmartContractTransactionsResult()
//        );
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
//        return new Callback<>() {
//
//            @Override
//            public void onSuccess(List<SmartContractTransactionData> transactionList) throws CreditsException {
//
//                keepTransactions(selectedContract.getBase58Address(), transactionList);
//                List<SmartContractTransactionData> smartContractTransactionDataList =
//                        getKeptContractsTransactions().getOrDefault(selectedContract.getBase58Address(), new ArrayList<>());
//
//                smartContractTransactionDataList.forEach(transactionData -> {
//
//                    SmartTransInfoData smartInfo = transactionData.getSmartInfo();
//                    SmartContractTransactionTabRow tableRow = new SmartContractTransactionTabRow();
//
//                    tableRow.setId(transactionData.getBlockNumber() + "." + transactionData.getIndexIntoBlock());
//                    tableRow.setAmount(GeneralConverter.toString(transactionData.getAmount()));
//                    tableRow.setSender(encodeToBASE58(transactionData.getSource()));
//                    tableRow.setReceiver(encodeToBASE58(transactionData.getTarget()));
////                    tableRow.setBlockNumber(transactionData.getBlockNumber() + "." + transactionData.getIndexIntoBlock());
//                    tableRow.setType(getTransactionDescType(transactionData));
////                    tableRow.setMethod(transactionData.getMethod());
////                    tableRow.setParams(transactionData.getParams());
//                    tableRow.setSmartInfo(smartInfo);
//
//                    if (smartInfo == null) {
//                        approvedList.add(tableRow);
//                    } else if (smartInfo.isSmartDeploy()) {
//                        SmartDeployTransInfoData smartDeployTransInfoData = smartInfo.getSmartDeployTransInfoData();
//                        if (smartDeployTransInfoData.getState() == SmartOperationStateData.SOS_Success) {
//                            approvedList.add(tableRow);
//                        } else {
//                            unapprovedList.add(tableRow);
//                        }
//                    } else if (smartInfo.isSmartExecution()) {
//                        SmartExecutionTransInfoData smartExecutionTransInfoData = smartInfo.getSmartExecutionTransInfoData();
//                        if (smartExecutionTransInfoData.getState() == SmartOperationStateData.SOS_Success) {
//                            approvedList.add(tableRow);
//                        } else {
//                            unapprovedList.add(tableRow);
//                        }
//                    } else if (smartInfo.isSmartState()) {
//                        SmartStateTransInfoData smartStateTransInfoData = smartInfo.getSmartStateTransInfoData();
//                        if (smartStateTransInfoData.success) {
//                            approvedList.add(tableRow);
//                        } else {
//                            unapprovedList.add(tableRow);
//                        }
//                    }
//                });
//
//                session.unapprovedTransactions.forEach((id, value) -> {
//
//                    if (smartContractTransactionDataList.stream().anyMatch(smartContractTransactionData -> {
//                        SmartTransInfoData smartInfo = smartContractTransactionData.getSmartInfo();
//                        if (smartInfo == null) {
//                            return true;
//                        }
//                        return smartContractTransactionData.getInnerId() == id && !smartInfo.isSmartState();
//                    })) {
//                        session.unapprovedTransactions.remove(id);
//                    } else {
//                        SmartContractTransactionTabRow tableRow = new SmartContractTransactionTabRow();
//                        tableRow.setId(value.getId());
//                        tableRow.setAmount(value.getAmount());
//                        tableRow.setSender(value.getSource());
//                        tableRow.setReceiver(value.getTarget());
//                        tableRow.setType("unknown");
////                        tableRow.setMethod(value.getScMethod());
////                        tableRow.setParams(value.getScParams());
//                        unapprovedList.add(tableRow);
//                    }
//                });
//
//                Platform.runLater(() -> {
//                    approvedTableView.getItems().addAll(approvedList);
//                });
//                Platform.runLater(() -> {
//                    unapprovedTableView.getItems().addAll(unapprovedList);
//                });
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                LOGGER.error(e.getMessage());
//                if (e instanceof NodeClientException) {
//                    showError(NODE_ERROR);
//                } else {
//                    showError(ERR_GETTING_TRANSACTION_HISTORY + "\n" + e.getMessage());
//                }
//            }
//        };
        return null;
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
                    VistaNavigator.showModalForm(VistaNavigator.SMART_CONTRACT_TRANSACTION, params);
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
        idColumn.setCellValueFactory(new PropertyValueFactory<>("contractAddress"));
        addTooltipToColumnCells(idColumn);

        TableColumn<SmartContractTabRow, String> favColumn = new TableColumn<>();
        favColumn.setPrefWidth(tableView.getPrefWidth() * 0.1);
        favColumn.setCellValueFactory(new PropertyValueFactory<>("favoriteButton"));

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
                final var contractAddress = row.getItem().getContractAddress();
                database.getSmartContract(contractAddress, handleGetSmartContract());
            }
        };
    }

    private Callback<SmartContract> handleGetSmartContract() {
        return new Callback<>() {
            @Override
            public void onSuccess(SmartContract smartContract) throws CreditsException {
                try {
                    final var smartContractClass = compileContractClass(smartContract.getByteCodeObjectList()).getRootClass();
                    final var contractAddress = smartContract.getWallet().getAddress();
                    final var sourceCode = smartContract.getSourceCode();
                    selectedContract = new CompiledSmartContract(contractAddress, sourceCode, smartContractClass);
                    refreshSmartContractForm();
                } catch (CompilationException e) {
                    LOGGER.error("can'r compile smart contract. Reason {}", ExceptionUtils.getRootCauseMessage(e));
                }
            }

            @Override
            public void onError(Throwable e) {

            }
        };
    }

    private EventHandler<ActionEvent> handleFavoriteButtonEvent(ToggleButton pressedButton, CompiledSmartContract compiledSmartContract) {
        return event -> switchFavoriteState(pressedButton, compiledSmartContract);
    }

    private void switchFavoriteState(ToggleButton favoriteButton, CompiledSmartContract compiledSmartContract) {
//        if (favoriteContracts.containsKey(compiledSmartContract.getBase58Address())) {
//            favoriteContracts.remove(compiledSmartContract.getBase58Address());
//            favoriteButton.setSelected(false);
//            setFavoriteCurrentContract(compiledSmartContract, false);
//            changeFavoriteStateIntoTab(smartContractTableView, compiledSmartContract, false);
//        } else {
//            favoriteContracts.put(compiledSmartContract.getBase58Address(), compiledSmartContract);
//            favoriteButton.setSelected(true);
//            tbFavorite.setSelected(true);
//            setFavoriteCurrentContract(compiledSmartContract, true);
//            changeFavoriteStateIntoTab(smartContractTableView, compiledSmartContract, true);
//        }
//        refreshFavoriteContractsTab();
    }

    private void changeFavoriteStateIntoTab(TableView<SmartContractTabRow> table, SmartContractData smartContractData, boolean isSelected) {
        table.getItems()
                .stream()
                .filter(row -> row.getContractAddress().equals(smartContractData.getBase58Address()))
                .findFirst()
                .ifPresent(row -> row.getFavoriteButton().setSelected(isSelected));
        table.refresh();
    }

    private void setFavoriteCurrentContract(SmartContractData smartContractData, boolean isSelected) {
//        if (selectedContract != null &&
//                smartContractData.getBase58Address().equals(selectedContract.getBase58Address())) {
//            tbFavorite.setSelected(isSelected);
//        } else {
//            tbFavorite.setSelected(false);
//        }
    }

    private CompiledSmartContract compileSmartContract(SmartContractData smartContractData) {
//        return new CompiledSmartContract(
//                smartContractData,
//                compileContractClass(smartContractData.getSmartContractDeployData().getByteCodeObjects()),
//                smartContractData.getSmartContractDeployData().getByteCodeObjects());
        return null;
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

    private void findInFavoriteThenSelect(String contractAddress, ToggleButton tbFavorite) {
        if (favoriteContracts.containsKey(contractAddress)) {
            tbFavorite.setSelected(true);
        } else {
            tbFavorite.setSelected(false);
        }
    }

    private void refreshContractsTab() {
        database.getDeployerContractsAddressList(session.account, new Callback<>() {
            @Override
            public void onSuccess(List<String> resultData) throws CreditsException {
                smartContractTableView.getItems()
                        .addAll(resultData.stream().map(address -> new SmartContractTabRow(address, new ToggleButton())).collect(toList()));
            }

            @Override
            public void onError(Throwable e) {
                LOGGER.error("can't read smart contract addresses from database. Reason:{}", ExceptionUtils.getRootCauseMessage(e));

            }
        });
    }

    private void refreshFavoriteContractsTab() {
        if (favoriteContractTableView != null && favoriteContracts != null) {
            favoriteContractTableView.getItems().clear();
            favoriteContracts.forEach((contractName, contractData) -> addContractToTable(favoriteContractTableView, contractData));
        }
        if (session != null) {
            session.favoriteContractsKeeper.keepObject(favoriteContracts);
        }
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

    private ArrayList<Object> getObjectsFromParamsField() {
        final var params = selectedMethod.getParameters();
        final var paramsValuesContainer = pParamsContainer.getChildren();
        final var paramsAsObject = new ArrayList<>();
        for (int i = 0, j = 0; i < paramsValuesContainer.size(); i++) {
            final var node = paramsValuesContainer.get(i);
            if (node instanceof TextField) {
                final var paramValue = ((TextField) node).getText();
                final var parameter = params[j++];
                paramsAsObject.add(createObjectFromString(paramValue, parameter.getType()));
            }
        }
        return paramsAsObject;
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
//        ToggleButton favoriteButton = new ToggleButton();
//        favoriteButton.setOnAction(handleFavoriteButtonEvent(favoriteButton, smartContractData));
//        SmartContractTabRow row = new SmartContractTabRow(smartContractData.getBase58Address(), favoriteButton);
//        findInFavoriteThenSelect(smartContractData, favoriteButton);
//        table.getItems().add(row);
    }
}