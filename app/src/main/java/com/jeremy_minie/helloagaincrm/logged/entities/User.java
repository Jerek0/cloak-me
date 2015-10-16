package com.jeremy_minie.helloagaincrm.logged.entities;

/**
 * Created by jminie on 13/10/2015.
 */
public class User {
    private String username;
    private String mail;
    private String uid;
    private String avatar;

    public User() {}

    public User(String uid, String username, String mail) {
        this.uid = uid;
        this.username = username;
        this.mail = mail;
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

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
