package com.neu.halp.client;


public interface MonitoredAppointmentScheduler extends AppointmentScheduler {

    /*
     * Allows the client to check if they have booked an appointment
     */
    public boolean hasBooked();
}
