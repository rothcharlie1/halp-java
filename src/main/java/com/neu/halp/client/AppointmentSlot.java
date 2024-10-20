package com.neu.halp.client;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.concurrent.locks.ReentrantLock;

public class AppointmentSlot {
    public int week;
    public DayOfWeek day;
    public LocalTime time;
    public final ReentrantLock lock;

    public AppointmentSlot(int week, DayOfWeek day, LocalTime time) {
        this.week = week;
        this.day = day;
        this.time = time;
        this.lock = new ReentrantLock();
    }

    public Appointment toAppointment(AdoptablePet pet, Vet vet) {
        return new AppointmentImpl(vet, week, day, time, pet);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        AppointmentSlot other = (AppointmentSlot) obj;
        return week == other.week && day == other.day && time.equals(other.time);
    }

    
}

