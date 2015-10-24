package com.jeremy_minie.helloagaincrm.util;

import android.util.Log;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.jeremy_minie.helloagaincrm.logged.entities.User;
import com.jeremy_minie.helloagaincrm.logged.entities.UserSecrets;
import com.jeremy_minie.helloagaincrm.logged.fragments.ProfileFragment;
import com.jeremy_minie.helloagaincrm.util.encryption.AesCryptoUtils;
import com.jeremy_minie.helloagaincrm.util.encryption.RsaCryptoUtils;
import com.jeremy_minie.helloagaincrm.util.encryption.RsaEcb;
import com.jeremy_minie.helloagaincrm.util.generators.UsernameGenerator;
import com.tozny.crypto.android.AesCbcWithIntegrity;

import org.spongycastle.util.encoders.Base64;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
    private UserSecrets userSecrets;

    public void authWithPassword(String mail, final String password, final FirebaseAuthListener listener) {
        ref.authWithPassword(mail, password, new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                ref.child("users/" + authData.getUid() + "/avatar").setValue(authData.getProviderData().get("profileImageURL"));
                getUserByUid(authData.getUid(), listener, password);
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

    public void createDiscussion(final String target_uid) {
        /*
            FIRST STEP : Add a channel to channels list
         */
        final Firebase newDiscussion = ref.child("channels").push();
        final String newDiscussionKey = newDiscussion.getKey();
        // Add each user to the discussion
        newDiscussion.child("users").push().setValue(user.getUid());
        newDiscussion.child("users").push().setValue(target_uid);
        newDiscussion.child("last_message_uid").setValue(null);

        /*
            SECOND STEP : Generate an password and salt for AES secret key
         */
        // Generate a password
        SecureRandom random = new SecureRandom();
        final String password = new BigInteger(130, random).toString(32);
        // Generate a salt
        final byte[] salt = AesCryptoUtils.getSalt();

        /*
            THIRD STEP : Encrypt password with user's public key and store channel's information to it's profile
         */
        String encrypted = null;
        try {
            encrypted = RsaEcb.encrypt(password, userSecrets.getPublicKey());
        } catch (GeneralSecurityException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Map<String, Object> newDiscussionMap = new HashMap<String, Object>();
        newDiscussionMap.put("target_uid", target_uid);
        newDiscussionMap.put("encrypted_passphrase", encrypted);
        newDiscussionMap.put("salt", Base64.toBase64String(salt));
        newDiscussionMap.put("newMessages", false);
        ref.child("users/"+user.getUid()+"/channels/"+newDiscussionKey+"").setValue(newDiscussionMap);

        /*
            FOURTH STEP : Encrypt password with target's public key and store channel's information to it's profile
         */
        ref.child("users/" + target_uid).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String encrypted = null;
                try {
                    encrypted = RsaEcb.encrypt(password, RsaEcb.getRSAPublicKeyFromString((String) dataSnapshot.child("security/public_key").getValue()));
                } catch (GeneralSecurityException | UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                Map<String, Object> newDiscussionMap = new HashMap<String, Object>();
                newDiscussionMap.put("target_uid", user.getUid());
                newDiscussionMap.put("encrypted_passphrase", encrypted);
                newDiscussionMap.put("salt", Base64.toBase64String(salt));
                newDiscussionMap.put("newMessages", false);
                ref.child("users/"+target_uid+"/channels/"+newDiscussionKey+"").setValue(newDiscussionMap);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });
    }

    public void unAuth() {
        Log.d(TAG, "unAuth");
        ref.unauth();
    }

    public void getUserByUid(String uid, final FirebaseAuthListener listener, final String password) {
        ref.child("users/" + uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                user = new User();
                user.fillFromSnapshot(snapshot);

                userSecrets = new UserSecrets((String) snapshot.child("security/public_key").getValue(), (String) snapshot.child("security/encrypted_private_key").getValue(), (String) snapshot.child("security/salt").getValue(), password);
                listener.onSuccessAuth();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });
    }

    public void getUserByUid(String uid, final FirebaseDataListener listener) {
        ref.child("users/" + uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                listener.onDataChanged(snapshot);
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

    public void getUsersByName(String name, final FirebaseDataListener listener) {
        Query queryRef;
        if(name.length()>0)
            queryRef = ref.child("users").orderByChild("username_lower_case").startAt(name.toLowerCase() + "").endAt(name.toLowerCase() + "\uf8ff");
        else
            queryRef = ref.child("users");

        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
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

    public void generateUser(String uid, String mail, String password) {
        Firebase userRef = ref.child("users").child(uid);

        // # Generated a new user object
        user = new User(uid, UsernameGenerator.getInstance().newUsername(), mail);
        user.setColor(-12813891);

        // # Save the user on firebase

        // ## BASIC DATA

        userRef.child("username").setValue(user.getUsername());
        userRef.child("username_lower_case").setValue(user.getUsername().toLowerCase());
        userRef.child("mail").setValue(user.getMail());
        userRef.child("color").setValue(user.getColor());

        // ## SECURITY

        // ### Génération clés RSA
        KeyPair keyPair = RsaCryptoUtils.getKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        // ### Génération d'une clé privée AES avec un salt
        byte[] salt = AesCryptoUtils.getSalt();
        AesCbcWithIntegrity.SecretKeys keys = AesCryptoUtils.getSecretKeys(password, salt);
        String saltString = Base64.toBase64String(salt);

        // ### Chiffrage de la clé privée RSA avec AES
        AesCbcWithIntegrity.CipherTextIvMac encryptedPrivateKey = null;
        try {
            encryptedPrivateKey = AesCbcWithIntegrity.encrypt(RsaEcb.getPrivateKeyString(privateKey), keys);
        } catch (UnsupportedEncodingException | GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // ### Sauvegarde des clés de chiffrage et du salt dans le profil en base
        try {
            userRef.child("security/public_key").setValue(RsaEcb.getPublicKeyString(publicKey));
        } catch (IOException e) {
            e.printStackTrace();
        }
        userRef.child("security/encrypted_private_key").setValue(encryptedPrivateKey.toString());
        userRef.child("security/salt").setValue(saltString);
    }

    public void saveUser(final ProfileFragment listener) {
        Firebase userRef = ref.child("users").child(getUser().getUid());

        Map<String, Object> userMap = new HashMap<String, Object>();
        userMap.put("username", getUser().getUsername());
        userMap.put("username_lower_case", getUser().getUsername().toLowerCase());
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

    public void getUserDiscussionsList(final FirebaseDataListener listener) {
        ref.child("users/"+getUser().getUid()+"/channels").addValueEventListener(new ValueEventListener() {
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

    public User getUser() {
        return user;
    }

    public UserSecrets getUserSecrets() {
        return userSecrets;
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
