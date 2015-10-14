package com.jeremy_minie.helloagaincrm;

import android.app.Application;

import com.firebase.client.Firebase;

/**
 * Created by jminie on 13/10/2015.
 */
public class HelloAgainCrmApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Firebase.setAndroidContext(this);
    }
}
