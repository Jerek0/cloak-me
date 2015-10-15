package com.jeremy_minie.helloagaincrm.user;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.jeremy_minie.helloagaincrm.FirebaseManager;
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
        ProfileFragment fragment = new ProfileFragment();
        fragment.setUser(new User(intent.getCharSequenceExtra(MainActivity.USERNAME), intent.getCharSequenceExtra(MainActivity.MAIL), intent.getCharSequenceExtra(MainActivity.PASSWORD)));
        getSupportFragmentManager().beginTransaction().add(R.id.mainContainer, fragment).commit();

        Snackbar.make(findViewById(R.id.mainContainer), "You are now logged in :) !", Snackbar.LENGTH_LONG)
                .setAction("DISMISS", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "Dismiss");
                    }
                })
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        FirebaseManager.getInstance().unAuth();
    }
}
