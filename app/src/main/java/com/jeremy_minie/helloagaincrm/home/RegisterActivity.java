package com.jeremy_minie.helloagaincrm.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.firebase.client.FirebaseError;
import com.jeremy_minie.helloagaincrm.util.FirebaseManager;
import com.jeremy_minie.helloagaincrm.R;
import com.jeremy_minie.helloagaincrm.home.fragments.RegisterFragment;

import java.util.Map;

/**
 *
 * RegisterActivity
 *
 * Uses RegisterFragment
 *
 */
public class RegisterActivity extends AppCompatActivity implements RegisterFragment.RegisterListener, FirebaseManager.FirebaseRegisterListener {

    private static final String TAG = "RegisterActivity";
    public static final String INFO = TAG + ".Info";
    public static final String MAIL = TAG + ".Mail";
    public static final String PWD = TAG + ".Password";

    private String current_mail;
    private String current_pwd;

    private RegisterFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Add a toolbar w/ title and cross to close
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Register");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(android.R.drawable.ic_menu_close_clear_cancel);

        // Adds register fragment
        fragment = new RegisterFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.mainContainer, fragment).commit();
    }

    /**
     * OnOptionsItemSelected
     *
     * Allows to finish the current activity when home button (cross) is clicked instead of a new Intent to MainActivity
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * RegisterListener - On Register button is clicked -> Try to create a user
     * @param mail
     * @param password
     */
    @Override
    public void onRegisterClicked(CharSequence mail, CharSequence password) {
        // Get the data
        current_mail = mail.toString();
        current_pwd = password.toString();
        // Create a user (password is given for AES encryption of RSA private key, but it is not stored afterwards)
        FirebaseManager.getInstance().createUser(current_mail, current_pwd, this);
    }

    /**
     * FirebaseRegisterListener - On successful registration -> Get back to MainActivity
     * @param stringObjectMap
     */
    @Override
    public void onSuccessRegister(Map<String, Object> stringObjectMap) {
        fragment.mRegisterButton.setEnabled(true);

        // Firebase created a user on its own authentication system, but we have to store other datas
        // Tells firebase to generate a user to our users list
        FirebaseManager.getInstance().generateUser(stringObjectMap.get("uid").toString(), current_mail, current_pwd);

        // Get back to MainActivity with some extras to pre-fill inputs
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(INFO, "You are now registered, please login");
        intent.putExtra(MAIL, current_mail);
        intent.putExtra(PWD, current_pwd);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

        finish();
    }

    /**
     * On Registration failure -> Show error message in a toast
     *
     * @param firebaseError
     */
    @Override
    public void onError(FirebaseError firebaseError) {
        fragment.mRegisterButton.setEnabled(true);
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
