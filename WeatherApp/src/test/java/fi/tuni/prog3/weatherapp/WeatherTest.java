package fi.tuni.prog3.weatherapp;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

public class WeatherTest {

    public Location getTestLocation() {
        return new Location("Test", 0.0, 0.0, "AB");
    }

    @Test
    void settersAndGetters() {
        Location location = getTestLocation();
        LocalDateTime time = LocalDateTime.now();
        Weather weather = new Weather(location, time);

        weather.setWindSpeed(10);
        assertEquals(10, weather.getWindSpeed());

        weather.setTemperature(25.5);
        assertEquals(25.5, weather.getTemperature());

        weather.setAirPressure(1015);
        assertEquals(1015, weather.getAirPressure());

        weather.setVisibility(1000);
        assertEquals(1000, weather.getVisibility());

        weather.setRainAmount(5.0);
        assertEquals(5.0, weather.getRainAmount());

        weather.setSnowAmount(2.5);
        assertEquals(2.5, weather.getSnowAmount());

        weather.setCloudinessPercent(75);
        assertEquals(75, weather.getCloudinessPercent());

        weather.setHumidityPercent(60);
        assertEquals(60, weather.getHumidityPercent());

        weather.setProbabilityOfPrecipitation(1);
        assertEquals(1.0, weather.getProbabilityOfPrecipitation());

        weather.setMaxTemperature(12);
        assertEquals(12, weather.getMaxTemperature());

        weather.setMinTemperature(1);
        assertEquals(1, weather.getMinTemperature());

        // Test for negative values in setters
        assertThrows(IllegalArgumentException.class, () -> weather.setWindSpeed(-1));
        assertThrows(IllegalArgumentException.class, () -> weather.setAirPressure(-10));
        assertThrows(IllegalArgumentException.class, () -> weather.setVisibility(-500));
        assertThrows(IllegalArgumentException.class, () -> weather.setRainAmount(-1.0));
        assertThrows(IllegalArgumentException.class, () -> weather.setSnowAmount(-0.5));
        assertThrows(IllegalArgumentException.class, () -> weather.setCloudinessPercent(-10));
        assertThrows(IllegalArgumentException.class, () -> weather.setCloudinessPercent(110));
        assertThrows(IllegalArgumentException.class, () -> weather.setHumidityPercent(-5));
        assertThrows(IllegalArgumentException.class, () -> weather.setHumidityPercent(120));
        assertThrows(IllegalArgumentException.class, () -> weather.setProbabilityOfPrecipitation(-5));
        assertThrows(IllegalArgumentException.class, () -> weather.setProbabilityOfPrecipitation(120));
    }

    public void runFromRainAmountToName(String text, double rainAmount) {
        assertEquals(text, Weather.fromPrecipitationAmountToName(rainAmount));
    }

    // Test for converting valid rainAmounts to their names;
    @Test
    public void testValidFromRainAmountToName() {
        runFromRainAmountToName("None", 0);
        runFromRainAmountToName("None", 0.2);

        runFromRainAmountToName("Light Rain", 0.3);
        runFromRainAmountToName("Light Rain", 0.8);

        runFromRainAmountToName("Moderate Rain", 1.0);
        runFromRainAmountToName("Moderate Rain", 4.4);

        runFromRainAmountToName("Heavy Rain", 4.5);
    }

    // Test for illegal rain amount
    @Test
    public void testInvalidFromRainAmountToName() {
        assertThrows(IllegalArgumentException.class, () -> {
            // Illegal rain amount
            Weather.fromPrecipitationAmountToName(-0.1);
        });
    }

    public void runFromWindSpeedToName(String text, int windSpeed) {
        assertEquals(text, Weather.fromWindSpeedToName(windSpeed));
    }

    // Test for converting valid wind speeds to their names;
    @Test
    public void testValidFromWindSpeedToName() {
        runFromWindSpeedToName("Calm", 0);

        runFromWindSpeedToName("Light Wind", 1);
        runFromWindSpeedToName("Light Wind", 3);

        runFromWindSpeedToName("Moderate Wind", 4);
        runFromWindSpeedToName("Moderate Wind", 7);

        runFromWindSpeedToName("Strong Wind", 8);
        runFromWindSpeedToName("Strong Wind", 20);

        runFromWindSpeedToName("Storm", 21);
        runFromWindSpeedToName("Storm", 32);

        runFromWindSpeedToName("Violent Storm", 33);
    }

    // Test for illegal wind speed
    @Test
    public void testInvalidFromWindSpeedToName() {
        assertThrows(IllegalArgumentException.class, () -> {
            // Illegal wind speed
            Weather.fromWindSpeedToName(-1);
        });
    }

    @Test
    void testFromCelsiusToFahrenheit() {
        assertEquals(32.0, Weather.fromCelsiusToFarenheit(0.0));
        assertEquals(212.0, Weather.fromCelsiusToFarenheit(100.0));
        assertEquals(-40.0, Weather.fromCelsiusToFarenheit(-40.0));
    }

    @Test
    void testFromKelvinToCelsius() {
        assertEquals(0.0, Weather.fromKelvinToCelsius(273.15));
        assertEquals(-273.15, Weather.fromKelvinToCelsius(0.0));
        assertEquals(100.0, Weather.fromKelvinToCelsius(373.15));
    }

    @Test
    void testFromCelsiusToKelvin() {
        assertEquals(273.15, Weather.fromCelsiusToKelvin(0.0));
        assertEquals(373.15, Weather.fromCelsiusToKelvin(100.0));
        assertEquals(0.0, Weather.fromCelsiusToKelvin(-273.15));
    }

    @Test
    void testFromMilliMeterToInch() {
        double millimeters = 254; // 10 inches in millimeters
        double expectedInches = 10.0;
        double result = Weather.fromMilliMeterToInch(millimeters);
        assertEquals(expectedInches, result, 0.0001);
    }

    @Test
    void testFromMPSoMPH() {
        double speedInMetersPerSecond = 1.0;
        double expectedSpeedInMPH = 2.23694;
        double result = Weather.fromMPSoMPH(speedInMetersPerSecond);
        assertEquals(expectedSpeedInMPH, result, 0.0001);
    }

    @Test
    void testFromHectoPascalsToINHG() {
        double pressureInHectoPascals = 1013.25; // Standard atmospheric pressure
        double expectedPressureInINHG = 29.9212598425;
        double result = Weather.fromHectoPascalsToINHG(pressureInHectoPascals);
        assertEquals(expectedPressureInINHG, result, 0.0001);
    }
}