package photoalbum;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import model.User;
import model.UserList;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import controller.LoginController;

/**
 * @author Harshita Singal
 * @author Aarushi Chandane
 */
public class PhotoAlbum extends Application {

	Stage mainStage;
	
	@Override
	public void start(Stage primaryStage) throws IOException, ClassNotFoundException {
		UserList list = new UserList();
		list.addUserToList(new User("Administrator", "admin", "admin"));
		list.addUserToList(new User("Harshita Singal", "harshita", "harshita"));
		list.addUserToList(new User("Aarushi Chandane", "aarushi", "aarushi"));
		UserList.write(list);
		mainStage = primaryStage;
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/view/LoginMenu.fxml"));
			BorderPane root = (BorderPane)loader.load();
			Scene scene = new Scene(root, 600, 400);
			mainStage.setScene(scene);
			mainStage.setTitle("Photo Album");
			mainStage.setResizable(false);
			mainStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Launches the JavaFX application.
	 *
	 * @param args the command-line arguments
	 * @throws Exception if an error occurs during application startup
	 */
	public static void main(String[] args) {
		launch(args);
	}
}
