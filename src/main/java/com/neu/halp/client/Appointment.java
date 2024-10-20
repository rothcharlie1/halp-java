package com.neu.halp.client;

import java.time.DayOfWeek;
import java.time.LocalTime;

public interface Appointment {
  Vet getVet();
  int getWeek();
  DayOfWeek getDay();
  LocalTime getTime();
  AdoptablePet getPatient();
}
