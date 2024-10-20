package com.neu.halp.test;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Collection;
import java.util.function.Function;

import com.neu.halp.client.Vet;

/**
 * A factory for greedily-adopting clients.
 */
public class EagerClientFactory {

    public EagerClientFactory() {}

    /**
     * Provides a TestClient with the provided patience.
     * @param patience The numbers of pets to be viewed before giving up.
     * @return A preferred implementation of TestClient
     */
    public TestClient newClient(int patience) {
        return new TestClientImpl(
            patience, 
            vets -> vets, 
            weeks -> weeks, 
            days -> days, 
            times -> times);
    }

    public TestClient newClient(
            int patience, 
            Function<Collection<Vet>,Collection<Vet>> vetPreference,
            Function<Collection<Integer>,Collection<Integer>> weekPreference,
            Function<Collection<DayOfWeek>,Collection<DayOfWeek>> dayPreference,
            Function<Collection<LocalTime>,Collection<LocalTime>> timePreference
        ) {
        return new TestClientImpl(patience, vetPreference, weekPreference, dayPreference, timePreference);
    }
}
