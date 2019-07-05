package com.johadahl.app1;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.johadahl.app1.config.Config;
import com.johadahl.app1.quiet.FrameReceiverObservable;


import java.nio.charset.Charset;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.Subscriptions;

public class studentValidation extends AppCompatActivity {

    private Subscription frameSubscription = Subscriptions.empty();
    //private TextView receiveStatus;
    //private TextView receivedContent;

    private TextView status;

    private String payload;

    private static final String LOG_TAG = studentValidation.class.getCanonicalName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_validation);

        TextView status = (TextView) findViewById(R.id.status);
        status.setText("Validating...");

        payload =  getIntent().getStringExtra("passphrase");

        //receiveStatus = findViewById(R.id.receiveStatus);
        //receivedContent = findViewById(R.id.receiveContent);



        // TODO: Validate input from sensors
        // Validation will either be successfull or unsuccessful ==>
        // TODO: Add alert dialog for successfull validation with button to progress
        // TODO: Add alert dialog for unsuccessfull validation with button to retry validation

    }

    @Override
    protected void onStart() {
        super.onStart();


        setupReceiver();
    }

    private void setupReceiver() {

            subscribeToFrames();

    }

    @Override
    protected void onPause() {
        super.onPause();
        frameSubscription.unsubscribe();
    }

    private void subscribeToFrames() {
        frameSubscription.unsubscribe();
        frameSubscription = FrameReceiverObservable.create(this, Config.profile).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(buf -> {
            //receivedContent.setText(new String(buf, Charset.forName("UTF-8")));
            Long time = System.currentTimeMillis() / 1000;
            String timestamp = time.toString();
            //receiveStatus.setText("Received " + buf.length + " @" + timestamp);

            String received = new String(buf, Charset.forName("UTF-8"));


            if(payload == null){
                payload =  getIntent().getStringExtra("passphrase");
            }

            if(received.compareTo(payload) == 0){
                TextView status = (TextView) findViewById(R.id.status);
                status.setText("Confirmed!!");
            }

            Log.d(LOG_TAG, "Received " + buf.length + " @" + timestamp);
        }, error-> {

            TextView status = (TextView) findViewById(R.id.status);
            status.setText("Error: " + error.toString());

            Log.d(LOG_TAG,"error " + error.toString() );
        });
    }
}
