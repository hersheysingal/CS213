package controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.Album;
import model.User;
import model.UserList;

/**
 * This controller controls the UserHomepage.fxml file. This controller controls the 
 * album list of non-administrators as well as implements the ErrorMessage and Logout
 * interfaces.
 * @author Harshita Singal
 * @author Aarushi Chandane
 */
public class UserController implements ErrorMessage, Logout {

	@FXML
	ListView<Album> albumListView;

	@FXML
	Text nameText;

	private ObservableList<Album> obsList;
	private List<Album> albums = new ArrayList<Album>();
	private User user;
	private UserList userList;

	/**
	 * This method sets up all welcome information and initializes on start.
	 * @param mainStage initializes the start
	 */
	public void start(Stage mainStage) {
		nameText.setText("Welcome, " + user.getName());
		albums = user.getAlbums();
		obsList = FXCollections.observableArrayList(albums);
		albumListView.setCellFactory(new Callback<ListView<Album>, ListCell<Album>>() {
			@Override
			public ListCell<Album> call(ListView<Album> photo) {
				return new AlbumCell();
			}
		});
		albumListView.setItems(obsList);
	}

	/**
	 * This method redirects the user to the selected album.
	 * @param mainStage
	 * @throws IOException exception for switching scenes.
	 * @throws ClassNotFoundException exception for switching scenes.
	 */
	public void goToAlbum(Stage mainStage) throws ClassNotFoundException, IOException {
		int index = albumListView.getSelectionModel().getSelectedIndex();
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/PhotoList.fxml"));
		Parent parent = (Parent) loader.load();
		PhotoListController ctrl = loader.<PhotoListController>getController();
		ctrl.setAlbum(albums.get(index));
		Scene scene = new Scene(parent);
		ctrl.start(mainStage);
		mainStage.setScene(scene);
		mainStage.show();
	}

	/**
	 * This method allows for the user to logout and return to the login page.
	 * @param event triggers this method.
	 * @throws ClassNotFoundException Exception for switching scenes
	 */
	@FXML
	protected void handleLogoutButton(ActionEvent event) throws ClassNotFoundException {
		logout(event);
	}

