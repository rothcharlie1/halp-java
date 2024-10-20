package com.neu.halp.client;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.neu.halp.clock.ClockListener;
import com.neu.halp.data.VetSpec;

public class VetWorker implements Runnable, ClockListener {
    private VetSpec spec;
    private List<Appointment> appointments;
    private Map<Appointment, ClientNotificationReceiver> receivers = new HashMap<>();
    private DayOfWeek currentDay;
    private LocalTime currentTime;
    private Map<Appointment, AppointmentResult> results;
    private ShelterEntry shelter;

    public VetWorker(VetSpec spec, List<Appointment> appointments, Map<Appointment, AppointmentResult> results, ShelterEntry shelter) {
        this.spec = spec;
        this.appointments = appointments;
        this.results = results;
        this.shelter = shelter;
    }

    public void giveReceiver(ClientNotificationReceiver receiver, Appointment slot) {
        if (!appointments.contains(slot)) {
            throw new IllegalArgumentException("Appointment slot not in schedule");
        }
        receivers.put(slot, receiver);
    }

    @Override
    public void run() {
        while (true) {
            var next = nextAppointment();
            if (next.isPresent()) {
                var appt = next.get();
                if (receivers.containsKey(appt)) {
                    receivers.get(appt).concerns(); // you have been doctored
                    synchronized (results) {
                        results.put(appt, AppointmentResult.COMPLETED);
                    }
                }
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            reportClientNoShows();
            tryVaccinating();
        }   
    }

    @Override
    public void startDay(DayOfWeek day) {
        this.currentDay = day;
    }

    @Override
    public void itIsNow(LocalTime time) {
        this.currentTime = time;
    }

    @Override
    public void finishDay(DayOfWeek day) {}

    private Optional<Appointment> nextAppointment() {
        for (Appointment slot : appointments) {
            if (slot.getDay() == currentDay && (slot.getTime().isAfter(currentTime) || slot.getTime().equals(currentTime))) {
                return Optional.of(slot);
            }
        }
        return Optional.empty();
    }

    private void reportClientNoShows() {
        for (Appointment slot : appointments) {
            if (slot.getDay().equals(currentDay) && slot.getTime().isBefore(currentTime)) {
                if (!receivers.containsKey(slot)) {
                    synchronized (results) {
                        results.put(slot, AppointmentResult.CLIENT_NO_SHOW);
                    }
                }
            }
        }
    }

    private void tryVaccinating() {
        if (hasTwoFreeSlots()) {
            var viewer = shelter.viewAnimals();
            var pet = viewer.viewNext();
            while (pet.isPresent()) {
                if (!pet.get().getVaccinated() && viewer.tryPlay()) {
                    var animal = (AdoptableAnimal) pet.get();
                    animal.vaccinate();
                    return;
                }
                pet = viewer.viewNext();
            }
        }
    }

    private boolean hasTwoFreeSlots() {
        return currentSlotIsFree() && nextSlotIsFree();
    }

    private boolean currentSlotIsFree() {
        for (Appointment slot : appointments) {
            if (slot.getDay().equals(currentDay) && slot.getTime().equals(currentTime)) {
                return false;
            }
        }
        return true;
    }

    private boolean nextSlotIsFree() {
        for (Appointment slot : appointments) {
            if (slot.getDay().equals(currentDay) && slot.getTime().equals(currentTime.plusMinutes(30))) {
                return false;
            }
        }
        return true;
    }
    
}
