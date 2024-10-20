package com.neu.halp.client;

import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

public class CenterClientEntryImpl implements CenterClientEntry {
    private final CenterEntry centerEntry;
    private final ExecutorService threadPool;

    public CenterClientEntryImpl(CenterEntry centerEntry, ExecutorService threadPool) {
        this.centerEntry = centerEntry;
        this.threadPool = threadPool;
    }

    @Override
    public void connectClient(Consumer<CenterEntry> client) {
        threadPool.submit(() -> {
            client.accept(centerEntry);
        });
    }
    
}
