package com.jeremy_minie.helloagaincrm.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.firebase.client.FirebaseError;
import com.jeremy_minie.helloagaincrm.util.FirebaseManager;
import com.jeremy_minie.helloagaincrm.R;
import com.jeremy_minie.helloagaincrm.home.fragments.LoginFragment;
import com.jeremy_minie.helloagaincrm.logged.LoggedActivity;
import com.jeremy_minie.helloagaincrm.util.generators.QuotesFactory;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 *
 * HOME Activity
 *
 * Uses LoginFragment
 *
 */
public class MainActivity extends AppCompatActivity implements LoginFragment.LoginListener, FirebaseManager.FirebaseAuthListener {

    private static final String TAG = "MainActivity";
    public static final String USERNAME = TAG+".username";
    public static final String PASSWORD = TAG+".password";
    public static final String MAIL = TAG+".mail";

    private LoginFragment fragment;

    @Bind(R.id.quoteCarousel)
    TextView mQuoteCarousel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);

        // Adds login fragment
        fragment = new LoginFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.mainContainer, fragment).commit();

        // Launch butterknife binding
        ButterKnife.bind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Load another quote when getting back to this activity
        mQuoteCarousel.setText(QuotesFactory.getInstance().randomQuote());
    }

    /**
     * The activity being a SingleTop, each Intent is received here after the first one
     * @param intent
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        // Shows a snackbar if an INFO extra is given to the activity, in order to notify the user
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
        // Pre-fill mail and password inputs if coming from register activity
        if(intent.getStringExtra(RegisterActivity.MAIL) != null) {
            fragment.mLoginMail.setText(intent.getStringExtra(RegisterActivity.MAIL));
        }
        if(intent.getStringExtra(RegisterActivity.PWD) != null) {
            fragment.mLoginPassword.setText(intent.getStringExtra(RegisterActivity.PWD));
        }
    }

    /**
     * LoginListener - On login button clicked -> FirebaseAuth
     *
     * @param mail
     * @param password
     */
    @Override
    public void onLoginClicked(final CharSequence mail, final CharSequence password) {
        FirebaseManager.getInstance().authWithPassword(mail.toString(), password.toString(), this);
    }

    /**
     * LoginListener - On register button clicked -> open RegisterActivity
     */
    @Override
    public void onRegisterClicked() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    /**
     * FirebaseAuthListener - On successful authentication -> launch LoggedActivity
     */
    @Override
    public void onSuccessAuth() {
        fragment.mLoginButton.setEnabled(true);
        fragment.mLoginPassword.setText("");
        Intent intent = new Intent(this, LoggedActivity.class);
        startActivity(intent);
    }

    /**
     * FirebaseAuthListener - On failed authentication -> Displays error message in a Snackbar
     * @param firebaseError
     */
    @Override
    public void onError(FirebaseError firebaseError) {
        fragment.mLoginButton.setEnabled(true);
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
