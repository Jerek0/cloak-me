package com.jeremy_minie.helloagaincrm.util.encryption;

import com.tozny.crypto.android.AesCbcWithIntegrity;

import org.spongycastle.util.Arrays;
import org.spongycastle.util.encoders.Base64;
import org.spongycastle.util.io.pem.PemObject;
import org.spongycastle.util.io.pem.PemWriter;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.Signature;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.PSSParameterSpec;
import java.security.spec.RSAKeyGenParameterSpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

public class RsaEcb {

    static {
        Security.insertProviderAt(new org.spongycastle.jce.provider.BouncyCastleProvider(), 1);
    }

    private static final int RSA_KEY_LENGTH_BITS = 4096;

    private static final String CIPHER_TRANSFORMATION = "RSA/ECB/OAEPWithSHA512AndMGF1Padding";

    private static final String CIPHER = "RSA";
    private static final String CIPHER_PROVIDER = "SC";

    private static final String SIGNATURE_ALGORITHM = "SHA512withRSA/PSS";
    private static final String PSS_DIGEST_ALGORITHM = "SHA-512";
    private static final String PSS_MASK_ALGORITHM = "MGF1";
    private static final int PSS_SALT_LEN = 32;
    private static final int PSS_TRAILER_FIELD = 1;

    private static final String RANDOM_ALGORITHM = "SHA1PRNG";

    private static final String PEM_PUBLIC_KEY = "PUBLIC KEY";
    private static final String PEM_PRIVATE_KEY = "PRIVATE KEY";
    private static final String PUBLIC_KEY_HEADER = "BEGIN PUBLIC KEY";
    private static final String PUBLIC_KEY_FOOTER = "END PUBLIC KEY";
    private static final String PRIVATE_KEY_HEADER = "BEGIN PRIVATE KEY";
    private static final String PRIVATE_KEY_FOOTER = "END PRIVATE KEY";

    private static final String ENCODING = "UTF-8";

    public static KeyPair generateKeys() throws GeneralSecurityException {
        // Ensure fixPrng() get called
        AesCbcWithIntegrity.generateSalt();

        SecureRandom random = SecureRandom.getInstance(RANDOM_ALGORITHM);

        RSAKeyGenParameterSpec spec = new RSAKeyGenParameterSpec(RSA_KEY_LENGTH_BITS, RSAKeyGenParameterSpec.F4);
        KeyPairGenerator generator = KeyPairGenerator.getInstance(CIPHER, CIPHER_PROVIDER);
        generator.initialize(spec, random);

        return generator.generateKeyPair();
    }

    public static String getPrivateKeyString(PrivateKey key) throws IOException {
        return getKeyString(new PemObject(PEM_PRIVATE_KEY, key.getEncoded()));
    }

    public static String getPublicKeyString(PublicKey key) throws IOException {
        return getKeyString(new PemObject(PEM_PUBLIC_KEY, key.getEncoded()));
    }

    public static String getKeyString(PemObject pem) throws IOException {
        StringWriter keyStringWriter = new StringWriter();
        PemWriter pemWriter = new PemWriter(keyStringWriter);
        pemWriter.writeObject(pem);
        pemWriter.flush();
        pemWriter.close();

        return keyStringWriter.toString();
    }

    public static KeyPair keys(String privateKeyStr, String publicKeyStr) throws GeneralSecurityException, UnsupportedEncodingException {
        PublicKey pubKey = getRSAPublicKeyFromString(publicKeyStr);
        PrivateKey privKey = getRSAPrivateKeyFromString(privateKeyStr);

        return new KeyPair(pubKey, privKey);
    }

    public static PublicKey getRSAPublicKeyFromString(String publicKeyPEM) throws GeneralSecurityException, UnsupportedEncodingException {
        publicKeyPEM = stripPublicKeyHeaders(publicKeyPEM);
        KeyFactory keyFactory = KeyFactory.getInstance(CIPHER, CIPHER_PROVIDER);
        byte[] publicKeyBytes = Base64.decode(publicKeyPEM.getBytes(ENCODING));
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(publicKeyBytes);
        return keyFactory.generatePublic(x509KeySpec);
    }

