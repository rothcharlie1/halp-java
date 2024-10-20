package com.neu.halp.test;

import com.neu.halp.client.AdoptablePet;
import com.neu.halp.client.Vet;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Collection;
import java.util.function.Function;

public class BasicSchedulingClientFactory {

    public BasicSchedulingClientFactory() {
    }

    public BasicSchedulingClient newClient(
            AdoptablePet pet,
            Function<Collection<Vet>,Collection<Vet>> vetPreference,
            Function<Collection<Integer>,Collection<Integer>> weekPreference,
            Function<Collection<DayOfWeek>,Collection<DayOfWeek>> dayPreference,
            Function<Collection<LocalTime>,Collection<LocalTime>> timePreference) {
        return new BasicSchedulingClientImpl(pet, vetPreference, weekPreference, dayPreference, timePreference);
    }
}
