package com.credits.wallet.desktop;

public class SaveNodeApiQueriesTest {
//
//    WalletApp walletApp;
//    @Mock
//    NodeApiServiceImpl mockNodeApiService;
//
//    ObjectKeeper getWalletIdKeeper;
//    String account = "5B3YXqDTcWQFGAqEJQJP3Bg1ZK8FFtHtgCiFLT5VAxpe";
//    static AtomicInteger walletId = new AtomicInteger(0);
//    static AtomicInteger smartContractTransactionId = new AtomicInteger(0);
//    static AtomicInteger transactionId = new AtomicInteger(0);
//    String startForm;
//
//    @Before
//    public void setUp() {
//        MockitoAnnotations.initMocks(this);
//        mockNodeApiService.nodeClient = NodeThriftApiClient.getInstance("127.0.0.1", 9090);
//    }
//
//    @Ignore
//    @Test
//    public void serializeNodeApiQueries() throws Exception {
//
//        doAnswer(InvocationOnMock::callRealMethod).when(mockNodeApiService).getBalance(any());
//        doAnswer(InvocationOnMock::callRealMethod).when(mockNodeApiService).getBalance(any());
//        doAnswer(InvocationOnMock::callRealMethod).when(mockNodeApiService).getSmartContract(any());
//        doAnswer(InvocationOnMock::callRealMethod).when(mockNodeApiService).getSmartContractAddressList(any());
//        doAnswer(InvocationOnMock::callRealMethod).when(mockNodeApiService).getTransaction(any());
//        doAnswer(InvocationOnMock::callRealMethod).when(mockNodeApiService).getSmartContractList(any());
//        doAnswer(InvocationOnMock::callRealMethod).when(mockNodeApiService).getTransactionsState(any(),any());
//        doAnswer(InvocationOnMock::callRealMethod).when(mockNodeApiService).getWalletId(any());
//        doAnswer(InvocationOnMock::callRealMethod).when(mockNodeApiService).getWalletTransactionsCount(any());
//        doAnswer(InvocationOnMock::callRealMethod).when(mockNodeApiService).getWalletId(any());
//
//        doAnswer(invocation -> beforeInvokeLogic(invocation,"transaction", transactionId)).when(mockNodeApiService).transactionFlow(any());
//        doAnswer(invocation -> beforeInvokeLogic(invocation,"smartTransaction", smartContractTransactionId)).when(mockNodeApiService).smartContractTransactionFlow(any());
//
//        NodeApiService mock = mockNodeApiService;
//
//        walletApp = new WalletApp();
//        startForm = VistaNavigator.WELCOME;
//        runApp();
//    }
//
//    private Object beforeInvokeLogic(InvocationOnMock invocation, String objectName, AtomicInteger idKeeper) throws Throwable {
//        System.out.println("save object" + objectName);
//        System.out.println(invocation.getArguments());
//        String path = ".." + File.separator + ".." + File.separator + ".." + File.separator + "cache" + File.separator + "test" + File.separator + account + File.separator;
//        getWalletIdKeeper = new ObjectKeeper<>(account, path + objectName + idKeeper.getAndIncrement() + ".ser");
//        getWalletIdKeeper.keepObject(invocation.getArguments());
//        getWalletIdKeeper.flush();
//        return invocation.callRealMethod();
//    }
//
//    private void runApp() throws InterruptedException {
////        new JFXPanel();
//        Platform.runLater(() -> {
//            try {
//                walletApp.start(new Stage());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        });
//        Thread.currentThread().join();
//    }

}
