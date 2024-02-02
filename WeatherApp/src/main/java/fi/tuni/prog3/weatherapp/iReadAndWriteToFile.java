package fi.tuni.prog3.weatherapp;

/**
 * Interface with methods to read from a file and write to a file.
 */
public interface iReadAndWriteToFile {

    /**
     * Reads JSON from the given file.
     * 
     * @param fileName name of the file to read from.
     * @throws WeatherAppException if the file doesn't exist or if the json is malformed.
     */
    void readFromFile(String fileName) throws WeatherAppException;

    /**
     * Write the student progress as JSON into the given file.
     * 
     * @param fileName name of the file to write to.
     * @throws WeatherAppException When writing to file fails.
     */
    void writeToFile(String fileName) throws WeatherAppException;
}
