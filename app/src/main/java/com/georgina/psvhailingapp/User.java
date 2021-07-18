package com.georgina.psvhailingapp;

public class User {
    public String fullName,email,number;

    public User()
    {

    }
    public User(String fullName, String email, String number) {
        this.fullName = fullName;
        this.email = email;
        this.number= number;
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
}
