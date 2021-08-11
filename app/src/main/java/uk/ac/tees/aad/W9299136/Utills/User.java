package uk.ac.tees.aad.W9299136.Utills;

import java.util.HashMap;

public class User {
    private String userID, username, email, address, image;

    public User(String userID, String username, String email, String address, String image) {
        this.userID = userID;
        this.username = username;
        this.email = email;
        this.address = address;
        this.image = image;
    }

    public User() {
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
