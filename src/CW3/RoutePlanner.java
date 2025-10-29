package CW3;

import CW3.Attraction;
import CW3.City;
import CW3.GraphTraversal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;
import java.util.Comparator;

public class RoutePlanner implements GraphTraversal<City> {
    private Map<City, List<Road>> graph;

    public RoutePlanner(Map<City, List<Road>> graph) {
        this.graph = graph;
    }

    public List<City> findOptimalRoute(City start, City end, List<Attraction> attractions) {
        // no attraction, use shortest path
        if (attractions == null || attractions.isEmpty()) {
            return findShortestPath(start, end);
        }

        List<Attraction> validAttractions = new ArrayList<>();
        for (Attraction attraction : attractions) {
            if (attraction != null) {
                validAttractions.add(attraction);
            }
        }

        // If there are no valid attractions, use the shortest path directly
        if (validAttractions.isEmpty()) {
            return findShortestPath(start, end);
        }

        Set<City> uniqueAttractionCities = new HashSet<>();
        for (Attraction attraction : validAttractions) {
            uniqueAttractionCities.add(attraction.getCity());
        }
        List<City> attractionCities = new ArrayList<>(uniqueAttractionCities);

        attractionCities.removeIf(city -> city.equals(start) || city.equals(end));

        if (attractionCities.isEmpty()) {
            return findShortestPath(start, end);
        }

        // one attraction
        if (attractionCities.size() == 1) {
            City attractionCity = attractionCities.get(0);

            List<City> path1 = findShortestPath(start, attractionCity);
            List<City> path2 = findShortestPath(attractionCity, end);

            List<City> completePath = new ArrayList<>(path1.subList(0, path1.size() - 1));
            completePath.addAll(path2);

            return completePath;
        }

        // two, compare
        if (attractionCities.size() == 2) {
            City attraction1 = attractionCities.get(0);
            City attraction2 = attractionCities.get(1);

            // shortest path
            List<City> startToAttr1 = findShortestPath(start, attraction1);
            List<City> attr1ToAttr2 = findShortestPath(attraction1, attraction2);
            List<City> attr2ToEnd = findShortestPath(attraction2, end);

            List<City> startToAttr2 = findShortestPath(start, attraction2);
            List<City> attr2ToAttr1 = findShortestPath(attraction2, attraction1);
            List<City> attr1ToEnd = findShortestPath(attraction1, end);

            // 1: start -> attraction1 -> attraction2 -> end
            List<City> order1 = new ArrayList<>();
            order1.addAll(startToAttr1.subList(0, startToAttr1.size() - 1)); // Excluding the last city to avoid duplication
            order1.addAll(attr1ToAttr2.subList(0, attr1ToAttr2.size() - 1));
            order1.addAll(attr2ToEnd);

            // 2: start -> attraction2 -> attraction1 -> end
            List<City> order2 = new ArrayList<>();
            order2.addAll(startToAttr2.subList(0, startToAttr2.size() - 1)); // Excluding the last city to avoid duplication
            order2.addAll(attr2ToAttr1.subList(0, attr2ToAttr1.size() - 1));
            order2.addAll(attr1ToEnd);

            int distance1 = calculatePathWeight(order1);
            int distance2 = calculatePathWeight(order2);

            return distance1 <= distance2 ? order1 : order2;
        }

        // For more attractions, use the greedy algorithm

        List<City> allCities = new ArrayList<>();
        allCities.add(start);
        allCities.addAll(attractionCities);
        allCities.add(end);

        int[][] distanceMatrix = buildDistanceMatrix(allCities);

        List<City> optimalOrder = new ArrayList<>();
        optimalOrder.add(start);

        City currentCity = start;

        List<City> remainingCities = new ArrayList<>(attractionCities);

        while (!remainingCities.isEmpty()) {
            // Find the closest city to your current city
            int nearestIndex = -1;
            int shortestDistance = Integer.MAX_VALUE;

            for (int i = 0; i < remainingCities.size(); i++) {
                City nextCity = remainingCities.get(i);

                // Find the index of two cities in allCities
                int currentIndex = allCities.indexOf(currentCity);
                int nextIndex = allCities.indexOf(nextCity);

                if (distanceMatrix[currentIndex][nextIndex] < shortestDistance) {
                    shortestDistance = distanceMatrix[currentIndex][nextIndex];
                    nearestIndex = i;
                }
            }

            City nextCity = remainingCities.get(nearestIndex);
            optimalOrder.add(nextCity);

            currentCity = nextCity;

            remainingCities.remove(nearestIndex);
        }

        if (!currentCity.equals(end)) {
            optimalOrder.add(end);
        }

        // Expand the path between cities to include all intermediate cities
        List<City> completePath = new ArrayList<>();
        for (int i = 0; i < optimalOrder.size() - 1; i++) {
            City from = optimalOrder.get(i);
            City to = optimalOrder.get(i + 1);

            List<City> segment = findShortestPath(from, to);

            // Add all cities except the last one (to avoid duplicates)
            if (!segment.isEmpty()) {
                completePath.addAll(segment.subList(0, segment.size() - 1));
            }
        }

        completePath.add(end);

        return completePath;
    }

