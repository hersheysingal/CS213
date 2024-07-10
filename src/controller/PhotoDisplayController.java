package controller;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.Album;
import model.Photo;
import model.Tag;
import model.User;
import model.UserList;

/**
 * This controller controls the PhotoList.fxml and PhotoSearch.fxml files. 
 * This controller implements the ErrorMessage and Logout interfaces.
 * @author Harshita Singal
 * @author Aarushi Chandane
 */
public class PhotoDisplayController implements ErrorMessage, Logout {

	/**
     * Indicates that the photo was selected from a photo list.
     */
	public final static int CAME_FROM_PHOTO_LIST = 0;
	/**
     * Indicates that the photo was found using a search.
     */
	public final static int CAME_FROM_PHOTO_SEARCH = 1;

	@FXML
	ListView<Tag> tagListView;

	@FXML
	ImageView photoImageView;

	@FXML
	Text captionText, photoDateText;

	@FXML
	Button previousPhotoBtn, nextPhotoBtn;

	private ObservableList<Tag> obsList;
	private int photoIndex;
	private Album album;
	private User user;
	private List<Photo> photos;
	private UserList userList;
	private int key;

	/**
	 * This method updates all photo details and initializes on the start.
	 * @param mainStage initializes the start
	 */
	
	
	
	public void start(Stage mainStage) {
		if (key == CAME_FROM_PHOTO_LIST) {
			photos = album.getPhotos();
		}
		updatePhotoDetails();
	}
	
