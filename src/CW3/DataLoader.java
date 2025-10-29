package CW3;

import CW3.Attraction;
import CW3.City;
import CW3.DataLoadable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class DataLoader implements DataLoadable {
    private List<Attraction> attractions = new ArrayList<>();
    private Map<City, List<Road>> roadMap = new HashMap<>();
    private Map<String, City> cityMap = new HashMap<>();

    @Override
    public void loadData(String filePath) throws IOException {
        if (filePath.endsWith("attractions.csv")) {
            loadAttractions(filePath);
        } else if (filePath.endsWith("roads.csv")) {
            loadRoads(filePath);
        } else {
            throw new IllegalArgumentException("Unsupported file type: " + filePath);
        }
    }
    //read the data
    public void loadAttractions(String filePath) throws IOException {
        attractions.clear();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            // skip first
            String header = reader.readLine();
            if (header == null) {
                throw new IOException("Empty file: " + filePath);
            }

            String line;
            while ((line = reader.readLine()) != null) {
                // Handling commas
                int lastCommaIndex = line.lastIndexOf(',');
                if (lastCommaIndex == -1) {
                    System.err.println("Invalid line format: " + line);
                    continue;
                }

                String attractionName = line.substring(0, lastCommaIndex).trim();
                String locationStr = line.substring(lastCommaIndex + 1).trim();

                // state
                String[] locationParts = locationStr.split(" ");
                if (locationParts.length < 2) {
                    System.err.println("Invalid location format: " + locationStr);
                    continue;
                }

                // state code
                String stateCode = locationParts[locationParts.length - 1];

                // city
                StringBuilder cityName = new StringBuilder();
                for (int i = 0; i < locationParts.length - 1; i++) {
                    cityName.append(locationParts[i]);
                    if (i < locationParts.length - 2) {
                        cityName.append(" ");
                    }
                }


                City city = new City(cityName.toString(), stateCode);

                // lower case for city
                String cityKey = city.toString().toLowerCase();
                cityMap.put(cityKey, city);

                attractions.add(new Attraction(attractionName, city));
            }
        }
    }


    public void loadRoads(String filePath) throws IOException {
        roadMap.clear();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {

            String header = reader.readLine();
            if (header == null) {
                throw new IOException("Empty file: " + filePath);
            }

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length != 3) {
                    System.err.println("Invalid line format: " + line);
                    continue;
                }

                String cityAStr = parts[0].trim();
                String cityBStr = parts[1].trim();
                int distance;

                try {
                    distance = Integer.parseInt(parts[2].trim());
                } catch (NumberFormatException e) {
                    System.err.println("Invalid distance: " + parts[2]);
                    continue;
                }

                // City A
                String[] cityAParts = cityAStr.split(" ");
                if (cityAParts.length < 2) {
                    System.err.println("Invalid city format: " + cityAStr);
                    continue;
                }

                String stateCodeA = cityAParts[cityAParts.length - 1];
                StringBuilder cityNameA = new StringBuilder();
                for (int i = 0; i < cityAParts.length - 1; i++) {
                    cityNameA.append(cityAParts[i]);
                    if (i < cityAParts.length - 2) {
                        cityNameA.append(" ");
                    }
                }

                // City B
                String[] cityBParts = cityBStr.split(" ");
                if (cityBParts.length < 2) {
                    System.err.println("Invalid city format: " + cityBStr);
                    continue;
                }

                String stateCodeB = cityBParts[cityBParts.length - 1];
                StringBuilder cityNameB = new StringBuilder();
                for (int i = 0; i < cityBParts.length - 1; i++) {
                    cityNameB.append(cityBParts[i]);
                    if (i < cityBParts.length - 2) {
                        cityNameB.append(" ");
                    }
                }


                City cityA = new City(cityNameA.toString(), stateCodeA);
                City cityB = new City(cityNameB.toString(), stateCodeB);

                // Add cities to the mapping table
                String cityKeyA = cityA.toString().toLowerCase();
                String cityKeyB = cityB.toString().toLowerCase();

                cityMap.put(cityKeyA, cityA);
                cityMap.put(cityKeyB, cityB);



                Road road = new Road(cityA, cityB, distance);

                // ADD path to graph
                roadMap.computeIfAbsent(cityA, k -> new ArrayList<>()).add(road);
                roadMap.computeIfAbsent(cityB, k -> new ArrayList<>()).add(road);
            }
        }
    }


    public List<Attraction> getAttractions() {
        return new ArrayList<>(attractions);
    }


    public Map<City, List<Road>> getRoadMap() {
        return new HashMap<>(roadMap);
    }


    public Map<String, City> getCityMap() {
        return new HashMap<>(cityMap);
    }
}