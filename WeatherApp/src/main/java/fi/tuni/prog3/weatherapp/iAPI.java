package fi.tuni.prog3.weatherapp;

import java.util.List;

/**
 * Interface for extracting data from the OpenWeatherMap API.
 */
public interface iAPI {

    /**
     * Returns a list of found locations with that name.
     * 
     * @param locationName Name of the location to create.
     * @return List of Location objects.
     * @throws WeatherAppException      when the server returns an error or the
     *                                  network connection fails.
     * @throws IllegalArgumentException if the location string is invalid.
     */
    List<Location> lookUpLocation(String locationName) throws WeatherAppException, IllegalArgumentException;

    /**
     * Returns the 4 day hourly forecast for the given location.
     * 
     * @param location the location which the weather is searched for.
     * @return List of weather objects.
     * @throws WeatherAppException when the server returns an error or the network
     *                             connection fails.
     */
    List<Weather> getHourlyWeather(Location location) throws WeatherAppException;

    /**
     * Returns a 16 day forecast for the given location.
     * 
     * @param location the location which the weather is searched for.
     * @return List of weather objects.
     * @throws WeatherAppException when the server returns an error or the network
     *                             connection fails.
     */
    List<Weather> getDailyForecast(Location location) throws WeatherAppException;
}
