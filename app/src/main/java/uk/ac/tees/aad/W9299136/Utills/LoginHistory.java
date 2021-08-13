package uk.ac.tees.aad.W9299136.Utills;

public class LoginHistory {
    private String date;
    private String email;

    public LoginHistory(String date, String email) {
        this.date = date;
        this.email = email;
    }

    public LoginHistory() {
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
