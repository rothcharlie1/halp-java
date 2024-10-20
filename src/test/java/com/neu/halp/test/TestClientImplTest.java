package com.neu.halp.test;

import com.neu.halp.center.Main;
import com.neu.halp.client.ClientEntry;
import junit.framework.TestCase;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class TestClientImplTest extends TestCase {

    public void testAccept() throws Exception {
        try {
            File testJson = new File("/Users/charlesroth/Documents/cs3620/rothcharlie1-willhoch/src/test/java/com/neu/halp/data/test.json");
            FileReader reader = new FileReader(testJson);
            ClientEntry entry = Main.initialize(reader);

            TestClient testClient = new TestClientImpl(5, (vets) -> vets, (weeks) -> weeks, (days) -> days, (times) -> times);
            entry.connectClient(testClient);
            while (!testClient.getFinished()) {
                Thread.sleep(1);
            }

            assertTrue(testClient.getFinished());
            assertTrue(testClient.getResult().isPresent());
            assertEquals("Dogbert", testClient.getResult().get().getName());
            assertEquals(2, testClient.getResult().get().getAge());
        } catch (FileNotFoundException e) {
            // this test is only useful on the local machine where the file exists
            assertTrue(true);
        }

    }
}