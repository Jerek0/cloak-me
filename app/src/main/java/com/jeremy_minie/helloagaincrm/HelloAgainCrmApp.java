package com.jeremy_minie.helloagaincrm;

import android.app.Application;

import com.firebase.client.Firebase;

/**
 * Created by jminie on 13/10/2015.
 *
 * This application allows to give Firebase the AppContext once
 *
 */
public class HelloAgainCrmApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Firebase.setAndroidContext(this);
    }
}
