package com.jeremy_minie.helloagaincrm.home.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.jeremy_minie.helloagaincrm.R;

import butterknife.ButterKnife;
import butterknife.Bind;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {


    private static final String TAG = "LoginFragment";

    private LoginListener mListener;
    private Context context;

    @Bind(R.id.loginMail)
    public TextView mLoginMail;
    @Bind(R.id.loginPassword)
    public TextView mLoginPassword;
    @Bind(R.id.loginButton)
    public Button mLoginButton;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            // Set our context as LoginListener (used for callbacks)
            mListener = (LoginListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement LoginFragment.LoginListener");
        }
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        // Launch butterknife binding
        ButterKnife.bind(this, view);

        // Get last used mail from shared preferences
        String lastMail = context.getSharedPreferences("cloakme", Context.MODE_PRIVATE)
                .getString("mail","");

        // If there's one -> fill the input and request focus on mLoginPassword
        if(lastMail!=null) { // TODO - Condition fails
            mLoginMail.setText(lastMail);
            mLoginPassword.requestFocus();
        }

        return view;
    }

    @OnClick(R.id.loginButton)
    void onLoginClick() {
        // Disable button to prevent spamming
        mLoginButton.setEnabled(false);

        // Call our Listener to notify the request
        mListener.onLoginClicked(mLoginMail.getText(), mLoginPassword.getText());

        // Save the mail value in sharedPreferences for later connections
        context.getSharedPreferences("cloakme", Context.MODE_PRIVATE)
                .edit()
                .putString("mail", mLoginMail.getText().toString())
                .commit();
    }

    @OnClick(R.id.registerButton)
    void onRegisterClick() {
        // Call our Listener to notify the request
        mListener.onRegisterClicked();
    }

    /**
     * Login fragment listener to implement
     */
    public interface LoginListener {
        void onLoginClicked(CharSequence username, CharSequence password);
        void onRegisterClicked();
    }
}
