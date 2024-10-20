package com.neu.halp.clock;

import java.time.DayOfWeek;

public interface ClockService {
  DayOfWeek listen(ClockListener listener);
  void stopListening(ClockListener listener);
  void start();
  void stop();
}
