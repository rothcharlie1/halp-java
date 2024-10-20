package com.neu.halp.client;

import com.neu.halp.data.Animal;
import com.neu.halp.data.Day;
import com.neu.halp.data.VetSpec;
import junit.framework.TestCase;
import org.junit.Before;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.*;

public class ClinicEntryImplTest extends TestCase {
    private URL testURL;

    @Before
    public void init() {
        try {
            testURL = new URL("https://google.com");
        } catch (MalformedURLException e) {
            System.out.println("Test url invalid");
        }
    }

    public void testInitSchedule() {
        String[] specialties = {"test"};
        Day[] schedule1 = {Day.SATURDAY, Day.FRIDAY};
        VetSpec vet1 = new VetSpec("1", specialties, schedule1);
        var shelter = new ShelterEntryImpl(new AdoptablePet[] {}, 1000);

        ClinicEntryImpl clinic = new ClinicEntryImpl(new VetSpec[] {vet1}, shelter, 1000);

        Map<Vet, List<AppointmentSlot>> schedule = clinic.initSchedule(new VetSpec[]{vet1});

        // should be 20 slots per week day, 10 per weekend day, four weeks so 120 total
        assertEquals(120, schedule.entrySet().iterator().next().getValue().size());
    }

    public void testScheduler() {
        String[] specialties = {"test"};
        Day[] schedule1 = {Day.SATURDAY, Day.FRIDAY};
        VetSpec vet1 = new VetSpec("1", specialties, schedule1);
        Animal cat3 = new Animal("Bingus", 3, PetType.Cat, true, testURL);
        AdoptablePet pet = new AdoptableAnimal(cat3);

        var shelter = new ShelterEntryImpl(new AdoptablePet[] {pet}, 1000);

        ClinicEntryImpl clinic = new ClinicEntryImpl(new VetSpec[] {vet1},shelter, 1000);
        AppointmentScheduler sched = clinic.requestAppointment(pet);

        assertEquals("1", sched.availableVets().iterator().next().getName());
        assertEquals(List.of(0, 1, 2, 3), sched.availableWeeks());
        var blah = sched.availableDays();
        assertTrue(blah.contains(DayOfWeek.FRIDAY));
        assertTrue(blah.contains(DayOfWeek.SATURDAY));

        sched.selectVet(new VetImpl(vet1));
        sched.selectWeek(0);
        sched.selectDay(DayOfWeek.FRIDAY);
        sched.selectTime(LocalTime.of(12, 0));

        assertEquals(
            sched.currentSelection(), 
            new TentativeAppointmentImpl(
                Optional.of(new VetImpl(vet1)), 
                Optional.of(0), 
                Optional.of(DayOfWeek.FRIDAY), 
                Optional.of(LocalTime.of(12, 0))));
    }
}