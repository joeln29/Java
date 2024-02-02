package fi.tuni.prog3.weatherapp;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;

/**
 * JavaFX WeatherApp
 */
public class WeatherApp extends Application {
    private static final String API_KEY = "9543676bf2f7706ca43996f18229c13b";
    private static final String LOCATION_FILE_NAME = "locations.json";
    private static final String SEARCH_HISTORY_FILE_NAME = "search.json";
    private static final int DAILY_FORECAST_LENGTH = 7;
    private static final int HOURLY_FORECAST_LENGHT = 12;
    private static final int WINDOW_WIDTH = 700;

    private static Scene scene;
    private WeatherAPIReader api;
    private Favourites favourites;
    private List<Weather> hourlyWeather;
    private boolean isMetric;
    private List<Weather> dailyWeather;

    /**
     * Initializes the weather data
     * 
     * @return list of hourly weather
     */
    private List<String> initialize() {
        // Trying to read locations from the JSON file
        try {
            favourites = new Favourites();
            favourites.readFromFile(LOCATION_FILE_NAME);
            api = new WeatherAPIReader(API_KEY);
            hourlyWeather = new ArrayList<>();
            try {
                updateWeather();
            } catch (WeatherAppException e) {
                e.printStackTrace();
            }
            // Load default values if file cannot be read
        } catch (WeatherAppException e) {
            hourlyWeather = new ArrayList<>();
            api = new WeatherAPIReader(API_KEY);
            try {
                List<Location> locations = api.lookUpLocation("Siilinjärvi");
                favourites.setCurrentLocation(locations.get(0));
                updateWeather();
            } catch (WeatherAppException error) {
                error.printStackTrace();
            }
        }

        // Try to read search history from file
        String json;
        try {
            json = Files.readString(Path.of(SEARCH_HISTORY_FILE_NAME));

        } catch (IOException e) {
            System.out.println("Search history not found, defaulting to empty list.");
            return new ArrayList<>();
        }
        Gson gson = new Gson();
        ArrayList<String> data = gson.fromJson(json, ArrayList.class);

        // Check if is malformed.
        if (data == null) {
            return new ArrayList<>();
        }
        return data;
    }

    /**
     * Check if given hour is day- or night time
     * 
     * @param hour time in hours
     * @return true if night false otherwise
     */
    public static boolean isDay(int hour) {
        return hour >= 6 && hour <= 18;
    }

    /**
     * Select correct image for specific weather
     * 
     * @param currentWeather weather wanted to display as image
     * @param isDay          boolean which determines if it's currently day
     * @return string, name of the image
     */
    public static String selectCorrectImage(Weather currentWeather, boolean isDay) {
        String ending;
        String beginning;
        String number;

        double cloudiness = currentWeather.getCloudinessPercent();
        // Check right ending
        if (cloudiness >= 80) {
            ending = ".png";
        } else if (isDay) {
            ending = "-day.png";
        } else {
            ending = "-night.png";
        }

        // Check for clear sky (no rain / snow)
        if (cloudiness < 0.2 && currentWeather.getSnowAmount() < 0.3 && currentWeather.getRainAmount() < 0.3) {
            return "clear" + ending;
        } else if (cloudiness >= 80 && currentWeather.getSnowAmount() < 0.3 && currentWeather.getRainAmount() < 0.3) {
            return "cloudy.png";
        }

        double checked;
        if (currentWeather.getSnowAmount() < 0.3 && currentWeather.getRainAmount() < 0.3) {
            beginning = "cloudy";
            // Select correct cloudiness percent
            if (currentWeather.getCloudinessPercent() < 0.4) {
                number = "-1";
            } else if (currentWeather.getCloudinessPercent() < 0.6) {
                number = "-2";
            } else {
                number = "-3";
            }
            return beginning + number + ending;

        } else if (currentWeather.getSnowAmount() > currentWeather.getRainAmount()) {
            beginning = "snowy";
            checked = currentWeather.getSnowAmount();
        } else {
            beginning = "rainy";
            checked = currentWeather.getRainAmount();
        }

        // Check for number (amount of rain / snow / clouds)
        if (checked < 1) {
            number = "-1";
        } else if (checked < 4.4) {
            number = "-2";
        } else {
            number = "-3";
        }

        return beginning + number + ending;
    }

