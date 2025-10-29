package CW3;

import java.io.IOException;
import java.util.*;

import CW3.Attraction;
import CW3.City;
import CW3.DataLoader;
import sorting.SortingAlgorithms;

public class Main {
    private static Map<String, City> cityMap = new HashMap<>();
    private static Map<String, Attraction> attractionMap = new HashMap<>();
    private static Map<City, List<Road>> roadMap;
    private static List<Attraction> attractions;
    private static RoutePlanner routePlanner;

    // Static variable storing the result of a calculation for use by JavaFX
    private static List<City> calculatedRoute;
    private static List<Attraction> selectedAttractions;
    private static int calculatedDistance;

    public static void main(String[] args) {
        try {
            loadData();

            processConsoleInput();

            // If the route has been calculated, start the visualization
            if (calculatedRoute != null && !calculatedRoute.isEmpty()) {
                RoutePlannerApp.launchApp(args);
            }

        } catch (IOException e) {
            System.err.println("Error loading data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void loadData() throws IOException {
        DataLoader dataLoader = new DataLoader();

        dataLoader.loadAttractions("attractions.csv");
        dataLoader.loadRoads("roads.csv");

        attractions = dataLoader.getAttractions();
        roadMap = dataLoader.getRoadMap();
        cityMap = dataLoader.getCityMap();


        for (Attraction attraction : attractions) {// Create an attraction mapping table for easy searching
            attractionMap.put(attraction.getName().toLowerCase(), attraction);
        }


        routePlanner = new RoutePlanner(roadMap);
    }

    private static void processConsoleInput() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter 'exit' at any time to quit");

        // Start city
        System.out.print("Enter starting city (e.g., New York NY): ");
        String startInput = scanner.nextLine().trim();
        if (startInput.equalsIgnoreCase("exit")) {
            scanner.close();
            return;
        }

        City startCity = findCity(startInput);
        if (startCity == null) {
            System.out.println("CW3.City not found. Please try again.");
            scanner.close();
            return;
        }

        System.out.print("Enter destination city (e.g., Chicago IL): ");
        String endInput = scanner.nextLine().trim();
        if (endInput.equalsIgnoreCase("exit")) {
            scanner.close();
            return;
        }

        City endCity = findCity(endInput);
        if (endCity == null) {
            System.out.println("CW3.City not found. Please try again.");
            scanner.close();
            return;
        }

        // Attraction
        System.out.print("Enter attractions (comma-separated, e.g., Hollywood Sign, Liberty Bell): ");
        String attractionsInput = scanner.nextLine().trim();
        if (attractionsInput.equalsIgnoreCase("exit")) {
            scanner.close();
            return;
        }

        selectedAttractions = new ArrayList<>();
        if (!attractionsInput.isEmpty()) {
            String[] attractionNames = attractionsInput.split(",");
            for (String name : attractionNames) {
                String trimmedName = name.trim().toLowerCase();
                Attraction attraction = findAttraction(trimmedName);
                if (attraction == null) {
                    System.out.println("Warning: CW3.Attraction '" + name.trim() + "' not found, skipping.");
                } else {
                    selectedAttractions.add(attraction);
                }
            }
        }

        // Find shortest path
        try {
            calculatedRoute = routePlanner.findOptimalRoute(startCity, endCity, selectedAttractions);
            calculatedDistance = routePlanner.calculatePathWeight(calculatedRoute);


            System.out.println("\nResult:");
            System.out.println("Start: " + startCity);
            System.out.println("Destination: " + endCity);
            System.out.println("Attractions: " + selectedAttractions);
            System.out.print("Optimal Route: [");
            for (int i = 0; i < calculatedRoute.size(); i++) {
                System.out.print(calculatedRoute.get(i));
                if (i < calculatedRoute.size() - 1) {
                    System.out.print(", ");
                }
            }
            System.out.println("]");
            System.out.println("Total Distance: " + calculatedDistance + " miles");
            System.out.println();
            compareAllAlgorithms(startCity, endCity, selectedAttractions, routePlanner);

            // Task C
            runSortingTests();
        } catch (Exception e) {
            System.out.println("Error calculating route: " + e.getMessage());
            e.printStackTrace();
        }

        scanner.close();
    }

    public static void compareAllAlgorithms(City start, City end, List<Attraction> attractions, RoutePlanner planner) {

        // Greedy + Dijkstra
        long t1 = System.nanoTime();
        List<City> greedyRoute = planner.findOptimalRoute(start, end, attractions);
        long t2 = System.nanoTime();
        int d1 = planner.calculatePathWeight(greedyRoute);
        System.out.println("[Greedy + Dijkstra] Distance: " + d1 + " | Time: " + (t2 - t1) / 1_000_000 + " ms");

        // TSP Brute-force - skip if attractions > 6
        if (attractions.size() <= 6) {
            long t3 = System.nanoTime();
            List<City> tspRoute = planner.findTSPOptimalRoute(start, end, attractions);
            long t4 = System.nanoTime();
            int d2 = planner.calculatePathWeight(tspRoute);
            System.out.println("[TSP Brute-force] Distance: " + d2 + " | Time: " + (t4 - t3) / 1_000_000 + " ms");
        } else {
            System.out.println("[TSP Brute-force] Skipped (too many attractions: " + attractions.size() + ")");
        }


        // Held-Karp Dynamic Programming
        long t5 = System.nanoTime();
        List<City> heldRoute = planner.findOptimalRouteHeldKarp(start, end, attractions);
        long t6 = System.nanoTime();
        int d3 = planner.calculatePathWeight(heldRoute);
        System.out.println("[Held-Karp DP] Distance: " + d3 + " | Time: " + (t6 - t5) / 1_000_000 + " ms");

        // A*
        if (attractions == null || attractions.isEmpty()) {
            long t7 = System.nanoTime();
            List<City> astarRoute = planner.findShortestPathAStar(start, end);
            long t8 = System.nanoTime();
            int d4 = planner.calculatePathWeight(astarRoute);
            System.out.println("[A*] Distance: " + d4 + " | Time: " + (t8 - t7) / 1_000_000 + " ms");
        }

    }


    private static void runSortingTests() {

        String[] datasets = {
                "1000places_sorted.csv",
                "1000places_random.csv",
                "10000places_sorted.csv",
                "10000places_random.csv"
        };

        try {

            for (String dataset : datasets) {
                System.out.println("\n" + dataset + ":");

                Map<String, Long> results = SortingAlgorithms.testDataset(dataset);


                System.out.println("  Insertion Sort: " + SortingAlgorithms.formatTime(results.get("Insertion")) + " ms");
                System.out.println("  Quick Sort: " + SortingAlgorithms.formatTime(results.get("Quick")) + " ms");
                System.out.println("  Merge Sort: " + SortingAlgorithms.formatTime(results.get("Merge")) + " ms");
            }

        } catch (IOException e) {
            System.err.println("Error running sorting tests: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static City findCity(String input) {

        City city = cityMap.get(input.toLowerCase());
        if (city != null) {
            return city;
        }

        // not distinguishing capitals from lower case letters
        for (String key : cityMap.keySet()) {
            if (key.equalsIgnoreCase(input)) {
                return cityMap.get(key);
            }
        }


        String[] parts = input.trim().split(" ");
        if (parts.length >= 2) {
            // The last part is the state and the rest are city names
            String state = parts[parts.length - 1];
            StringBuilder cityName = new StringBuilder();

            for (int i = 0; i < parts.length - 1; i++) {
                cityName.append(parts[i]);
                if (i < parts.length - 2) {
                    cityName.append(" ");
                }
            }

            String searchKey = cityName + " " + state;


            city = cityMap.get(searchKey.toLowerCase());
            if (city != null) {
                return city;
            }


            for (String key : cityMap.keySet()) {
                if (key.equalsIgnoreCase(searchKey)) {
                    return cityMap.get(key);
                }
            }
        }

        return null;
    }

    private static Attraction findAttraction(String name) {
        // Direct match (exact match)
        Attraction attraction = attractionMap.get(name);
        if (attraction != null) {
            return attraction;
        }

        // Try a partial match
        for (Attraction a : attractions) {
            if (a.getName().toLowerCase().contains(name) ||
                    name.contains(a.getName().toLowerCase())) {
                return a;
            }
        }

        return null;
    }

    public static List<City> getCalculatedRoute() {
        return calculatedRoute;
    }

    public static List<Attraction> getSelectedAttractions() {
        return selectedAttractions;
    }

    public static int getCalculatedDistance() {
        return calculatedDistance;
    }

    public static Map<String, City> getCityMap() {
        return cityMap;
    }
}