package com.jeremy_minie.helloagaincrm.logged.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jeremy_minie.helloagaincrm.R;
import com.jeremy_minie.helloagaincrm.logged.entities.User;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";
    @Bind(R.id.profileUserName) TextView mProfileUserName;
    @Bind(R.id.profileMail) TextView mProfileMail;
    @Bind(R.id.profilePassword) TextView mProfilePassword;

    private User user;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, view);

        //updateUser();

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    public void setUser(User user) {
        this.user = user;
    }

    private void updateUser() {
        mProfileUserName.setText(user.getUsername(), TextView.BufferType.EDITABLE);
        mProfileMail.setText(user.getMail(), TextView.BufferType.EDITABLE);
        mProfilePassword.setText(user.getPassword(), TextView.BufferType.EDITABLE);
    }
}