    private int[][] buildDistanceMatrix(List<City> cities) {
        int n = cities.size();
        int[][] distances = new int[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                distances[i][j] = Integer.MAX_VALUE;
            }
            // Distance to self is 0
            distances[i][i] = 0;
        }

        // Calculate the shortest path between all city pairs
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                City from = cities.get(i);
                City to = cities.get(j);

                // If it is the same city, the distance is 0
                if (from.equals(to)) {
                    distances[i][j] = 0;
                    distances[j][i] = 0;
                    continue;
                }

                try {
                    List<City> path = findShortestPath(from, to);
                    int distance = calculatePathWeight(path);

                    distances[i][j] = distance;
                    distances[j][i] = distance;  // The matrix is symmetric
                } catch (Exception e) {
                    System.err.println("Warning: No path found between " + from + " and " + to);
                }
            }
        }

        return distances;
    }

    @Override
    public List<City> findShortestPath(City start, City end) {
        // If the start and end points are the same, the list containing the city is returned.
        if (start.equals(end)) {
            List<City> sameCity = new ArrayList<>();
            sameCity.add(start);
            return sameCity;
        }

        Map<City, Integer> distances = new HashMap<>();

        Map<City, City> previous = new HashMap<>();

        Set<City> visited = new HashSet<>();

        PriorityQueue<City> queue = new PriorityQueue<>(
                (c1, c2) -> Integer.compare(
                        distances.getOrDefault(c1, Integer.MAX_VALUE),
                        distances.getOrDefault(c2, Integer.MAX_VALUE)
                )
        );

        for (City city : graph.keySet()) {
            distances.put(city, Integer.MAX_VALUE);
        }
        distances.put(start, 0);

        queue.add(start);

        while (!queue.isEmpty()) {
            City current = queue.poll();

            if (current.equals(end)) {
                break;
            }

            if (visited.contains(current)) {
                continue;
            }

            visited.add(current);

            List<Road> roads = graph.getOrDefault(current, new ArrayList<>());

            for (Road road : roads) {
                City neighbor = road.getOtherNode(current);

                if (visited.contains(neighbor)) {
                    continue;
                }

                int newDistance = distances.get(current) + road.getWeight();
                // if is shorter update
                if (newDistance < distances.getOrDefault(neighbor, Integer.MAX_VALUE)) {
                    distances.put(neighbor, newDistance);
                    previous.put(neighbor, current);


                    queue.add(neighbor);
                }
            }
        }

        List<City> path = new ArrayList<>();
        City current = end;

        if (!previous.containsKey(end) && !start.equals(end)) {
            return path;
        }

        while (current != null) {
            path.add(current);
            current = previous.get(current);
        }

        Collections.reverse(path);

        return path;
    }

    //A* Shortest Path
    public List<City> findShortestPathAStar(City start, City goal) {
        Map<City, Integer> gScore = new HashMap<>();
        Map<City, Integer> fScore = new HashMap<>();
        Map<City, City> cameFrom = new HashMap<>();

        Comparator<City> comparator = Comparator.comparingInt(c -> fScore.getOrDefault(c, Integer.MAX_VALUE));
        PriorityQueue<City> openSet = new PriorityQueue<>(comparator);



        for (City city : graph.keySet()) {
            gScore.put(city, Integer.MAX_VALUE);
            fScore.put(city, Integer.MAX_VALUE);
        }

        gScore.put(start, 0);
        fScore.put(start, heuristic(start, goal));
        openSet.add(start);

        while (!openSet.isEmpty()) {
            City current = openSet.poll();

            if (current.equals(goal)) {
                return reconstructPath(cameFrom, current);
            }

            for (Road road : graph.getOrDefault(current, new ArrayList<>())) {
                City neighbor = road.getOtherNode(current);
                int tentative = gScore.get(current) + road.getWeight();
                if (tentative < gScore.getOrDefault(neighbor, Integer.MAX_VALUE)) {
                    cameFrom.put(neighbor, current);
                    gScore.put(neighbor, tentative);
                    fScore.put(neighbor, tentative + heuristic(neighbor, goal));
                    openSet.add(neighbor);
                }
            }
        }

        return new ArrayList<>();
    }

    public List<City> findTSPOptimalRoute(City start, City end, List<Attraction> attractions) {
        List<City> nodes = new ArrayList<>();
        for (Attraction a : attractions) {
            City c = a.getCity();
            if (!nodes.contains(c) && !c.equals(start) && !c.equals(end)) {
                nodes.add(c);
            }
        }

        List<City> allCities = new ArrayList<>();
        allCities.add(start);
        allCities.addAll(nodes);
        allCities.add(end);

        List<List<City>> permutations = new ArrayList<>();
        permute(nodes, 0, permutations);

        int minDistance = Integer.MAX_VALUE;
        List<City> bestPath = new ArrayList<>();

        for (List<City> perm : permutations) {
            List<City> candidate = new ArrayList<>();
            candidate.add(start);
            candidate.addAll(perm);
            candidate.add(end);

            List<City> fullPath = new ArrayList<>();
            boolean valid = true;
            for (int i = 0; i < candidate.size() - 1; i++) {
                List<City> segment = findShortestPath(candidate.get(i), candidate.get(i + 1));
                if (segment.isEmpty()) {
                    valid = false;
                    break;
                }
                fullPath.addAll(segment.subList(0, segment.size() - 1));
            }
            fullPath.add(end);

            if (valid) {
                int distance = calculatePathWeight(fullPath);
                if (distance < minDistance) {
                    minDistance = distance;
                    bestPath = fullPath;
                }
            }
        }

        return bestPath;
    }

    private void permute(List<City> list, int l, List<List<City>> result) {
        if (l == list.size()) {
            result.add(new ArrayList<>(list));
            return;
        }
        for (int i = l; i < list.size(); i++) {
            Collections.swap(list, i, l);
            permute(list, l + 1, result);
            Collections.swap(list, i, l);
        }
    }

    private int heuristic(City a, City b) {
        return Math.abs(a.getName().hashCode() - b.getName().hashCode()) % 100;
    }

    private List<City> reconstructPath(Map<City, City> cameFrom, City current) {
        List<City> path = new ArrayList<>();
        while (current != null) {
            path.add(current);
            current = cameFrom.get(current);
        }
        Collections.reverse(path);
        return path;
    }



    public List<City> findOptimalRouteHeldKarp(City start, City end, List<Attraction> attractions) {
        if (attractions == null || attractions.isEmpty()) {

            return findShortestPath(start, end);
        }
        List<City> nodes = new ArrayList<>();
        for (Attraction a : attractions) {
            City c = a.getCity();
            if (!nodes.contains(c) && !c.equals(start) && !c.equals(end)) {
                nodes.add(c);
            }
        }

        int n = nodes.size();
        Map<String, Integer> memo = new HashMap<>();
        Map<String, City> parent = new HashMap<>();

        List<City> allCities = new ArrayList<>();
        allCities.add(start);
        allCities.addAll(nodes);
        allCities.add(end);
        int[][] dist = buildDistanceMatrix(allCities);

        for (int i = 0; i < n; i++) {
            int d = dist[0][i + 1];
            memo.put((1 << i) + "," + i, d);
        }

        for (int s = 2; s <= n; s++) {
            for (int subset : generateSubsets(n, s)) {
                for (int last = 0; last < n; last++) {
                    if ((subset & (1 << last)) == 0) continue;
                    int prev = subset ^ (1 << last);
                    int min = Integer.MAX_VALUE;
                    for (int k = 0; k < n; k++) {
                        if (k == last || (prev & (1 << k)) == 0) continue;
                        int cost = memo.getOrDefault(prev + "," + k, Integer.MAX_VALUE);
                        int total = cost + dist[k + 1][last + 1];
                        String key = subset + "," + last;
                        if (total < memo.getOrDefault(key, Integer.MAX_VALUE)) {
                            memo.put(key, total);
                            parent.put(key, nodes.get(k));
                        }
                    }
                }
            }
        }

        int full = (1 << n) - 1, lastIndex = -1, minCost = Integer.MAX_VALUE;
        for (int i = 0; i < n; i++) {
            int cost = memo.getOrDefault(full + "," + i, Integer.MAX_VALUE) + dist[i + 1][n + 1];
            if (cost < minCost) {
                minCost = cost;
                lastIndex = i;
            }
        }

        List<City> path = new ArrayList<>();
        path.add(end);
        int state = full, curr = lastIndex;
        while (state != 0) {
            City node = nodes.get(curr);
            path.add(node);
            int prevState = state ^ (1 << curr);
            City prev = parent.get(state + "," + curr);
            curr = nodes.indexOf(prev);
            state = prevState;
        }
        path.add(start);
        Collections.reverse(path);
        return path;
    }

    private List<Integer> generateSubsets(int n, int k) {
        List<Integer> results = new ArrayList<>();
        int limit = 1 << n;
        for (int i = 0; i < limit; i++) {
            if (Integer.bitCount(i) == k) results.add(i);
        }
        return results;
    }

    @Override
    public int calculatePathWeight(List<City> path) {
        int totalDistance = 0;

        for (int i = 0; i < path.size() - 1; i++) {
            City from = path.get(i);
            City to = path.get(i + 1);


            if (from.equals(to)) {
                continue;
            }


            boolean foundRoad = false;
            List<Road> roads = graph.getOrDefault(from, new ArrayList<>());

            for (Road road : roads) {
                if (road.getSource().equals(from) && road.getDestination().equals(to) ||
                        road.getSource().equals(to) && road.getDestination().equals(from)) {
                    totalDistance += road.getWeight();
                    foundRoad = true;
                    break;
                }
            }

            if (!foundRoad) {
                throw new IllegalStateException("No direct road found between " + from + " and " + to);
            }
        }

        return totalDistance;
    }


    public Map<City, List<Road>> getGraph() {
        return graph;
    }
}