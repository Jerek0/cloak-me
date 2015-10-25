package com.jeremy_minie.helloagaincrm.util.encryption;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.PrivateKey;

/**
 * Created by jerek0 on 22/10/15.
 */
public class RsaCryptoUtils {

    private static final String TAG = "RsaCryptoUtils";

    /**
     * getKeyPair
     *
     * Allows to generate a RSA KeyPair
     *
     * @return KeyPair
     */
    @NonNull
    public static KeyPair getKeyPair() {
        KeyPair keyPair = null;
        try {
            keyPair = RsaEcb.generateKeys();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }

        if (keyPair == null) throw new AssertionError();

        try {
            Log.d(TAG, "public key: " + RsaEcb.getPublicKeyString(keyPair.getPublic()));
            Log.d(TAG, "private key: " + RsaEcb.getPrivateKeyString(keyPair.getPrivate()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return keyPair;
    }

}
