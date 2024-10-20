package com.neu.halp.client;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;

public class GreedyAppointmentSchedulerReceiver implements ClientNotificationReceiver {
    private Function<Collection<Vet>,Collection<Vet>> vetPreference;
    private Function<Collection<Integer>,Collection<Integer>> weekPreference;
    private Function<Collection<DayOfWeek>,Collection<DayOfWeek>> dayPreference;
    private Function<Collection<LocalTime>,Collection<LocalTime>> timePreference;
    private Optional<Appointment> petAppointment = Optional.empty();


    public GreedyAppointmentSchedulerReceiver(
            Function<Collection<Vet>,Collection<Vet>> vetPreference,
            Function<Collection<Integer>,Collection<Integer>> weekPreference,
            Function<Collection<DayOfWeek>,Collection<DayOfWeek>> dayPreference,
            Function<Collection<LocalTime>,Collection<LocalTime>> timePreference) {
        this.vetPreference = vetPreference;
        this.weekPreference = weekPreference;
        this.dayPreference = dayPreference;
        this.timePreference = timePreference;
    }

    @Override
    public void playTimeout() {
    }

    @Override
    public void appointmentTimeout() {
    }

    @Override
    public void scheduleAnAppointment(AppointmentScheduler scheduler) {
        Collection<Vet> preferredVets = vetPreference.apply(scheduler.availableVets());
        if (preferredVets.isEmpty()) {
            scheduler.cancel();
            return;
        }
        scheduler.selectVet(preferredVets.iterator().next());

        Collection<Integer> preferredWeeks = weekPreference.apply(scheduler.availableWeeks());
        if (preferredWeeks.isEmpty()) {
            scheduler.cancel();
            return;
        }
        scheduler.selectWeek(preferredWeeks.iterator().next());

        Collection<DayOfWeek> preferredDays = dayPreference.apply(scheduler.availableDays());
        if (preferredDays.isEmpty()) {
            scheduler.cancel();
            return;
        }
        scheduler.selectDay(preferredDays.iterator().next());

        Collection<LocalTime> preferredTimes = timePreference.apply(scheduler.availableTimes());
        if (preferredTimes.isEmpty()) {
            scheduler.cancel();
            return;
        }
        scheduler.selectTime(preferredTimes.iterator().next());

        if (scheduler.finalizeDetails()) {
            System.out.println("Booking appointment");
            petAppointment = Optional.of(scheduler.book());
        } else {
            scheduler.cancel();
        }
    }

    public Optional<Appointment> getAppointment() {
        return petAppointment;
    }

    @Override
    public String concerns() {
        return "tummyache";
    }
    
}
