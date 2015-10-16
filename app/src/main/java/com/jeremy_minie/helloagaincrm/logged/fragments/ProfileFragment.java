package com.jeremy_minie.helloagaincrm.logged.fragments;



import android.os.Bundle;
import android.support.annotation.BinderThread;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jeremy_minie.helloagaincrm.R;
import com.jeremy_minie.helloagaincrm.util.FirebaseManager;
import com.jeremy_minie.helloagaincrm.util.image.CircleTransform;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";
    private View view;

    @Bind(R.id.profileUserName) TextView mProfileUserName;
    @Bind(R.id.avatar) ImageView mAvatar;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, view);
        updateUser();

        return view;
    }

    private void updateUser() {
        mProfileUserName.setText(FirebaseManager.getInstance().getUser().getUsername(), TextView.BufferType.EDITABLE);
        Picasso.with(view.getContext()).load(FirebaseManager.getInstance().getUser().getAvatar() + "&s=250").transform(new CircleTransform()).into(mAvatar);
    }
}
