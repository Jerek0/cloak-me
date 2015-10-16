package com.jeremy_minie.helloagaincrm.logged.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jeremy_minie.helloagaincrm.R;
import com.jeremy_minie.helloagaincrm.home.RegisterActivity;
import com.jeremy_minie.helloagaincrm.logged.AddDiscussionActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class DiscussionsFragment extends Fragment {

    private View view;

    public DiscussionsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_discussions, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @OnClick(R.id.addDiscussionButton)
    void onAddClick() {
        Intent intent = new Intent(getActivity(), AddDiscussionActivity.class);
        startActivity(intent);
    }

}
