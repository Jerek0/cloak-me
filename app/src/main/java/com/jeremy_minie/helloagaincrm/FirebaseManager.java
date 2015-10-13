package com.jeremy_minie.helloagaincrm;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.Map;

/**
 * Created by jminie on 13/10/2015.
 */
public class FirebaseManager {
    private static FirebaseManager ourInstance = new FirebaseManager();

    public static FirebaseManager getInstance() {
        return ourInstance;
    }

    private Firebase ref;

    private FirebaseManager() {
        ref = new Firebase("https://helloagaincrm.firebaseio.com/");
    }

    public void authWithPassword(String mail, String password, final FirebaseListener listener) {
        ref.authWithPassword(mail, password, new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                listener.onSuccessAuth(authData);
            }
            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                listener.onError(firebaseError);
            }
        });
    }

    public void createUser(String mail, String password, final FirebaseListener listener) {
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

    public interface FirebaseListener {
        void onSuccessAuth(AuthData authData);
        void onSuccessRegister(Map<String, Object> stringObjectMap);
        void onError(FirebaseError firebaseError);
    }
}
