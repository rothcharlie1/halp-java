package com.neu.halp.clock;

import java.time.DayOfWeek;
import java.time.LocalTime;

public interface ClockListener {
  void startDay(DayOfWeek day);
  void itIsNow(LocalTime time);
  void finishDay(DayOfWeek day);
}
