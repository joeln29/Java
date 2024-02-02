package fi.tuni.prog3.weatherapp;

/**
 * Class representing a location and its name and coordinates.
 */
public class Location {
    private String name;
    private double x;
    private double y;
    private String countryCode;

    /**
     * Constructor for class Location.
     * 
     * @param name        the name of the location
     * @param x           coordinate
     * @param y           coordinate
     * @param countryCode ISO 3166 standard country code. Note that the validity is
     *                    not checked.
     */
    public Location(String name, double x, double y, String countryCode) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.countryCode = countryCode;
    }

    /**
     * Sets name for the location
     * 
     * @param name new name of the location.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets coordinates for the location
     * 
     * @param x the x-coordinate
     * @param y the y-coordinate
     */
    public void setCoordinates(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Get the name of the location
     * 
     * @return name of the location
     */
    public String getName() {
        return name;
    }

    /**
     * Get the country code of the location
     * 
     * @return the country code
     */
    public String getCountryCode() {
        return countryCode;
    }

    /**
     * Returns the X-coordinate of the location
     * 
     * @return the X-coordinate
     */
    public double getX() {
        return x;
    }

    /**
     * Returns the Y-coordinate of the location
     * 
     * @return the Y-coordinate
     */
    public double getY() {
        return y;
    }

    /**
     * Compare distances between location 1 and 2
     * 
     * @param second location to be compared
     * @return the distance between two points
     */
    public double distanceTo(Location second) {
        double secondX = second.getX();
        double secondY = second.getY();
        double result = Math.hypot(secondX - this.x, secondY - this.y);
        return result;
    }

    /**
     * Location and its data in String form
     * 
     * @return string of location and its data
     */
    @Override
    public String toString() {
        return String.format("Location %s, (%f, %f), Country Code: %s",
                name, x, y, countryCode);
    }
}
