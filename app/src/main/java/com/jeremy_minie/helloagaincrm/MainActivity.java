package com.jeremy_minie.helloagaincrm;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.jeremy_minie.helloagaincrm.user.LoginFragment;

public class MainActivity extends AppCompatActivity implements LoginFragment.LoginListener {

    private static final String TAG = "MainActivity";

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
            // Login
            return true;
        } else if (item.getItemId() == R.id.menuMainLoginItem) {
            // register
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLoginClicked(CharSequence username, CharSequence password) {
        Log.d(TAG, "onClickedSendBtn " + username + " " + password);
        Snackbar.make(findViewById(R.id.mainContainer), "Click", Snackbar.LENGTH_SHORT)
                .setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "undo");
                    }
                })
                .show();
    }
}
