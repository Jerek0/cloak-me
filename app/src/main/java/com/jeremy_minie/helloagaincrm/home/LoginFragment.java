package com.jeremy_minie.helloagaincrm.home;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    @Bind(R.id.loginMail) TextView mLoginMail;
    @Bind(R.id.loginPassword) TextView mLoginPassword;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (LoginListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement LoginFragment.LoginListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @OnClick(R.id.loginButton)
    void onLoginClick() {
        mListener.onLoginClicked(mLoginMail.getText(), mLoginPassword.getText());
    }

    @OnClick(R.id.registerButton)
    void onRegisterClick() {
        mListener.onRegisterClicked();
    }

    public interface LoginListener {
        void onLoginClicked(CharSequence username, CharSequence password);
        void onRegisterClicked();
    }
}
