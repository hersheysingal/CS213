package controller;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.*;

/**
 * This controller controls the LoginMenu.fxml file. 
 * @author Harshita Singal
 * @author Aarushi Chandane
 */
public class LoginController {

	@FXML
	private Button loginButton;
	@FXML
	private TextField usernameField;
	@FXML
	private PasswordField passwordField;
	@FXML
	private Text actionTarget;

	/**
	 * This method checks if the user's login credentials are correct, if the user is an
	 * administrator or not. If the credentials are incorrect, the user will be notified.
	 * @param event triggers this method.
	 * @throws ClassNotFoundException Exception for switching scenes.
	 */
	@FXML
	protected void handleLoginButtonAction(ActionEvent event) throws ClassNotFoundException {
		String username = usernameField.getText();
		String password = passwordField.getText();
		Parent parent;
		try {
			System.out.println(UserList.read());
		} catch(IOException e) {
			e.printStackTrace();
		}
		UserList list = null;
		try {
			list = UserList.read();
			System.out.println(list);
		} catch(IOException e) {
			e.printStackTrace();
		}
		try {
			if(list.isUserInList(username, password)) {
				if(username.equals("admin") && password.equals("admin")) {
					FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Administrator.fxml"));
					parent = (Parent) loader.load();
					Administrator ctrl = loader.getController();
					Scene scene = new Scene(parent);
					Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
					ctrl.start(stage);
					stage.setScene(scene);
					stage.show();
				} else {
					FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/UserHomepage.fxml"));
					parent = (Parent) loader.load();
					UserController ctrl = loader.<UserController>getController();
					ctrl.setUserList(list);
					ctrl.setUser(list.getUserByUsername(username));
					Scene scene = new Scene(parent);
					Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
					ctrl.start(stage);
					stage.setScene(scene);
					stage.show();
				}
			} else {
				actionTarget.setText("Incorrect username/password. Please try again.");
			}																							
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
