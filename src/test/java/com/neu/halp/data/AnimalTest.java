package com.neu.halp.data;

import com.neu.halp.client.PetType;
import junit.framework.TestCase;
import org.junit.Before;

import java.net.MalformedURLException;
import java.net.URL;

public class AnimalTest extends TestCase {
    private URL testURL;

    @Before
    public void init() {
        try {
            testURL = new URL("https://google.com");
        } catch (MalformedURLException e) {
            System.out.println("Test url invalid");
        }
    }

    public void testIsPetTypePredicate() {
        Animal testDog = new Animal("Rover", 3, PetType.Dog, true, testURL);
        assertEquals(Animal.isPetTypePredicate(PetType.Dog).test(testDog), true);
        assertEquals(Animal.isPetTypePredicate(PetType.Cat).test(testDog), false);
    }
}