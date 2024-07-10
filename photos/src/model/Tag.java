package model;

import java.io.Serializable;

/**
 * This class is a model for a tag, a photo has a tag with two attributes. The tag key
 * is in relation to the key of the tag and the tag value is in relation to the value of
 * the tag. This class implements the serializable interface.
 * @author Harshita Singal
 * @author Aarushi Chandane
 */
public class Tag implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * The key for the tag.
	 */
	private String key;
	/**
	 * The value for the tag.
	 */
	private String value;

	/**
	 * Default constructor for the tag.
	 * @param key is the tag key.
	 * @param value is the tag value.
	 */
	public Tag(String key, String value) {
		this.key = key;
		this.value = value;
	}
	
	/**
	 * @return true if the object is equal with the tag.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Tag)) {
			return false;
		}
		Tag tag = (Tag) obj;
		return tag.getValue().equals(value) && tag.getKey().equals(key);
	}

	/**
	 * @return type The tag type
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @return value The tag value
	 */
	public String getValue() {
		return value;
	}
	
	/**
	 * This function is called when the tags store in a hash set.
	 */
	@Override
	public int hashCode() {
		return value.hashCode() + key.hashCode();
	}

	/**
	 * @param key The key to set.
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * @param value The value to set.
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * @return "key : value"
	 */
	public String toString() {
		return key + " : " + value;
	}

}
