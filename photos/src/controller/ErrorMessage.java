package controller;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 * The ErrorMessage Interface shows an error alert and is used throughout the program when
 * prompted.
 * @author Harshita Singal
 * @author Aarushi Chandane
 */
public interface ErrorMessage {

	/**
	 * This method creates a new alert dialog when an error occurs within the program.
	 * @param error will display the alert.
	 */
	default void showError(String error) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Error");
		alert.setHeaderText("Error");
		String content = error;
		alert.setContentText(content);
		alert.showAndWait();
	}

}
