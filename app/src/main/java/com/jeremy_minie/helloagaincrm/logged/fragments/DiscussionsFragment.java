package com.jeremy_minie.helloagaincrm.logged.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.jeremy_minie.helloagaincrm.R;
import com.jeremy_minie.helloagaincrm.logged.AddDiscussionActivity;
import com.jeremy_minie.helloagaincrm.logged.adapters.DiscussionsAdapter;
import com.jeremy_minie.helloagaincrm.logged.entities.Discussion;
import com.jeremy_minie.helloagaincrm.util.FirebaseManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class DiscussionsFragment extends Fragment implements DiscussionsAdapter.OnItemClickListener, FirebaseManager.FirebaseDataListener, Discussion.DiscussionListener {

    private View view;

    private List<Discussion> discussionsList;
    private DiscussionsAdapter adapter;

    public DiscussionsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_discussions, container, false);
        ButterKnife.bind(this, view);

        FirebaseManager.getInstance().getUserDiscussionsList(this);

        return view;
    }

    private void updateAdapter(List<Discussion> discussionsList) {
        // Lookup the recyclerview in activity layout
        RecyclerView rvDiscussions = (RecyclerView) view.findViewById(R.id.rvDiscussions);
        // Create adapter passing in the sample user data
        adapter = new DiscussionsAdapter(discussionsList);
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

    @Override
    public void onDataChanged(DataSnapshot snapshot) {
        discussionsList = new ArrayList<Discussion>();

        for (DataSnapshot postSnapshot: snapshot.getChildren()) {
            Discussion discussion = new Discussion(postSnapshot);
            discussion.setListener(this);
            discussionsList.add(discussion);
        }

        Collections.sort(discussionsList, new Comparator<Discussion>() {
            @Override
            public int compare(Discussion d1, Discussion d2) {
                return (int) (d2.getTimestamp() - d1.getTimestamp()); // Descending
            }
        });

        Collections.sort(discussionsList, new Comparator<Discussion>() {
            @Override
            public int compare(Discussion d1, Discussion d2) {
                return d2.getNewMessages().compareTo(d1.getNewMessages()); // Descending
            }
        });

        updateAdapter(discussionsList);
    }

    public void onDiscussionLoaded(Discussion discussion) {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onCancelled(FirebaseError firebaseError) {

    }
}
