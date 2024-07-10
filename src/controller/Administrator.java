package controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.*;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;

/**
 * This controller controls the Administrator.fxml file. It controls the addition and 
 * deletion of users.
 * @author Harshita Singal
 * @author Aarushi Chandane
 */
public class Administrator implements ErrorMessage, Logout {

	@FXML
	TableView<User> table;

	@FXML
	TableColumn<User, String> usernameColumn;

	@FXML
	TableColumn<User, String> passwordColumn;

	@FXML
	TableColumn<User, String> nameColumn;

	@FXML
	TableColumn<User, User> deleteColumn;

	private ObservableList<User> obsList;
	private List<User> users = new ArrayList<User>();
	private UserList userList;

	/**
	 * Loads in all users and initializes on start.
	 * @param mainStage The main stage
	 * @throws IOException	Exception for reading the list of users.
	 * @throws ClassNotFoundException Exception for reading the list of users.
	 */
	public void start(Stage mainStage) throws ClassNotFoundException, IOException {
		userList = UserList.read();
		users = userList.getUserList();
		obsList = FXCollections.observableArrayList(users);
		usernameColumn.setCellValueFactory(new Callback<CellDataFeatures<User, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<User, String> user) {
				return new SimpleStringProperty(user.getValue().getUsername());
			}
		});
		nameColumn.setCellValueFactory(new Callback<CellDataFeatures<User, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<User, String> user) {
				return new SimpleStringProperty(user.getValue().getName());
			}
		});
		passwordColumn.setCellValueFactory(new Callback<CellDataFeatures<User, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<User, String> user) {
				return new SimpleStringProperty(user.getValue().getPassword());
			}
		});
		deleteColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		deleteColumn.setCellFactory(param -> new TableCell<User, User>() {
			private final Button deleteButton = new Button("Delete");
			@Override
			protected void updateItem(User user, boolean empty) {
				super.updateItem(user, empty);
				if (user == null) {
					setGraphic(null);
					return;
				}
				setGraphic(deleteButton);
				deleteButton.setOnAction(event -> {
					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setTitle("Delete User");
					alert.setHeaderText("Are you sure you want to delete the user?");
					String text = "Click 'OK' to delete: " + user.getUsername() + ".";
					alert.setContentText(text);
					Optional<ButtonType> result = alert.showAndWait();
					if(!user.getName().equals("admin") && result.isPresent()) {
						obsList.remove(user);
						users.remove(user);
						try {
							UserList.write(userList);
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else if(user.getName().equals("admin")) {
						showError("You cannot delete the administrator!");
					}
				});
			}
		});
		table.setItems(obsList);
		if(!obsList.isEmpty()) {
			table.getSelectionModel().select(0);
		}
	}
	
	/**
	 * This method checks if the proper fields are filled and returns null if no errors
	 * are found.
	 * @return an error message for the missing fields and null if none found.
	 */
	private String checkFields(String username, String password, String name) {
		if (username.trim().isEmpty()) {
			return "Please enter a username";
		} else if (password.trim().isEmpty()) {
			return "Please enter a password";
		} else if (name.trim().isEmpty()) {
			return "Please enter your fullname";
		}
		if(userList.userExists(username)) {
			return "Username taken! Please enter a new username.";
		} else {
			return null;
		}
	}
	
	/**
	 * This method allows for the administrator to add a new user.
	 * @param event triggers this method.
	 */
	@FXML
	private void handleAddButton(ActionEvent event) throws IOException {
		int index = table.getSelectionModel().getSelectedIndex();
		Dialog<User> dialog = new Dialog<>();
		dialog.setTitle("User Creation");
		dialog.setHeaderText("Please add a new user to Photos!");
		dialog.setResizable(true);
		Label usernameLabel = new Label("Username: ");
		Label passwordLabel = new Label("Password: ");
		Label nameLabel = new Label("User's Full Name: ");
		TextField usernameTextField = new TextField();
		TextField passwordTextField = new TextField();
		TextField nameTextField = new TextField();
		GridPane grid = new GridPane();
		grid.add(usernameLabel, 1, 1);
		grid.add(usernameTextField, 2, 1);
		grid.add(passwordLabel, 1, 2);
		grid.add(passwordTextField, 2, 2);
		grid.add(nameLabel, 1, 3);
		grid.add(nameTextField, 2, 3);
		dialog.getDialogPane().setContent(grid);
		ButtonType enterButton = new ButtonType("Enter", ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().add(enterButton);
		dialog.setResultConverter(new Callback<ButtonType, User>() {
			@Override
			public User call(ButtonType button) {
				if (button == enterButton) {
					String error = checkFields(usernameTextField.getText(), passwordTextField.getText(), nameTextField.getText());
					if (error != null) {
						showError(error);
						return null;
					}
					return new User(nameTextField.getText().trim(), usernameTextField.getText().trim(), passwordTextField.getText().trim());
				}
				return null;
			}
		});
		Optional<User> result = dialog.showAndWait();
		if (result.isPresent()) {
			User temporaryUser = (User) result.get();
			obsList.add(temporaryUser);
			users.add(temporaryUser);
			UserList.write(userList);
			if (obsList.size() == 1) {
				table.getSelectionModel().select(0);
			} else {
				index = 0;
				for(User user : userList.getUserList()) {
					if(user == temporaryUser) {
						table.getSelectionModel().select(index);
						break;
					}
					index++;
				}
			}
		}
	}

	/**
	 * This method allows for the administrator to logout and return to the login page.
	 * @param event triggers this method.
	 * @throws ClassNotFoundException for switching scenes.
	 */
	@FXML
	protected void handleLogoutButton(ActionEvent event) throws ClassNotFoundException {
		logout(event);
	}

}
