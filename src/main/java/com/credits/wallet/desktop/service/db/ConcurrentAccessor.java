package com.credits.wallet.desktop.service.db;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;

class ConcurrentAccessor {
    final ReadWriteLock lock = new ReentrantReadWriteLock(true);

    <R> Supplier<R> write(Supplier<R> supplier) {
        return wrapLock(lock.writeLock(), supplier);
    }

    <R> Supplier<R> read(Supplier<R> supplier) {
        return wrapLock(lock.readLock(), supplier);
    }

    void write(Runnable runnable) {
        wrapLock(lock.writeLock(), runnable);
    }

    void read(Runnable runnable) {
        wrapLock(lock.readLock(), runnable);
    }

    <R> Supplier<R> wrapLock(Lock lock, Supplier<R> supplier) {
        return () -> {
            try {
                lock.lock();
                return supplier.get();
            } finally {
                lock.unlock();
            }
        };
    }

    private void wrapLock(Lock lock, Runnable runnable) {
        try {
            lock.lock();
            runnable.run();
        } finally {
            lock.unlock();
        }
    }
}