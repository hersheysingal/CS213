package model;

import java.io.Serializable;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

/**
 * SerializableImage serializes an image. Records the height, width, and 2D-array of pixels
 * then maps these values in order to be serialized.
 * This model implements Serializable.
 * @author Harshita Singal
 * @author Aarushi Chandane
 *  */
public class SerializableImage implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/**
	 * The width of the image in pixels.
	 */
	private int width;
	/**
	 * The height of the image in pixels.
	 */
	private int height;
	/**
	 * A two-dimensional array of integers representing the pixels of the image.
	 */
	private int[][] pixels;

	/**
	 * Default Constructor
	 */
	public SerializableImage() {
	
	}
	
	/**
	 * This method checks if two images are equal. Serializable images are equivalent
	 * if their attributes are the same.
	 * @param image is the serializable image to be checked.
	 * @return true if they're equal.
	 */
	public boolean equals(SerializableImage image) {
		if (width != image.getWidth()) {
			return false;
		}	
		if (height != image.getHeight()) {
			return false;
		}
		for(int i = 0; i < width; i++) {
			for(int j = 0; j < height; j++) {
				if (pixels[i][j] != image.getPixels()[i][j]) {
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * @return height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * This method converts a given 2D-array (pixels) into an image.
	 * @return image
	 */
	public Image getImage() {
		WritableImage image = new WritableImage(width, height);
		PixelWriter write = image.getPixelWriter();
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				write.setArgb(i, j, pixels[i][j]);
			}
		}
		return image;
	}
	
	/**
	 * @return pixels is an int 2D-array.
	 */
	public int[][] getPixels() {
		return pixels;
	}

	/**
	 * @return width
	 */
	public int getWidth() {
		return width;
	}
	
	/**
	 * This method converts an image into a 2D-array of pixels.
	 * @param image is the image to be converted.
	 */
	public void setImage(Image image) {
		width = ((int) image.getWidth());
		height = ((int) image.getHeight());
		pixels = new int[width][height];
		PixelReader read = image.getPixelReader();
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				pixels[i][j] = read.getArgb(i, j);
			}
		}			
	}

}
