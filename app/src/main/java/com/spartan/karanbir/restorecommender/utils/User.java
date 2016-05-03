package com.spartan.karanbir.restorecommender.utils;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by ranaf on 5/3/2016.
 */
public class User implements Serializable{

    private String firstname, lastname, username, password;
    private JSONObject user_preference;

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public JSONObject getUser_preference() {
        return user_preference;
    }

    public void setUser_preference(JSONObject user_preference) {
        this.user_preference = user_preference;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }
}
