package com.credits.wallet.desktop.service.db;

import com.credits.client.node.exception.NodeClientException;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static com.credits.general.thrift.ThriftClientPool.ThriftClientException;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.apache.commons.lang3.exception.ExceptionUtils.getRootCauseMessage;

@Slf4j
public class UpdateDatabaseService {
    public static final int databaseUpdateInterval = 5;
    private final ScheduledExecutorService updateDbService;
    private final DatabaseService database;
    private String updateAddress;
    private ScheduledFuture<?> currentUpdateTask;
    private ReadWriteLock readWriteLock;

    public UpdateDatabaseService(DatabaseService database) {
        this.database = database;
        final var threadFactory = new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "db-update-thread");
            }
        };
        updateDbService = Executors.newSingleThreadScheduledExecutor(threadFactory);
        readWriteLock = new ReentrantReadWriteLock();
    }

    public void startUpdateDatabaseDaemon(String address) {
        updateAddress = address;
        currentUpdateTask = updateDbService.scheduleWithFixedDelay(this::updateTask, 0, databaseUpdateInterval, SECONDS);
    }

    private void updateTask() {
        final var address = getUpdateAddress();
        if (address != null) {
            try {
                database.syncUpdateAccountDatabase(address);
            } catch (NodeClientException | ThriftClientException e) {
                log.error("node unreachable. Reason {}", getRootCauseMessage(e));
            } catch (Exception e) {
                log.error("can't update database. Reason {}", getRootCauseMessage(e));
            }

        }
    }

    public String getUpdateAddress() {
        final var lock = readWriteLock.readLock();
        try {
            lock.lock();
            return updateAddress;
        } finally {
            lock.unlock();
        }
    }

    public synchronized void changeUpdateAddress(String address) {
        final var lock = readWriteLock.writeLock();
        try {
            lock.lock();
            updateAddress = address;
        } finally {
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
