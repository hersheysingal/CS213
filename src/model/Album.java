package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.image.Image;

/**
 * This class models an album, it has a name, list of photos, the earliest photo, and 
 * the oldest photo in the album. This class implements the serializable interface.
 * @author Harshita Singal
 * @author Aarushi Chandane
 */
public class Album implements Serializable {

	private static final long serialVersionUID = 1L;
	/**
     * The name of the user.
     */
	private String name;
	/**
     * The list of photos associated with the user.
     */
	private List<Photo> photos;
	/**
     * The oldest photo associated with the user.
     */
	private Photo oldestPhoto;
	/**
     * The earliest photo associated with the user.
     */
	private Photo earliestPhoto;

	/**
	 *
	 * The default constructor for the Album.
	 * @param name is the name of the Album.
	 */
	public Album(String name) {
		this.name = name;
		earliestPhoto = null;
		oldestPhoto = null;
		photos = new ArrayList<Photo>();
	}
	
	/**
	 * 
	 * This method deals with adding photos to the album.
	 * @param photo Photo to be added to album
	 */
	public void addPhoto(Photo photo) {
		photos.add(photo);
		findOldestPhoto();
		findEarliestPhoto();
	}
	
	/**
	 * This method finds the earliest photo in the album.
	 */
	public void findEarliestPhoto() {
		if(photos.size() == 0) {
			return;
		}
		Photo pointer = photos.get(0);
		for(Photo photo : photos) {
			if(photo.getCalendar().compareTo(pointer.getCalendar()) > 0) {
				pointer = photo;
			}
		}
		earliestPhoto = pointer;
	}
	
	/**
	 * This method iterates through the album to find the oldest photo within the album.
	 */
	public void findOldestPhoto() {
		if(photos.size() == 0) {
			return;
		}
		Photo pointer = photos.get(0);
		for(Photo photo : photos) {
			if (photo.getCalendar().compareTo(pointer.getCalendar()) < 0) {
				pointer = photo;
			}
		}
		oldestPhoto = pointer;
	}
	
	/**
	 * 
	 * @return the number of photos in the album.
	 */
	public int getCount() {
		return photos.size();
	}
	
	/**
	 * 
	 * The getter method for the album name.
	 * @return album name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * 
	 * @return oldestPhoto
	 */
	public String getOldestPhotoDate() {
		if (oldestPhoto == null)
			return "Unable to complete request.";
		return oldestPhoto.getDate();
	}
	
	/**
	 * 
	 * 
	 * @param index index at which the photo belongs in the list of photos
	 * @return photo at index in photos list
	 */
	public Photo getPhoto(int index) {
		return photos.get(index);
	}
	
	/**
	 * 
	 * This method removes a given photo from the album.
	 * @param index is the photo to be removed.
	 */
	public void removePhoto(int index) {
		photos.remove(index);
		findOldestPhoto();
		findEarliestPhoto();
	}

	/**
	 * 
	 * The setter method for the album name.
	 * @param name Album name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * getEarliestPhotoDate
	 * 
	 * @return earliest photo date in string form
	 */
	public String getEarliestPhotoDate() {
		if (earliestPhoto == null)
			return "NA";
		return earliestPhoto.getDate();
	}

	/**
	 * getDateRange
	 * 
	 * @return date of oldest to earliest photo date
	 */
	public String getDateRange() {
		return getOldestPhotoDate() + " - " + getEarliestPhotoDate();
	}

	/**
	 * getAlbumPhoto
	 * 
	 * @return image of first photo in the list
	 */
	public Image getAlbumPhoto() {
		if(photos.isEmpty()) {
			return null;
		}	
		return photos.get(0).getImage();
	}

	/**
	 * getPhotos
	 * 
	 * @return the list of all photos in the album
	 */
	public List<Photo> getPhotos() {
		return photos;
	}

	/**
	 * findIndexByPhoto Given a photo, find the index to which it belongs in an
	 * album
	 * 
	 * @param photo The photo the index of the photo
	 * @return the index at which the photo belongs in the photos list
	 */
	public int findIndexByPhoto(Photo photo) {
		for(int i = 0; i < photos.size(); i++) {
			if(photos.get(i).equals(photo)) {
				return i;
			}
		}		
		return -1;
	}

}
