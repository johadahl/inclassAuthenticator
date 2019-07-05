package com.johadahl.app1.tone;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

import java.lang.reflect.Array;

/**
 * Created by thoma on 21.02.2018.
 */

public class ToneGeneration {


    public static final String LOG_TAG = ToneGeneration.class.getCanonicalName();


    private short[] buildTone(boolean[]tones, double frequency, int durationMs){


        short[] samples = new short[0];

        for(int i = 0; i < tones.length; i++){
            short[] sample;
            if(tones[i]){
                sample = generateToneArray(frequency, durationMs);
                Log.d(LOG_TAG, "Generated ToneArray: " + i);
            }
            else{
                sample = generateBlankArray(durationMs);
                Log.d(LOG_TAG, "Generated BlankArray: " + i);
            }


            samples = concatenate(samples,sample);

        }


        Log.d(LOG_TAG,"Duration samples: " + samples.length / 44100);
        return samples;



    }

    private AudioTrack generateTrack(short[] samples){
        int count = samples.length;

        Log.d(LOG_TAG, "Count: " + count);

        AudioTrack track = new AudioTrack(AudioManager.STREAM_MUSIC, 44100,
                AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT,
                count * (Short.SIZE / 8), AudioTrack.MODE_STATIC);
        track.write(samples, 0, count);
        return track;
    }


    private short[] generateToneArray(double freqHz, int durationMs){
        int count = (int)(44100.0 * (durationMs / 1000.0)) & ~1;
        short[] samples = new short[count];
        for(int i = 0; i < count; i += 1){
            short sample = (short)(Math.sin( Math.PI * i / (44100.0 / freqHz)) * 0x7FFF);

            samples[i] = sample;
        }


        return samples;
    }

    private short[] generateBlankArray(int durationMs){
        int count = (int)(44100.0 * (durationMs / 1000.0)) & ~1;
        short[] sampleBlank = new short[count];
        for(int i = 0; i < count; i += 1){
            sampleBlank[i] = 0;
        }

        return sampleBlank;
    }



    public static <T> T concatenate(T a, T b) {
        if (!a.getClass().isArray() || !b.getClass().isArray()) {
            throw new IllegalArgumentException();
        }

        Class<?> resCompType;
        Class<?> aCompType = a.getClass().getComponentType();
        Class<?> bCompType = b.getClass().getComponentType();

        if (aCompType.isAssignableFrom(bCompType)) {
            resCompType = aCompType;
        } else if (bCompType.isAssignableFrom(aCompType)) {
            resCompType = bCompType;
        } else {
            throw new IllegalArgumentException();
        }

        int aLen = Array.getLength(a);
        int bLen = Array.getLength(b);

        @SuppressWarnings("unchecked")
        T result = (T) Array.newInstance(resCompType, aLen + bLen);
        System.arraycopy(a, 0, result, 0, aLen);
        System.arraycopy(b, 0, result, aLen, bLen);

        return result;
    }
}
