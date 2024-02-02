package fi.tuni.prog3.weatherapp;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LocationTest {

    /**
     * Test Location constructor
     */
    @Test
    public void testValidConstructor() {
        Location testLocation = new Location("Test", 1.0, 2.0, "AB");

        assertEquals("Test", testLocation.getName());
        assertEquals(1, testLocation.getX());
        assertEquals(2, testLocation.getY());
    }

    /**
     * Test setName() method
     */
    @Test
    public void testSetName() {
        Location testLocation = new Location("Test", 0.0, 0.0, "AB");

        testLocation.setName("Test123");

        assertEquals("Test123", testLocation.getName());
    }

    /**
     * Test setCoordinates() method
     */
    @Test
    public void testSetCoordinates() {
        Location testLocation = new Location("Test", 0.0, 0.0, "AB");

        testLocation.setCoordinates(2, 9);

        assertEquals(2, testLocation.getX());
        assertEquals(9, testLocation.getY());
    }

    /**
     * Test getName() method
     */
    @Test
    public void testGetName() {
        Location testLocation = new Location("Test", 0.0, 0.0, "AB");

        assertEquals("Test", testLocation.getName());
    }

    /**
     * Test getCountryCode() method
     */
    @Test
    public void testGetCountryCode() {
        Location testLocation = new Location("Test", 0.0, 0.0, "AB");

        assertEquals("AB", testLocation.getCountryCode());
    }

    /**
     * Test getX() method
     */
    @Test
    public void testGetX() {
        Location testLocation = new Location("Test", 9.0, 0.0, "AB");

        assertEquals(9.0, testLocation.getX());
    }

    /**
     * Test getY() method
     */
    @Test
    public void testGetY() {
        Location testLocation = new Location("Test", 0.0, 6.0, "AB");

        assertEquals(6.0, testLocation.getY());
    }

    /**
     * Test distanceTo() method
     */
    @Test
    public void testDistanceTo() {
        Location testLocation1 = new Location("Location_1", 2.0, 7.0, "AB");
        Location testLocation2 = new Location("Location_2", 5.0, 3.0, "CD");

        double distance = testLocation1.distanceTo(testLocation2);

        assertEquals(5.0, distance, 0.0001);
    }

    /**
     * Test toString() method
     */
    @Test
    public void testToString() {
        Location testLocation = new Location("Test", 2.000000, 7.000000, "AB");

        String expected = String.format("Location Test, (%f, %f), Country Code: AB", 2.000000, 7.000000);

        assertEquals(expected, testLocation.toString());
    }

}
