package com.jeremy_minie.helloagaincrm.logged.entities;

import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.jeremy_minie.helloagaincrm.util.FirebaseManager;

/**
 * Created by jerek0 on 24/10/15.
 */
public class Discussion implements FirebaseManager.FirebaseDataListener {

    private static final String TAG = "Discussion";
    private User target;
    private String lastMessage = "Last received message from this discussion which can be very very very long !";
    private Boolean newMessages = false;
    private Long timestamp = 786791400l; // Default timestamp if no one is given -> author's date of birth :)
    private String uid;

    private DiscussionListener listener;

    public Discussion(DataSnapshot snapshot) {
        this.uid = snapshot.getKey();

        if(snapshot.child("newMessages").getValue() != null)
            this.newMessages = (Boolean) snapshot.child("newMessages").getValue();
        if(snapshot.child("timestamp").getValue() != null)
            this.timestamp = (Long) snapshot.child("timestamp").getValue();
        FirebaseManager.getInstance().getUserByUid((String) snapshot.child("target_uid").getValue(), this);
    }

    public Discussion(User target, String lastMessage) {
        this.target = target;
        this.lastMessage = lastMessage;
        this.newMessages = false;
    }

    public User getTarget() {
        return target;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    @Override
    public void onDataChanged(DataSnapshot snapshot) {
        this.target= new User();
        target.fillFromSnapshot(snapshot);

        listener.onDiscussionLoaded(this);
    }

    @Override
    public void onCancelled(FirebaseError firebaseError) {

    }

    public void setListener(DiscussionListener listener) {
        this.listener = listener;
    }

    public Boolean getNewMessages() {
        return newMessages;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }


    public interface DiscussionListener {
        void onDiscussionLoaded(Discussion discussion);
    }
}
