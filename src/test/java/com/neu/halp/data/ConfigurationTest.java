package com.neu.halp.data;

import com.neu.halp.client.PetType;
import junit.framework.TestCase;
import org.junit.Before;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

public class ConfigurationTest extends TestCase {
    private URL testURL;

    @Before
    public void init() {
        try {
            testURL = new URL("https://google.com");
        } catch (MalformedURLException e) {
            System.out.println("Test url invalid");
        }
    }

    public void testOldestAnimal() {
        VetSpec[] testVets = {};
        Animal cat1 = new Animal("Cat1", 2, PetType.Cat, true, testURL);
        Animal cat2 = new Animal("Tuna", 3, PetType.Cat, true, testURL);
        Animal cat3 = new Animal("Bingus", 3, PetType.Cat, true, testURL);

        Animal[] testAnimals1 = {cat1};
        Animal[] testAnimals2 = {cat1, cat2, cat3};

        Configuration config1 = new Configuration(testAnimals1, testVets);
        Configuration config2 = new Configuration(testAnimals2, testVets);

        assertEquals(config1.oldestAnimal(PetType.Dog), Optional.empty());
        assertEquals(config1.oldestAnimal(PetType.Cat), Optional.of("Cat1"));
        assertEquals(config2.oldestAnimal(PetType.Cat), Optional.of("Bingus"));
    }

    public void testMostStaffedDay() {
        String[] specialties = {"test"};
        Day[] schedule1 = {Day.SATURDAY, Day.FRIDAY};
        Day[] schedule2 = {Day.SATURDAY, Day.FRIDAY, Day.WEDNESDAY};
        Day[] schedule3 = {Day.SATURDAY};
        VetSpec vet1 = new VetSpec("1", specialties, schedule1);
        VetSpec vet2 = new VetSpec("2", specialties, schedule2);
        VetSpec vet3 = new VetSpec("3", specialties, schedule3);

        Animal[] testAnimals = {new Animal("name", 3, PetType.Dog, true, testURL)};
        VetSpec[] vets1 = {vet1, vet2, vet3};
        VetSpec[] vets2 = {vet1, vet2};

        Configuration config1 = new Configuration(testAnimals, vets1);
        Configuration config2 = new Configuration(testAnimals, vets2);

        // test outright winner
        assertEquals(config1.mostStaffedDay(), Day.SATURDAY);
        // test a tie
        assertEquals(config2.mostStaffedDay(), Day.FRIDAY);
    }

    public void testComputeSeniors() {
        String[] specialties = {"test"};
        Day[] schedule1 = {Day.SATURDAY, Day.FRIDAY};
        Day[] schedule2 = {Day.SATURDAY, Day.FRIDAY, Day.WEDNESDAY};
        Day[] schedule3 = {Day.SATURDAY};
        VetSpec vet1 = new VetSpec("1", specialties, schedule1);
        VetSpec vet2 = new VetSpec("2", specialties, schedule2);
        VetSpec vet3 = new VetSpec("3", specialties, schedule3);
        VetSpec[] vets = {vet1, vet2, vet3};

        Animal cat1 = new Animal("Cat1", 2, PetType.Cat, true, testURL);
        Animal cat2 = new Animal("Cat2", 3, PetType.Cat, true, testURL);
        Animal cat3 = new Animal("Cat3", 3, PetType.Cat, true, testURL);

        Animal[] testAnimals = {cat1, cat2, cat3};

        Configuration config = new Configuration(testAnimals, vets);

        Seniors output = config.computeSeniors();


        assertEquals(output.availability(), Day.SATURDAY);
        assertNull(output.seniors()[0]);
        assertEquals(output.seniors()[1], "Cat2");
    }
}