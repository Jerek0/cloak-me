package com.jeremy_minie.helloagaincrm.logged.fragments;



import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.BinderThread;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.jeremy_minie.helloagaincrm.R;
import com.jeremy_minie.helloagaincrm.util.FirebaseManager;
import com.jeremy_minie.helloagaincrm.util.image.CircleTransform;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";
    private View view;

    @Bind(R.id.profileUserName) TextView mProfileUserName;
    @Bind(R.id.avatar) ImageView mAvatar;
    @Bind(R.id.profileButton) Button mProfileButton;

    @Bind(R.id.profileHeader) RelativeLayout mProfileHeader;
    @Bind(R.id.profileColor1) FrameLayout mProfileColor1;
    @Bind(R.id.profileColor2) FrameLayout mProfileColor2;
    @Bind(R.id.profileColor3) FrameLayout mProfileColor3;
    @Bind(R.id.profileColor4) FrameLayout mProfileColor4;
    @Bind(R.id.profileColor5) FrameLayout mProfileColor5;
    @Bind(R.id.profileColor6) FrameLayout mProfileColor6;
    @Bind(R.id.profileColor7) FrameLayout mProfileColor7;
    @Bind(R.id.profileColor8) FrameLayout mProfileColor8;

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

    @OnClick({R.id.profileColor1, R.id.profileColor2, R.id.profileColor3, R.id.profileColor4, R.id.profileColor5, R.id.profileColor6, R.id.profileColor7, R.id.profileColor8})
    void onProfileColorClick(FrameLayout profileColor) {
        Drawable background = profileColor.getBackground();

        if (background instanceof ColorDrawable) {
            mProfileHeader.setBackgroundColor(((ColorDrawable) background).getColor());
            System.out.println(((ColorDrawable) background).getColor());
        }
    }

    @OnClick(R.id.profileButton)
    void onUpdateProfile() {
        FirebaseManager.getInstance().getUser().setUsername(mProfileUserName.getText().toString());
        Drawable background = mProfileHeader.getBackground();
        if (background instanceof ColorDrawable) {
            FirebaseManager.getInstance().getUser().setColor(((ColorDrawable) background).getColor());
        }
        FirebaseManager.getInstance().saveUser(this);

        mProfileButton.setEnabled(false);
    }

    private void updateUser() {
        mProfileHeader.setBackgroundColor(FirebaseManager.getInstance().getUser().getColor());
        mProfileUserName.setText(FirebaseManager.getInstance().getUser().getUsername(), TextView.BufferType.EDITABLE);
        Picasso.with(view.getContext()).load(FirebaseManager.getInstance().getUser().getAvatar() + "&s=250").transform(new CircleTransform()).into(mAvatar);
    }

    public void onUpdateSuccess() {
        mProfileButton.setEnabled(true);
    }
    public void onUpdateError(String errorMessage) {
        mProfileButton.setEnabled(true);

        Snackbar.make(view, errorMessage, Snackbar.LENGTH_SHORT)
                .setAction("DISMISS", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "Dismiss");
                    }
                })
                .show();
    }
}
