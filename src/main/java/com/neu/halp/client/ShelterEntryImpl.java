package com.neu.halp.client;

import java.io.Reader;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

import com.google.gson.Gson;
import com.google.gson.JsonStreamParser;
import com.neu.halp.data.Animal;

public class ShelterEntryImpl implements ShelterEntry {
    private AdoptablePetState[] pets;
    private final int clientIdleTimeoutMilliseconds;
    private Optional<ClinicEntryImpl> clinicEntry = Optional.empty();
    private ExecutorService threadPool;

    public ShelterEntryImpl(AdoptablePet[] pets, int clientIdleTimeoutMilliseconds) {
        this.pets = new AdoptablePetState[pets.length];
        for (int i = 0; i < pets.length; i++) {
            this.pets[i] = new AdoptablePetState(pets[i]);
        }
        this.clientIdleTimeoutMilliseconds = clientIdleTimeoutMilliseconds;
        this.threadPool = Executors.newCachedThreadPool();
    }

    public ShelterEntryImpl(AdoptablePet[] pets, int clientIdleTimeoutMilliseconds, ExecutorService threadPool) {
        this(pets, clientIdleTimeoutMilliseconds);
        this.threadPool = threadPool;
    }

    public void registerClinic(ClinicEntryImpl clinicEntry) {
        this.clinicEntry = Optional.of(clinicEntry);
    }

    @Override
    public PetViewer viewAnimals() {
        if (clinicEntry.isPresent()) {
            return new PetViewerImpl(pets, clientIdleTimeoutMilliseconds, clinicEntry.get(), threadPool);
        }
        return new PetViewerImpl(pets, clientIdleTimeoutMilliseconds, threadPool);
    }

    @Override
    public PetViewer viewAnimals(ClientNotificationReceiver clientNotificationReceiver) {
        if (clinicEntry.isPresent()) {
            return new PetViewerImpl(pets, clientIdleTimeoutMilliseconds, clientNotificationReceiver,
                    clinicEntry.get(), threadPool);
        }
        return new PetViewerImpl(pets, clientIdleTimeoutMilliseconds, clientNotificationReceiver, threadPool);
    }

    @Override
    public void importAnimals(Reader reader) {
        final JsonStreamParser jsr = new JsonStreamParser(reader);

        if (jsr.hasNext()) {
            var json = jsr.next();
            Gson gson = new Gson();
            var newAnimals = gson.fromJson(json, Animal[].class);
            var newPets = Arrays.stream(newAnimals).map(AdoptableAnimal::new).map(AdoptablePetState::new);
            this.pets = Stream.concat(Arrays.stream(this.pets), newPets).toArray(AdoptablePetState[]::new);
        }
    }
}
