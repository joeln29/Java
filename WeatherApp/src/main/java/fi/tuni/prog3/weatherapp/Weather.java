package fi.tuni.prog3.weatherapp;

import java.time.LocalDateTime;

/**
 * The Weather class represents a weather event.
 */
public class Weather {

    private Location location;
    private LocalDateTime time;

    private double windSpeed;
    private double temperature;
    private double minTemperature;
    private double maxTemperature;

    private int airPressure;
    private int visibility;
    private double rainAmount;
    private double snowAmount;
    private int cloudinessPercent;
    private int humidityPercent;
    private double probabilityOfPrecipitation;

    /**
     * Constructor for Class Weather.
     * 
     * @param location location of for the weather event.
     * @param time     time of the weather event.
     */
    public Weather(Location location, LocalDateTime time) {
        this.location = location;
        this.time = time;
    }

    /**
     * Converts wind speed to a descriptive name.
     *
     * @param windSpeed Wind speed value.
     * @return A string representing the wind speed category.
     * @throws IllegalArgumentException when windSpeed is negative.
     */
    public static String fromWindSpeedToName(int windSpeed) throws IllegalArgumentException {
        if (windSpeed < 0) {
            throw new IllegalArgumentException("Wind speed must not be negative!");
        } else if (windSpeed == 0) {
            return "Calm";
        } else if (windSpeed <= 3) {
            return "Light Wind";
        } else if (windSpeed <= 7) {
            return "Moderate Wind";
        } else if (windSpeed <= 20) {
            return "Strong Wind";
        } else if (windSpeed <= 32) {
            return "Storm";
        } else {
            return "Violent Storm";
        }
    }

    /**
     * Converts rain amount to a descriptive name.
     *
     * @param precipitationAmount Amount of precipitation.
     * @return A string representing the precipitation amount.
     * @throws IllegalArgumentException when precipitationAmount is negative.
     */
    public static String fromPrecipitationAmountToName(double precipitationAmount) throws IllegalArgumentException {
        if (precipitationAmount < 0) {
            throw new IllegalArgumentException("PrecipitationAmount must not be negative!");
        } else if (precipitationAmount < 0.3) {
            return "None";
        } else if (precipitationAmount <= 0.9) {
            return "Light Rain";
        } else if (precipitationAmount <= 4.4) {
            return "Moderate Rain";
        } else {
            return "Heavy Rain";
        }
    }

    /**
     * Converts the given celsius temperature to fahrenheit.
     * 
     * @param temperature Temperature in Celsius.
     * @return the specified temperature in Farenheit.
     */
    public static double fromCelsiusToFarenheit(double temperature) {
        return temperature * 1.8 + 32;
    }

    /**
     * Converts the given kelvin temperature to Celsius.
     * 
     * @param temperature Temperature in Kelvins.
     * @return the specified temperature in Celsius.
     */
    public static double fromKelvinToCelsius(double temperature) {
        return temperature - 273.15;
    }

    /**
     * Converts the given Celsisus temperature to Kelvins.
     * 
     * @param temperature Temperature in Celsius.
     * @return the specified temperature in Kelvins.
     */
    public static double fromCelsiusToKelvin(double temperature) {
        return temperature + 273.15;
    }

    /**
     * Converts Fahrenheit to Celsius.
     * 
     * @param temperature temperature in Fahrenheit to be converted
     * @return double Fahrenheit
     */
    public static double fromFahrenheitToCelcius(double temperature) {
        return (temperature - 32) * 1.8;
    }

    /**
     * Coverts millimeters to inches.
     * 
     * @param millimeters Converted unit in millimeters.
     * @return The argument as inches.
     */
    public static double fromMilliMeterToInch(double millimeters) {
        return millimeters / 25.4;
    }

    /**
     * Converts a speed in meters per second to miles per hour.
     * 
     * @param speed speed in meters per second (m/s).
     * @return the same speed in miles per hour (mph).
     */
    public static double fromMPSoMPH(double speed) {
        return speed * 2.23694;
    }

    /**
     * Converts hectopascals to inches of mercury (inHg).
     * 
     * @param pressure pressure in hectopascals.
     * @return the same pressure in inches of mercury (inHg).
     */
    public static double fromHectoPascalsToINHG(double pressure) {
        return pressure * 0.0295299830714;
    }

    /**
     * Gets the location associated with this weather information.
     *
     * @return The location.
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Gets the time of the weather information.
     *
     * @return The time.
     */
    public LocalDateTime getTime() {
        return time;
    }

    /**
     * Gets the wind speed in meters per second.
     *
     * @return The wind speed.
     */
    public double getWindSpeed() {
        return windSpeed;
    }

    /**
     * Gets the cloudiness percentage.
     *
     * @return The cloudiness percentage.
     */
    public int getCloudinessPercent() {
        return cloudinessPercent;
    }

    /**
     * Gets the temperature in Celsius.
     *
     * @return The temperature.
     */
    public double getTemperature() {
        return temperature;
    }

    /**
     * Gets the air pressure hectopascals.
     *
     * @return The air pressure.
     */
    public int getAirPressure() {
        return airPressure;
    }

    /**
     * Gets the visibility in meters.
     *
     * @return The visibility.
     */
    public int getVisibility() {
        return visibility;
    }

