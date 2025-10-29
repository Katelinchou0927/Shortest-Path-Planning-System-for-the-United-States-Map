package CW3;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class RoutePlannerApp extends Application {


    private static volatile boolean javaFxLaunched = false;

    @Override
    public void start(Stage primaryStage) {
        try {

            MapView mapView = new MapView();

            // calculate path
            mapView.drawRoute(
                    Main.getCalculatedRoute(),
                    Main.getSelectedAttractions(),
                    Main.getCalculatedDistance()
            );

            Scene scene = new Scene(mapView.getMapPane(), 800, 600);

            primaryStage.setTitle("American CW3.Road Trip Planner");
            primaryStage.setScene(scene);
            primaryStage.show();

            primaryStage.setOnCloseRequest(e -> {
                Platform.exit();
                System.exit(0);
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void launchApp(String[] args) {
        if (!javaFxLaunched) {
            javaFxLaunched = true;

            new Thread(() -> {
                Application.launch(RoutePlannerApp.class, args);
            }).start();

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}