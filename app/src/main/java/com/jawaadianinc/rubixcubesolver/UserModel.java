package com.jawaadianinc.rubixcubesolver;

import org.jetbrains.annotations.NotNull;

public class UserModel {

    //This is the user class model, each user has a unique ID based on their Google account along with a name and email

    private String id;
    private String name;
    private String email;
    private String date;

    //Constructor
    public UserModel(String id, String name, String email, String date) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.date = date;
    }

    public UserModel() {
    }

    @NotNull
    @Override
    //For calling it to a string format
    public String toString() {
        return "Name: " + name + "\nEmail: " + email + "\nDate Joined: " + date;

//        return "UserModel{" +
//                "id=" + id +
//                ", name='" + name + '\'' +
//                ", email='" + email + '\'' +
//                '}';
    }

    //Getters and setters in Kotlin.
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
