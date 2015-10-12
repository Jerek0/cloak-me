package com.jeremy_minie.helloagaincrm.user;

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

import android.support.annotation.Nullable;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {


    private static final String TAG = "LoginFragment";
    private LoginListener mListener;

    @Nullable
    @Bind(R.id.userName)
    TextView mLoginUserName;
    @Nullable
    @Bind(R.id.password)
    TextView mLoginPassword;

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

    @OnClick(R.id.sendButton)
    void onClick() {
        mListener.onLoginClicked(mLoginUserName.getText(), mLoginPassword.getText());
    }

    public interface LoginListener {
        void onLoginClicked(CharSequence username, CharSequence password);
    }
}
