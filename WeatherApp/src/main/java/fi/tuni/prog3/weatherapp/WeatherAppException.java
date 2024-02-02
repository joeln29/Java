package fi.tuni.prog3.weatherapp;

/**
 * Exception class for WeatherAPP.
 */
public class WeatherAppException extends Exception {
    /**
     * Constructor for WeatherAppException.
     * @param message message forwarded to the Exception
     */
    public WeatherAppException(String message) {
        super(message);
    }
}
