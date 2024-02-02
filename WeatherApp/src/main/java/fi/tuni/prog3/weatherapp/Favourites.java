package fi.tuni.prog3.weatherapp;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

/**
 * Class representing current and favourite locations
 */
public class Favourites implements iReadAndWriteToFile {
    private Location currentLocation;
    private List<Location> favourites;

    /**
     * Constructs favourite object with no current location
     */
    public Favourites() {
        this.currentLocation = null;
        this.favourites = new ArrayList<>();
    }

    /**
     * Updates the current location
     * 
     * @param newLocation the location to be set as current location
     */
    public void setCurrentLocation(Location newLocation) {
        this.currentLocation = newLocation;
    }

    /**
     * Gets the current location
     * 
     * @return the name of the current location
     */
    public Location getCurrentLocation() {
        return currentLocation;
    }

    /**
     * Add specific location to the favourites
     * 
     * @param location the location to be added
     */
    public void addFavourite(Location location) {
        if (!favourites.contains(location)) {
            favourites.add(location);
        }
    }

    /**
     * Remove specific location from the favourites
     * 
     * @param location the location to be removed
     */
    public void removeFavourite(Location location) {
        if (favourites.contains(location)) {
            favourites.remove(location);
        }
    }

    /**
     * Removes the first found location matched by its name.
     * @param locationName name of the location that needs to be removed.
     * 
     */
    public void removeByName(String locationName) {
        for (int i = 0; i < favourites.size(); i++) {
            if (favourites.get(i).getName().equals(locationName)) {
                favourites.remove(i);
                break;
            }
        }
    }

    /**
     * Checks if a given name exists (case insensitive) in the favourites.
     * @param name name of the checked location.
     * @return true if the name is found, false otherwise.
     */
    public boolean checkIfNameExists(String name) {
        for (Location location : favourites) {
            if (location.getName().toLowerCase().equals(name.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Collect a list of the current favourite locations
     * 
     * @return list of favourite locations
     */
    public List<Location> getFavourites() {
        List<Location> currentFavourites = new ArrayList<>();
        for (Location location : favourites) {
            currentFavourites.add(location);
        }
        return currentFavourites;
    }

    @Override
    public void readFromFile(String fileName) throws WeatherAppException {
        String json;
        try {
            json = Files.readString(Path.of(fileName));
        } catch (IOException e) {
            throw new WeatherAppException("Error reading data from file.");
        }
        Gson gson = new Gson();
        Favourites data = gson.fromJson(json, getClass());

        // Fill this object with new values
        if (data.currentLocation == null) {
            throw new WeatherAppException("Invalid JSON format.");
        }
        this.currentLocation = data.currentLocation;
        this.favourites = data.favourites;
    }

    @Override
    public void writeToFile(String fileName) throws WeatherAppException {
        if (currentLocation == null) {
            throw new WeatherAppException("Current lcoation is null.");
        }
        Gson gson = new Gson();
        String json = gson.toJson(this);
        try (FileWriter fileWriter = new FileWriter(fileName)) {
            fileWriter.write(json);
        } catch (IOException e) {
            throw new WeatherAppException("Error writing data to file.");
        }
    }
}
