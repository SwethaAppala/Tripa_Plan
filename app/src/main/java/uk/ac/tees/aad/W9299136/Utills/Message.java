package uk.ac.tees.aad.W9299136.Utills;

public class Message {
     String message,key,userID,date,username;

    public Message(String message, String key, String userID, String date, String username) {
        this.message = message;
        this.key = key;
        this.userID = userID;
        this.date = date;
        this.username = username;
    }

    public Message() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
