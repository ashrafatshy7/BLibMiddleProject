package enteties;

import java.io.Serializable;
/**
 * Represents a user entity in the system.
 * Implements Serializable for object serialization.
 */
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    /** The unique ID of the user. */
    private String ID;

    /** The name of the user. */
    private String name;

    /** The phone number of the user. */
    private String phoneNumber;

    /** The email address of the user. */
    private String email;

    /** The password of the user. */
    private String password;

    /**
     * Constructs a User instance with all required details.
     *
     * @param ID The unique identifier of the user.
     * @param name The name of the user.
     * @param phoneNumber The phone number of the user.
     * @param email The email address of the user.
     * @param password The password of the user.
     */
    public User(String ID, String name, String phoneNumber, String email, String password) {
        this.ID = ID;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.password = password;
    }

    /**
     * Constructs a User instance without a password.
     *
     * @param ID The unique identifier of the user.
     * @param name The name of the user.
     * @param phoneNumber The phone number of the user.
     * @param email The email address of the user.
     */
    public User(String ID, String name, String phoneNumber, String email) {
        this.ID = ID;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    /**
     * Constructs a User instance with an ID only.
     *
     * @param ID The unique identifier of the user.
     */
    public User(String ID) {
        this.ID = ID;
    }

    /**
     * Retrieves the user ID.
     *
     * @return The user's ID.
     */
    public String getID() {
        return ID;
    }

    /**
     * Sets the user ID.
     *
     * @param ID The ID to set.
     */
    public void setID(String ID) {
        this.ID = ID;
    }

    /**
     * Retrieves the name of the user.
     *
     * @return The user's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the user.
     *
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Retrieves the phone number of the user.
     *
     * @return The user's phone number.
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Sets the phone number of the user.
     *
     * @param phoneNumber The phone number to set.
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Retrieves the email of the user.
     *
     * @return The user's email.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email of the user.
     *
     * @param email The email to set.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Retrieves the password of the user.
     *
     * @return The user's password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password of the user.
     *
     * @param password The password to set.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Returns a string representation of the user.
     *
     * @return A formatted string containing user details.
     */
    @Override
    public String toString() {
        return "User [ID=" + ID + ", name=" + name + ", phoneNumber=" + phoneNumber + ", email=" + email + "]";
    }
}
