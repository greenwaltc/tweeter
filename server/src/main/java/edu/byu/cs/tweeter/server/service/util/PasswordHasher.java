package edu.byu.cs.tweeter.server.service.util;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import edu.byu.cs.tweeter.util.Pair;

public class PasswordHasher {
    public static Pair<byte[], byte[]> hashPassword(String plainTxtPassword) {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);

        return hashPasswordHelper(plainTxtPassword, salt);
    }

    public static Pair<byte[], byte[]> hashPassword(String plainTxtPassword, byte[] salt) {
        return hashPasswordHelper(plainTxtPassword, salt);
    }

    private static Pair<byte[], byte[]> hashPasswordHelper(String plainTxtPassword, byte[] salt) {

        byte[] hash = null;
        try {
            KeySpec spec = new PBEKeySpec(plainTxtPassword.toCharArray(), salt, 2048, 128);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            hash = factory.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return new Pair<>(hash, salt);
    }

    public static boolean verifyPassword(String plainTxtPassword, byte[] salt, byte[] otherHash) {
        Pair<byte[], byte[]> hashSaltPair = hashPassword(plainTxtPassword, salt);
        return Arrays.equals(hashSaltPair.getFirst(), otherHash);
    }
}
