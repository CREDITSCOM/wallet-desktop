package com.credits.wallet.desktop;

public class UITest {
//
//    String walletAddress;
//    String addressOne;
//    String addressTwo;
//    String addressThree;
//
//    /*
//    TransactionFlowResultData successResponse = new TransactionFlowResultData(new ApiResponseData(SUCCESS, "Success"),1312, GeneralConverter
//        .decodeFromBASE58(addressOne), GeneralConverter.decodeFromBASE58(addressTwo), new Variant(V_STRING, "success variant response"));
//    */
//    @Mock
//    NodeApiService mockNodeApiService;
//    @Mock
//    NodeInteractionService mockNodeInteractionService;
//    @Mock
//    Properties mockProperties;
//    @Mock
//    WalletApp mockWalletApp;
//
//    String startForm;
//
//    @Before
//    public void setUp() throws IOException {
//        MockitoAnnotations.initMocks(this);
//        doCallRealMethod().when(mockWalletApp).start(any());
//
//        doAnswer((Answer<Void>) invocationOnMock -> {
//            WelcomeController welcomeController = new WelcomeController();
//            welcomeController.session = new Session(walletAddress);
//            VistaNavigator.setCurrentVistaController(welcomeController);
//            VistaNavigator.loadFirstForm(startForm);
//            return null;
//        }).when(mockWalletApp).loadFirstForm(any());
//
//        when(mockNodeApiService.getBalance(anyString())).thenReturn(new BigDecimal("1000.123456789012345678"));
//
//        addressOne = "11111111111111111111111111111111111111111111";
//        addressTwo = "22222222222222222222222222222222222222222222";
//        addressThree = "33333333333333333333333333333333333333333333";
//
//        AppState.setPrivateKey(getPrivateKey());
//    }
//
//    private PrivateKey getPrivateKey() {
//        byte[] privateKeyByteArr = GeneralConverter.decodeFromBASE58("28Q9apRu1FyjzoRCoaiyZggbNjbERYh6K11LS94vNyC3HndJN35KBwb4rujEZoBja6j8A3sK7sCgGCU2jxiBr9sh");
//        PrivateKey privateKey = Ed25519.bytesToPrivateKey(privateKeyByteArr);
////        Assert.assertEquals(privateKey.hashCode(), -2062620138);
//        return privateKey;
//    }
//
//    @Ignore
//    @Test
//    public void allForms() throws Exception {
//        startForm = VistaNavigator.WELCOME;
//        //balances
//        doAnswer(returnBalance(new BigDecimal("2443113.00192177821876551"))).when(mockNodeInteractionService)
//            .getBalanceOfToken(anyString(), any());
//
//        when(mockNodeApiService.getTransactionsState(any(), any())).thenReturn(FakeData.transactionsStateGetResultData);
//
//        //transactions
//        when(mockNodeApiService.getTransactionList(any(), anyLong(), anyLong())).thenReturn(
//                FakeData.transactionsDataList
//        );
//        //        when(mockNodeApiService.transactionFlow(any())).thenReturn(successResponse);
//        when(mockNodeApiService.getWalletTransactionsCount(any())).thenReturn(new Long(1));
//        when(mockNodeApiService.getWalletId(walletAddress)).thenReturn(1);
//        when(mockNodeApiService.getWalletId(addressTwo)).thenReturn(2);
//        when(mockNodeApiService.getWalletId(addressThree)).thenReturn(0);
//
//        //smart-contracts
//        //when(mockNodeApiService.getSmartContract(any())).thenReturn(FakeData.smartContractDataList.get(1));
//        when(mockNodeApiService.getSmartContractList(any())).thenReturn(FakeData.smartContractDataList);
//
//        when(mockNodeApiService.smartContractTransactionFlow(any())).thenReturn(FakeData.transactionFlowResultData1);
//        runApp();
//    }
//
//    @Ignore
//    @Test
//    public void smartContractsForm() throws Exception {
//        when(mockNodeApiService.getSmartContractList(any())).thenReturn(FakeData.smartContractDataList);
//        startForm = VistaNavigator.SMART_CONTRACT;
//        when(mockNodeApiService.smartContractTransactionFlow(any())).thenReturn(FakeData.transactionFlowResultData1);
//        when(mockNodeApiService.getSmartContractList(any())).thenReturn(FakeData.smartContractDataList);
//        when(mockNodeApiService.getSmartContractTransactionList(any(), anyLong(), anyLong())).thenReturn(FakeData.smartContractTransactionsDataList);
//        when(mockNodeApiService.getTransactionsState(any(), any())).thenReturn(FakeData.transactionsStateGetResultData);
//        runApp();
//    }
//
//    @Ignore
//    @Test
//    public void deployForm() throws Exception {
//        startForm = VistaNavigator.SMART_CONTRACT_DEPLOY;
//        when(mockNodeApiService.smartContractTransactionFlow(any())).thenReturn(FakeData.transactionFlowResultData1);
//        runApp();
//    }
//
//    @SuppressWarnings("unchecked")
//    private Answer<Void> returnBalance(BigDecimal balance) {
//        return answer -> {
//            ((Callback<BigDecimal>) answer.getArgument(1)).onSuccess(balance);
//            return null;
//        };
//    }
//
//    private void runApp() throws InterruptedException {
//        Platform.runLater(() -> {
//            try {
//                mockWalletApp.start(new Stage());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        });
//        Thread.currentThread().join();
//    }
}
