package com.johadahl.app1.util;

import com.johadahl.app1.config.Config;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;



/**
 * Created by thomas on 06.02.2018.
 */

public class WordToByte {


    private static final String LOG_TAG = WordToByte.class.getCanonicalName();


    public static byte[] wordtoByte(String word){

        try {
            String hash = getHashedString(word);


            byte[] in = word.getBytes();

            return in;
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Exception: " + e.getLocalizedMessage());
        }

        return null;

    }

    public static String getHashedString(String hashString) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance(Config.getHashAlgorithm());
        messageDigest.update(hashString.getBytes());
        return new String(messageDigest.digest());
    }

    public static boolean[] convertByteToBits(byte[] bs, int length) {
        boolean[] in = new boolean[Byte.SIZE*bs.length];
        int offset = 0;
        for (byte b : bs) {
            for (int i=0; i<Byte.SIZE; i++) in[i+offset] = (b >> i & 0x1) != 0x0;
            offset+=Byte.SIZE;
        }



        boolean[] out = new boolean[length];
        for(int i = 0; i< length; i++)
            out[i] = in[i];

        return out;
    }
}
