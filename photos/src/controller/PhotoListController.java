package controller;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.imageio.ImageIO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.Album;
import model.Photo;
import model.SerializableImage;
import model.User;
import model.UserList;

/**
 * This controller controls the PhotoList.fxml as well as the PhotoDisplay.fxml files.
 * This controller implements the ErrorMessage and Logout interfaces.
 * @author Harshita Singal
 * @author Aarushi Chandane
 */
public class PhotoListController implements ErrorMessage, Logout {

	@FXML
	ListView<Photo> photoListView;

	@FXML
	ImageView albumImageView;

	@FXML
	Text albumDateRangeText, oldestPhotoText, numPhotosText;

	@FXML
	Label albumNameText;

	private ObservableList<Photo> obsList;
	private List<Photo> photos;
	private Album album;
	private User user;
	private UserList userList;

	/**
	 * This method loads all the album information and initializes on start.
	 * @param mainStage sets the scene.
	 */
	public void start(Stage mainStage) {
		albumNameText.setText(album.getName());
		updateAlbumDetails();
		photos = album.getPhotos();
		obsList = FXCollections.observableArrayList(photos);
		photoListView.setCellFactory(new Callback<ListView<Photo>, ListCell<Photo>>() {
			@Override
			public ListCell<Photo> call(ListView<Photo> photo) {
				return new PhotoCell();
			}
		});
		photoListView.setItems(obsList);
	}

