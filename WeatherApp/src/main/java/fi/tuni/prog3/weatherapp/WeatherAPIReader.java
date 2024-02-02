package fi.tuni.prog3.weatherapp;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

/**
 * Class for retrieving data from OpenWeathermap API
 * (https://openweathermap.org/api).
 */
public class WeatherAPIReader implements iAPI {
    final static char[] ILLEGAL_CHARACTERS = { ';', '/', '{', '}', '^', '\n', ' ' };
    final int LOCATION_CALL_LIMIT = 5;
    final int FORECAST_CALL_LIMIT = 16;
    HttpClient client;
    String apiKey;

    /**
     * Default constructor for WeatherAPIReader class.
     * 
     * @param apiKey API-key to use when making requests.
     */
    public WeatherAPIReader(String apiKey) {
        client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .version(HttpClient.Version.HTTP_2)
                .build();
        this.apiKey = apiKey;
    }

    /**
     * Checks if a given status code is Ok, so in the 200-299 range.
     * 
     * @param statusCode status code to check.
     * @return true if status code is Ok, otherwise false.
     */
    private static boolean isStatusCodeOk(int statusCode) {
        return statusCode / 100 == 2;
    }

    /**
     * Checks if the given string can be used in an URI.
     * 
     * @param input String to be checked.
     * @return true if the given string contains invalid characters.
     */
    private static boolean isInvalidInput(String input) {
        for (char illegalChar : ILLEGAL_CHARACTERS) {
            if (input.indexOf(illegalChar) != -1) {
                return true;
            }
        }
        return false;
    }

