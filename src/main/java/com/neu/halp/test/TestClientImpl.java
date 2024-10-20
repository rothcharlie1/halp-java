package com.neu.halp.test;

import com.neu.halp.client.AdoptablePet;
import com.neu.halp.client.Appointment;
import com.neu.halp.client.GreedyAppointmentSchedulerReceiver;
import com.neu.halp.client.PetViewer;
import com.neu.halp.client.ShelterEntry;
import com.neu.halp.client.Vet;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;

public class TestClientImpl implements TestClient {
    private boolean finished;
    private Optional<AdoptablePet> result;
    private int patience;
    private GreedyAppointmentSchedulerReceiver receiver;

    public TestClientImpl(
            int patience, 
            Function<Collection<Vet>,Collection<Vet>> vetPreference,
            Function<Collection<Integer>,Collection<Integer>> weekPreference,
            Function<Collection<DayOfWeek>,Collection<DayOfWeek>> dayPreference,
            Function<Collection<LocalTime>,Collection<LocalTime>> timePreference) {
        this.finished = false;
        this.result = Optional.empty();
        this.patience = patience;
        this.receiver = new GreedyAppointmentSchedulerReceiver(vetPreference, weekPreference, dayPreference, timePreference);
    }

    @Override
    public boolean getFinished() {
        return this.finished;
    }

    @Override
    public Optional<AdoptablePet> getResult() {
        return this.result;
    }

    /**
     * Performs this operation on the given argument.
     *
     * @param shelterEntry the input argument
     */
    @Override
    public void accept(ShelterEntry shelterEntry) {
        PetViewer viewer = shelterEntry.viewAnimals(receiver);
        recurTryAdoption(viewer);
    }

    private void recurTryAdoption(PetViewer viewer) {
        if (patience == 0) {
            this.finished = true;
            return;
        }
        Optional<AdoptablePet> next = viewer.viewNext();
        this.patience--;
        if (next.isPresent()) {
            if (viewer.tryPlay()) {
                if (viewer.tryAdopt()) {
                    this.finished = true;
                    this.result = next;
                } else {
                    recurTryAdoption(viewer);
                }
            } else {
                recurTryAdoption(viewer);
            }
        } else {
            this.finished = true;
            return;
        }
    }

    @Override
    public Optional<Appointment> getAppointment() {
        return receiver.getAppointment();
    }
}
