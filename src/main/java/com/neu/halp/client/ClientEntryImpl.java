package com.neu.halp.client;

import java.util.function.Consumer;

public class ClientEntryImpl implements ClientEntry {
    private final ShelterEntry shelterEntry;

    public ClientEntryImpl(ShelterEntry shelterEntry) {
        this.shelterEntry = shelterEntry;
    }

    @Override
    public void connectClient(Consumer<ShelterEntry> client) {
        Thread clientThread = new Thread(() -> {
            client.accept(shelterEntry);
        });
        clientThread.start();
    }
}
