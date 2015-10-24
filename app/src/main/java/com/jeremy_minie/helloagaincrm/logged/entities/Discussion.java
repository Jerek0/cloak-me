package com.jeremy_minie.helloagaincrm.logged.entities;

import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.jeremy_minie.helloagaincrm.util.FirebaseManager;
import com.tozny.crypto.android.AesCbcWithIntegrity;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;

/**
 * Created by jerek0 on 24/10/15.
 */
public class Discussion {

    private static final String TAG = "Discussion";
    private User target;
    private String lastMessage = "Loading last message";
    private Boolean newMessages = false;
    private Long timestamp = 786791400l; // Default timestamp if no one is given -> author's date of birth :)
    private String uid;

    private DiscussionListener listener;
    private UserListener userListener = new UserListener();
    private MessageListener messageListener = new MessageListener();

    public Discussion(DataSnapshot snapshot) {
        this.uid = snapshot.getKey();

        if(snapshot.child("newMessages").getValue() != null)
            this.newMessages = (Boolean) snapshot.child("newMessages").getValue();
        if(snapshot.child("timestamp").getValue() != null)
            this.timestamp = (Long) snapshot.child("timestamp").getValue();

        FirebaseManager.getInstance().getUserByUid((String) snapshot.child("target_uid").getValue(), userListener);
        FirebaseManager.getInstance().getMessageByUid((String) snapshot.child("last_message_id").getValue(), messageListener);
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

    public class UserListener implements FirebaseManager.FirebaseDataListener{

        @Override
        public void onDataChanged(DataSnapshot snapshot) {
            target= new User();
            target.fillFromSnapshot(snapshot);

            listener.onDiscussionLoaded(Discussion.this);
        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {

        }
    }

    public class MessageListener implements FirebaseManager.FirebaseDataListener {

        @Override
        public void onDataChanged(DataSnapshot snapshot) {

            // Decrypt message
            AesCbcWithIntegrity.SecretKeys keys = FirebaseManager.getInstance().getUserSecrets().getDiscussionsKeys().get(uid);
            // Recreate CipherTextIvMac
            AesCbcWithIntegrity.CipherTextIvMac dataToDecrypt = new AesCbcWithIntegrity.CipherTextIvMac((String) snapshot.child("encrypted_content").getValue());

            // Decrypt!
            String decrypted = null;
            try {
                decrypted = AesCbcWithIntegrity.decryptString(dataToDecrypt, keys);
            } catch (UnsupportedEncodingException | GeneralSecurityException e) {
                e.printStackTrace();
            }
            if(decrypted==null) {
                throw new AssertionError();
            }
            lastMessage = decrypted;

            listener.onDiscussionLoaded(Discussion.this);
        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {
            System.out.println(firebaseError.getMessage());
        }
    }

    public interface DiscussionListener {
        void onDiscussionLoaded(Discussion discussion);
    }
}
