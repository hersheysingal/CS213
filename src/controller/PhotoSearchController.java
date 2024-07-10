package controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
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
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.Album;
import model.Photo;
import model.Tag;
import model.User;
import model.UserList;


/**
 * This controller allows the user to traverse through their photos. This controller
 * implements the ErrorMessage and Logout interfaces.
 * @author Harshita Singal
 * @author Aarushi Chandane
 */
public class PhotoSearchController implements ErrorMessage, Logout {

	@FXML
	Button createAlbumBtn;

	@FXML
	TextField tagValueTextField, tagTypeTextField;

	@FXML
	DatePicker fromDate, toDate;

	@FXML
	TableView<Tag> tagTable;

	@FXML
	TableColumn<Tag, String> tagTypeColumn, tagValueColumn;

	@FXML
	TableColumn<Tag, Tag> tagDeleteColumn;

	@FXML
	ListView<Photo> photoListView;
	
	
	private ObservableList<Photo> photoObsList;
	private ObservableList<Tag> tagObsList;
	private User user;
	private List<Photo> photos;
	private List<Tag> tags;
	private UserList userList;

	/**
	 * This method loads all the photos and initializes on start.
	 * @param mainStage initializes the start
	 */
	public void start(Stage mainStage) {
		tags = new ArrayList<Tag>();
		tagObsList = FXCollections.observableArrayList(tags);
		photos = getAllPhotos();
		createAlbumBtn.setDisable(photos.isEmpty());
		photoObsList = FXCollections.observableArrayList(photos);
		photoListView.setCellFactory(new Callback<ListView<Photo>, ListCell<Photo>>() {
			@Override
			public ListCell<Photo> call(ListView<Photo> photo) {
				return new SearchPhotoCell();
			}
		});
		photoListView.setItems(photoObsList);
		tagTypeColumn.setCellValueFactory(new Callback<CellDataFeatures<Tag, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<Tag, String> tag) {
				return new SimpleStringProperty(tag.getValue().getKey());
			}
		});
		tagValueColumn.setCellValueFactory(new Callback<CellDataFeatures<Tag, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<Tag, String> tag) {
				return new SimpleStringProperty(tag.getValue().getValue());
			}
		});
		tagDeleteColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tagDeleteColumn.setCellFactory(param -> new TableCell<Tag, Tag>() {
			private final Button deleteButton = new Button("Delete");
			@Override
			protected void updateItem(Tag tag, boolean empty) {
				super.updateItem(tag, empty);
				if(tag == null) {
					setGraphic(null);
					return;
				}
				setGraphic(deleteButton);
				deleteButton.setOnAction(event -> {
					tagObsList.remove(tag);
					tags.remove(tag);
				});
			}
		});
		tagTable.setItems(tagObsList);
		
	}

	/**
	 * This method allows the user to add and displays it on the table of tags for search.
	 */
	@FXML
	protected void addTag() {
		String tagKey = tagTypeTextField.getText().trim();
		String tagValue = tagValueTextField.getText().trim();
		if (tagKey.isEmpty()) {
			showError("Tag key is required!");
			return;
		}
		if (tagValue.isEmpty()) {
			showError("Tag value is required!");
			return;
		}
		tagTypeTextField.setText("");
		tagValueTextField.setText("");
		boolean alreadyContained = false;
		for(Tag tag : tags) {
			if (tag.getKey().equals(tagKey) && tag.getValue().equals(tagValue)) {
				alreadyContained = true;
				break;
			}
		}
		Tag temporaryTag = new Tag(tagKey, tagValue);
		if (alreadyContained) {
			showError("You can't search for duplicate tags!");
			return;
		}
		tagObsList.add(temporaryTag);
		tags.add(temporaryTag);
	}

	/**
	 * @return A list of the user's unique photos.
	 */
	public List<Photo> getAllPhotos() {
		List<Photo> photos = new ArrayList<Photo>();
		List<Album> albums = user.getAlbums();
		for (int i = 0; i < albums.size(); i++) {
			for (Photo photo : albums.get(i).getPhotos()) {
				if(!photos.contains(photo)) {
					photos.add(photo);
				}
			}
		}
		return photos;
	}

	/**
	 * This method creates an album from the search and prompts the user to enter a new
	 * album name.
	 * @param event triggers this method.
	 * @throws IOException Exception for the dialog
	 */
	@FXML
	protected void createAlbumFromResults(ActionEvent event) throws IOException {
		Dialog<Album> dialog = new Dialog<>();
		dialog.setTitle("Album Creation");
		dialog.setHeaderText("Create a new album and add to photos.");
		dialog.setResizable(true);
		Label albumLabel = new Label("Album Name: ");
		TextField albumTextField = new TextField();
		albumTextField.setPromptText("Album Name");
		GridPane grid = new GridPane();
		grid.add(albumLabel, 1, 1);
		grid.add(albumTextField, 2, 1);
		dialog.getDialogPane().setContent(grid);
		ButtonType enterButton = new ButtonType("Enter", ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().add(enterButton);
		dialog.setResultConverter(new Callback<ButtonType, Album>() {
			@Override
			public Album call(ButtonType button) {
				if (button == enterButton) {
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
		Album temporaryAlbum = null;
		Optional<Album> result = dialog.showAndWait();
		if (result.isPresent()) {
			temporaryAlbum = (Album) result.get();
			user.getAlbums().add(temporaryAlbum);
			for (Photo photo : photos) {
				temporaryAlbum.addPhoto(photo);
			}
			UserList.write(userList);
		}
	}

	private String checkFields(String albumName) {
		if (albumName.trim().isEmpty()) {
			return "Album Name is a required field.";
		}
		if (user.albumNameExists(albumName)) {
			return albumName + " already exists.";
		} else {
			return null;
		}
	}

	@FXML
	protected void searchPhotos(ActionEvent e) {
		List<Photo> allPhotos = getAllPhotos();
		LocalDate from, to;
		if (tags.isEmpty() && fromDate.getValue() == null && toDate.getValue() == null) {
			return;
		}
		if (fromDate.getValue() != null && toDate.getValue() != null && fromDate.getValue().isAfter(toDate.getValue())) {
			showError("Dates must be picked in chronological order!");
		}
		if (fromDate.getValue() == null) {
			from = LocalDate.MIN;
		} else {
			from = fromDate.getValue();
		}
		if (toDate.getValue() == null) {
			to = LocalDate.MAX;
		} else {
			to = toDate.getValue();
		}
		photos.clear();
		photoObsList.clear();
		for (Photo photo : allPhotos) {
			if (tags.isEmpty()) {
				if (photo.isWithinDateRange(from, to)) {
					photos.add(photo);
					photoObsList.add(photo);
				}
			} else {
				if (photo.hasSubset(tags) && photo.isWithinDateRange(from, to)) {
					photos.add(photo);
					photoObsList.add(photo);
				}
			}
		}
		createAlbumBtn.setDisable(photos.isEmpty());
	}

	/**
	 * This method allows for the user to logout and return to the login page.
	 * @param event triggers this method.
	 * @throws ClassNotFoundException Exception for switching scenes.
	 */
	@FXML
	protected void handleLogoutButton(ActionEvent event) throws ClassNotFoundException {
		logout(event);
	}

	/**
	 * This method return the user to the UserHomepage.fxml scene.
	 * @param event triggers this method.
	 * @throws ClassNotFoundException Exception for switching scenes.
	 */
	@FXML
	protected void backToAlbums(ActionEvent event) throws ClassNotFoundException {
		Parent parent;
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/UserHomepage.fxml"));
			parent = (Parent) loader.load();
			UserController ctrl = loader.<UserController>getController();
			ctrl.setUser(user);
			ctrl.setUserList(userList);
			Scene scene = new Scene(parent);
			Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
			ctrl.start(stage);
			stage.setScene(scene);
			stage.show();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param user The user to set.
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * @param userList The userList to set.
	 */
	public void setUlist(UserList userList) {
		this.userList = userList;
	}

	/**
	 * The overridden ListCell displays the search photo information in a ListView cell.
	 */
	private class SearchPhotoCell extends ListCell<Photo> {
		AnchorPane apane = new AnchorPane();
		StackPane spane = new StackPane();
		ImageView imageView = new ImageView();
		Label captionLabel = new Label();
		Button deletePhotoBtn = new Button("Delete");
		Button editPhotoBtn = new Button("Edit");
		Button viewPhotoBtn = new Button("View");
		public SearchPhotoCell() {
			super();
			imageView.setFitWidth(45.0);
			imageView.setFitHeight(45.0);
			imageView.setPreserveRatio(true);
			StackPane.setAlignment(imageView, Pos.CENTER);
			spane.getChildren().add(imageView);
			spane.setPrefHeight(55.0);
			spane.setPrefWidth(45.0);
			AnchorPane.setLeftAnchor(spane, 0.0);
			AnchorPane.setLeftAnchor(captionLabel, 55.0);
			AnchorPane.setTopAnchor(captionLabel, 0.0);
			AnchorPane.setRightAnchor(deletePhotoBtn, 0.0);
			AnchorPane.setBottomAnchor(deletePhotoBtn, 0.0);
			AnchorPane.setLeftAnchor(editPhotoBtn, 55.0);
			AnchorPane.setBottomAnchor(editPhotoBtn, 0.0);
			AnchorPane.setLeftAnchor(viewPhotoBtn, 115.0);
			AnchorPane.setBottomAnchor(viewPhotoBtn, 0.0);
			deletePhotoBtn.setVisible(false);
			editPhotoBtn.setVisible(false);
			viewPhotoBtn.setVisible(false);
			apane.getChildren().addAll(spane, captionLabel, deletePhotoBtn, editPhotoBtn, viewPhotoBtn);
			apane.setPrefHeight(55.0);
			captionLabel.setMaxWidth(300.0);
			setGraphic(apane);
		}
		@Override
		public void updateItem(Photo photo, boolean empty) {
			super.updateItem(photo, empty);
			setText(null);
			if (photo == null) {
				imageView.setImage(null);
				captionLabel.setText("");
				deletePhotoBtn.setVisible(false);
				editPhotoBtn.setVisible(false);
				viewPhotoBtn.setVisible(false);
			}
			if (photo != null) {
				imageView.setImage(photo.getImage());
				captionLabel.setText("Caption: " + photo.getCaption());
				deletePhotoBtn.setVisible(true);
				editPhotoBtn.setVisible(true);
				viewPhotoBtn.setVisible(true);
				deletePhotoBtn.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						deletePhoto(event, photo);
					}
				});
				editPhotoBtn.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						editPhoto(event, photo);
					}
				});
				viewPhotoBtn.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						try {
							viewPhoto(event, photo);
						} catch(ClassNotFoundException | IOException e) {
							e.printStackTrace();
						}
					}
				});
			}
		}

		/**
		 * This method deletes a selected photo by the user once prompted with an alert 
		 * message.
		 * @param event triggers this method.
		 * @param photo is the photo to be deleted.
		 */
		public void deletePhoto(ActionEvent event, Photo photo) {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Delete photo");
			alert.setHeaderText("There's no going back!");
			String content = "Are you sure you want to delete this photo?";
			alert.setContentText(content);
			Optional<ButtonType> result = alert.showAndWait();
			if (result.isPresent()) {
				for (Album album : user.getAlbums()) {
					int index = album.findIndexByPhoto(photo);
					album.removePhoto(index);
					photoObsList.remove(photo);
					try {
						UserList.write(userList);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}

		/**
		 * This method allows for the user to edit a selected photo.
		 * @param event triggers this method.
		 * @param photo is the photo to be edited.
		 */
		public void editPhoto(ActionEvent event, Photo photo) {
			Dialog<Album> dialog = new Dialog<>();
			dialog.setTitle("Edit Caption");
			dialog.setHeaderText("Edit the caption for this photo.");
			dialog.setResizable(true);
			Label captionLabel = new Label("Caption: ");
			TextArea captionTextArea = new TextArea();
			captionTextArea.setPromptText(photo.getCaption());
			GridPane grid = new GridPane();
			grid.add(captionLabel, 1, 1);
			grid.add(captionTextArea, 2, 1);
			dialog.getDialogPane().setContent(grid);
			ButtonType enterButton = new ButtonType("Enter", ButtonData.OK_DONE);
			dialog.getDialogPane().getButtonTypes().add(enterButton);
			Optional<Album> result = dialog.showAndWait();
			if (result.isPresent()) {
				photo.setCaption(captionTextArea.getText());
				updateItem(photo, true);
				try {
					UserList.write(userList);
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}

		/**
		 * This method displays a photo using the PhotoDisplayController.
		 * @param event triggers this method.
		 * @param photo is the photo being viewed.
		 * @throws IOException exception for switching scenes.
		 * @throws ClassNotFoundException exception for switching scenes.
		 */
		public void viewPhoto(ActionEvent event, Photo photo) throws IOException, ClassNotFoundException {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/PhotoDisplay.fxml"));
			Parent parent = (Parent) loader.load();
			PhotoDisplayController ctrl = loader.<PhotoDisplayController>getController();
			ctrl.setPhotoIndex(photos.indexOf(photo));
			ctrl.setPhotos(photos);
			ctrl.setUser(user);
			ctrl.setUserList(userList);
			ctrl.setKey(PhotoDisplayController.CAME_FROM_PHOTO_SEARCH);
			Scene scene = new Scene(parent);
			Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
			ctrl.start(stage);
			stage.setScene(scene);
			stage.show();
		}
	}
}
