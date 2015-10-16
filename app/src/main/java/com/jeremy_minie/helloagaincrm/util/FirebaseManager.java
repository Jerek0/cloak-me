package com.jeremy_minie.helloagaincrm.util;

import android.util.Log;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.jeremy_minie.helloagaincrm.logged.entities.User;
import com.jeremy_minie.helloagaincrm.logged.fragments.ProfileFragment;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jminie on 13/10/2015.
 */
public class FirebaseManager {
    private static final String TAG = "FirebaseManager";
    private static FirebaseManager ourInstance = new FirebaseManager();

    public static FirebaseManager getInstance() {
        return ourInstance;
    }

    private Firebase ref;
    private FirebaseManager() {
        ref = new Firebase("https://helloagaincrm.firebaseio.com/");
    }
    private User user;

    public void authWithPassword(String mail, String password, final FirebaseAuthListener listener) {
        ref.authWithPassword(mail, password, new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                ref.child("users/"+authData.getUid() + "/avatar").setValue(authData.getProviderData().get("profileImageURL"));
                getUserByUid(authData.getUid(), listener);
            }
            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                listener.onError(firebaseError);
            }
        });
    }

    public void createUser(String mail, String password, final FirebaseRegisterListener listener) {
        ref.createUser(mail, password, new Firebase.ValueResultHandler<Map<String, Object>>() {
            @Override
            public void onSuccess(Map<String, Object> stringObjectMap) {
                listener.onSuccessRegister(stringObjectMap);
            }

            @Override
            public void onError(FirebaseError firebaseError) {
                listener.onError(firebaseError);
            }
        });
    }

    public void unAuth() {
        Log.d(TAG, "unAuth");
        ref.unauth();
    }

    public void getUserByUid(String uid, final FirebaseAuthListener listener) {
        ref.child("users/" + uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                System.out.println(snapshot);
                user = snapshot.getValue(User.class);
                user.setUid(snapshot.getKey());
                listener.onSuccessAuth();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });
    }

    public void getUsersList(final FirebaseDataListener listener) {
        ref.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listener.onDataChanged(dataSnapshot);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                listener.onCancelled(firebaseError);
            }
        });
    }

    public void generateUser(String uid, String mail) {
        Firebase userRef = ref.child("users").child(uid);
        user = new User(uid, UsernameGenerator.getInstance().newUsername(), mail);
        user.setColor(-12813891);
        userRef.child("username").setValue(user.getUsername());
        userRef.child("mail").setValue(user.getMail());
        userRef.child("color").setValue(user.getColor());
    }

    public void saveUser(final ProfileFragment listener) {
        Firebase userRef = ref.child("users").child(getUser().getUid());

        Map<String, Object> userMap = new HashMap<String, Object>();
        userMap.put("username", getUser().getUsername());
        userMap.put("color", getUser().getColor());
        userRef.updateChildren(userMap, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError != null) {
                    listener.onUpdateError(firebaseError.getMessage());
                } else {
                    listener.onUpdateSuccess();
                }
            }
        });
    }

    public User getUser() {
        return user;
    }

    public interface FirebaseAuthListener {
        void onSuccessAuth();
        void onError(FirebaseError firebaseError);
    }

    public interface FirebaseRegisterListener {
        void onSuccessRegister(Map<String, Object> stringObjectMap);
        void onError(FirebaseError firebaseError);
    }

    public interface FirebaseDataListener {
        void onDataChanged(DataSnapshot snapshot);
        void onCancelled(FirebaseError firebaseError);
    }
}
