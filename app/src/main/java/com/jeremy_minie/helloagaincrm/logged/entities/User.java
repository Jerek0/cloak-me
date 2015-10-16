package com.jeremy_minie.helloagaincrm.logged.entities;

import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;

import com.jeremy_minie.helloagaincrm.R;

/**
 * Created by jminie on 13/10/2015.
 */
public class User {
    private String username;
    private String mail;
    private String uid;
    private String avatar;
    private int color;

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

    public String getAvatar() {
        return avatar;
    }

    public int getColor() {
        return color;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }


    public void setColor(int color) {
        this.color = color;
    }

    public void setUsername(String value) {
        this.username = value;
    }
}
