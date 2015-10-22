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

    @NonNull
    private static PrivateKey getPrivateKey(Context context) {
        String privateKeyString = context.getSharedPreferences("Crypto", Context.MODE_PRIVATE)
                .getString("private key", "");

        Log.d(TAG, privateKeyString);
        PrivateKey privateKey = null;
        try {
            privateKey = RsaEcb.getRSAPrivateKeyFromString(privateKeyString);
        } catch (GeneralSecurityException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if (privateKey == null) throw new AssertionError();

        Log.d(TAG, privateKey.toString());
        return privateKey;
    }

    private static void storeKeys(Context context, KeyPair keyPair) {
        // Do the same for public key
        try {
            Log.d(TAG, RsaEcb.getPrivateKeyString(keyPair.getPrivate()));
            context.getSharedPreferences("Crypto", Context.MODE_PRIVATE)
                    .edit()
                    .putString("private key", RsaEcb.getPrivateKeyString(keyPair.getPrivate()))
                    .commit();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
