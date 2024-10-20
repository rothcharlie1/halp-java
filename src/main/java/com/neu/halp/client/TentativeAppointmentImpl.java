package com.neu.halp.client;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Objects;
import java.util.Optional;

public class TentativeAppointmentImpl implements TentativeAppointment {
    private Optional<Vet> vet;
    private Optional<Integer> week;
    private Optional<DayOfWeek> day;
    private Optional<LocalTime> time;

    public TentativeAppointmentImpl(Optional<Vet> vet, Optional<Integer> week, Optional<DayOfWeek> day,
            Optional<LocalTime> time) {
        this.vet = vet;
        this.week = week;
        this.day = day;
        this.time = time;
    }

    @Override
    public Optional<Vet> getVet() {
        return this.vet;
    }

    @Override
    public Optional<Integer> getWeek() {
        return this.week;
    }

    @Override
    public Optional<DayOfWeek> getDay() {
        return this.day;
    }

    @Override
    public Optional<LocalTime> getTime() {
        return this.time;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        TentativeAppointmentImpl other = (TentativeAppointmentImpl) obj;
        return vet.equals(other.vet) &&
                week.equals(other.week) &&
                day.equals(other.day) &&
                time.equals(other.time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vet, week, day, time);
    }

}