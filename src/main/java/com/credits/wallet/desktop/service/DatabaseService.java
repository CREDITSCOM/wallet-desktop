package com.credits.wallet.desktop.service;

public interface DatabaseService {

    void keepLogin(String address);

    void updateTransactionsOnAddress(String address);
}
