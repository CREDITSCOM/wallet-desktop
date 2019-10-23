package com.credits.wallet.desktop.service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static java.util.concurrent.TimeUnit.SECONDS;

public class UpdateDatabaseService {
    public static final int databaseUpdateInterval = 5;
    private final ScheduledExecutorService updateDbService;
    private final DatabaseService database;
    private String updateAddress;
    private ScheduledFuture<?> currentUpdateTask;
    private ReadWriteLock readWriteLock;

    public UpdateDatabaseService(DatabaseService database) {
        this.database = database;
        updateDbService = Executors.newSingleThreadScheduledExecutor();
        readWriteLock = new ReentrantReadWriteLock();
    }


    public void startUpdateDatabaseDaemon(String address) {
        updateAddress = address;
        currentUpdateTask = updateDbService.scheduleWithFixedDelay(this::updateTask, 0, databaseUpdateInterval, SECONDS);
    }

    private void updateTask() {
        final var address= getUpdateAddress();
        if (address != null) {
            database.updateTransactionsOnAddress(address);
        }
    }

    public String getUpdateAddress() {
        final var lock = readWriteLock.readLock();
        try {
            lock.lock();
            return updateAddress;
        }finally {
            lock.unlock();
        }
    }

    public synchronized void changeUpdateAddress(String address){
        final var lock = readWriteLock.writeLock();
        try {
            lock.lock();
            updateAddress = address;
        }finally {
            lock.unlock();
        }
    }

    public boolean daemonIsStarted() {
        return currentUpdateTask != null && !currentUpdateTask.isDone();
    }

    public void stopUpdateDatabaseDaemon() {
        updateDbService.shutdown();
    }

}
