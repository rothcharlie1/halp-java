package com.neu.halp.center;

import com.google.gson.Gson;
import com.google.gson.JsonStreamParser;
import com.neu.halp.client.*;
import com.neu.halp.clock.ClockService;
import com.neu.halp.data.Animal;
import com.neu.halp.data.Configuration;
import com.neu.halp.data.VetSpec;

import java.io.Reader;
import java.util.concurrent.ExecutorService;

public class Main {

    public static ClientEntry initialize(Reader configReader) {
        final JsonStreamParser jsr = new JsonStreamParser(configReader);

        if (jsr.hasNext()) {
            final Configuration config = Configuration.fromJsonElement(jsr.next());
            Animal[] inputAnimals = config.animals();
            AdoptablePet[] newAnimals = new AdoptablePet[inputAnimals.length];

            for (int i = 0; i < newAnimals.length; i++) {
                newAnimals[i] = new AdoptableAnimal(inputAnimals[i]);
            }
            ShelterEntry shelterEntry = new ShelterEntryImpl(newAnimals, 1000);
            return new ClientEntryImpl(shelterEntry);
        } else {
            throw new IllegalArgumentException("JSON not found on the Reader");
        }
    }

    // public static ClinicClientEntry initializeClinic(Reader configReader) {
    //     final JsonStreamParser jsr = new JsonStreamParser(configReader);

    //     if (jsr.hasNext()) {
    //         final Configuration config = Configuration.fromJsonElement(jsr.next());
    //         VetSpec[] inputVets = config.clinicians();
    //         ClinicEntry clinicEntry = new ClinicEntryImpl(inputVets, 1000);
    //         return new ClinicClientEntryImpl(clinicEntry);
    //     } else {
    //         throw new IllegalArgumentException("JSON not found on the Reader");
    //     }
    // }

    public static CenterClientEntry initializeCenter(
            Reader configReader,
            int clientIdleTimeoutMilliseconds,
            ExecutorService threadPool) {
        final JsonStreamParser jsr = new JsonStreamParser(configReader);

        if (jsr.hasNext()) {
            final Configuration config = Configuration.fromJsonElement(jsr.next());
            Animal[] inputAnimals = config.animals();
            AdoptablePet[] newAnimals = new AdoptablePet[inputAnimals.length];

            for (int i = 0; i < newAnimals.length; i++) {
                newAnimals[i] = new AdoptableAnimal(inputAnimals[i]);
            }
            ShelterEntry shelterEntry = new ShelterEntryImpl(newAnimals, clientIdleTimeoutMilliseconds, threadPool);
            VetSpec[] inputVets = config.clinicians();
            ClinicEntry clinicEntry = new ClinicEntryImpl(inputVets, shelterEntry, clientIdleTimeoutMilliseconds, threadPool);

            CenterEntry center = new CenterEntryImpl(newAnimals, inputVets, clientIdleTimeoutMilliseconds);

            return new CenterClientEntryImpl(center, threadPool);
        } else {
            throw new IllegalArgumentException("JSON not found on the Reader");
        }
    }

    public static CenterClientEntry initializeCenter(
        Reader configReader,
        int clientIdleTimeoutMilliseconds,
        ExecutorService threadPool,
        ClockService clockService) {

        final JsonStreamParser jsr = new JsonStreamParser(configReader);

        if (jsr.hasNext()) {
            final Configuration config = Configuration.fromJsonElement(jsr.next());
            Animal[] inputAnimals = config.animals();
            AdoptablePet[] newAnimals = new AdoptablePet[inputAnimals.length];

            for (int i = 0; i < newAnimals.length; i++) {
                newAnimals[i] = new AdoptableAnimal(inputAnimals[i]);
            }
            ShelterEntry shelterEntry = new ShelterEntryImpl(newAnimals, clientIdleTimeoutMilliseconds, threadPool);
            VetSpec[] inputVets = config.clinicians();
            ClinicEntryImpl clinicEntry = new ClinicEntryImpl(inputVets, shelterEntry, clientIdleTimeoutMilliseconds, clockService, threadPool);

            clockService.listen(clinicEntry);
            CenterEntry center = new CenterEntryImpl(newAnimals, inputVets, clientIdleTimeoutMilliseconds);

            return new CenterClientEntryImpl(center, threadPool);
        } else {
            throw new IllegalArgumentException("JSON not found on the Reader");
        }
    }
}
