package com.neu.halp.client;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Collection;

public interface AppointmentScheduler {
  Collection<Vet> availableVets();
  void selectVet(Vet vet);

  Collection<Integer> availableWeeks();
  void selectWeek(int week);

  Collection<DayOfWeek> availableDays();
  void selectDay(DayOfWeek futureDate);

  Collection<LocalTime> availableTimes();
  void selectTime(LocalTime localTime);

  boolean finalizeDetails();
  Appointment book();

  void reset();
  void cancel();

  TentativeAppointment currentSelection();


}
