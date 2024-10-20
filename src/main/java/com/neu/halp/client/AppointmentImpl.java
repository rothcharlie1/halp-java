package com.neu.halp.client;

import java.time.DayOfWeek;
import java.time.LocalTime;

public class AppointmentImpl implements Appointment {
    private final Vet vet;
    private final int week;
    private final DayOfWeek day;
    private final LocalTime time;
    private final AdoptablePet patient;

    public AppointmentImpl(Vet vet, int week, DayOfWeek day, LocalTime time, AdoptablePet patient) {
        this.vet = vet;
        this.week = week;
        this.day = day;
        this.time = time;
        this.patient = patient;
    }

    @Override
    public Vet getVet() {
        return this.vet;
    }

    @Override
    public int getWeek() {
        return this.week;
    }

    @Override
    public DayOfWeek getDay() {
        return this.day;
    }

    @Override
    public LocalTime getTime() {
        return this.time;
    }

    @Override
    public AdoptablePet getPatient() {
        return this.patient;
    }

}
