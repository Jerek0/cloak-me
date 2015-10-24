package com.jeremy_minie.helloagaincrm.logged.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jeremy_minie.helloagaincrm.R;
import com.jeremy_minie.helloagaincrm.home.RegisterActivity;
import com.jeremy_minie.helloagaincrm.logged.AddDiscussionActivity;
import com.jeremy_minie.helloagaincrm.logged.adapters.DiscussionsAdapter;
import com.jeremy_minie.helloagaincrm.logged.adapters.UsersAdapter;
import com.jeremy_minie.helloagaincrm.logged.entities.Discussion;
import com.jeremy_minie.helloagaincrm.logged.entities.User;
import com.jeremy_minie.helloagaincrm.util.generators.UsernameGenerator;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class DiscussionsFragment extends Fragment implements DiscussionsAdapter.OnItemClickListener {

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

        List<Discussion> discussionList = new ArrayList<Discussion>();

        for (int i = 0; i<20; i++) {
            User user = new User("", UsernameGenerator.getInstance().newUsername(), "jeremy.minie@gmail.com");

            Discussion discussion = new Discussion(user, "c'est pour savoir si tu veux aller manger au frenchy");
            discussionList.add(discussion);
        }

        updateAdapter(discussionList);

        return view;
    }

    private void updateAdapter(List<Discussion> discussionsList) {
        // Lookup the recyclerview in activity layout
        RecyclerView rvDiscussions = (RecyclerView) view.findViewById(R.id.rvDiscussions);
        // Create adapter passing in the sample user data
        DiscussionsAdapter adapter = new DiscussionsAdapter(discussionsList);
        adapter.setOnItemClickListener(this);
        // Attach the adapter to the recyclerview to populate items
        rvDiscussions.setAdapter(adapter);
        // Set layout manager to position the items
        rvDiscussions.setLayoutManager(new LinearLayoutManager(view.getContext()));
        // That's all!
        rvDiscussions.setHasFixedSize(false);
    }

    @OnClick(R.id.addDiscussionButton)
    void onAddClick() {
        Intent intent = new Intent(getActivity(), AddDiscussionActivity.class);
        startActivity(intent);
    }

    @Override
    public void onItemClick(View itemView, String uid) {

    }
}
