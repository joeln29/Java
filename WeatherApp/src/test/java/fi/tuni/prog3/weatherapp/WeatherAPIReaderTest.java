package fi.tuni.prog3.weatherapp;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class WeatherAPIReaderTest {

    public WeatherAPIReader getAPI() {
        return new WeatherAPIReader("9543676bf2f7706ca43996f18229c13b");
    }

    public Location getValidLocation() {
        return new Location("Tampere", 61.44998651474023, 23.857621181380157, "FI");
    }

    @Test
    public void testValidLocationLookup() throws WeatherAppException {
        String locationName = "Siilinjärvi";
        WeatherAPIReader api = getAPI();
        List<Location> locations = api.lookUpLocation(locationName);
        assertEquals(locationName, locations.get(0).getName());
        assertEquals("FI", locations.get(0).getCountryCode());

        String locationNameWithSpaces = "This location has spaces.";
        assertDoesNotThrow(() -> {
            api.lookUpLocation(locationNameWithSpaces);
        });
    }

    @Test
    public void testInvalidLocationName() {
        WeatherAPIReader api = getAPI();

        String locationNameWithSpecialCharacters = "What is this location{}";
        assertThrows(IllegalArgumentException.class, () -> {
            api.lookUpLocation(locationNameWithSpecialCharacters);
        });

        String emptyLocation = "";
        assertThrows(IllegalArgumentException.class, () -> {
            api.lookUpLocation(emptyLocation);
        });
    }

    @Test
    public void testHourlyWeather() throws WeatherAppException {
        Location location = getValidLocation();
        WeatherAPIReader api = getAPI();
        List<Weather> weather = api.getHourlyWeather(location);
        assertNotEquals(weather.size(), 0);
    }

    @Test
    public void testDailyForecast() throws WeatherAppException {
        Location location = getValidLocation();
        WeatherAPIReader api = getAPI();
        List<Weather> weather = api.getDailyForecast(location);
        assertEquals(weather.size(), 16);
    }
}
