package com.jeremy_minie.helloagaincrm;

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
import com.firebase.client.FirebaseError;
import com.jeremy_minie.helloagaincrm.home.LoginFragment;
import com.jeremy_minie.helloagaincrm.home.RegisterFragment;
import com.jeremy_minie.helloagaincrm.user.UserActivity;

import java.util.Map;

public class MainActivity extends AppCompatActivity implements LoginFragment.LoginListener, FirebaseManager.FirebaseAuthListener {

    private static final String TAG = "MainActivity";
    public static final String USERNAME = TAG+".username";
    public static final String PASSWORD = TAG+".password";
    public static final String MAIL = TAG+".mail";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);

        // Adds login fragment
        LoginFragment fragment = new LoginFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.mainContainer, fragment).commit();

        Intent intent = getIntent();

        if(intent.getStringExtra(RegisterActivity.INFO) != null) {
            Snackbar.make(findViewById(R.id.mainContainer), intent.getStringExtra(RegisterActivity.INFO), Snackbar.LENGTH_SHORT)
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
    public void onLoginClicked(final CharSequence mail, final CharSequence password) {
        Log.d(TAG, "onClickedLoginBtn");
        FirebaseManager.getInstance().authWithPassword(mail.toString(), password.toString(), this);
    }

    @Override
    public void onRegisterClicked() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    @Override
    public void onSuccessAuth(AuthData authData) {
        System.out.println("User ID: " + authData.getUid() + ", Provider: " + authData.getProvider());

        Intent intent = new Intent(this, UserActivity.class);
        intent.putExtra(MAIL, authData.getProviderData().get("email").toString());
        startActivity(intent);
    }

    @Override
    public void onError(FirebaseError firebaseError) {
        Log.e(TAG, firebaseError.toString());
        Snackbar.make(findViewById(R.id.mainContainer), firebaseError.getMessage(), Snackbar.LENGTH_SHORT)
                .setAction("DISMISS", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "Dismiss");
                    }
                })
                .show();
    }
}
