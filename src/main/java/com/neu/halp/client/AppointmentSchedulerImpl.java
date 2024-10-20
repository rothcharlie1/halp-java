package com.neu.halp.client;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class AppointmentSchedulerImpl implements AppointmentScheduler {
    private Optional<Vet> vet;
    private Optional<Integer> week;
    private Optional<DayOfWeek> day;
    private Optional<LocalTime> time;
    private AdoptablePet pet;
    private List<Vet> vets;
    private Map<Vet, List<AppointmentSlot>> masterSchedule;
    private Optional<AppointmentSlot> finalAppt;
    private final int clientIdleTimeoutMilliseconds;
    private Optional<ClientNotificationReceiver> clientNotificationReceiver;
    private DelayedExecutor scheduler;
    private Optional<Future<?>> future = Optional.empty();
    private Map<AdoptablePet, List<Appointment>> appointments;
    private Map<Vet, List<Appointment>> vetAppointments;

    public AppointmentSchedulerImpl(
            AdoptablePet pet,
            List<Vet> vets,
            Map<Vet, List<AppointmentSlot>> masterSchedule,
            Map<AdoptablePet, List<Appointment>> appointments,
            Map<Vet, List<Appointment>> vetAppointments,
            int clientIdleTimeoutMilliseconds) {
        this.pet = pet;
        this.vets = vets;
        this.vet = Optional.empty();
        this.week = Optional.empty();
        this.day = Optional.empty();
        this.time = Optional.empty();
        this.masterSchedule = masterSchedule;
        this.finalAppt = Optional.empty();
        this.clientIdleTimeoutMilliseconds = clientIdleTimeoutMilliseconds;
        this.clientNotificationReceiver = Optional.empty();
        this.scheduler = new DelayedExecutor(Executors.newCachedThreadPool());
        this.appointments = appointments;
        this.vetAppointments = vetAppointments;
    }

    public AppointmentSchedulerImpl(
            AdoptablePet pet,
            List<Vet> vets,
            Map<Vet, List<AppointmentSlot>> masterSchedule,
            Map<AdoptablePet, List<Appointment>> appointments,
            Map<Vet, List<Appointment>> vetAppointments,
            int clientIdleTimeoutMilliseconds,
            ClientNotificationReceiver clientNotificationReceiver,
            ExecutorService threadPool) {
        this(pet, vets, masterSchedule, appointments, vetAppointments, clientIdleTimeoutMilliseconds);
        this.clientNotificationReceiver = Optional.of(clientNotificationReceiver);
        this.scheduler = new DelayedExecutor(threadPool);
    }

    @Override
    public Collection<Vet> availableVets() {
        if (this.vet.isPresent()) {
            return Arrays.asList(this.vet.get());
        } else {
            return this.vets;
        }
    }

    @Override
    public void selectVet(Vet vet) {
        this.vet = Optional.of(vet);
    }

    @Override
    public Collection<Integer> availableWeeks() {
        if (this.week.isPresent()) {
            for (Vet vet : this.availableVets()) {
                for (AppointmentSlot slot : masterSchedule.get(vet)) {
                    if (slot.week == this.week.get() && isCompatibleSlot(slot)) {
                        return Arrays.asList(this.week.get());
                    }
                }
            }
            return new ArrayList<>();
        } else {
            Set<Integer> weeks = new HashSet<>();
            for (Vet vet : this.availableVets()) {
                for (AppointmentSlot slot : masterSchedule.get(vet)) {
                    if (isCompatibleSlot(slot))
                        weeks.add(slot.week);
                }
            }
            return new ArrayList<>(weeks);
        }
    }

    private boolean isCompatibleSlot(AppointmentSlot slot) {
        if (this.week.isPresent() && this.week.get() != slot.week) {
            return false;
        }
        if (this.day.isPresent() && this.day.get() != slot.day) {
            return false;
        }
        if (this.time.isPresent() && this.time.get() != slot.time) {
            return false;
        }
        return true;
    }

    @Override
    public void selectWeek(int week) {
        this.week = Optional.of(week);
    }

    @Override
    public Collection<DayOfWeek> availableDays() {
        if (this.day.isPresent()) {
            for (Vet vet : this.availableVets()) {
                for (AppointmentSlot slot : masterSchedule.get(vet)) {
                    if (slot.day == this.day.get() && isCompatibleSlot(slot)) {
                        return Arrays.asList(this.day.get());
                    }
                }
            }
            return new ArrayList<>();
        } else {
            Set<DayOfWeek> days = new HashSet<DayOfWeek>();
            for (Vet vet : this.availableVets()) {
                for (AppointmentSlot slot : masterSchedule.get(vet)) {
                    if (isCompatibleSlot(slot))
                        days.add(slot.day);
                }
            }
            return new ArrayList<>(days);
        }
    }

    @Override
    public void selectDay(DayOfWeek futureDate) {
        this.day = Optional.of(futureDate);
    }

    @Override
    public Collection<LocalTime> availableTimes() {
        if (this.time.isPresent()) {
            for (Vet vet : this.availableVets()) {
                for (AppointmentSlot slot : masterSchedule.get(vet)) {
                    if (slot.time == this.time.get() && isCompatibleSlot(slot)) {
                        return Arrays.asList(this.time.get());
                    }
                }
            }
            return new ArrayList<>();
        } else {
            Set<LocalTime> times = new HashSet<LocalTime>();
            for (Vet vet : this.availableVets()) {
                for (AppointmentSlot slot : masterSchedule.get(vet)) {
                    if (isCompatibleSlot(slot))
                        times.add(slot.time);
                }
            }
            return new ArrayList<>(times);
        }
    }

    @Override
    public void selectTime(LocalTime localTime) {
        this.time = Optional.of(localTime);
    }

    @Override
    public boolean finalizeDetails() {
        if (vet.isPresent() && week.isPresent() && day.isPresent() && time.isPresent()) {
            for (AppointmentSlot slot : masterSchedule.get(vet.get())) {
                if (slot.equals(new AppointmentSlot(week.get(), day.get(), time.get()))) {
                    if (slot.lock.tryLock()) {
                        this.future = Optional.of(scheduler.runAfter(clientIdleTimeoutMilliseconds, () -> {
                            slot.lock.unlock();
                            if (clientNotificationReceiver.isPresent()) {
                                clientNotificationReceiver.get().appointmentTimeout();
                            }
                        }));
                        this.finalAppt = Optional.of(slot);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Books assuming finalizeDetails has been called.
     * 
     * @return The booked Appointment
     */
    @Override
    public Appointment book() {
        this.scheduler.shutdown();
        if (this.finalAppt.isPresent() && this.finalAppt.get().lock.isHeldByCurrentThread()) {
            this.masterSchedule.get(this.vet.get()).remove(this.finalAppt.get());
            var currentAppointment = this.finalAppt.get().toAppointment(this.pet, this.vet.get());
            synchronized (this.appointments) {
                if (!this.appointments.containsKey(this.pet)) {
                    this.appointments.put(this.pet, new ArrayList<Appointment>());
                }
                this.appointments.get(this.pet).add(this.finalAppt.get().toAppointment(this.pet, this.vet.get()));
            }
            synchronized (this.vetAppointments) {
                if (!this.vetAppointments.containsKey(this.vet.get())) {
                    this.vetAppointments.put(this.vet.get(), new ArrayList<Appointment>());
                }
                this.vetAppointments.get(this.vet.get()).add(this.finalAppt.get().toAppointment(this.pet, this.vet.get()));
            }
            this.finalAppt.get().lock.unlock();
            return currentAppointment;
        } else {
            throw new RuntimeException("Protocol violated by booking unreserved appointment.");
        }
    }

    @Override
    public void reset() {
        if (this.future.isPresent()) {
            this.future.get().cancel(true);
            this.future = Optional.empty();
        }
        this.vet = Optional.empty();
        this.week = Optional.empty();
        this.day = Optional.empty();
        this.time = Optional.empty();
    }

    @Override
    public void cancel() {
        this.scheduler.shutdown();
        if (this.finalAppt.isPresent() && this.finalAppt.get().lock.isHeldByCurrentThread()) {
            this.finalAppt.get().lock.unlock();
        }
    }

    @Override
    public TentativeAppointment currentSelection() {
        return new TentativeAppointmentImpl(this.vet, this.week, this.day, this.time);
    }

}
