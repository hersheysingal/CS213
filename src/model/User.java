package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * This class is a model for a user. Each user has a full name, username, password, and
 * a list of albums. This class also implements the serializable interface.
 * @author Harshita Singal
 * @author Aarushi Chandane
 */
public class User implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * The name of the user.
	 */
	private String name;
	/**
	 * The username of the user.
	 */
	private String username;
	/**
	 * The password of the user.
	 */
	private String password;
	/**
	 * The list of the albums for the user.
	 */
	private List<Album> albums;

	/**
	 * The default constructor for the user.
	 * @param name is the name of the user.
	 * @param username is the user's username.
	 * @param password is the user's password.
	 */
	public User(String name, String username, String password) {
		this.name = name;
		this.username = username;
		this.password = password;
		albums = new ArrayList<Album>();
	}
	

	/**
	 * This method create a new album.
	 * @param albumName is to be the album name.
	 */
	public void addAlbum(String albumName) {
		albums.add(new Album(albumName));
	}
	
	/**
	 * This method adds an existing album to the list of albums.
	 * @param album is to added to the list.
	 */
	public void addAlbum(Album album) {
		this.albums.add(album);
	}
	
	/**
	 * This method checks to see whether an album of the same name exists.
	 * @param albumName is the album name to be checked before adding.
	 * @return true if the album name already exists.
	 */
	public boolean albumNameExists(String albumName) {
		for(Album album : albums) {
			if (album.getName().toLowerCase().equals(albumName.trim().toLowerCase())) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * This method adds a photo to the album at the given album index.
	 * @param photo is the item to be added.
	 * @param index is the index of the album from the album list.
	 */
	public void addPhotoToAlbum(Photo photo, int index) {
		albums.get(index).addPhoto(photo);
	}
	
	/**
	 * This method changes the user's password to the new password.
	 * @param password is to become the new password.
	 */
	public void changePassword(String password) {
		this.password = password;
	}
	
	/**
	 * @return albums
	 */
	public List<Album> getAlbums() {
		return albums;
	}
	
	/**
	 * getAlbumIndexByAlbum
	 * This method returns the index of an album in the list.
	 * @param album is the album to be checked.
	 * @return i or the index of the album.
	 */
	public int getAlbumIndexByAlbum(Album album) {
		for(int i = 0; i < albums.size(); i++) {
			if(albums.get(i).getName().equals(album.getName())) {
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * @param name is the album name.
	 * @return album if the name exists.
	 */
	public Album getAlbumByName(String name) {
		for(Album album : albums) {
			if(album.getName().equals(name)) {
				return album;
			}
		}
		return null;
	}

	/**
	 * @return name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @return password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @return username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * This method removes a selected album from the user's list of albums.
	 * @param album is the album to be removed.
	 * 
	 */
	public void removeAlbum(Album album) {
		albums.remove(album);
	}
	
}
