module CPT204CW3 {
    requires javafx.controls;
    requires javafx.fxml;

    opens CW3 to javafx.fxml;
    exports CW3;

    opens sorting to javafx.fxml;
    exports sorting;
}
