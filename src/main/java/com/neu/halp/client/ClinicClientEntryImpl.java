package com.neu.halp.client;

import java.util.function.Consumer;

public  class ClinicClientEntryImpl implements ClinicClientEntry {

    private final ClinicEntry clinicEntry;

    public ClinicClientEntryImpl(ClinicEntry clinicEntry) {
        this.clinicEntry = clinicEntry;
    }

    @Override
    public void connectClient(Consumer<ClinicEntry> client) {
        Thread clientThread = new Thread(() -> {
            client.accept(clinicEntry);
        });
        clientThread.start();
    }
    
}