    /**
     * Gets the humidity percentage.
     *
     * @return The humidity percentage.
     */
    public int getHumidityPercent() {
        return humidityPercent;
    }

    /**
     * Gets the amount of rain in millimeters.
     *
     * @return The amount of rain.
     */
    public double getRainAmount() {
        return rainAmount;
    }

    /**
     * Gets the amount of snow in millimeters.
     *
     * @return The amount of snow.
     */
    public double getSnowAmount() {
        return snowAmount;
    }

    /**
     * Gets the probability of precipitation.
     *
     * @return The probability of precipitation.
     */
    public double getProbabilityOfPrecipitation() {
        return probabilityOfPrecipitation;
    }

    /**
     * Sets the wind speed in meters per second.
     *
     * @param windSpeed The wind speed to set.
     * @throws IllegalArgumentException if the wind speed is negative.
     */
    public void setWindSpeed(double windSpeed) {
        if (windSpeed < 0) {
            throw new IllegalArgumentException("Setting a negative wind speed is not allowed.");
        }
        this.windSpeed = windSpeed;
    }

    /**
     * Sets the temperature in Celsius.
     *
     * @param temperature The temperature to set in Celsius.
     */
    public void setTemperature(double temperature) {
        // Update temp to Celsius
        this.temperature = temperature;
    }

    /**
     * Sets the air pressure in hectopascals.
     *
     * @param airPressure The air pressure to set.
     * @throws IllegalArgumentException if the air pressure is negative.
     */
    public void setAirPressure(int airPressure) {
        if (airPressure < 0) {
            throw new IllegalArgumentException("Setting a negative air pressure is not allowed.");
        }
        this.airPressure = airPressure;
    }

    /**
     * Sets the visibility in meters.
     *
     * @param visibility The visibility to set.
     * @throws IllegalArgumentException if the visibility is negative.
     */
    public void setVisibility(int visibility) {
        if (visibility < 0) {
            throw new IllegalArgumentException("Setting a negative visibility is not allowed.");
        }
        this.visibility = visibility;
    }

    /**
     * Sets the amount of rain in millimeters.
     *
     * @param rainAmount The amount of rain to set.
     * @throws IllegalArgumentException if the rain amount is negative.
     */
    public void setRainAmount(double rainAmount) {
        if (rainAmount < 0) {
            throw new IllegalArgumentException("Setting a negative rain amount is not allowed.");
        }
        this.rainAmount = rainAmount;
    }

    /**
     * Sets the amount of snow in millimeters.
     *
     * @param snowAmount The amount of snow to set.
     * @throws IllegalArgumentException if the snow amount is negative.
     */
    public void setSnowAmount(double snowAmount) {
        if (snowAmount < 0) {
            throw new IllegalArgumentException("Setting a negative snow amount is not allowed.");
        }
        this.snowAmount = snowAmount;
    }

    /**
     * Sets the cloudiness percentage.
     *
     * @param cloudinessPercent The cloudiness percentage to set.
     * @throws IllegalArgumentException if the cloudiness percentage is not between
     *                                  0 and 100.
     */
    public void setCloudinessPercent(int cloudinessPercent) {
        if (cloudinessPercent < 0 || cloudinessPercent > 100) {
            throw new IllegalArgumentException("Cloudiness percentage must be between 0 and 100.");
        }
        this.cloudinessPercent = cloudinessPercent;
    }

    /**
     * Sets the humidity percentage.
     *
     * @param humidityPercent The humidity percentage to set.
     * @throws IllegalArgumentException if the humidity percentage is not between 0
     *                                  and 100.
     */
    public void setHumidityPercent(int humidityPercent) {
        if (humidityPercent < 0 || humidityPercent > 100) {
            throw new IllegalArgumentException("Humidity percentage must be between 0 and 100.");
        }
        this.humidityPercent = humidityPercent;
    }

    /**
     * Sets the probability of precipitation as a percentage.
     *
     * @param probabilityOfPrecipitation The probability of precipitation to set.
     * @throws IllegalArgumentException if the probability of precipitation is not
     *                                  between 0 and 1.
     */
    public void setProbabilityOfPrecipitation(double probabilityOfPrecipitation) {
        if (probabilityOfPrecipitation < 0 || probabilityOfPrecipitation > 1) {
            throw new IllegalArgumentException("Probability of precipitation must be between 0 and 1.");
        }
        this.probabilityOfPrecipitation = probabilityOfPrecipitation;
    }

    /**
     * Gets the current max temperature for the current weather event.
     * 
     * @return max temperature for the current weather event.
     */
    public double getMaxTemperature() {
        return maxTemperature;
    }

    /**
     * Sets the current max temperature for the current weather event.
     * 
     * @param maxTemperature Max temperature for the current weather event.
     */
    public void setMaxTemperature(double maxTemperature) {
        this.maxTemperature = maxTemperature;
    }

    /**
     * Gets the current minimum temperature for the current weather event.
     * 
     * @return Minimum temperature for the current weather event.
     */
    public double getMinTemperature() {
        return minTemperature;
    }

    /**
     * Sets the current minimum temperature for the current weather event.
     * 
     * @param minTemperature Current minimum temperature for the current weather
     *                       event.
     */
    public void setMinTemperature(double minTemperature) {
        this.minTemperature = minTemperature;
    }
}
