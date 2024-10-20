package com.neu.halp.client;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Optional;

public interface TentativeAppointment {
  Optional<Vet> getVet();
  Optional<Integer> getWeek();
  Optional<DayOfWeek> getDay();
  Optional<LocalTime> getTime();
}
