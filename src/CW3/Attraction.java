package CW3;

public class Attraction {
    private String name;
    private City city;

    public Attraction(String name, City city) {

        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("CW3.Attraction name cannot be null or empty");
        }
        if (city == null) {
            throw new IllegalArgumentException("CW3.City cannot be null");
        }

        this.name = name.trim();
        this.city = city;
    }

    public String getName() {
        return name;
    }

    public City getCity() {
        return city;
    }

    @Override
    public String toString() {
        return name + " in " + city.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Attraction other = (Attraction) obj;
        return name.equalsIgnoreCase(other.name) && city.equals(other.city);
    }

    @Override
    public int hashCode() {
        int result = name.toLowerCase().hashCode();
        result = 31 * result + city.hashCode();
        return result;
    }
}