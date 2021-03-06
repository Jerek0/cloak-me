package com.jeremy_minie.helloagaincrm.logged.entities;

import android.util.Log;

import com.jeremy_minie.helloagaincrm.util.encryption.AesCryptoUtils;
import com.jeremy_minie.helloagaincrm.util.encryption.RsaCryptoUtils;
import com.jeremy_minie.helloagaincrm.util.encryption.RsaEcb;
import com.tozny.crypto.android.AesCbcWithIntegrity;

import org.spongycastle.util.encoders.Base64;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;


/**
 * Created by jerek0 on 22/10/15.
 */
public class UserSecrets {

    private static final String TAG = "UserSecrets";
    private PrivateKey privateKey;
    private PublicKey publicKey;

    private HashMap<String, AesCbcWithIntegrity.SecretKeys> discussionsKeys;

    public UserSecrets(String publicKey, String encryptedPrivateKey, String salt, String password) {

        discussionsKeys = new HashMap<String, AesCbcWithIntegrity.SecretKeys>();

        // ## DECRYPT PRIVATE KEY
        try {
            // Store public key
            this.publicKey = RsaEcb.getRSAPublicKeyFromString(publicKey);

            // Regenerate secret keys from password and salt
            AesCbcWithIntegrity.SecretKeys keysDecrypt;
            keysDecrypt = AesCryptoUtils.getSecretKeys(password, Base64.decode(salt));

            // Recreate CipherTextIvMac
            AesCbcWithIntegrity.CipherTextIvMac dataToDecrypt = new AesCbcWithIntegrity.CipherTextIvMac(encryptedPrivateKey);

            // Decrypt!
            String decrypted = null;
            try {
                decrypted = AesCbcWithIntegrity.decryptString(dataToDecrypt, keysDecrypt);
            } catch (UnsupportedEncodingException | GeneralSecurityException e) {
                e.printStackTrace();
            }

            if (decrypted != null) {
                this.privateKey = RsaEcb.getRSAPrivateKeyFromString(decrypted);

                System.out.println(this.privateKey);
            } else {
                // Oh no! Decryption failed!
                throw new AssertionError();
            }
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public HashMap<String, AesCbcWithIntegrity.SecretKeys> getDiscussionsKeys() {
        return discussionsKeys;
    }

    public void addDiscussionKey(String discussion_uid, AesCbcWithIntegrity.SecretKeys keys) {
        discussionsKeys.put(discussion_uid, keys);
    }
}
