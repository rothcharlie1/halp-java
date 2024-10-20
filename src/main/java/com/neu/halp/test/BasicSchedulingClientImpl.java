package com.neu.halp.test;

import com.neu.halp.client.AdoptablePet;
import com.neu.halp.client.Appointment;
import com.neu.halp.client.ClinicEntry;
import com.neu.halp.client.Vet;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.Optional;

public class BasicSchedulingClientImpl implements BasicSchedulingClient {
    private boolean finished = false;
    private Optional<Appointment> result = Optional.empty();
    private AdoptablePet pet;
    private Function<Collection<Vet>,Collection<Vet>> vetPreference;
    private Function<Collection<Integer>,Collection<Integer>> weekPreference;
    private Function<Collection<DayOfWeek>,Collection<DayOfWeek>> dayPreference;
    private Function<Collection<LocalTime>,Collection<LocalTime>> timePreference;

    public BasicSchedulingClientImpl(
            AdoptablePet pet,
            Function<Collection<Vet>,Collection<Vet>> vetPreference,
            Function<Collection<Integer>,Collection<Integer>> weekPreference,
            Function<Collection<DayOfWeek>,Collection<DayOfWeek>> dayPreference,
            Function<Collection<LocalTime>,Collection<LocalTime>> timePreference) {
        this.pet = pet;
        this.vetPreference = vetPreference;
        this.weekPreference = weekPreference;
        this.dayPreference = dayPreference;
        this.timePreference = timePreference;
    }

    @Override
    public boolean getFinished() {
        return finished;
    }

    @Override
    public Optional<Appointment> getResult() {
        return result;
    }

    /**
     * Performs this operation on the given argument.
     *
     * @param clinicEntry the input argument
     */
    @Override
    public void accept(ClinicEntry clinicEntry) {
        var scheduler = clinicEntry.requestAppointment(pet);

        Collection<Vet> preferredVets = vetPreference.apply(scheduler.availableVets());
        if (preferredVets.isEmpty()) {
            scheduler.cancel();
            finished = true;
            return;
        }
        scheduler.selectVet(preferredVets.iterator().next());

        Collection<Integer> preferredWeeks = weekPreference.apply(scheduler.availableWeeks());
        if (preferredWeeks.isEmpty()) {
            scheduler.cancel();
            finished = true;
            return;
        }
        scheduler.selectWeek(preferredWeeks.iterator().next());

        Collection<DayOfWeek> preferredDays = dayPreference.apply(scheduler.availableDays());
        if (preferredDays.isEmpty()) {
            scheduler.cancel();
            finished = true;
            return;
        }
        scheduler.selectDay(preferredDays.iterator().next());

        Collection<LocalTime> preferredTimes = timePreference.apply(scheduler.availableTimes());
        if (preferredTimes.isEmpty()) {
            scheduler.cancel();
            finished = true;
            return;
        }
        scheduler.selectTime(preferredTimes.iterator().next());

        if (scheduler.finalizeDetails()) {
            System.out.println("Booking appointment");
            result = Optional.of(scheduler.book());
        } else {
            scheduler.cancel();
        }
        finished = true;
    }
}