    /**
     * Updates list of hourly weather
     * 
     * @throws WeatherAppException if api data is invalid
     */
    private void updateWeather() throws WeatherAppException {
        hourlyWeather = api.getHourlyWeather(favourites.getCurrentLocation());
        dailyWeather = api.getDailyForecast(favourites.getCurrentLocation());
    }

    /**
     * Searches location data from the API
     * 
     * @param locationName the location name to be searched
     * @return true if the updating was successful
     */
    private boolean searchLocation(String locationName) {
        if (locationName.isEmpty()) {
            return false;
        }
        try {
            List<Location> temp = api.lookUpLocation(locationName);
            if (temp.isEmpty()) {
                return false;
            }
            favourites.setCurrentLocation(temp.get(0));
            updateWeather();
            return true;
        } catch (WeatherAppException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Update all weather values in GUI
     */
    private void updateGUIValues() {
        Weather mainWeather = hourlyWeather.get(0);
        int hour = mainWeather.getTime().getHour();
        Label currentLocation = (Label) scene.lookup("#currentLocation");
        currentLocation.setText(String.format("%s", favourites.getCurrentLocation().getName()));

        TextField searchBar = (TextField) scene.lookup("#searchBar");
        searchBar.setText("");

        Label temperature = (Label) scene.lookup("#temperature");
        Label airPressure = (Label) scene.lookup("#airPressure");
        Label rainAmount = (Label) scene.lookup("#rainAmount");

        Label humidityPercent = (Label) scene.lookup("#humidity");
        humidityPercent.setText(String.format("%d %%", mainWeather.getHumidityPercent()));

        InputStream mainInputStream = getClass()
                .getResourceAsStream("/images/" + selectCorrectImage(mainWeather, isDay(hour)));
        BorderPane mainImageBorder = (BorderPane) scene.lookup("#mainImagePane");
        ImageView mainImage = (ImageView) mainImageBorder.getChildren().get(0);
        mainImage.setImage(new Image(mainInputStream, 100, 100, true, false));

        if (isMetric) {
            temperature.setText(String.format("%.1f °C", hourlyWeather.get(0).getTemperature()));
            rainAmount.setText(String.format("%.2f mm", mainWeather.getRainAmount()));
            airPressure.setText(String.format("%d Pa", mainWeather.getAirPressure()));
        } else {
            double celsius = hourlyWeather.get(0).getTemperature();
            temperature.setText(String.format("%.1f °F", Weather.fromCelsiusToFarenheit(celsius)));

            rainAmount.setText(String.format("%.2f'", Weather.fromMilliMeterToInch(mainWeather.getRainAmount())));
            airPressure
                    .setText(String.format("%.1f inHg", Weather.fromHectoPascalsToINHG(mainWeather.getAirPressure())));

        }

        // Update Daily forecast
        for (int i = 0; i < DAILY_FORECAST_LENGTH; i++) {
            GridPane weatherPanel = (GridPane) scene.lookup("#weatherPanel" + i);
            Weather weather = dailyWeather.get(i);
            InputStream inputStream = getClass().getResourceAsStream("/images/" + selectCorrectImage(weather, true));

            BorderPane imageBorder = (BorderPane) weatherPanel.getChildren().get(1);
            ImageView image = (ImageView) imageBorder.getChildren().get(0);
            image.setImage(new Image(inputStream));

            double dailyMax = weather.getMaxTemperature();
            double dailyMin = weather.getMinTemperature();
            String text;
            if (isMetric) {
                text = String.format("%.1f ... %.1f °C", dailyMin, dailyMax);
            } else {
                text = String.format("%.1f ... %.1f F", Weather.fromCelsiusToFarenheit(dailyMin),
                        Weather.fromCelsiusToFarenheit(dailyMax));
            }
            Label dailyTemperature = (Label) weatherPanel.getChildren().get(2);
            dailyTemperature.setText(text);
        }

        // Update hourly weather
        for (int i = 0; i < HOURLY_FORECAST_LENGHT; i++) {
            Label temp = (Label) scene.lookup("#temperature" + i);
            Label wind = (Label) scene.lookup("#wind" + i);
            Label rain = (Label) scene.lookup("#rain" + i);
            Label pressure = (Label) scene.lookup("#pressure" + i);
            Label humidity = (Label) scene.lookup("#humidity" + i);

            humidity.setText(String.format("%d %%", hourlyWeather.get(i).getHumidityPercent()));
            if (isMetric) {
                temp.setText(String.format("%.1f°C", hourlyWeather.get(i).getTemperature()));
                wind.setText(String.format("%.1f m/s", hourlyWeather.get(i).getWindSpeed()));
                rain.setText(String.format("%.2f mm", hourlyWeather.get(i).getRainAmount()));
                pressure.setText(String.format("%d Pa", hourlyWeather.get(i).getAirPressure()));
            } else {
                double celcius = hourlyWeather.get(i).getTemperature();
                temp.setText(String.format("%.1f F", Weather.fromCelsiusToFarenheit(celcius)));
                wind.setText(String.format("%.1f mph", Weather.fromMPSoMPH(hourlyWeather.get(i).getWindSpeed())));
                rain.setText(
                        String.format("%.2f in", Weather.fromMilliMeterToInch(hourlyWeather.get(i).getRainAmount())));
                pressure.setText(String.format("%.1f inHg",
                        Weather.fromHectoPascalsToINHG(hourlyWeather.get(i).getAirPressure())));
            }
        }
        updateComboBox();
        updateFavourite();
    }

    /**
     * Updates the favourite button status
     */
    private void updateFavourite() {
        Location currentLoc = favourites.getCurrentLocation();
        Button favouriteButton = (Button) scene.lookup("#favouriteButton");
        if (!favourites.checkIfNameExists(currentLoc.getName())) {
            favouriteButton.setText("Add Favourite");
        } else {
            favouriteButton.setText("Remove Favourite");
        }
    }

    /**
     * Updates the combo box values
     */
    private void updateComboBox() {
        ComboBox<String> comboBox = (ComboBox<String>) scene.lookup("#favouriteList");
        ObservableList<String> favouriteNames = FXCollections.observableArrayList();
        for (Location i : favourites.getFavourites()) {
            String name = i.getName();
            favouriteNames.add(name);
        }
        comboBox.setItems(favouriteNames);
        comboBox.setPromptText("Favourites");
    }

    @Override
    public void start(Stage stage) {
        // Show temperature using Celsius on startup
        List<String> searchHistory = initialize();
        isMetric = true;

        // MainGrid containing multiple subgrids
        stage.setTitle("WeatherApp");
        GridPane mainGrid = new GridPane();
        mainGrid.setMinWidth(WINDOW_WIDTH);
        scene = new Scene(mainGrid, WINDOW_WIDTH, 550);
        stage.setScene(scene);

        // Top row for current location and search
        GridPane infoGrid = new GridPane();
        infoGrid.setMinWidth(WINDOW_WIDTH);
        Button searchButton = new Button("Search");
        searchButton.setMinWidth(55);
        Label currentLocation = new Label(favourites.getCurrentLocation().getName());
        currentLocation.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        currentLocation.setId("currentLocation");
        currentLocation.setWrapText(true);

        // Update button with correct text
        String text;
        if (favourites.checkIfNameExists(favourites.getCurrentLocation().getName())) {
            text = "Remove Favourite";
        } else {
            text = "Add Favourite";
        }
        Button favouriteButton = new Button(text);
        favouriteButton.setId("favouriteButton");
        favouriteButton.setMinWidth(115);

        TextField searchBar = new TextField("");
        searchBar.setId("searchBar");

        infoGrid.add(favouriteButton, 0, 0);
        infoGrid.add(currentLocation, 1, 0);
        infoGrid.add(searchBar, 2, 0);
        infoGrid.add(searchButton, 3, 0);

        GridPane.setMargin(favouriteButton, new Insets(5, 100, 5, 5));
        GridPane.setMargin(currentLocation, new Insets(5, 100, 5, 0));
        GridPane.setMargin(searchButton, new Insets(5, 5, 5, 5));

        mainGrid.add(infoGrid, 0, 0);

        // Main Weather Box
        GridPane mainWeather = new GridPane();
        mainWeather.setPrefHeight(180);
        mainWeather.setMinWidth(WINDOW_WIDTH);
        mainWeather.setStyle("-fx-background-color: #BDB76B;");

        Label temperature = new Label(String.format("%.1f °C", hourlyWeather.get(0).getTemperature()));
        temperature.setFont(Font.font("Arial", FontWeight.BOLD, 50));
        temperature.setId("temperature");

        Label humidityLabel = new Label("Humidity:");
        Label humidity = new Label(String.format("%d %%", hourlyWeather.get(0).getHumidityPercent()));
        humidityLabel.setFont(Font.font("Arial", 14));
        humidity.setFont(Font.font("Arial", 14));
        humidity.setId("humidity");

        Label airPressure = new Label("Air Pressure:");
        Label airPressureInt = new Label(String.format("%d Pa", hourlyWeather.get(0).getAirPressure()));
        airPressure.setFont(Font.font("Arial", 14));
        airPressureInt.setFont(Font.font("Arial", 14));
        airPressureInt.setId("airPressure");

        Label rainAmount = new Label("Rain Amount:");
        Label rainAmountDouble = new Label(String.format("%.1f mm", hourlyWeather.get(0).getRainAmount()));
        rainAmount.setFont(Font.font("Arial", 14));
        rainAmountDouble.setFont(Font.font("Arial", 14));
        rainAmountDouble.setId("rainAmount");

        mainWeather.add(temperature, 5, 1);

        mainWeather.add(humidityLabel, 0, 3);
        mainWeather.add(humidity, 1, 3);

        mainWeather.add(airPressure, 0, 4);
        mainWeather.add(airPressureInt, 1, 4);

        mainWeather.add(rainAmount, 0, 5);
        mainWeather.add(rainAmountDouble, 1, 5);

        int hour = hourlyWeather.get(0).getTime().getHour();

        BorderPane imagePane = new BorderPane();
        imagePane.setId("mainImagePane");
        InputStream mainImageInputStream = getClass()
                .getResourceAsStream("/images/" + selectCorrectImage(hourlyWeather.get(0), isDay(hour)));
        ImageView mainImage = new ImageView(new Image(mainImageInputStream, 100, 100, true, false));
        imagePane.setCenter(mainImage);
        mainWeather.add(imagePane, 4, 2, 1, 4);

        GridPane.setMargin(humidityLabel, new Insets(5, 5, 5, 100));
        GridPane.setMargin(airPressure, new Insets(5, 5, 5, 100));
        GridPane.setMargin(rainAmount, new Insets(5, 5, 5, 100));

        mainGrid.add(mainWeather, 0, 1);

        // Row for button and ComboBoxes
        GridPane grid3 = new GridPane();
        ComboBox<String> historyComboBox = new ComboBox<>();
        ObservableList<String> history = FXCollections.observableArrayList();
        history.addAll(searchHistory);

        historyComboBox.setEditable(false);
        historyComboBox.setPromptText("History");
        historyComboBox.setId("history");
        historyComboBox.setItems(history);
        Button clear = new Button("Clear History");

        Button unitChange = new Button("Imperial");
        unitChange.setMinWidth(100);
        unitChange.setMaxWidth(100);

        // Create ComboBox for displaying favourites
        ObservableList<String> favouriteNames = FXCollections.observableArrayList();
        for (Location i : favourites.getFavourites()) {
            String name = i.getName();
            favouriteNames.add(name);
        }
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setItems(favouriteNames);
        comboBox.setPromptText("Favourites");
        comboBox.setId("favouriteList");

        GridPane.setMargin(historyComboBox, new Insets(5, 5, 5, 5));
        GridPane.setMargin(unitChange, new Insets(5, 5, 5, 5));

        grid3.add(unitChange, 0, 0);
        grid3.add(comboBox, 1, 0);
        grid3.add(historyComboBox, 2, 0);
        grid3.add(clear, 3, 0);
        mainGrid.add(grid3, 0, 2);

        // Weather for the following N days
        GridPane sevenDayWeather = new GridPane();
        sevenDayWeather.setPrefWidth(WINDOW_WIDTH);
        for (int i = 0; i < DAILY_FORECAST_LENGTH; i++) {
            GridPane weatherPanel = new GridPane();
            weatherPanel.setPadding(new Insets(5, 2, 5, 2));
            weatherPanel.setId("weatherPanel" + i);
            weatherPanel.setMinWidth(WINDOW_WIDTH / DAILY_FORECAST_LENGTH);

            // If this isn't last element
            if (i != HOURLY_FORECAST_LENGHT - 1) {
                weatherPanel.setStyle("-fx-border-color: black; -fx-border-width: 0 1 0 0px;");
            }
            LocalDateTime currentDate = hourlyWeather.get(0).getTime().plusDays(i);
            String weekday = currentDate.getDayOfWeek().toString();

            Label date = new Label(weekday);
            date.setMinWidth(WINDOW_WIDTH / DAILY_FORECAST_LENGTH);

            // Weather icon
            InputStream inputStream = getClass()
                    .getResourceAsStream("/images/" + selectCorrectImage(dailyWeather.get(i), true));
            ImageView image = new ImageView(new Image(inputStream));
            BorderPane imageBorder = new BorderPane();
            imageBorder.setCenter(image);

            double dailyMax = dailyWeather.get(i).getMaxTemperature();
            double dailyMin = dailyWeather.get(i).getMinTemperature();
            Label sevenDayTempereture = new Label(String.format("%.1f ... %.1f °C", dailyMin, dailyMax));

            weatherPanel.add(date, 0, 0);
            weatherPanel.add(imageBorder, 0, 1);
            weatherPanel.add(sevenDayTempereture, 0, 2);
            sevenDayWeather.add(weatherPanel, i, 0);

            GridPane.setHalignment((Node) date, HPos.CENTER);
        }
        mainGrid.add(sevenDayWeather, 0, 3);

        FlowPane hourlyWeatherGrid = new FlowPane();
        hourlyWeatherGrid.setHgap(5);
        hourlyWeatherGrid.setPadding(new Insets(5, 5, 5, 5));

        LocalDateTime currentTime = hourlyWeather.get(0).getTime();
        for (int i = 0; i < HOURLY_FORECAST_LENGHT; i++) {
            GridPane subGrid = new GridPane();
            subGrid.setMinWidth(52);
            // If this isn't last element
            if (i != HOURLY_FORECAST_LENGHT - 1) {
                subGrid.setStyle("-fx-border-color: black; -fx-border-width: 0 1 0 0px;");
            }
            LocalDateTime currentHour = currentTime.plusHours(i);

            Label time = new Label(String.format("%02d", currentHour.getHour()));
            Label temp = new Label(String.format("%.1f°C", hourlyWeather.get(i).getTemperature()));
            Label wind = new Label(String.format("%.1f m/s", hourlyWeather.get(i).getWindSpeed()));
            Label rain = new Label(String.format("%.1f mm", hourlyWeather.get(i).getRainAmount()));
            Label pressure = new Label(String.format("%d Pa", hourlyWeather.get(i).getAirPressure()));
            Label humid = new Label(String.format("%d %%", hourlyWeather.get(i).getHumidityPercent()));

            time.setId("time" + i);
            temp.setId("temperature" + i);
            wind.setId("wind" + i);
            rain.setId("rain" + i);
            pressure.setId("pressure" + i);
            humid.setId("humidity" + i);

            subGrid.add(time, 0, 0);
            subGrid.add(temp, 0, 1);
            subGrid.add(wind, 0, 2);
            subGrid.add(rain, 0, 3);
            subGrid.add(pressure, 0, 4);
            subGrid.add(humid, 0, 5);

            hourlyWeatherGrid.getChildren().add(subGrid);
        }
        mainGrid.add(hourlyWeatherGrid, 0, 5);

        // Search location by name
        searchButton.setOnAction(event -> {
            String searchedLocation = searchBar.getText();
            if (searchedLocation.isEmpty()) {
                return;
            }
            if (!history.contains(searchedLocation)) {
                history.add(searchedLocation);
                historyComboBox.setItems(history);
            }
            if (!searchLocation(searchedLocation)) {
                return;
            }
            updateGUIValues();
        });

        // Display location from the favourites tab
        comboBox.setOnAction(event -> {
            String selectedFavourite = comboBox.getSelectionModel().getSelectedItem();
            if (selectedFavourite != null) {
                for (Location i : favourites.getFavourites()) {
                    if (i.getName().equals(selectedFavourite)) {
                        favourites.setCurrentLocation(i);
                        try {
                            updateWeather();
                        } catch (WeatherAppException e) {
                            System.err.println("Failed to fetch data from the server: " + e);
                        }
                        updateGUIValues();
                        break;
                    }
                }
            }
        });

        // Display location from history if one is found
        historyComboBox.setOnAction(event -> {
            try {
                String selectedLocation = historyComboBox.getSelectionModel().getSelectedItem();
                List<Location> locations = api.lookUpLocation(selectedLocation);
                if (!locations.isEmpty()) {
                    favourites.setCurrentLocation(locations.get(0));
                    updateWeather();
                    updateGUIValues();
                }
            } catch (WeatherAppException e) {
                e.printStackTrace();
            }
        });

        // Change shown units
        unitChange.setOnAction(event -> {
            if (isMetric) {
                unitChange.setText("Metric");
                isMetric = false;
            } else {
                unitChange.setText("Imperial");
                isMetric = true;
            }
            updateGUIValues();
        });

        // Add or delete favourites
        favouriteButton.setOnAction(event -> {
            Location currentLoc = favourites.getCurrentLocation();
            if (favourites.checkIfNameExists(currentLoc.getName())) {
                favourites.removeByName(currentLoc.getName());
            } else {
                favourites.addFavourite(favourites.getCurrentLocation());
            }
            updateFavourite();
            updateComboBox();
        });

        // Clear history
        clear.setOnAction(event -> {
            historyComboBox.getItems().clear();
        });

        stage.setResizable(true);
        stage.show();
    }

    @Override
    public void stop() {
        // Write locations to file
        try {
            favourites.writeToFile(LOCATION_FILE_NAME);
        } catch (WeatherAppException e) {
            System.err.println(String.format("Failed to write to disk: %s", e));
        }

        // Write search history to file
        ComboBox<String> history = (ComboBox<String>) scene.lookup("#history");
        List<String> values = history.getItems();

        Gson gson = new Gson();
        String json = gson.toJson(values);
        try (FileWriter fileWriter = new FileWriter(SEARCH_HISTORY_FILE_NAME)) {
            fileWriter.write(json);
        } catch (IOException e) {
            System.err.println("Writing to file failed: " + e);
        }
    }

    public static void main(String[] args) {
        launch();
    }
}