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

/**
 * FIREBASE MANAGER (Singleton)
 *
 * Manages every request between the app and the database
 *
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
        // Set our firebase url
        ref = new Firebase("https://helloagaincrm.firebaseio.com/");
    }

    private User user; // Store our current user
    private UserSecrets userSecrets; // Store our current user secrets

    /* #######################################
    #                                        #
    #             USERS RELATED              #
    #                                        #
    ######################################## */

    /* #### REGISTRATION #### */

    /**
     * createUser
     *
     * Basic Firebase registration by mail/password
     *
     * @param mail
     * @param password
     * @param listener - FirebaseRegisterListener for callbacks
     */
    public void createUser(String mail, String password, final FirebaseRegisterListener listener) {
        ref.createUser(mail, password, new Firebase.ValueResultHandler<Map<String, Object>>() {
            @Override
            public void onSuccess(Map<String, Object> stringObjectMap) {
                // Successful callback
                listener.onSuccessRegister(stringObjectMap);
            }

            @Override
            public void onError(FirebaseError firebaseError) {
                listener.onError(firebaseError);
            }
        });
    }

    /* #### Authentication #### */

    /**
     * authWithPassword
     *
     * Basic Firebase default authentication by mail/password
     *
     * @param mail
     * @param password
     * @param listener - The FireBaseAuthListener for callbacks
     */
    public void authWithPassword(String mail, final String password, final FirebaseAuthListener listener) {
        ref.authWithPassword(mail, password, new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                // Set a gravatar according to user's mail
                // TODO - Put somewhere else and automate with MD5 hash instead of picking on AuthData
                ref.child("users/" + authData.getUid() + "/avatar").setValue(authData.getProviderData().get("profileImageURL"));

                // Ask for user's profile
                // Transmit listener -> our request isn't finished
                getUserByUid(authData.getUid(), listener, password);
            }

            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                listener.onError(firebaseError);
            }
        });
    }

    /**
     * getUserByUid
     *
     * Request a user after Authentication only (because of the listener)
     * Fills our current user and its secrets
     *
     * @param uid
     * @param listener
     * @param password
     */
    public void getUserByUid(String uid, final FirebaseAuthListener listener, final String password) {
        // Use a SingleValueEvent here because we don't need updates
        ref.child("users/" + uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // Fill our current user with snapshot
                user = new User();
                user.fillFromSnapshot(snapshot);

                // Fil our userSecrets
                userSecrets = new UserSecrets((String) snapshot.child("security/public_key").getValue(), (String) snapshot.child("security/encrypted_private_key").getValue(), (String) snapshot.child("security/salt").getValue(), password);

                // Finally, successfull callback
                listener.onSuccessAuth();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                listener.onError(firebaseError);
            }
        });

        // We do not return the ValueEventListener because it's a SingleValueEvent so we don't need to removeListener
    }

    /**
     * unAuth
     *
     * Allows to close current session with Firebase
     *
     */
    public void unAuth() {
        Log.d(TAG, "unAuth");

        // Firebase UnAuth
        ref.unauth();

        // Free some memory
        user = null;
        userSecrets = null;
    }

    /* #### REQUESTING DATA #### */

    /**
     * getUserByUid
     *
     * Basic request for a User's profile by its Uid
     *
     * @param uid
     * @param listener
     */
    public void getUserByUid(String uid, final FirebaseDataListener listener) {
        // Use a SingleValueEvent here because we don't need updates
        ref.child("users/" + uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // Successful callback
                listener.onDataChanged(snapshot);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });

        // We do not return the ValueEventListener because it's a SingleValueEvent so we don't need to removeListener
    }

    /**
     * getUsersList
     *
     * Basic request for all users list
     *
     * @param listener - FirebaseDataListener for callbacks
     */
    public void getUsersList(final FirebaseDataListener listener) {
        // Use a SingleValueEvent here because we don't need updates
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

        // We do not return the ValueEventListener because it's a SingleValueEvent so we don't need to removeListener
    }

    /**
     * getUsersByName
     *
     * More advanced request allowing to get only users with a name begining with "name" attribute
     *
     * @param name - name prefix wanted
     * @param listener - FirebaseDataListener for callbacks
     */
    public void getUsersByName(String name, final FirebaseDataListener listener) {

        // Creates a query w/ orderByChild username_lower_case if name prefix given isn't null
        // We use a lower case version of username because Firebase isn't able to make queries without considering case
        Query queryRef;
        if(name.length()>0)
            queryRef = ref.child("users").orderByChild("username_lower_case").startAt(name.toLowerCase() + "").endAt(name.toLowerCase() + "\uf8ff");
        else
            queryRef = ref.child("users");

        // Use a SingleValueEvent here because we don't need updates
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

        // We do not return the ValueEventListener because it's a SingleValueEvent so we don't need to removeListener
    }

    /* #### SAVING DATA #### */

    /**
     * generateUser
     *
     * Allows to fill a new user profile on Firebase
     *
     * @param uid
     * @param mail
     * @param password
     */
    public void generateUser(String uid, String mail, String password) {
        // Get the user profile ref by its uid
        Firebase userRef = ref.child("users").child(uid);

        // # Generated a new user object
        user = new User(uid, UsernameGenerator.getInstance().newUsername(), mail);
        // Sets default color (noob blue)
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

    /**
     * saveUser
     *
     * Allows to update Firebase user's profile w/ current
     *
     * @param listener
     */
    public void saveUser(final ProfileFragment listener) {
        // Get the user ref by its uid
        Firebase userRef = ref.child("users").child(getUser().getUid());

        // Create an update HashMap w/ current user's data
        // We use an HashMap because some of User.class properties aren't stored online
        Map<String, Object> userMap = new HashMap<String, Object>();
        userMap.put("username", getUser().getUsername());
        userMap.put("username_lower_case", getUser().getUsername().toLowerCase());
        userMap.put("color", getUser().getColor());

        // Update database ref with anonymous listener !
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


    /* #######################################
    #                                        #
    #    CHANNELS / DISCUSSIONS RELATED      #
    #                                        #
    ######################################## */

    /* #### CREATE / SAVE DATA #### */

    /**
     * createDiscussion
     *
     * Allows to generate a new discussion between our user and another one
     *
     * @param target_uid - The other user's uid
     */
    public void createDiscussion(final String target_uid) {
        /*
            FIRST STEP : Add a channel to channels list
         */
        final Firebase newDiscussion = ref.child("channels").push();
        final String newDiscussionKey = newDiscussion.getKey();
        final Long currentTime = System.currentTimeMillis();
        // Add each user to the discussion
        newDiscussion.child("users").push().setValue(user.getUid());
        newDiscussion.child("users").push().setValue(target_uid);
        newDiscussion.child("last_message_uid").setValue(null);

        /*
            SECOND STEP : Generate a password (passphrase) and a salt for AES secret key
         */
        // Generate a password
        SecureRandom random = new SecureRandom();
        final String password = new BigInteger(130, random).toString(32);
        // Generate a salt
        final byte[] salt = AesCryptoUtils.getSalt();

        /*
            THIRD STEP : Encrypt password with current user's public key and store channel's information to it's profile
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
        newDiscussionMap.put("timestamp", currentTime);
        ref.child("users/" + user.getUid() + "/channels/" + newDiscussionKey + "").setValue(newDiscussionMap);

        /*
            FOURTH STEP : Encrypt password with target's public key and store channel's information to it's profile
         */
        // Anonymous ValueEventListener cause no callback is needed (dataChange will do the work!)
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
                newDiscussionMap.put("timestamp", currentTime);
                ref.child("users/" + target_uid + "/channels/" + newDiscussionKey + "").setValue(newDiscussionMap);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });
    }

    /**
     * markDiscussionAsRead
     *
     * Mark a discussion as readed on current user's side
     *
     * @param discussion_uid
     */
    public void markDiscussionAsRead(String discussion_uid) {
        ref.child("users/"+user.getUid()+"/channels/"+discussion_uid+"/newMessages").setValue(false);
    }

    /* #### READ DATA #### */

    /**
     * getUserDiscussionsList
     *
     * Basic request for current user's dicussions list
     * Dynamic ValueEventListener -> Will notify callbacks at each modification in Firebase until listener is removed
     *
     * @param listener
     * @return ValueEventListenr -> Will be usefull to kill listener on unAuth for example
     */
    public ValueEventListener getUserDiscussionsList(final FirebaseDataListener listener) {

        ValueEventListener vel = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // At each update, try to update all Discussion keys
                updateDiscussionKeys(dataSnapshot);

                // Successful callback
                listener.onDataChanged(dataSnapshot);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                listener.onCancelled(firebaseError);
            }
        };

        ref.child("users/" + getUser().getUid() + "/channels").addValueEventListener(vel);
        return vel;
    }

    /**
     * removeUserDiscussionsListener
     * Linked to getUserDiscussionsList
     *
     * Allows to remove event listener for user's discussions list
     *
     * @param vel_uid - Get the current user uid (in case our version already has been cleared)
     * @param vel - The ValueEventListener to remove
     */
    public void removeUserDiscussionsListener(String vel_uid, ValueEventListener vel) {
        System.out.println("removeUserDiscussionsListener");
        ref.child("users/" + vel_uid + "/channels").removeEventListener(vel);
    }

    /**
     * updateDiscussionKeys
     *
     * Allows to decrypt and add each discussion's secret key to our userSecrets discussions keys list
     *
     * @param snapshot
     */
    public void updateDiscussionKeys(DataSnapshot snapshot) {
        // Get our current list of discussions' secret keys
        HashMap<String, AesCbcWithIntegrity.SecretKeys> discussionsKeys = userSecrets.getDiscussionsKeys();

        // For each discussion in the snapshot, check if we have the key and add it if necessary
        for (DataSnapshot postSnapshot: snapshot.getChildren()) {
            if(discussionsKeys.get(postSnapshot.getKey())==null) {

                // GET RAW DATA
                // Get the encrypted passphrase and its salt
                String encrypted_passphrase = (String) postSnapshot.child("encrypted_passphrase").getValue();
                byte[] salt = Base64.decode((String) postSnapshot.child("salt").getValue()); // Directly decode salt

                // Decrypt!
                String decrypted = null;
                try {
                    decrypted = RsaEcb.decrypt(encrypted_passphrase, userSecrets.getPrivateKey());
                } catch (GeneralSecurityException | UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                if (decrypted == null) {
                    throw new AssertionError();
                }

                // Generate AES keys from passphrase
                AesCbcWithIntegrity.SecretKeys keys = AesCryptoUtils.getSecretKeys(decrypted, salt);

                // Store discussion's AES Keys in userSecrets
                userSecrets.addDiscussionKey(postSnapshot.getKey(), keys);
            }
        }
    }

    /* #######################################
    #                                        #
    #           MESSAGES RELATED             #
    #                                        #
    ######################################## */

    /* #### CREATE / SAVE DATA #### */

    /**
     * newMessage
     *
     * Allows to post a new message from current user to a discussion
     *
     * @param discussionUid
     * @param content
     */
    public void newMessage(final String discussionUid, String content) {

        // Get discussion keys from our userSecrets
        AesCbcWithIntegrity.SecretKeys keys = userSecrets.getDiscussionsKeys().get(discussionUid);

        // Encrypt message content
        AesCbcWithIntegrity.CipherTextIvMac encrypted = null;
        try {
            encrypted = AesCbcWithIntegrity.encrypt(content, keys);
        } catch (UnsupportedEncodingException | GeneralSecurityException e) {
            e.printStackTrace();
        }

        // Uh Oh, something went wrong!
        if (encrypted == null) throw new AssertionError();

        // Stringify our encrypted content
        String encryptedString = encrypted.toString();

        // SEND TO FIREBASE
        final Long currentTimestamp = System.currentTimeMillis();
        Map<String, Object> newMessageMap = new HashMap<String, Object>();
        newMessageMap.put("channel", discussionUid);
        newMessageMap.put("author", user.getUid());
        newMessageMap.put("timestamp", currentTimestamp);
        newMessageMap.put("encrypted_content", encryptedString);
        final String message_uid = ref.child("messages/").push().getKey();
        ref.child("messages/"+message_uid).setValue(newMessageMap);

        // Get channel's users and notify them
        ref.child("channels/"+discussionUid+"/users").addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Notify each user by updating it's own version of the channel
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Map<String, Object> updateMap = new HashMap<String, Object>();
                    updateMap.put("timestamp", currentTimestamp);
                    updateMap.put("newMessages", true);
                    updateMap.put("last_message_id", message_uid);
                    ref.child("users/" + postSnapshot.getValue() + "/channels/" + discussionUid).updateChildren(updateMap);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    /* #### REQUEST / READ DATA #### */

    /**
     * getMessagesByChannelUid
     *
     * Basic request allowing to get all messages belonging to a channel
     * Dynamic ValueEventListener -> Will notify callbacks at each modification in Firebase until listener is removed
     *
     * @param channelUid
     * @param listener
     * @return
     */
    public ValueEventListener getMessagesByChannelUid(String channelUid,final FirebaseDataListener listener) {

        ValueEventListener vel = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listener.onDataChanged(dataSnapshot);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                listener.onCancelled(firebaseError);
            }
        };

        ref.child("messages/").orderByChild("channel").startAt(channelUid).endAt(channelUid).addValueEventListener(vel);
        return vel;
    }

    /**
     * removeMessagesListener
     * Linked to getMessagesByChannelUid
     *
     * Allows to remove event listener for dicussion's messages list
     *
     * @param channelUid - The channel uid we have to stop listening
     * @param vel - The value event listener to remove from listening
     */
    public void removeMessagesListener(String channelUid, ValueEventListener vel) {
        ref.child("messages/").orderByChild("channel").startAt(channelUid).endAt(channelUid).removeEventListener(vel);
    }

    /**
     * getMessageByUid
     *
     * Basic request allowing to get only one message by it's UID
     *
     * @param messageUid
     * @param listener
     */
    public void getMessageByUid(String messageUid, final FirebaseDataListener listener) {
        // Use a SingleValueEvent here because we don't need updates
        ref.child("messages/" + messageUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listener.onDataChanged(dataSnapshot);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                listener.onCancelled(firebaseError);
            }
        });

        // We do not return the ValueEventListener because it's a SingleValueEvent so we don't need to removeListener
    }

    /* #######################################
    #                                        #
    #           GETTERS / SETTERS            #
    #                                        #
    ######################################## */

    public User getUser() {
        return user;
    }

    public UserSecrets getUserSecrets() {
        return userSecrets;
    }

    /* #######################################
    #                                        #
    #           FIREBASE LISTENERS           #
    #                                        #
    ######################################## */

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
