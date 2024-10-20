package com.neu.halp.clock;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class ClockServiceImpl implements ClockService {
    private PuppetClockService puppet;
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private long appointmentPeriodMillis;

    public ClockServiceImpl(DayOfWeek currentDay, LocalTime currentTime, long appointmentPeriodMillis) {
        this.appointmentPeriodMillis = appointmentPeriodMillis;
        this.puppet = new PuppetClockServiceImpl(currentDay, currentTime);
    }

    @Override
    public DayOfWeek listen(ClockListener listener) {
        return puppet.listen(listener);
    }

    @Override
    public void stopListening(ClockListener listener) {
        puppet.stopListening(listener);
    }

    @Override
    public void start() {
        scheduler.scheduleAtFixedRate(() -> puppet.nextStep(), 0, appointmentPeriodMillis, TimeUnit.MILLISECONDS);
    }

    @Override
    public void stop() {
        scheduler.shutdown();
    }   
}
