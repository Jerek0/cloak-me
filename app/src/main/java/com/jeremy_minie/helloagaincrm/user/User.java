package com.jeremy_minie.helloagaincrm.user;

/**
 * Created by jminie on 13/10/2015.
 */
public class User {
    private CharSequence username;
    private CharSequence mail;
    private CharSequence password;

    public User(CharSequence u, CharSequence m, CharSequence p) {
        username = u;
        mail = m;
        password = p;
    }

    public CharSequence getUsername() {
        return username;
    }

    public CharSequence getMail() {
        return mail;
    }

    public CharSequence getPassword() {
        return password;
    }
}
