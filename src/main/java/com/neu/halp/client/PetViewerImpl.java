package com.neu.halp.client;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class PetViewerImpl implements PetViewer {
    private final AdoptablePetState[] pets;
    private int currentPetIndex;
    private Optional<ClientNotificationReceiver> clientNotificationReceiver;
    private final int clientIdleTimeoutMilliseconds;
    private final DelayedExecutor scheduler;
    private Optional<ClinicEntryImpl> clinicEntry = Optional.empty();
    private Optional<Future<?>> future = Optional.empty();

    public PetViewerImpl(AdoptablePetState[] pets, int clientIdleTimeoutMilliseconds, ExecutorService threadPool) {
        this.pets = pets;
        this.currentPetIndex = -1;
        this.clientNotificationReceiver = Optional.empty();
        this.clientIdleTimeoutMilliseconds = clientIdleTimeoutMilliseconds;
        this.scheduler = new DelayedExecutor(threadPool);
    }

    public PetViewerImpl(AdoptablePetState[] pets, int clientIdleTimeoutMilliseconds,
            ClientNotificationReceiver receiver, ExecutorService threadPool) {
        this(pets, clientIdleTimeoutMilliseconds, threadPool);
        this.clientNotificationReceiver = Optional.of(receiver);
    }

    public PetViewerImpl(AdoptablePetState[] pets, int clientIdleTimeoutMilliseconds, ClinicEntryImpl clinicEntry,
            ExecutorService threadPool) {
        this(pets, clientIdleTimeoutMilliseconds, threadPool);
        this.clinicEntry = Optional.of(clinicEntry);
    }

    public PetViewerImpl(AdoptablePetState[] pets, int clientIdleTimeoutMilliseconds,
            ClientNotificationReceiver receiver, ClinicEntryImpl clinicEntry, ExecutorService threadPool) {
        this(pets, clientIdleTimeoutMilliseconds, threadPool);
        this.clientNotificationReceiver = Optional.of(receiver);
        this.clinicEntry = Optional.of(clinicEntry);
    }

    @Override
    public Optional<AdoptablePet> viewNext() {
        // put the previous pet back in playable mode
        if (this.future.isPresent()) {
            this.future.get().cancel(true);
            this.future = Optional.empty();
        }
        if (currentPetIndex >= 0 && this.pets[currentPetIndex].lock.isHeldByCurrentThread()) {
            this.pets[currentPetIndex].lock.unlock();
        }
        for (int i = currentPetIndex + 1; i < this.pets.length; i++) {
            if (!pets[i].isAdopted && pets[i].lock.tryLock()) {
                try {
                    this.currentPetIndex = i;
                    return Optional.of(pets[i].pet);
                } finally {
                    pets[i].lock.unlock();
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean tryPlay() {
        if (this.pets[currentPetIndex].lock.tryLock()) {
            this.future = Optional.of(scheduler.runAfter(clientIdleTimeoutMilliseconds, () -> {
                this.pets[currentPetIndex].lock.unlock();
                if (clientNotificationReceiver.isPresent()) {
                    clientNotificationReceiver.get().playTimeout();
                }
            }));
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean tryAdopt() {
        if (this.future.isPresent()) {
            this.future.get().cancel(true);
            this.future = Optional.empty();
        }
        if (!pets[currentPetIndex].isAdopted && pets[currentPetIndex].lock.tryLock()) {
            try {
                if (!pets[currentPetIndex].pet.getVaccinated() && clinicEntry.isPresent()
                        && clientNotificationReceiver.isPresent()) {
                    MonitoredAppointmentScheduler scheduler;
                    scheduler = clinicEntry.get().requestMonitoredAppointment(pets[currentPetIndex].pet,
                            clientNotificationReceiver.get());
                    this.scheduler.runAfter(0, () -> clientNotificationReceiver.get().scheduleAnAppointment(scheduler));

                    long startTime = System.currentTimeMillis();
                    while (!scheduler.hasBooked()
                            && System.currentTimeMillis() - startTime < clientIdleTimeoutMilliseconds) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                    if (scheduler.hasBooked()) {
                        pets[currentPetIndex].isAdopted = true;
                        return true;
                    } else {
                        scheduler.cancel();
                    }
                } else if (pets[currentPetIndex].pet.getVaccinated()) {
                    pets[currentPetIndex].isAdopted = true;
                    return true;
                }
            } finally {
                pets[currentPetIndex].lock.unlock();
            }
        }
        return false;
    }

    @Override
    public void finished() {
        if (this.future.isPresent()) {
            this.future.get().cancel(true);
            this.future = Optional.empty();
        }
        this.scheduler.shutdown();
        try {
            this.pets[currentPetIndex].lock.unlock();
        } catch (IllegalMonitorStateException e) {
        }
    }

}
