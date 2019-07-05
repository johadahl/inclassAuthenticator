package com.johadahl.app1.config;

/**
 * Created by thoma on 06.02.2018.
 */

public class Config {

    private static final String hashAlgorithm = "SHA-256";

    private static final int numberBytes = 10;

    public static int getNumberBytes() {
        return numberBytes;
    }

    public static String getHashAlgorithm() {
        return hashAlgorithm;
    }


    public static final int frequency = 5000;

    public static final int length_bytes = 10;

    public static final int duration = 1000;

    public static final String profile = "ultrasonic";

}