	/**
	 * This method allows the user to add a tag to the displayed photo.
	 * @param event triggers this method.
	 */
	@FXML
	protected void addTag(ActionEvent event) {
		Dialog<Album> dialog = new Dialog<>();
		dialog.setTitle("Tag Addition");
		dialog.setHeaderText("Please enter a tag for this photo.");
		dialog.setResizable(true);
		Label keyLabel = new Label("Tag Key: ");
		TextField keyTextField = new TextField();
		Label valueLabel = new Label("Tag Value: ");
		TextField valueTextField = new TextField();
		GridPane grid = new GridPane();
		grid.add(keyLabel, 1, 1);
		grid.add(keyTextField, 2, 1);
		grid.add(valueLabel, 1, 2);
		grid.add(valueTextField, 2, 2);
		dialog.getDialogPane().setContent(grid);
		ButtonType enterButton = new ButtonType("Enter", ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().add(enterButton);
		Optional<Album> result = dialog.showAndWait();
		String error = checkFields(keyTextField.getText(), valueTextField.getText());
		if (result.isPresent()) {
			if (error != null) {
				showError(error);
			} else {
				Photo photo = photos.get(photoIndex);
				photo.addTag(keyTextField.getText(), valueTextField.getText());
				updatePhotoDetails();
				try {
					UserList.write(userList);
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Allows the user to return to the previous scene. If the key equals CAME_FROM_PHOTO_LIST,
	 * the user will return to the PhotoList scene, otherwise the user will return to the 
	 * CAME_FROM_PHOTO_SEARCH scene. 
	 * @param event triggers this method.
	 * @throws ClassNotFoundException Exception for switching scenes
	 */
	@FXML
	protected void back(ActionEvent event) throws ClassNotFoundException {
		Parent parent;
		try {
			if(key == CAME_FROM_PHOTO_LIST) {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/PhotoList.fxml"));
				parent = (Parent) loader.load();
				PhotoListController ctrl = loader.<PhotoListController>getController();
				ctrl.setUser(user);
				ctrl.setAlbum(album);
				ctrl.setUserList(userList);
				Scene scene = new Scene(parent);
				Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
				ctrl.start(stage);
				stage.setScene(scene);
				stage.show();
			}
			if (key == CAME_FROM_PHOTO_SEARCH) {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/PhotoSearch.fxml"));
				parent = (Parent) loader.load();
				PhotoSearchController ctrl = loader.<PhotoSearchController>getController();
				ctrl.setUser(user);
				ctrl.setUlist(userList);
				Scene scene = new Scene(parent);
				Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
				ctrl.start(stage);
				stage.setScene(scene);
				stage.show();
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This method checks if the proper fields are filled and returns null if no errors
	 * are found.
	 * @param key of the tag being checked.
	 * @param value of the tag being checked
	 * @return an error message for the missing fields and null if none found.
	 */
	private String checkFields(String key, String value) {
		for(Tag tag : photos.get(photoIndex).getTags()) {
			if (tag.getKey().equals(key) && tag.getValue().equals(value)) {
				return "This tag already is present for this photo. Please enter a new tag.";
			}
		}
		if(key.equals("") || value.equals("")) {
			return "Please enter in a key & value!";
		}
		return null;
	}
	
	/**
	 * This method allows the user to delete a tag.
	 * @param event triggers this method.
	 * @param tag is the tag to be deleted.
	 */
	public void deleteTag(ActionEvent event, Tag tag) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Tag Deletion");
		alert.setHeaderText("Are you sure you want to delete the following?");
		String text = "Click 'OK' to delete: " + tag.getKey() + " : " + tag.getValue() + ".";
		alert.setContentText(text);
		Optional<ButtonType> result = alert.showAndWait();
		if (result.isPresent()) {
			Photo photo = photos.get(photoIndex);
			for(int i = 0; i < photo.getTags().size(); i++) {
				if (photo.getTag(i).equals(tag)) {
					photo.removeTag(i);
					updatePhotoDetails();
					try {
						UserList.write(userList);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	/**
	 * This method allows for the user to edit a tag.
	 * @param event triggers this method.
	 * @param tag is the tag to be edited.
	 */
	public void editTag(ActionEvent event, Tag tag) {
		Dialog<User> dialog = new Dialog<>();
		dialog.setTitle("Edit Tag");
		dialog.setHeaderText("Please enter a new key or value for the tag!");
		dialog.setResizable(true);
		Label keyLabel = new Label("Tag Key: ");
		TextField keyTextField = new TextField();
		keyTextField.setText(tag.getKey());
		Label valueLabel = new Label("Tag Value: ");
		TextField valueTextField = new TextField();
		valueTextField.setText(tag.getValue());
		GridPane grid = new GridPane();
		grid.add(keyLabel, 1, 1);
		grid.add(keyTextField, 2, 1);
		grid.add(valueLabel, 1, 2);
		grid.add(valueTextField, 2, 2);
		dialog.getDialogPane().setContent(grid);
		ButtonType enterButton = new ButtonType("Enter", ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().add(enterButton);
		Optional<User> result = dialog.showAndWait();
		String error = checkFields(keyTextField.getText(), valueTextField.getText());
		if (result.isPresent()) {
			if (error != null) {
				showError(error);
			} else {
				Photo photo = photos.get(photoIndex);
				for(int i = 0; i < photo.getTags().size(); i++) {
					if (photo.getTag(i).equals(tag)) {
						photo.editTag(i, keyTextField.getText(), valueTextField.getText());
						updatePhotoDetails();
						try {
							UserList.write(userList);
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
				}
			}
		}
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
	 * This method displays the next photo of the album and or list of photos.
	 * @param event triggers this method.
	 */
	@FXML
	protected void nextPhoto(ActionEvent event) {
		photoIndex++;
		updatePhotoDetails();
	}

	/**
	 * 
	 * This method displays the previous photo of the album and or list of photos.
	 * @param event triggers this method.
	 */
	@FXML
	protected void previousPhoto(ActionEvent event) {
		photoIndex--;
		updatePhotoDetails();
	}

	/**
	 * This method is called when toggling through photos and updates the image,
	 * caption, and tags.
	 */
	public void updatePhotoDetails() {
		photoImageView.setImage(photos.get(photoIndex).getImage());
		captionText.setText("Caption: " + photos.get(photoIndex).getCaption());
		photoDateText.setText("Photo Date: " + photos.get(photoIndex).getDate());
		obsList = FXCollections.observableArrayList(photos.get(photoIndex).getTags());
		tagListView.setCellFactory(new Callback<ListView<Tag>, ListCell<Tag>>() {
			@Override
			public ListCell<Tag> call(ListView<Tag> tag) {
				return new TagCell();
			}
		});
		tagListView.setItems(obsList);
		previousPhotoBtn.setDisable(photoIndex == 0);
		nextPhotoBtn.setDisable(photoIndex == photos.size() - 1);
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
	 * @param index The photo index to set.
	 */
	public void setPhotoIndex(int index) {
		photoIndex = index;
	}
	/**
	 * @param userList The userList to set.
	 */
	public void setUserList(UserList userList) {
		this.userList = userList;
	}
	/**
	 * @param photos The photos to set.
	 */
	public void setPhotos(List<Photo> photos) {
		this.photos = photos;
	}
	/**
	 * @param key The key to set.
	 */
	public void setKey(int key) {
		this.key = key;
	}

	/**
	 * The overridden ListCell displays the Tag data in the ListView.
	 */
	private class TagCell extends ListCell<Tag> {
		AnchorPane apane = new AnchorPane();
		Label tagLabel = new Label();
		Button deleteTagBtn = new Button("Delete");
		Button editTagBtn = new Button("Edit");
		public TagCell() {
			super();
			AnchorPane.setLeftAnchor(tagLabel, 0.0);
			AnchorPane.setTopAnchor(tagLabel, 0.0);
			AnchorPane.setRightAnchor(deleteTagBtn, 0.0);
			AnchorPane.setTopAnchor(deleteTagBtn, 0.0);
			AnchorPane.setRightAnchor(editTagBtn, 80.0);
			AnchorPane.setTopAnchor(editTagBtn, 0.0);
			tagLabel.setMaxWidth(200.0);
			deleteTagBtn.setVisible(false);
			editTagBtn.setVisible(false);
			apane.getChildren().addAll(tagLabel, deleteTagBtn, editTagBtn);
			setGraphic(apane);
		}

		@Override
		public void updateItem(Tag tag, boolean empty) {
			super.updateItem(tag, empty);
			setText(null);
			if (tag != null) {
				tagLabel.setText(tag.toString());
				deleteTagBtn.setVisible(true);
				editTagBtn.setVisible(true);
				deleteTagBtn.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						deleteTag(event, tag);
					}
				});
				editTagBtn.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						editTag(event, tag);
					}
				});
			}
		}
	}

}