    /**
     * Converts a unix epoch time to LocalDateTime.
     * 
     * @param epochTime unix epoch time
     * @return time in LocalDateTime format.
     */
    private static LocalDateTime epochToTime(long epochTime) {
        Instant instant = Instant.ofEpochSecond(epochTime);
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    /**
     * Converts a server returned JSON string to a list of locations.
     * 
     * @param json the JSON string to be converted.
     * @return list of locations found in the JSON.
     * @throws WeatherAppException if the JSON is malformed or has the wrong schema.
     */
    private static List<Location> parseLocationJson(String json) throws WeatherAppException {

        List<Location> locations = new ArrayList<>();
        try {
            Gson gson = new Gson();
            JsonArray locationsJson = gson.fromJson(json, JsonArray.class);
            // Loop over all values and create the actual Location objects
            for (JsonElement loc : locationsJson) {
                JsonObject location = loc.getAsJsonObject();
                String name = location.get("name").getAsString();
                double latitude = location.get("lat").getAsDouble();
                double longitude = location.get("lon").getAsDouble();
                String countryCode = location.get("country").getAsString();
                locations.add(new Location(name, latitude, longitude, countryCode));
            }
            return locations;
        } catch (Exception e) {
            throw new WeatherAppException("Json string is malformed.");
        }
    }

    /**
     * Converts a server returned hourly forecast JSON string to a list of Weather
     * elements.
     * 
     * @param json     the JSON string to be converted.
     * @param location location for which the weather is searched for.
     * @return list of Weather objects, with timestamps one hour apart.
     * @throws WeatherAppException if the JSON is malformed or has the wrong schema.
     */
    private static List<Weather> parseHourlyWeatherForecast(String json, Location location) throws WeatherAppException {
        try {
            List<Weather> weatherList = new ArrayList<>();
            Gson gson = new Gson();
            JsonArray weatherArray = gson.fromJson(json, JsonObject.class).get("list").getAsJsonArray();

            for (JsonElement current : weatherArray) {
                JsonObject weather = current.getAsJsonObject();
                long epoch = weather.get("dt").getAsLong();
                LocalDateTime time = epochToTime(epoch);

                JsonObject main = weather.get("main").getAsJsonObject();
                // Commented line can be used to fetch weather status:
                // Group of weather parameters (Rain, Snow, Clouds etc.)
                // https://openweathermap.org/api/hourly-forecast#list
                // JsonArray weatherStatus = weather.get("weather").getAsJsonArray();

                // Get all values from forecast
                double windSpeed = weather.get("wind").getAsJsonObject().get("speed").getAsDouble();
                double temperature = main.get("temp").getAsDouble();
                double maxTemperature = main.get("temp_max").getAsDouble();
                double minTemperature = main.get("temp_min").getAsDouble();

                int airPressure = main.get("pressure").getAsInt();
                int visibility = weather.get("visibility").getAsInt();
                double rainAmount = 0;
                if (weather.has("rain")) {
                    rainAmount = weather.get("rain").getAsJsonObject().get("1h").getAsDouble();
                }

                double snowAmount = 0;
                if (weather.has("snow")) {
                    rainAmount = weather.get("snow").getAsJsonObject().get("1h").getAsDouble();
                }
                int cloudinessPercent = weather.get("clouds").getAsJsonObject().get("all").getAsInt();
                int humidityPercent = main.get("humidity").getAsInt();
                double pop = weather.get("pop").getAsDouble();
                Weather obj = new Weather(location, time);

                // Set all values correct.
                obj.setWindSpeed(windSpeed);
                obj.setTemperature(Weather.fromKelvinToCelsius(temperature));
                obj.setMaxTemperature(Weather.fromKelvinToCelsius(maxTemperature));
                obj.setMinTemperature(Weather.fromKelvinToCelsius(minTemperature));
                obj.setAirPressure(airPressure);
                obj.setVisibility(visibility);
                obj.setRainAmount(rainAmount);
                obj.setSnowAmount(snowAmount);
                obj.setCloudinessPercent(cloudinessPercent);
                obj.setHumidityPercent(humidityPercent);
                obj.setProbabilityOfPrecipitation(pop);

                weatherList.add(obj);
            }
            return weatherList;
        } catch (Exception e) {
            throw new WeatherAppException("Json string is malformed.");
        }
    }

    /**
     * Converts a server returned 16 day forecast JSON string to a list of Weather
     * elements.
     * 
     * @param json     the JSON string to be converted.
     * @param location location for which the weather is searched for.
     * @return list of Weather objects, with timestamps one hour apart.
     * @throws WeatherAppException if the JSON is malformed or has the wrong schema.
     */
    private static List<Weather> parseDailyForecast(String json, Location location)
            throws WeatherAppException {
        try {
            List<Weather> weatherList = new ArrayList<>();
            Gson gson = new Gson();
            JsonArray weatherArray = gson.fromJson(json, JsonObject.class).get("list").getAsJsonArray();
            for (JsonElement current : weatherArray) {
                JsonObject weather = current.getAsJsonObject();
                long epoch = weather.get("dt").getAsLong();
                LocalDateTime time = epochToTime(epoch);

                double windSpeed = weather.get("speed").getAsDouble();
                double temperature = weather.get("temp").getAsJsonObject().get("day").getAsDouble();
                double maxTemperature = weather.get("temp").getAsJsonObject().get("max").getAsDouble();
                double minTemperature = weather.get("temp").getAsJsonObject().get("min").getAsDouble();
                int airPressure = weather.get("pressure").getAsInt();
                double rainAmount = 0;
                if (weather.has("rain")) {
                    rainAmount = weather.get("rain").getAsDouble();
                }

                double snowAmount = 0;
                if (weather.has("snow")) {
                    rainAmount = weather.get("snow").getAsDouble();
                }

                int cloudinessPercent = weather.get("clouds").getAsInt();
                int humidityPercent = weather.get("humidity").getAsInt();
                double pop = weather.get("pop").getAsDouble();
                Weather obj = new Weather(location, time);

                // Set all values correct.
                obj.setWindSpeed(windSpeed);
                obj.setTemperature(Weather.fromKelvinToCelsius(temperature));
                obj.setMaxTemperature(Weather.fromKelvinToCelsius(maxTemperature));
                obj.setMinTemperature(Weather.fromKelvinToCelsius(minTemperature));
                obj.setAirPressure(airPressure);
                obj.setRainAmount(rainAmount);
                obj.setSnowAmount(snowAmount);
                obj.setCloudinessPercent(cloudinessPercent);
                obj.setHumidityPercent(humidityPercent);
                obj.setProbabilityOfPrecipitation(pop);

                weatherList.add(obj);
            }
            return weatherList;
        } catch (Exception e) {
            throw new WeatherAppException("Json string is malformed.");
        }
    }

    // Reads data from here: https://openweathermap.org/api/geocoding-api
    @Override
    public List<Location> lookUpLocation(String locationName) throws WeatherAppException, IllegalArgumentException {
        // Encode spaces as %20 to avoid errors
        String encodedSpaces = locationName.replace(" ", "%20");
        // Check if the string can be used in the uri.
        if (isInvalidInput(encodedSpaces)) {
            throw new IllegalArgumentException("Location string contains illegal characters!");
        } else if (encodedSpaces.isEmpty()) {
            throw new IllegalArgumentException("Location string is empty!");
        }
        try {
            URI locationURI = URI
                    .create(String.format("http://api.openweathermap.org/geo/1.0/direct?q=%s&limit=%s&appid=%s",
                            encodedSpaces, LOCATION_CALL_LIMIT, apiKey));

            // Create the request and send it to the server
            HttpRequest locationRequest = HttpRequest.newBuilder().uri(locationURI).GET().build();
            HttpResponse<String> response = client.send(locationRequest, HttpResponse.BodyHandlers.ofString());

            // Check for response for errors
            if (!isStatusCodeOk(response.statusCode())) {
                throw new WeatherAppException(
                        String.format("Server returned an error with status code: %d", response.statusCode()));
            }

            return parseLocationJson(response.body());
        } catch (IOException | InterruptedException e) {
            throw new WeatherAppException("Network connection to the server failed!");
        } catch (JsonSyntaxException | NullPointerException e) {
            throw new WeatherAppException("The JSON got from the server was malformed!");
        }
    }

    // Reads data from here: https://openweathermap.org/api/hourly-forecast
    @Override
    public List<Weather> getHourlyWeather(Location location) throws WeatherAppException {
        try {
            URI locationURI = URI
                    .create(String.format(
                            "https://pro.openweathermap.org/data/2.5/forecast/hourly?lat=%f&lon=%f&appid=%s",
                            location.getX(), location.getY(), apiKey));

            // Create the request and send it to the server
            HttpRequest locationRequest = HttpRequest.newBuilder().uri(locationURI).GET().build();
            HttpResponse<String> response = client.send(locationRequest, HttpResponse.BodyHandlers.ofString());

            // Check for response for errors
            if (!isStatusCodeOk(response.statusCode())) {
                throw new WeatherAppException(
                        String.format("Server returned an error with status code: %d", response.statusCode()));
            }

            return parseHourlyWeatherForecast(response.body(), location);
        } catch (IOException | InterruptedException e) {
            throw new WeatherAppException("Network connection to the server failed!");
        }
    }

    // Reads data from here: https://openweathermap.org/forecast16
    @Override
    public List<Weather> getDailyForecast(Location location) throws WeatherAppException {
        try {
            URI locationURI = URI
                    .create(String.format(
                            "https://api.openweathermap.org/data/2.5/forecast/daily?lat=%f&lon=%f&cnt=%d&appid=%s",
                            location.getX(), location.getY(), FORECAST_CALL_LIMIT, apiKey));

            // Create the request and send it to the server
            HttpRequest locationRequest = HttpRequest.newBuilder().uri(locationURI).GET().build();
            HttpResponse<String> response = client.send(locationRequest, HttpResponse.BodyHandlers.ofString());

            // Check for response for errors
            if (!isStatusCodeOk(response.statusCode())) {
                throw new WeatherAppException(
                        String.format("Server returned an error with status code: %d", response.statusCode()));
            }

            return parseDailyForecast(response.body(), location);
        } catch (IOException | InterruptedException e) {
            throw new WeatherAppException("Network connection to the server failed!");
        }
    }
}
