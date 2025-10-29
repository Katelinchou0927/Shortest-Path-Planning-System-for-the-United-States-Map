package CW3;

public class City implements GraphNode {
    private String name;
    private String state;

    public City(String name, String state) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("CW3.City name cannot be null or empty");
        }
        if (state == null || state.trim().isEmpty()) {
            throw new IllegalArgumentException("State cannot be null or empty");
        }

        this.name = name.trim();
        this.state = state.trim();
    }

    public String getName() {
        return name;
    }

    public String getState() {
        return state;
    }

    @Override
    public String getId() {
        return name + " " + state;
    }

    @Override
    public String getDisplayName() {
        return name + " " + state;
    }

    @Override
    public String toString() {
        return name + " " + state;  // 格式应该与CSV文件中的格式一致
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        City other = (City) obj;
        return name.equalsIgnoreCase(other.name) && state.equalsIgnoreCase(other.state);
    }

    @Override
    public int hashCode() {
        return (name.toLowerCase() + state.toLowerCase()).hashCode();
    }
}