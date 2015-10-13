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

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.jeremy_minie.helloagaincrm.R;
import com.jeremy_minie.helloagaincrm.user.UserActivity;

import java.util.Map;

public class MainActivity extends AppCompatActivity implements LoginFragment.LoginListener, RegisterFragment.RegisterListener {

    private static final String TAG = "MainActivity";
    public static final String USERNAME = TAG+".username";
    public static final String PASSWORD = TAG+".password";
    public static final String MAIL = TAG+".mail";

    private Firebase fbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);

        Firebase.setAndroidContext(this);
        fbRef = new Firebase("https://helloagaincrm.firebaseio.com/");

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
    public void onLoginClicked(final CharSequence username, final CharSequence password) {
        final MainActivity scope = this;

        Log.d(TAG, "onClickedLoginBtn");
        if(username.length() > 0 && password.length() > 0) {
            fbRef.authWithPassword(username.toString(), password.toString(), new Firebase.AuthResultHandler() {
                @Override
                public void onAuthenticated(AuthData authData) {
                    System.out.println("User ID: " + authData.getUid() + ", Provider: " + authData.getProvider());

                    Intent intent = new Intent(scope, UserActivity.class);
                    intent.putExtra(USERNAME, username);
                    intent.putExtra(MAIL, username);
                    intent.putExtra(PASSWORD, password);
                    startActivity(intent);
                }
                @Override
                public void onAuthenticationError(FirebaseError firebaseError) {
                    Log.e(TAG,firebaseError.toString());
                    Snackbar.make(findViewById(R.id.mainContainer), firebaseError.getMessage(), Snackbar.LENGTH_SHORT)
                            .setAction("DISMISS", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Log.d(TAG, "Dismiss");
                                }
                            })
                            .show();
                }
            });
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
    }

    @Override
    public void onRegisterClicked(CharSequence username, CharSequence mail, CharSequence password) {
        Log.d(TAG, "onClickedRegisteerBtn " + username + " " + password);
        if(username.length() > 0 && password.length() > 0 && mail.length() > 0) {
            fbRef.createUser(mail.toString(), password.toString(), new Firebase.ValueResultHandler<Map<String, Object>>() {
                @Override
                public void onSuccess(Map<String, Object> result) {
                    System.out.println("Successfully created user account with uid: " + result.get("uid"));

                    LoginFragment fragment = new LoginFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer, fragment).commit();

                    Snackbar.make(findViewById(R.id.mainContainer), "You are now registered, please login", Snackbar.LENGTH_SHORT)
                            .setAction("DISMISS", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Log.d(TAG, "Dismiss");
                                }
                            })
                            .show();
                }
                @Override
                public void onError(FirebaseError firebaseError) {
                    // there was an error
                    Log.e(TAG,firebaseError.toString());
                    Snackbar.make(findViewById(R.id.mainContainer), firebaseError.getMessage(), Snackbar.LENGTH_SHORT)
                            .setAction("DISMISS", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Log.d(TAG, "Dismiss");
                                }
                            })
                            .show();
                }
            });
        } else {
            Snackbar.make(findViewById(R.id.mainContainer), "You have to fill all inputs", Snackbar.LENGTH_SHORT)
                    .setAction("DISMISS", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.d(TAG, "Dismiss");
                        }
                    })
                    .show();
        }
    }
}
