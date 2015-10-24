package com.jeremy_minie.helloagaincrm.logged.entities;

import com.firebase.client.DataSnapshot;
import com.jeremy_minie.helloagaincrm.util.generators.UsernameGenerator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jminie on 13/10/2015.
 */
public class User {
    private String username;
    private String username_lower_case;
    private String mail;
    private String uid;
    private String avatar;
    private int color;

    public User() {}

    public User(String uid, String username, String mail) {
        this.uid = uid;
        this.username = username;
        this.mail = mail;
        this.color = -12813891;
        this.avatar = "https://secure.gravatar.com/avatar/de1730191e42849751feeb687ee504b1?d=retro&s=250";
    }

    public static List<User> createUsersList(int numUsers) {
        List<User> usersList = new ArrayList<User>();

        for (int i = 1; i <= numUsers; i++) {
            usersList.add(new User("", UsernameGenerator.getInstance().newUsername(), ""));
        }

        return usersList;
    }

    public String getUsername() {
        return username;
    }

    public String getMail() {
        return mail;
    }

    public String getUid() {
        return uid;
    }

    public String getAvatar() {
        return avatar;
    }

    public int getColor() {
        return color;
    }

    public void fillFromSnapshot(DataSnapshot snapshot) {
        this.setUsername((String) snapshot.child("username").getValue());
        this.setMail((String) snapshot.child("mail").getValue());
        this.setColor(((Long) snapshot.child("color").getValue()).intValue());
        this.setAvatar((String) snapshot.child("avatar").getValue());
        this.setUid(snapshot.getKey());
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setMail(String value) {
        this.mail = value;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setUsername(String value) {
        this.username = value;
        this.username_lower_case = this.username.toLowerCase();
    }

    public String getUsername_lower_case() {
        return username_lower_case;
    }
}