	/**
	 * This method redirects the user to the PhotoSearch scene and initializes the
	 * PhotoSearchController.
	 * @param event triggers this method.
	 * @throws IOException exception for switching scenes
	 * @throws ClassNotFoundException exception for switching scenes
	 */
	@FXML
	private void searchPhotos(ActionEvent event) throws IOException, ClassNotFoundException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/PhotoSearch.fxml"));
		Parent parent = (Parent) loader.load();
		PhotoSearchController ctrl = loader.<PhotoSearchController>getController();
		ctrl.setUser(user);
		ctrl.setUlist(userList);
		Scene scene = new Scene(parent);
		Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
		ctrl.start(stage);
		stage.setScene(scene);
		stage.show();
	}

	/**
	 * This method allows the administrator to add a new user.
	 * @param event triggers this method.
	 */
	@FXML
	private void handleAddButton(ActionEvent event) throws IOException {
		int index = albumListView.getSelectionModel().getSelectedIndex();
		Dialog<Album> dialog = new Dialog<>();
		dialog.setTitle("Create a New Album");
		dialog.setHeaderText("Add a new album to PhotoBox!");
		dialog.setResizable(true);
		Label albumLabel = new Label("Album Name: ");
		TextField albumTextField = new TextField();
		albumTextField.setPromptText("Album Name");
		GridPane grid = new GridPane();
		grid.add(albumLabel, 1, 1);
		grid.add(albumTextField, 2, 1);
		dialog.getDialogPane().setContent(grid);
		ButtonType addButton = new ButtonType("Add", ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().add(addButton);
		dialog.setResultConverter(new Callback<ButtonType, Album>() {
			@Override
			public Album call(ButtonType button) {
				if(button == addButton) {
					String error = checkFields(albumTextField.getText());
					if (error != null) {
						showError(error);
						return null;
					}
					return new Album(albumTextField.getText().trim());
				}
				return null;
			}
		});
		Optional<Album> result = dialog.showAndWait();
		if(result.isPresent()) {
			Album tempAlbum = (Album) result.get();
			obsList.add(tempAlbum);
			albums.add(tempAlbum);
			UserList.write(userList);
			if (obsList.size() == 1) {
				albumListView.getSelectionModel().select(0);
			} else {
				index = 0;
				for (Album album : albums) {
					if (album == tempAlbum) {
						albumListView.getSelectionModel().select(index);
						break;
					}
					index++;
				}
			}
		}
	}

	/**
	 * This method checks if the proper fields are filled and returns null if no errors
	 * are found. 
	 * @param albumName is the name of the album to be checked.
	 * @return an error message for the missing fields and null if none found.
	 */
	private String checkFields(String albumName) {
		if (albumName.trim().isEmpty()) {
			return "Album Name is a required field.";
		}	
		if (user.albumNameExists(albumName)) {
			return albumName + " already exists! Please enter a new name.";
		} else {
			return null;
		}
	}

	/**
	 * @param user The user to set.
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * @param userList The userlist to set.
	 */
	public void setUserList(UserList userList) {
		this.userList = userList;
	}

	/**
	 * The overridden ListCell displays the album data in a ListView cell.
	 */
	private class AlbumCell extends ListCell<Album> {
		AnchorPane apane = new AnchorPane();
		StackPane spane = new StackPane();
		ImageView imageView = new ImageView();
		Label albumNameLabel = new Label();
		Label dateRangeLabel = new Label();
		Label oldestPhotoLabel = new Label();
		Label numPhotosLabel = new Label();
		Button deleteAlbumBtn = new Button("Delete");
		Button renameAlbumBtn = new Button("Rename");
		Button viewAlbumBtn = new Button("View");
		public AlbumCell() {
			super();
			imageView.setFitWidth(90.0);
			imageView.setFitHeight(90.0);
			imageView.setPreserveRatio(true);
			StackPane.setAlignment(imageView, Pos.CENTER);
			spane.getChildren().add(imageView);
			spane.setPrefHeight(110.0);
			spane.setPrefWidth(90.0);
			AnchorPane.setLeftAnchor(spane, 0.0);
			AnchorPane.setLeftAnchor(albumNameLabel, 100.0);
			AnchorPane.setTopAnchor(albumNameLabel, 0.0);
			AnchorPane.setLeftAnchor(dateRangeLabel, 100.0);
			AnchorPane.setTopAnchor(dateRangeLabel, 15.0);
			AnchorPane.setLeftAnchor(oldestPhotoLabel, 100.0);
			AnchorPane.setTopAnchor(oldestPhotoLabel, 30.0);
			AnchorPane.setLeftAnchor(numPhotosLabel, 100.0);
			AnchorPane.setTopAnchor(numPhotosLabel, 45.0);
			deleteAlbumBtn.setVisible(false);
			AnchorPane.setLeftAnchor(deleteAlbumBtn, 100.0);
			AnchorPane.setBottomAnchor(deleteAlbumBtn, 0.0);
			renameAlbumBtn.setVisible(false);
			AnchorPane.setRightAnchor(renameAlbumBtn, 0.0);
			AnchorPane.setBottomAnchor(renameAlbumBtn, 0.0);
			viewAlbumBtn.setVisible(false);
			AnchorPane.setRightAnchor(viewAlbumBtn, 0.0);
			AnchorPane.setTopAnchor(viewAlbumBtn, 0.0);
			apane.getChildren().addAll(spane, albumNameLabel, dateRangeLabel, oldestPhotoLabel, numPhotosLabel, deleteAlbumBtn, renameAlbumBtn, viewAlbumBtn);
			apane.setPrefHeight(110.0);
			albumNameLabel.setMaxWidth(250.0);
			setGraphic(apane);
		}

		@Override
		public void updateItem(Album album, boolean empty) {
			super.updateItem(album, empty);
			setText(null);
			if(album == null) {
				imageView.setImage(null);
				albumNameLabel.setText("");
				dateRangeLabel.setText("");
				oldestPhotoLabel.setText("");
				numPhotosLabel.setText("");
				deleteAlbumBtn.setVisible(false);
				renameAlbumBtn.setVisible(false);
				viewAlbumBtn.setVisible(false);
			} else {
				imageView.setImage(album.getAlbumPhoto());
				albumNameLabel.setText("Album name: " + album.getName());
				dateRangeLabel.setText(album.getDateRange());
				oldestPhotoLabel.setText("Oldest Photo: " + album.getOldestPhotoDate());
				numPhotosLabel.setText("Number of Photos: " + album.getCount());
				deleteAlbumBtn.setVisible(true);
				renameAlbumBtn.setVisible(true);
				viewAlbumBtn.setVisible(true);
				deleteAlbumBtn.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						deleteAlbum(event, album);
					}
				});
				renameAlbumBtn.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						renameAlbum(event, album);
					}
				});
				viewAlbumBtn.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						try {
							goToAlbumContent(event, album);
						} catch(ClassNotFoundException | IOException e) {
							e.printStackTrace();
						}
					}
				});
			}
		}

		/**
		 * This method allows the user to delete an album at a specified cell.
		 * @param event triggers this method.
		 * @param album is the album to be deleted.
		 */
		public void deleteAlbum(ActionEvent event, Album album) {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Album Deletion");
			alert.setHeaderText("Are you sure you want to delete the following?");
			String text = "Click 'OK' to delete: " + album.getName() + ".";
			alert.setContentText(text);
			Optional<ButtonType> result = alert.showAndWait();
			if(result.isPresent()) {
				obsList.remove(album);
				user.removeAlbum(album);
				try {
					UserList.write(userList);
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}

		/**
		 * This method allows the user to rename an album at a specified cell.
		 * @param event triggers this method.
		 * @param album is the album to be renamed.
		 */
		public void renameAlbum(ActionEvent event, Album album) {
			Dialog<User> dialog = new Dialog<>();
			dialog.setTitle("Rename Album");
			dialog.setHeaderText("Rename " + album.getName() + " ?");
			dialog.setResizable(true);
			Label nameLabel = new Label("New Name: ");
			TextField nameTextField = new TextField();
			GridPane grid = new GridPane();
			grid.add(nameLabel, 1, 1);
			grid.add(nameTextField, 2, 1);
			dialog.getDialogPane().setContent(grid);
			ButtonType renameButton = new ButtonType("Rename", ButtonData.OK_DONE);
			dialog.getDialogPane().getButtonTypes().add(renameButton);
			Optional<User> result = dialog.showAndWait();
			String error = checkFields(nameTextField.getText());
			if(result.isPresent()) {
				if(error != null) {
					showError(error);
					return;
				} else {
					album.setName(nameTextField.getText().trim());
					updateItem(album, true);
					try {
						UserList.write(userList);
					} catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
		}

		/**
		 * This method redirects to the PhotoList.fxml for the selected album.
		 * @param event triggers this method.
		 * @param album is the album to be viewed.
		 * @throws IOException exception for switching scenes.
		 * @throws ClassNotFoundException exception for switching scenes.
		 */
		public void goToAlbumContent(ActionEvent event, Album album) throws IOException, ClassNotFoundException {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/PhotoList.fxml"));
			Parent parent = (Parent) loader.load();
			PhotoListController ctrl = loader.<PhotoListController>getController();
			ctrl.setAlbum(album);
			ctrl.setUser(user);
			ctrl.setUserList(userList);
			Scene scene = new Scene(parent);
			Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
			ctrl.start(stage);
			stage.setScene(scene);
			stage.show();
		}
	}

}
