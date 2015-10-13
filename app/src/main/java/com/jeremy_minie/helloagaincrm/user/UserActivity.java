package com.jeremy_minie.helloagaincrm.user;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.jeremy_minie.helloagaincrm.R;
import com.jeremy_minie.helloagaincrm.home.MainActivity;

public class UserActivity extends AppCompatActivity {

    private User user;
    private static final String TAG = "UserActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        Toolbar toolbar = (Toolbar) findViewById(R.id.userToolbar);
        toolbar.setTitle("Your profile");
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        user = new User(intent.getStringExtra(MainActivity.USERNAME), intent.getStringExtra(MainActivity.MAIL),intent.getStringExtra(MainActivity.PASSWORD));

        Snackbar.make(findViewById(R.id.mainContainer), "You are now logged in :) !", Snackbar.LENGTH_LONG)
                .setAction("DISMISS", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "Dismiss");
                    }
                })
                .show();
    }
}
