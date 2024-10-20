package com.neu.halp.client;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ExecutorService;

import com.neu.halp.clock.ClockListener;
import com.neu.halp.clock.ClockService;
import com.neu.halp.data.Day;
import com.neu.halp.data.VetSpec;

public class ClinicEntryImpl implements ClinicEntry, ClockListener {
    private Map<Vet, List<AppointmentSlot>> masterSchedule;
    private Map<AdoptablePet, List<Appointment>> appointments = new HashMap<>();
    private Map<Vet, List<Appointment>> vetSchedule = new HashMap<>();
    private Map<Appointment, AppointmentResult> results = new HashMap<>();
    private List<Vet> vets;
    private final int clientIdleTimeoutMilliseconds;
    private ExecutorService threadPool;
    private DayOfWeek currentDay;
    private LocalTime currentTime;
    private Map<Vet, VetWorker> vetWorkers = new HashMap<>();

    public ClinicEntryImpl(VetSpec[] vetSpecs, ShelterEntry shelterEntry, int clientIdleTimeoutMilliseconds) {
        masterSchedule = initSchedule(vetSpecs);
        vets = masterSchedule.keySet().stream().toList();
        this.clientIdleTimeoutMilliseconds = clientIdleTimeoutMilliseconds;
    }

    public ClinicEntryImpl(VetSpec[] vetSpecs, ShelterEntry shelterEntry, int clientIdleTimeoutMilliseconds, ClockService clockService, ExecutorService threadPool) {
        this(vetSpecs, shelterEntry, clientIdleTimeoutMilliseconds);
        clockService.listen(this);
        for (VetWorker worker : vetWorkers.values()) {
            clockService.listen(worker);
        }
        this.threadPool = threadPool;
        for (VetSpec spec : vetSpecs) {
            var appointments = new ArrayList<Appointment>();
            var worker = new VetWorker(spec, appointments, results, shelterEntry);
            var vet = new VetImpl(spec);
            vetSchedule.put(vet, appointments);
            threadPool.submit(worker);
            vetWorkers.put(vet, worker);
        }
    }

    public ClinicEntryImpl(VetSpec[] vetSpecs, ShelterEntry shelterEntry, int clientIdleTimeoutMilliseconds, ExecutorService threadPool) {
        this(vetSpecs, shelterEntry, clientIdleTimeoutMilliseconds);
        this.threadPool = threadPool;
        for (VetSpec spec : vetSpecs) {
            var appointments = new ArrayList<Appointment>();
            var worker = new VetWorker(spec, appointments, results, shelterEntry);
            var vet = new VetImpl(spec);
            vetSchedule.put(vet, appointments);
            threadPool.submit(worker);
            vetWorkers.put(vet, worker);
        }
    }

    protected Map<Vet, List<AppointmentSlot>> initSchedule(VetSpec[] vetSpecs) {
        Map<Vet, List<AppointmentSlot>> map = new HashMap<>();
        for (VetSpec spec : vetSpecs) {
            List<AppointmentSlot> slots = new ArrayList<>();
            for (int week = 0; week <= 3; week++) {
                for (Day day : spec.schedule()) {
                    if (day == Day.SATURDAY || day == Day.SUNDAY) {
                        for (LocalTime time : generateTimes(LocalTime.of(9, 0), LocalTime.of(14, 0))) {
                            slots.add(new AppointmentSlot(week, DayOfWeek.of(day.ordinal() + 1), time));
                        }
                    } else {
                        for (LocalTime time : generateTimes(LocalTime.of(8, 0), LocalTime.of(18, 0))) {
                            slots.add(new AppointmentSlot(week, DayOfWeek.of(day.ordinal() + 1), time));
                        }
                    }
                }
            }
            map.put(new VetImpl(spec), slots);
        }
        return map;
    }

    private List<LocalTime> generateTimes(LocalTime start, LocalTime end) {
        List<LocalTime> times = new ArrayList<>();
        LocalTime time = start;
        while (time.isBefore(end)) {
            times.add(time);
            time = time.plusMinutes(30);
        }
        return times;
    }

    @Override
    public AppointmentScheduler requestAppointment(AdoptablePet pet) {
        return new AppointmentSchedulerImpl(pet, vets, masterSchedule, appointments, vetSchedule, clientIdleTimeoutMilliseconds);
    }

    @Override
    public AppointmentScheduler requestAppointment(
            AdoptablePet pet,
            ClientNotificationReceiver clientNotificationReceiver) {
        return new AppointmentSchedulerImpl(pet, vets, masterSchedule, appointments, vetSchedule, clientIdleTimeoutMilliseconds,
                clientNotificationReceiver, threadPool);
    }

    public MonitoredAppointmentScheduler requestMonitoredAppointment(AdoptablePet pet) {
        return new MonitoredAppointmentSchedulerImpl(pet, vets, masterSchedule, appointments, vetSchedule, clientIdleTimeoutMilliseconds,
                threadPool);
    }

    public MonitoredAppointmentScheduler requestMonitoredAppointment(
            AdoptablePet pet,
            ClientNotificationReceiver clientNotificationReceiver) {
        return new MonitoredAppointmentSchedulerImpl(pet, vets, masterSchedule, appointments, vetSchedule, clientIdleTimeoutMilliseconds,
                clientNotificationReceiver, threadPool);
    }

    @Override
    public boolean clientCheckIn(AdoptablePet patient, ClientNotificationReceiver clientNotificationReceiver)
            throws InterruptedException {
        // does this mf have an appointment?
        if (appointments.containsKey(patient)) {
            for (Appointment appointment : appointments.get(patient)) {
                if (appointment.getDay() == currentDay 
                    && (appointment.getTime().isAfter(currentTime) || appointment.getTime().equals(currentTime))) {
                    var worker = vetWorkers.get(appointment.getVet());
                    worker.giveReceiver(
                        clientNotificationReceiver, 
                        appointment
                    );
                    while (!results.containsKey(appointment) && !currentTime.isAfter(appointment.getTime())) {
                        Thread.sleep(50);
                    }
                    if (!results.containsKey(appointment)) {
                        synchronized (results) {
                            results.put(appointment, AppointmentResult.VET_NO_SHOW);
                        }
                        return false;
                    } else {
                        return true;
                    }

                }
            }
        }
        return false;
    }

    @Override
    public Map<Appointment, AppointmentResult> appointmentReport() {
       return results;
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

}