    public static PrivateKey getRSAPrivateKeyFromString(String privateKeyPEM) throws GeneralSecurityException, UnsupportedEncodingException {
        privateKeyPEM = stripPrivateKeyHeaders(privateKeyPEM);
        KeyFactory fact = KeyFactory.getInstance(CIPHER, CIPHER_PROVIDER);
        byte[] clear = Base64.decode(privateKeyPEM);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(clear);
        PrivateKey priv = fact.generatePrivate(keySpec);
        Arrays.fill(clear, (byte) 0);
        return priv;
    }

    public static String stripPublicKeyHeaders(String key) {
        //strip the headers from the key string
        StringBuilder strippedKey = new StringBuilder();
        String lines[] = key.split("\n");
        for (String line : lines) {
            if (!line.contains(PUBLIC_KEY_HEADER) && !line.contains(PUBLIC_KEY_FOOTER) &&
                    !isNullOrEmpty(line.trim())) {
                strippedKey.append(line.trim());
            }
        }
        return strippedKey.toString().trim();
    }

    public static String stripPrivateKeyHeaders(String key) {
        StringBuilder strippedKey = new StringBuilder();
        String lines[] = key.split("\n");
        for (String line : lines) {
            if (!line.contains(PRIVATE_KEY_HEADER) && !line.contains(PRIVATE_KEY_FOOTER) &&
                    !isNullOrEmpty(line.trim())) {
                strippedKey.append(line.trim());
            }
        }
        return strippedKey.toString().trim();
    }

    public static String encrypt(String plaintext, PublicKey publicKey) throws GeneralSecurityException, UnsupportedEncodingException {
        byte[] cipherText = encrypt(plaintext.getBytes(ENCODING), publicKey);
        return Base64.toBase64String(cipherText);
    }

    public static byte[] encrypt(byte[] plaintext, PublicKey publicKey) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION, CIPHER_PROVIDER);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(plaintext);
    }

    public static String decrypt(String cipherText, PrivateKey privateKey) throws GeneralSecurityException, UnsupportedEncodingException {
        return new String(decrypt(Base64.decode(cipherText), privateKey), ENCODING);
    }

    public static byte[] decrypt(byte[] cipherText, PrivateKey privateKey) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION, CIPHER_PROVIDER);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);

        return cipher.doFinal(cipherText);
    }

    public static String genSignature(String input, PrivateKey privateKey) throws UnsupportedEncodingException, GeneralSecurityException {
        byte[] signature = genSignature(input.getBytes(ENCODING), privateKey);
        return Base64.toBase64String(signature);
    }

    public static byte[] genSignature(byte[] input, PrivateKey privateKey) throws GeneralSecurityException {
        Signature s = getSignatureInstance();
        s.initSign(privateKey);
        s.update(input);

        return s.sign();
    }

    public static boolean checkSignature(String signature, String input, PublicKey publicKey) throws UnsupportedEncodingException, GeneralSecurityException {
        return checkSignature(Base64.decode(signature), input.getBytes(ENCODING), publicKey);
    }

    public static boolean checkSignature(byte[] signature, byte[] input, PublicKey publicKey) throws GeneralSecurityException {
        Signature s = getSignatureInstance();
        s.initVerify(publicKey);
        s.update(input);

        return s.verify(signature);
    }

    private static Signature getSignatureInstance() throws GeneralSecurityException {
        Signature s = Signature.getInstance(SIGNATURE_ALGORITHM, CIPHER_PROVIDER);
        AlgorithmParameterSpec algoSpec = new MGF1ParameterSpec(PSS_DIGEST_ALGORITHM);
        PSSParameterSpec spec1 = new PSSParameterSpec(PSS_DIGEST_ALGORITHM, PSS_MASK_ALGORITHM, algoSpec, PSS_SALT_LEN, PSS_TRAILER_FIELD);
        s.setParameter(spec1);
        return s;
    }


    public static boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }
}
