package com.neu.halp.client;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class DelayedExecutor {
    private final ExecutorService threadPool;

    public DelayedExecutor(ExecutorService threadPool) {
        this.threadPool = threadPool;
    }

    public Future<?> runAfter(long milliseconds, Runnable action) {
        return threadPool.submit(() -> {
            try {
                Thread.sleep(milliseconds);
            } catch (InterruptedException e) {
                throw new RuntimeException("DelayedExecutor was interrupted");
            }
            action.run();
        });
    }

    public void shutdown() {
        threadPool.shutdown();
    }
}