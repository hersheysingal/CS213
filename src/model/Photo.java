package model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.GridPane;

/**
 * This class is a model for a photo.
 * This class models a photo, implements the Serializable interface.
 * @author Harshita Singal
 * @author Aarushi Chandane
 */
public class Photo implements Serializable {

	
	private static final long serialVersionUID = 1L;
	
	/**
	 * The image represented as a SerializableImage object.
	 */
	private SerializableImage image;
	/**
	 * The caption of the photo.
	 */
	private String caption;
	/**
	 * A list of tags associated with the photo.
	 */
	private List<Tag> tags;
	/**
	 * The date and time when the photo was taken, represented as a Calendar object.
	 */
	private Calendar calendar;

	/**
	 * Default Photo Constructor
	 */
	public Photo() {
		caption = "";
		tags = new ArrayList<Tag>();
		calendar = Calendar.getInstance();
		calendar.set(Calendar.MILLISECOND, 0);
		image = new SerializableImage();
	}

	/**
	 * Default photo constructor given an image.
	 * @param image The image being set
	 */
	public Photo(Image image) {
		this();
		this.image.setImage(image);
	}

	/**
	 * Default photo constructor given a path 
	 * @param path The image path
	 */
	public Photo(String path) {
		// TODO Auto-generated constructor stub
		
	}

	/**
	 * @param key The tag key
	 * @param value The tag value
	 */
	public void addTag(String key, String value) {
		tags.add(new Tag(key, value));
	}

	/**
	 * This method changes the values of the tag key and tag value at a selected index.
	 * @param index is the tag index to be changed.
	 * @param key is the tag key to be changed.
	 * @param value is the tag value to be changed.
	 */
	public void editTag(int index, String key, String value) {
		tags.get(index).setKey(key);
		tags.get(index).setValue(value);
	}
	
	
	/**
	 * @return Caption gets returned
	 */
	public String getCaption() {
		return caption;
	}
	
	/**
	 * @return Calendar gets returned
	 */
	public Calendar getCalendar() {
		return calendar;
	}
	
	
	/**
	 * @param index is the index of the tag.
	 */
	public Tag getTag(int index) {
		return tags.get(index);
	}
	
	/**
	 * @return List of tags gets returned
	 */
	public List<Tag> getTags() {
		return tags;
	}

	/**
	 * @param index is the index of the tag to be removed.
	 */
	public void removeTag(int index) {
		tags.remove(index);
	}

	/**
	 * @param caption is the caption of the image.
	 */
	public void setCaption(String caption) {
		this.caption = caption;
	}

	/**
	 * @return the date using the calendar.
	 */
	public String getDate() {
		String[] str = calendar.getTime().toString().split("\\s+");
		return str[0] + " " + str[1] + " " + str[2] + ", " + str[5];
	}

	/**
	 * @return image
	 */
	public Image getImage() {
		return image.getImage();
	}
	
	/**
	 * @return The serializable image
	 */
	public SerializableImage getSerializableImage() {
		return image;
	}

	/**
	 * This method uses a hashset of tags, checks if the parameter is a subset of tags
	 * as well.
	 * @param tagList is the list being compared.
	 * @return true if tagList is a subset of tags.
	 */
	public boolean hasSubset(List<Tag> tagList) {
		Set<Tag> allTags = new HashSet<Tag>();
		allTags.addAll(tags);
		for (Tag tag : tagList) {
			if(!allTags.contains(tagList)) {
				return false;
			}		
		}
		return true;
	}

	/**
	 * @param fromDate is the LocalDate from.
	 * @param toDate is the LocalDate to.
	 * @return true if photo date is within range.
	 */
	public boolean isWithinDateRange(LocalDate fromDate, LocalDate toDate) {
		LocalDate date = calendar.getTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		return date.isBefore(toDate) && date.isAfter(fromDate) || date.equals(fromDate) || date.equals(toDate);
	}

}
