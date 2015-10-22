package com.jeremy_minie.helloagaincrm.util.encryption;

import android.support.annotation.NonNull;
import android.util.Log;
import com.tozny.crypto.android.AesCbcWithIntegrity;
import org.spongycastle.util.encoders.Base64;
import java.security.GeneralSecurityException;

/**
 * Created by jerek0 on 22/10/15.
 */
public class AesCryptoUtils {
    private static final String TAG = "AesCryptoUtils";

    public static byte[] getSalt() {
        byte[] salt = new byte[0];

        // Generate random 128bits salt
        try {
            salt = AesCbcWithIntegrity.generateSalt();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }

        // salt generation failed
        if (salt.length <= 0) throw new AssertionError();

        Log.d(TAG, "salt: " + Base64.toBase64String(salt));
        return salt;
    }

    @NonNull
    public static AesCbcWithIntegrity.SecretKeys getSecretKeys(String password, byte[] salt) {
        // The secret keys
        AesCbcWithIntegrity.SecretKeys keys = null;

        // Generate secret keys from password and salt
        // password must be kept secret
        // salt can be stored with each message
        try {
            keys = AesCbcWithIntegrity.generateKeyFromPassword(password, salt);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }

        // keys generation failed
        if (keys == null) throw new AssertionError();

        // Output "confidentialityKey:integrityKey"
        Log.d(TAG, "secret keys: " + keys.toString());

        return keys;
    }

}