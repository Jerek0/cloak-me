package com.jeremy_minie.helloagaincrm.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.jeremy_minie.helloagaincrm.R;
import com.jeremy_minie.helloagaincrm.home.LoginFragment;
import com.jeremy_minie.helloagaincrm.home.RegisterFragment;
import com.jeremy_minie.helloagaincrm.user.UserActivity;
import com.jeremy_minie.helloagaincrm.user.User;

public class MainActivity extends AppCompatActivity implements LoginFragment.LoginListener, RegisterFragment.RegisterListener {

    private static final String TAG = "MainActivity";
    public static final String USERNAME = TAG+".username";
    public static final String PASSWORD = TAG+".password";
    public static final String MAIL = TAG+".mail";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("HelloAgainCrm");
        setSupportActionBar(toolbar);

        // Adds login fragment
        LoginFragment fragment = new LoginFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.mainContainer, fragment).commit();

        // FLOATING BTN
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Snackbar.make(view, "CRM14 - Java Android, Rappels", Snackbar.LENGTH_LONG)
                                .setAction("Close", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Log.d(TAG, "close");
                                    }
                                }).show();
                    }
                }
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.menuMainLoginItem) {
            LoginFragment fragment = new LoginFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer, fragment).commit();
            return true;
        } else if (item.getItemId() == R.id.menuMainRegisterItem) {
            RegisterFragment fragment = new RegisterFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer, fragment).commit();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLoginClicked(CharSequence username, CharSequence password) {
        Log.d(TAG, "onClickedLoginBtn");
        // TODO - Opens new activity
        if(username.length() > 0 && password.length() > 0) {
            Intent intent = new Intent(this, UserActivity.class);
            intent.putExtra(USERNAME, username);
            intent.putExtra(PASSWORD, password);
            startActivity(intent);
        } else {
            Snackbar.make(findViewById(R.id.mainContainer), "Username and password don't match !", Snackbar.LENGTH_SHORT)
                    .setAction("DISMISS", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.d(TAG, "Dismiss");
                        }
                    })
                    .show();
        }

        // TODO - Use this after opening new activity
        /*Snackbar.make(findViewById(R.id.mainContainer), "Click", Snackbar.LENGTH_SHORT)
                .setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "undo");
                    }
                })
                .show();*/
    }

    @Override
    public void onRegisterClicked(CharSequence username, CharSequence mail, CharSequence password) {
        // TODO - Opens new activity
        Log.d(TAG, "onClickedRegisteerBtn " + username + " " + password);
    }
}
