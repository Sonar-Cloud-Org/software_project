package edu.najah.software.presentation;

import javafx.scene.control.Alert;

public final class AlertHelper {

    private AlertHelper() {

    }

    public static void show(
            Alert.AlertType type,
            String title,
            String message) {

        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}