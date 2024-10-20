package com.neu.halp.clock;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

public class PuppetClockServiceImpl implements PuppetClockService {
    private List<ClockListener> listeners = new ArrayList<>();
    private DayOfWeek currentDay;
    private LocalTime currentTime;

    public PuppetClockServiceImpl(DayOfWeek currentDay, LocalTime currentTime) {
        this.currentDay = currentDay;
        this.currentTime = currentTime;
    }

    @Override
    synchronized public DayOfWeek listen(ClockListener listener) {
        listeners.add(listener);
        return currentDay;
    }

    @Override
    synchronized public void stopListening(ClockListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void start() {
        notifyListeners();
    }

    @Override
    public void stop() {
    }

    @Override
    public void nextStep() {
        if (currentTime.equals(LocalTime.MAX)) {
            currentTime = LocalTime.MIN;
            currentDay = currentDay.plus(1);
        } else if (currentTime.equals(LocalTime.MIN)) {
            if (currentDay.equals(DayOfWeek.SATURDAY) || currentDay.equals(DayOfWeek.SUNDAY)) {
                currentTime = LocalTime.of(9, 0);
            } else {
                currentTime = LocalTime.of(8, 0);
            }
        } else if ((currentDay.equals(DayOfWeek.SATURDAY) || currentDay.equals(DayOfWeek.SUNDAY))
                    && currentTime.equals(LocalTime.of(14, 0))) {
            currentTime = LocalTime.MAX;
        } else if (currentTime.equals(LocalTime.of(18, 0))) {
            currentTime = LocalTime.MAX;
        } else {
            currentTime = currentTime.plusMinutes(30);
        }
        notifyListeners();
    }

    private void notifyListeners() {
        Function<ClockListener, Callable<Void>> task;
        if (currentTime.equals(LocalTime.MIN)) {
            task = (ClockListener listener) -> () -> {
                listener.startDay(currentDay);
                return null;
            };
        } else if (currentTime.equals(LocalTime.MAX)) {
            task = (ClockListener listener) -> () -> {
                listener.finishDay(currentDay);
                return null;
            };
        } else {
            task = (ClockListener listener) -> () -> {
                listener.itIsNow(currentTime);
                return null;
            };
        }

        ExecutorService executor = Executors.newFixedThreadPool(4);
        for (ClockListener listener : listeners) {
            executor.submit(task.apply(listener));
        }
    }

    
    
}

