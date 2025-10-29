package CW3;

import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

import java.io.InputStream;
import java.util.*;

public class MapView {

    private Pane mapPane;
    private Map<String, Point2D> cityCoordinates = new HashMap<>();
    private ImageView mapImageView;

    public MapView() {

        initializeCityCoordinates();

        createMapPane();
    }

    private void initializeCityCoordinates() {
        // set lociation
        cityCoordinates.put("new york ny", new Point2D(670, 230));
        cityCoordinates.put("los angeles ca", new Point2D(110, 340));
        cityCoordinates.put("chicago il", new Point2D(485, 235));
        cityCoordinates.put("houston tx", new Point2D(420, 420));
        cityCoordinates.put("phoenix az", new Point2D(200, 350));
        cityCoordinates.put("philadelphia pa", new Point2D(665, 245));
        cityCoordinates.put("san antonio tx", new Point2D(350, 430));
        cityCoordinates.put("san diego ca", new Point2D(150, 370));
        cityCoordinates.put("dallas tx", new Point2D(380, 380));
        cityCoordinates.put("san jose ca", new Point2D(100, 300));
        cityCoordinates.put("austin tx", new Point2D(380, 410));
        cityCoordinates.put("jacksonville fl", new Point2D(560, 420));
        cityCoordinates.put("fort worth tx", new Point2D(370, 395));
        cityCoordinates.put("columbus oh", new Point2D(550, 270));
        cityCoordinates.put("charlotte nc", new Point2D(600, 330));
    }

    private void createMapPane() {
        mapPane = new Pane();
        mapPane.setPrefSize(800, 600);


        try {
            // try many ways to find map
            InputStream is = null;

            is = MapView.class.getClassLoader().getResourceAsStream("resources/usa_map.png");
            if (is == null) {
                is = MapView.class.getResourceAsStream("/CW3/resources/usa_map.png");
            }

            if (is == null) {
                is = MapView.class.getClassLoader().getResourceAsStream("/CW3/resources/usa_map.png");
            }


            Image mapImage = new Image(is);
            mapImageView = new ImageView(mapImage);

            mapImageView.setFitWidth(800);
            mapImageView.setFitHeight(600);
            mapImageView.setPreserveRatio(true);
            mapImageView.setTranslateY(15);

            mapPane.getChildren().add(mapImageView);

            drawAllCities();// add city

        } catch (Exception e) {
            System.err.println("Failed to load map image: " + e.getMessage());
            e.printStackTrace();

            // If loading fails, use a solid color background
            mapPane.setStyle("-fx-background-color: #e8f4f8;");
            drawAllCities();
        }
    }

    private void drawAllCities() {
        for (Map.Entry<String, Point2D> entry : cityCoordinates.entrySet()) {
            String cityKey = entry.getKey();
            Point2D point = entry.getValue();

            Circle cityCircle = new Circle(point.getX(), point.getY(), 4);
            cityCircle.setFill(Color.BLUE);
            cityCircle.setStroke(Color.WHITE);
            cityCircle.setStrokeWidth(1);

            Label cityLabel = new Label(cityKey.toUpperCase());
            cityLabel.setLayoutX(point.getX() + 5);
            cityLabel.setLayoutY(point.getY() - 15);
            cityLabel.setStyle("-fx-font-size: 8; -fx-background-color: rgba(255,255,255,0.7); -fx-padding: 2;");

            mapPane.getChildren().addAll(cityCircle, cityLabel);
        }
    }

    public void drawRoute(List<City> route, List<Attraction> attractions, int totalDistance) {
        if (route == null || route.isEmpty()) {
            return;
        }

        // draw path
        for (int i = 0; i < route.size() - 1; i++) {
            City from = route.get(i);
            City to = route.get(i + 1);

            String fromKey = from.toString().toLowerCase();
            String toKey = to.toString().toLowerCase();

            if (cityCoordinates.containsKey(fromKey) && cityCoordinates.containsKey(toKey)) {
                Point2D fromPoint = cityCoordinates.get(fromKey);
                Point2D toPoint = cityCoordinates.get(toKey);

                Line line = new Line(fromPoint.getX(), fromPoint.getY(), toPoint.getX(), toPoint.getY());
                line.setStroke(Color.RED);
                line.setStrokeWidth(2.5);

                if (mapImageView != null) {
                    // If there is a map background, place the lines above the map and below the city points
                    mapPane.getChildren().add(1, line);
                } else {
                    // conversely
                    mapPane.getChildren().add(0, line);
                }
            }
        }

        // highlight
        if (route.size() > 0) {
            highlightCity(route.get(0), Color.GREEN, 8);
            highlightCity(route.get(route.size() - 1), Color.RED, 8);
        }

        if (attractions != null) {
            for (Attraction attraction : attractions) {
                highlightCity(attraction.getCity(), Color.ORANGE, 8);
            }
        }


        Label distanceLabel = new Label("Total distance: " + totalDistance + " miles");
        distanceLabel.setLayoutX(10);
        distanceLabel.setLayoutY(10);
        distanceLabel.setStyle("-fx-background-color: white; -fx-padding: 5; -fx-font-weight: bold;");

        mapPane.getChildren().add(distanceLabel);
    }

    private void highlightCity(City city, Color color, double radius) {
        String cityKey = city.toString().toLowerCase();
        if (cityCoordinates.containsKey(cityKey)) {
            Point2D point = cityCoordinates.get(cityKey);

            Circle highlight = new Circle(point.getX(), point.getY(), radius);
            highlight.setFill(color);
            highlight.setOpacity(0.6);
            highlight.setStroke(Color.WHITE);
            highlight.setStrokeWidth(1.5);

            mapPane.getChildren().add(highlight);
        }
    }

    public Pane getMapPane() {
        return mapPane;
    }
}