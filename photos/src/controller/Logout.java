package controller;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * The Logout Interface shows for scenes in which a user can logout.
 * @author Harshita Singal
 * @author Aarushi Chandane
 */
public interface Logout {

	/**
	 * This method allows for a user to logout of the program once the logout button is
	 * pressed.
	 * @param event triggers this method.
	 * @throws ClassNotFoundException Exception for switching scenes.
	 */
	default void logout(ActionEvent event) throws ClassNotFoundException {
		Parent parent;
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/LoginMenu.fxml"));
			parent = (Parent) loader.load();
			Scene scene = new Scene(parent);
			Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
			stage.setScene(scene);
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
