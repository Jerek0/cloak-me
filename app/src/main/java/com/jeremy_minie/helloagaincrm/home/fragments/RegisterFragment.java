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

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends Fragment {

    private static final String TAG = "RegisterFragment";
    private RegisterListener mListener;

    @Bind(R.id.registerMail) TextView mRegisterMail;
    @Bind(R.id.registerPassword) TextView mRegisterPassword;
    @Bind(R.id.registerButton)
    public Button mRegisterButton;

    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (RegisterListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement RegisterFragment.RegisterListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @OnClick(R.id.registerButton)
    void onClick() {
        mRegisterButton.setEnabled(false);
        mListener.onRegisterClicked(mRegisterMail.getText(), mRegisterPassword.getText());
    }

    public interface RegisterListener {
        void onRegisterClicked(CharSequence mail, CharSequence password);
    }

}
