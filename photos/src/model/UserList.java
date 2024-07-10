
package model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This class maintains the list of users for the program. The list of users are
 * serialized in the user.dat file in the dat folder of the project.
 * @author Harshita Singal
 * @author Aarushi Chandane
 */

public class UserList implements Serializable {

	private static final long serialVersionUID = 1L;
	/**
     * The directory where data files are stored.
     */
	public static final String storeDir = "dat";
	/**
     * The filename of the users data file.
     */
	public static final String storeFile = "users.dat";
	/**
	 * The list of users.
	 */
	private List<User> users;

	/**
	 * Default constructor for the UserList.
	 */
	public UserList() {
		users = new ArrayList<User>();
	}

	/**
	 * This method adds a user to the overall list of users.
	 * @param user is the user to be added to the list.
	 */
	public void addUserToList(User user) {
		users.add(user);
	}
	
	/**
	 * @param username is the user's username.
	 * @return user with the same username.
	 */
	public User getUserByUsername(String username) {
		for (User user : users) {
			if (user.getUsername().equals(username))
				return user;
		}
		return null;
	}
	
	/**
	 * This method returns the overall list of users for the program.
	 * @return users currently stored in the program.
	 */
	public List<User> getUserList() {
		return users;
	}
	
	/**
	 * This method checks is a username and password are present in the list.
	 * @param username is the username to be checked for in the overall list.
	 * @param password is the password to be checked for in the overall list.
	 * @return true if the combination exists.
	 */
	public boolean isUserInList(String username, String password) {
		for (User user : users) {
			if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * This reads the users.dat file and returns the list model containing the users.
	 * @return return the UserList model of all users.
	 * @throws IOException exception for serialization.
	 * @throws ClassNotFoundException exception for serialization.
	 */
	public static UserList read() throws IOException, ClassNotFoundException {
		ObjectInputStream input = new ObjectInputStream(new FileInputStream(storeDir + File.separator + storeFile));
		UserList list = (UserList) input.readObject();
		input.close();
		return list;
	}

	/**
	 * This method removes a specified user from the list.
	 * @param user is the user to be removed from the list.
	 */
	public void removeUserFromList(User user) {
		users.remove(user);
	}

	/**

	 * @return formatted user.
	 */
	public String toString() {
		if (users == null) {
			return "No users!";
		}
		String text = "";
		for(User user : users) {
			text += user.getUsername() + " ";
		}
		return text;
	}
	
	/**
	 * This method checks to see if the specified username exists.
	 * @param username is the username to be checked from the overall list.
	 * @return true if the username exists in the list.
	 */
	public boolean userExists(String username) {
		for (User user : users) {
			if (user.getUsername().equals(username)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Given the UserList model, write this data into users.dat, overwriting
	 * anything on there.
	 * 
	 * @param userList The UserList model to write with
	 * @throws IOException Exception for serialization
	 */
	public static void write(UserList userList) throws IOException {
		ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(storeDir + File.separator + storeFile));
		output.writeObject(userList);
		output.close();
	}

}
