package com.neu.halp.client;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

public class MonitoredAppointmentSchedulerImpl extends AppointmentSchedulerImpl
        implements MonitoredAppointmentScheduler {
    private boolean hasBooked = false;

    public MonitoredAppointmentSchedulerImpl(
            AdoptablePet pet,
            List<Vet> vets,
            Map<Vet, List<AppointmentSlot>> masterSchedule,
            Map<AdoptablePet, List<Appointment>> appointments,
            Map<Vet, List<Appointment>> vetSchedule,
            int clientIdleTimeoutMilliseconds,
            ExecutorService threadPool) {
        super(pet, vets, masterSchedule, appointments, vetSchedule, clientIdleTimeoutMilliseconds);
    }

    public MonitoredAppointmentSchedulerImpl(
            AdoptablePet pet,
            List<Vet> vets,
            Map<Vet, List<AppointmentSlot>> masterSchedule,
            Map<AdoptablePet, List<Appointment>> appointments,
            Map<Vet, List<Appointment>> vetSchedule,
            int clientIdleTimeoutMilliseconds,
            ClientNotificationReceiver clientNotificationReceiver,
            ExecutorService threadPool) {
        super(pet, vets, masterSchedule, appointments, vetSchedule, clientIdleTimeoutMilliseconds, clientNotificationReceiver, threadPool);
    }

    @Override
    public Appointment book() {
        hasBooked = true;
        return super.book();
    }

    @Override
    public boolean hasBooked() {
        return hasBooked;
    }

}
