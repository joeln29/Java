package fi.tuni.prog3.weatherapp;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class WeatherAppTest {

    /**
     * Test selectCorrectImage with random values.
     */
    @Test
    public void testSelectCorrectImage() {
        Weather weather = new Weather(null, null);
        weather.setCloudinessPercent(0);
        weather.setSnowAmount(0);
        weather.setRainAmount(0);

        assertEquals(WeatherApp.selectCorrectImage(weather, true), "clear-day.png");
        assertEquals(WeatherApp.selectCorrectImage(weather, false), "clear-night.png");

        weather.setCloudinessPercent(85);
        assertEquals(WeatherApp.selectCorrectImage(weather, true), "cloudy.png");
        assertEquals(WeatherApp.selectCorrectImage(weather, false), "cloudy.png");
        weather.setSnowAmount(0.4);
        assertEquals(WeatherApp.selectCorrectImage(weather, true), "snowy-1.png");
        assertEquals(WeatherApp.selectCorrectImage(weather, false), "snowy-1.png");
        weather.setCloudinessPercent(15);
        weather.setRainAmount(1.1);
        assertEquals(WeatherApp.selectCorrectImage(weather, true), "rainy-2-day.png");
        assertEquals(WeatherApp.selectCorrectImage(weather, false), "rainy-2-night.png");

    }
}
