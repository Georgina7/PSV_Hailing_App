package com.georgina.psvhailingapp;

public class User {
    public String fullName,email,number, profileImagePath;

    public User(){ }

    public User(String fullName, String email, String number, String profileImagePath) {
        this.fullName = fullName;
        this.email = email;
        this.number= number;
        this.profileImagePath = profileImagePath;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getProfileImagePath() {
        return profileImagePath;
    }

    public void setProfileImagePath(String profileImagePath) {
        this.profileImagePath = profileImagePath;
    }
}
