package com.neu.halp.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Before;
import org.junit.Test;

import com.neu.halp.client.AdoptableAnimal;
import com.neu.halp.client.AdoptablePet;
import com.neu.halp.client.AppointmentScheduler;
import com.neu.halp.client.ClinicEntry;
import com.neu.halp.client.ClinicEntryImpl;
import com.neu.halp.client.PetType;
import com.neu.halp.client.ShelterEntryImpl;
import com.neu.halp.data.Animal;
import com.neu.halp.data.Day;
import com.neu.halp.data.VetSpec;

public class BasicSchedulingClientImplTest {
    private URL testURL;

    @Before
    public void init() {
        try {
            testURL = new URL("https://google.com");
        } catch (MalformedURLException e) {
            System.out.println("Test url invalid");
        }
    }
    @Test
    public void testAccept() {
        String[] specialties = {"test"};
        Day[] schedule1 = {Day.SATURDAY, Day.FRIDAY};
        VetSpec vet1 = new VetSpec("1", specialties, schedule1);
        Animal cat3 = new Animal("Bingus", 3, PetType.Cat, true, testURL);
        AdoptablePet pet = new AdoptableAnimal(cat3);
        var shelter = new ShelterEntryImpl(new AdoptablePet[] {pet}, 1000);

        ClinicEntry clinic = new ClinicEntryImpl(new VetSpec[] {vet1}, shelter, 1000);
        // AppointmentScheduler sched = clinic.requestAppointment(pet);

        BasicSchedulingClient client = new BasicSchedulingClientImpl(
                pet,
                (vets) -> vets,
                (weeks) -> weeks,
                (days) -> days,
                (times) -> times);

        client.accept(clinic);
        assertTrue(client.getFinished());
        assertTrue(client.getResult().isPresent());
    }
}