	/**
	 * This method allows the user to delete an album after confirmation.
	 * @param event triggers this method.
	 */
	@FXML
	protected void deleteAlbum(ActionEvent event) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Album Deletion");
		alert.setHeaderText("Are you sure you want to delete the album?");
		String text = "Click 'OK' to delete: " + album.getName() + ".";
		alert.setContentText(text);
		Optional<ButtonType> result = alert.showAndWait();
		if (result.isPresent()) {
			user.removeAlbum(album);
			try {
				UserList.write(userList);
				backToAlbums(event);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * This method returns the the UserHomepage.fxml if the user clicks the back button.
	 * @param event triggers this method.
	 * @throws ClassNotFoundException Exception for switching scenes
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
	 * This method opens the fileChooser allowing the user to select either .png or
	 * .jpeg files.
	 * @param event triggers this method.
	 * @throws IOException Exception for bringing up the fileChooser.
	 */
	@FXML
	protected void choosePhoto(ActionEvent event) throws IOException {
		FileChooser fileChooser = new FileChooser();
		FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.JPG");
		FileChooser.ExtensionFilter extFilterPNG = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.PNG");
		fileChooser.getExtensionFilters().addAll(extFilterJPG, extFilterPNG);
		fileChooser.setTitle("Upload Photo");
		Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
		File file = fileChooser.showOpenDialog(stage);
		if (file == null) {
			return;
		}
		BufferedImage bufferedImage = ImageIO.read(file);
		Image image = SwingFXUtils.toFXImage(bufferedImage, null);
		SerializableImage tempImage = new SerializableImage();
		tempImage.setImage(image);
		for(Photo photo : album.getPhotos()) {
			if (tempImage.equals(photo.getSerializableImage())) {
				showError("Photo already exists in album!");
				return;
			}
		}
		Photo tempPhoto = null;
		boolean photoFound = false;
		for(Album album : user.getAlbums()) {
			for(Photo photo : album.getPhotos()) {
				if(tempImage.equals(photo.getSerializableImage())) {
					tempPhoto = photo;
					photoFound = true;
					System.out.println("Photo found!");
					break;
				}
				if(photoFound) {
					break;
				}
			}
		}
		if(!photoFound) {
			tempPhoto = new Photo(image);
		}
		album.addPhoto(tempPhoto);
		obsList.add(tempPhoto);
		UserList.write(userList);
		updateAlbumDetails();
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
	 * The overridden ListCell displays the photo information in a ListView cell.
	 */
	private class PhotoCell extends ListCell<Photo> {
		AnchorPane apane = new AnchorPane();
		StackPane spane = new StackPane();
		ImageView imageView = new ImageView();
		Label captionLabel = new Label();
		Button deletePhotoBtn = new Button("Delete");
		Button editPhotoBtn = new Button("Edit");
		Button movePhotoBtn = new Button("Move");
		Button viewPhotoBtn = new Button("View");
		public PhotoCell() {
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
			AnchorPane.setRightAnchor(movePhotoBtn, 70.0);
			AnchorPane.setBottomAnchor(movePhotoBtn, 0.0);
			AnchorPane.setLeftAnchor(viewPhotoBtn, 115.0);
			AnchorPane.setBottomAnchor(viewPhotoBtn, 0.0);
			deletePhotoBtn.setVisible(false);
			editPhotoBtn.setVisible(false);
			movePhotoBtn.setVisible(false);
			viewPhotoBtn.setVisible(false);
			apane.getChildren().addAll(spane, captionLabel, deletePhotoBtn, editPhotoBtn, movePhotoBtn, viewPhotoBtn);
			apane.setPrefHeight(55.0);
			captionLabel.setMaxWidth(300.0);
			setGraphic(apane);
		}
		@Override
		public void updateItem(Photo photo, boolean empty) {
			super.updateItem(photo, empty);
			setText(null);
			if(photo == null) {
				imageView.setImage(null);
				captionLabel.setText("");
				deletePhotoBtn.setVisible(false);
				editPhotoBtn.setVisible(false);
				movePhotoBtn.setVisible(false);
				viewPhotoBtn.setVisible(false);
			}
			if(photo != null) {
				imageView.setImage(photo.getImage());
				captionLabel.setText("Caption: " + photo.getCaption());
				deletePhotoBtn.setVisible(true);
				editPhotoBtn.setVisible(true);
				movePhotoBtn.setVisible(true);
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
				movePhotoBtn.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						movePhoto(event, photo);
					}
				});
				viewPhotoBtn.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						try {
							viewPhoto(event, photo);
						} catch (ClassNotFoundException | IOException e) {
							e.printStackTrace();
						}
					}
				});
			}
		}

		/**
		 * This method allows the user to delete a photo and will be prompted by an
		 * alert.
		 * @param event triggers this method.
		 * @param photo is the photo to be deleted.
		 */
		public void deletePhoto(ActionEvent event, Photo photo) {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Photo Deletion");
			alert.setHeaderText("Are you sure you want to delete the following?");
			String text = "Click 'OK' to delete the photo.";
			alert.setContentText(text);
			Optional<ButtonType> result = alert.showAndWait();
			if(result.isPresent()) {
				int index = album.findIndexByPhoto(photo);
				album.removePhoto(index);
				obsList.remove(photo);
				updateAlbumDetails();
				try {
					UserList.write(userList);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		/**
		 * This method allows the user to edit a photo.
		 * @param event triggers this method.
		 * @param photo is the photo to be edited.
		 */
		public void editPhoto(ActionEvent event, Photo photo) {
			Dialog<Album> dialog = new Dialog<>();
			dialog.setTitle("Edit Caption");
			dialog.setHeaderText("Please enter a new caption!");
			dialog.setResizable(true);
			Label captionLabel = new Label("Caption: ");
			TextArea captionTextArea = new TextArea();
			captionTextArea.setText(photo.getCaption());
			GridPane grid = new GridPane();
			grid.add(captionLabel, 1, 1);
			grid.add(captionTextArea, 2, 1);
			dialog.getDialogPane().setContent(grid);
			captionTextArea.setWrapText(true);
			ButtonType enterButton = new ButtonType("Enter", ButtonData.OK_DONE);
			dialog.getDialogPane().getButtonTypes().add(enterButton);
			Optional<Album> result = dialog.showAndWait();
			if(result.isPresent()) {
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
		 * This method allows the user to move a selected photo to another album.
		 * @param event triggers this method.
		 * @param photo is the photo to be relocated.
		 */
		public void movePhoto(ActionEvent event, Photo photo) {
			Dialog<Album> dialog = new Dialog<>();
			dialog.setTitle("Move Photo");
			dialog.setHeaderText("Move this photo to another album");
			dialog.setResizable(true);
			Label moveLabel = new Label("Album to move this photo to: ");
			List<String> albumNames = new ArrayList<String>();
			for(Album album : user.getAlbums()) {
				String temporary = album.getName();
				if(album.getName() != album.getName()) {
					albumNames.add(temporary);	
				}
			}
			ObservableList<String> options = FXCollections.observableArrayList(albumNames);
			ComboBox<String> moveComboBox = new ComboBox<String>(options);
			GridPane grid = new GridPane();
			grid.add(moveLabel, 1, 1);
			grid.add(moveComboBox, 1, 2);
			dialog.getDialogPane().setContent(grid);
			ButtonType moveButton = new ButtonType("Move", ButtonData.OK_DONE);
			dialog.getDialogPane().getButtonTypes().add(moveButton);
			Optional<Album> result = dialog.showAndWait();
			if (result.isPresent()) {
				String newAlbumName = moveComboBox.getSelectionModel().getSelectedItem();
				Album newAlbum = user.getAlbumByName(newAlbumName);
				newAlbum.addPhoto(photo);
				obsList.remove(photo);
				int index = album.findIndexByPhoto(photo);
				album.removePhoto(index);
				updateAlbumDetails();
				try {
					UserList.write(userList);
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}

		/**
		 * This method displays the photo using the PhotoDisplayController.
		 * @param event triggers this method.
		 * @param photo is the photo being viewed.
		 * @throws IOException Exception for switching scenes.
		 * @throws ClassNotFoundException Exception for switching scenes.
		 */
		public void viewPhoto(ActionEvent event, Photo photo) throws IOException, ClassNotFoundException {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/PhotoDisplay.fxml"));
			Parent parent = (Parent) loader.load();
			PhotoDisplayController ctrl = loader.<PhotoDisplayController>getController();
			ctrl.setPhotoIndex(album.findIndexByPhoto(photo));
			ctrl.setAlbum(album);
			ctrl.setUser(user);
			ctrl.setUserList(userList);
			ctrl.setKey(PhotoDisplayController.CAME_FROM_PHOTO_LIST);
			Scene scene = new Scene(parent);
			Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
			ctrl.start(stage);
			stage.setScene(scene);
			stage.show();
		}
	}

	/**
	 * @param album The album to set.
	 */
	public void setAlbum(Album album) {
		this.album = album;
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
	public void setUserList(UserList userList) {
		this.userList = userList;
	}
	
	/**
	 * This method updates the album's thumbnail and details.
	 */
	public void updateAlbumDetails() {
		albumImageView.setImage(album.getAlbumPhoto());
		albumDateRangeText.setText(album.getDateRange());
		oldestPhotoText.setText("Oldest Photo: " + album.getOldestPhotoDate());
		numPhotosText.setText("Number of Photos: " + album.getCount());
	}

}
