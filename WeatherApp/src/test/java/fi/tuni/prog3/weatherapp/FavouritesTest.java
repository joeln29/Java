package fi.tuni.prog3.weatherapp;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class FavouritesTest {

    public static Location getValidLocation() {
        return new Location("Test", 0, 0, null);
    }

    @Test
    public void testConstructor() {
        Favourites testFavourites = new Favourites();
        assertNull(testFavourites.getCurrentLocation());

        assertNotNull(testFavourites.getFavourites());
        assertTrue(testFavourites.getFavourites().isEmpty());
    }

    @Test
    public void testSetAndGetCurrentLocation() {
        Location testLocation = getValidLocation();
        Favourites testFavourites = new Favourites();
        testFavourites.setCurrentLocation(testLocation);
        assertEquals(testLocation, testFavourites.getCurrentLocation());
    }

    @Test
    public void testAddFavourite() {
        Location testLocation = getValidLocation();
        Favourites testFavourites = new Favourites();
        testFavourites.addFavourite(testLocation);
        assertTrue(testFavourites.getFavourites().contains(testLocation));
        // Check for duplicates
        testFavourites.addFavourite(testLocation);
        assertEquals(1, testFavourites.getFavourites().size());
    }

    @Test
    public void removeFavourite() {
        Location testLocation = getValidLocation();
        Favourites testFavourites = new Favourites();
        testFavourites.addFavourite(testLocation);
        testFavourites.removeFavourite(testLocation);
        assertFalse(testFavourites.getFavourites().contains(testLocation));
        assertEquals(0, testFavourites.getFavourites().size());
        // Try to remove non existing object
        Location testLocation2 = getValidLocation();
        testFavourites.removeFavourite(testLocation2);
        assertEquals(0, testFavourites.getFavourites().size());
    }

    @Test
    public void testGetFavourites() {
        Location testLocation = getValidLocation();
        Location testLocation2 = new Location("Test2", 1, 1, null);
        Favourites testFavourites = new Favourites();
        testFavourites.addFavourite(testLocation);
        testFavourites.addFavourite(testLocation2);

        assertTrue(testFavourites.getFavourites().contains(testLocation));
        assertTrue(testFavourites.getFavourites().contains(testLocation2));
        assertEquals(2, testFavourites.getFavourites().size());
        Location testLocation3 = new Location("Test2", 0, 0, null);
        assertFalse(testFavourites.getFavourites().contains(testLocation3));
    }

    public static void removeTestFile(String filename) {
        try {
            Files.deleteIfExists(Path.of(filename));
        } catch (IOException e) {
            System.err.println("Error removing the file!");
            e.printStackTrace();
        }
    }

    @Test
    public void testInvalidFileNameReadFromFile() {
        assertThrows(WeatherAppException.class, () -> {
            Favourites testFavourites = new Favourites();
            testFavourites.readFromFile("file_that_does_not_exist.txt");
        });
    }

    @Test
    public void testInvalidJsonFormatReadFromFile() throws IOException {
        String fileName = "wrongFormatRead.json";
        removeTestFile(fileName);

        // Create json with invalid format
        String invalidJson = "{\"name\":\"Invalid Location\", \"missing_field\": 42}";
        Files.write(Path.of(fileName), invalidJson.getBytes(), StandardOpenOption.CREATE);

        assertThrows(WeatherAppException.class, () -> {
            Favourites testFavourites = new Favourites();
            testFavourites.readFromFile(fileName);
        });

        removeTestFile(fileName);
    }

    @Test
    public void testOkRead() throws IOException {
        String fileName = "okRead.json";
        removeTestFile(fileName);
        String json = "{\"currentLocation\":{\"name\":\"Something\",\"x\":3,\"y\":2,\"id\":\"some ID\",\"countryCode\":\"NL\"},\"favourites\":[{\"name\":\"Helsinki\",\"x\":23.4,\"y\":23.1,\"id\":\"helsinki ID\",\"countryCode\":\"FI\"}]}";

        try (FileWriter fileWriter = new FileWriter(fileName)) {
            fileWriter.write(json);
        } catch (IOException e) {
            removeTestFile(fileName);
            throw new IOException();
        }
        Favourites testFavourites = new Favourites();

        // add some dummy data
        testFavourites.setCurrentLocation(new Location("Null Island", 0, 0, "NL"));
        testFavourites.addFavourite(new Location("Tampere", 23, 23, "FI"));

        assertDoesNotThrow(() -> {
            testFavourites.readFromFile(fileName);
        });

        assertTrue(testFavourites.getCurrentLocation().getName().equals("Something"));
        assertTrue(testFavourites.getFavourites().get(0).getName().equals("Helsinki"));

        removeTestFile(fileName);
    }

    @Test
    public void testCurrentAsNullWrite() {
        String fileName = "currentNullWrite.json";
        removeTestFile(fileName);
        assertThrows(WeatherAppException.class, () -> {
            Favourites testFavourites = new Favourites();
            testFavourites.writeToFile(fileName);
        });
        removeTestFile(fileName);
    }

    @Test
    public void testOkWrite() {
        String fileName = "okWrite.json";
        removeTestFile(fileName);
        Favourites testFavourites = new Favourites();

        // add some dummy data
        testFavourites.setCurrentLocation(new Location("Null Island", 0, 0, "NL"));
        testFavourites.addFavourite(new Location("Tampere", 23, 23, "FI"));

        assertDoesNotThrow(() -> {
            testFavourites.writeToFile(fileName);
        });
        removeTestFile(fileName);
    }

    @Test
    public void testWriteThenRead() {
        String fileName = "ReadAndWrite.json";
        removeTestFile(fileName);

        Favourites testFavourites = new Favourites();
        Favourites reader = new Favourites();
        // add some dummy data
        testFavourites.setCurrentLocation(new Location("Null Island", 0, 0, "NL"));
        testFavourites.addFavourite(new Location("Tampere", 23, 23, "FI"));

        assertDoesNotThrow(() -> {
            testFavourites.writeToFile(fileName);
            reader.readFromFile(fileName);
        });

        assertTrue(testFavourites.getCurrentLocation().getName().equals("Null Island"));
        assertTrue(testFavourites.getFavourites().get(0).getName().equals("Tampere"));

        removeTestFile(fileName);
    }

    @Test
    public void testRemoveByName() {
        Favourites testFavourites = new Favourites();

        testFavourites.addFavourite(new Location("Tampere", 0, 0, "FI"));
        testFavourites.addFavourite(new Location("Turku", 0, 0, "FI"));
        testFavourites.addFavourite(new Location("Helsinki", 0, 0, null));
        testFavourites.removeByName("Turku");

        assertTrue(testFavourites.getFavourites().size() == 2);
        assertTrue(testFavourites.getFavourites().get(0).getName().equals("Tampere"));
        assertTrue(testFavourites.getFavourites().get(1).getName().equals("Helsinki"));

        testFavourites.removeByName("Helsinki");

        assertTrue(testFavourites.getFavourites().size() == 1);
        assertTrue(testFavourites.getFavourites().get(0).getName().equals("Tampere"));

        testFavourites.removeByName("Tampere");
        assertTrue(testFavourites.getFavourites().isEmpty());
        testFavourites.removeByName("Tampere");
        assertTrue(testFavourites.getFavourites().isEmpty());
    }

    @Test
    public void testCheckIfNameExists() {
        Favourites testFavourites = new Favourites();

        testFavourites.addFavourite(new Location("Tampere", 0, 0, "FI"));
        testFavourites.addFavourite(new Location("Turku", 0, 0, "FI"));
        testFavourites.addFavourite(new Location("Helsinki", 0, 0, null));

        assertTrue(testFavourites.checkIfNameExists("Tampere"));
        assertTrue(testFavourites.checkIfNameExists("Turku"));
        assertTrue(testFavourites.checkIfNameExists("Helsinki"));
        assertFalse(testFavourites.checkIfNameExists("Oulu"));
    }
}
