package CW3;

public class Road implements GraphEdge<City> {
    private City cityA;
    private City cityB;
    private int distance;

    public Road(City cityA, City cityB, int distance) {
        if (cityA == null || cityB == null) {
            throw new IllegalArgumentException("Cities cannot be null");
        }
        if (distance < 0) {
            throw new IllegalArgumentException("Distance cannot be negative");
        }

        this.cityA = cityA;
        this.cityB = cityB;
        this.distance = distance;
    }

    @Override
    public City getSource() {
        return cityA;
    }

    @Override
    public City getDestination() {
        return cityB;
    }

    @Override
    public int getWeight() {
        return distance;
    }

    @Override
    public boolean connects(City node) {
        return cityA.equals(node) || cityB.equals(node);
    }

    @Override
    public City getOtherNode(City node) {
        if (cityA.equals(node)) return cityB;
        if (cityB.equals(node)) return cityA;
        throw new IllegalArgumentException("CW3.City " + node + " is not connected to this road");
    }

    public City getCityA() {
        return cityA;
    }

    public City getCityB() {
        return cityB;
    }

    public int getDistance() {
        return distance;
    }

    @Override
    public String toString() {
        return cityA.toString() + " <--> " + cityB.toString() + " (" + distance + " miles)";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Road other = (Road) obj;
        return distance == other.distance &&
                ((cityA.equals(other.cityA) && cityB.equals(other.cityB)) ||
                        (cityA.equals(other.cityB) && cityB.equals(other.cityA)));
    }

    @Override
    public int hashCode() {
        return cityA.hashCode() + cityB.hashCode() + distance;
    }
}