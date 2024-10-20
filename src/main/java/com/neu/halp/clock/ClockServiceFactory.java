package com.neu.halp.clock;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalTime;

public class ClockServiceFactory {

    public static ClockService newTimedClockService(int millisecondsBetweenEvents) {
        return new ClockServiceImpl(DayOfWeek.MONDAY, LocalTime.MIN, millisecondsBetweenEvents);
    }
  
    public static PuppetClockService newPuppetClockService() {
        return new PuppetClockServiceImpl(DayOfWeek.MONDAY, LocalTime.MIN);
    }
    
}
