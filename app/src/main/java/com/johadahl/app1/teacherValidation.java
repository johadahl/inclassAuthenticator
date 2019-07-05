package com.johadahl.app1;

import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import com.johadahl.app1.config.Config;

import org.quietmodem.Quiet.FrameTransmitter;
import org.quietmodem.Quiet.FrameTransmitterConfig;
import org.quietmodem.Quiet.ModemException;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;

import static com.johadahl.app1.config.Config.duration;
import static com.johadahl.app1.config.Config.frequency;
import static com.johadahl.app1.config.Config.length_bytes;
import static com.johadahl.app1.util.WordToByte.convertByteToBits;
import static com.johadahl.app1.util.WordToByte.wordtoByte;

public class teacherValidation extends AppCompatActivity implements View.OnClickListener {
    private static final String LOG_TAG = "TeacherValidation";


    private FrameTransmitter transmitter;

    private PlayValidationTask playValidationTask;


    private boolean playValidation = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_validation);

        final Button doneButton = findViewById(R.id.done);
        doneButton.setOnClickListener(this);





        final ToggleButton startStop = (ToggleButton) findViewById(R.id.start);



/*        int count = toneSample.length;

        int length = count * (Short.SIZE / 16);

        Log.d(LOG_TAG, "TS length: " + length);
        tone.setNotificationMarkerPosition(length/2);
        tone.setPlaybackPositionUpdateListener(new AudioTrack.OnPlaybackPositionUpdateListener() {
            @Override
            public void onPeriodicNotification(AudioTrack track) {
                // nothing to do
            }
            @Override
            public void onMarkerReached(AudioTrack track) {
                Log.d(LOG_TAG, "Audio track end of file reached...");
                startStop.setChecked(false);

            }
        });*/

        setupTransmitter();


        startStop.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Log.d(LOG_TAG, "toggledToStart");

                    handleSendClick();

                } else {
                    Log.d(LOG_TAG, "toggledToStop");

                    if(playValidationTask != null){
                        playValidationTask.cancel(true);
                    }
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (transmitter != null) {
            transmitter.close();
        }
        if(playValidationTask != null){
            playValidationTask.cancel(true);
        }
    }

    private void handleSendClick() {

        setupTransmitter();

        //send();
        String payload =  getIntent().getStringExtra("passphrase");


        playValidationTask = new PlayValidationTask();

        playValidationTask.execute(payload);

    }


    private class PlayValidationTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            String sender = params[0];


                try {
                    while(!isCancelled()){
                        transmitter.send(sender.getBytes());
                        Thread.sleep(1000);
                    }



                    transmitter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            return null;

        }

    }


    @Override
    public void onClick(View view) {
       Log.d(LOG_TAG, "clickedDone");
       Intent back = new Intent(this, MainActivity.class);
       startActivity(back);

        if(playValidationTask != null){
            playValidationTask.cancel(true);
        }

    }




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

    private void setupTransmitter() {
        FrameTransmitterConfig transmitterConfig;
        try {
            transmitterConfig = new FrameTransmitterConfig(this, Config.profile);
            transmitter = new FrameTransmitter(transmitterConfig);


        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ModemException e) {
            throw new RuntimeException(e);
        }
    }
}