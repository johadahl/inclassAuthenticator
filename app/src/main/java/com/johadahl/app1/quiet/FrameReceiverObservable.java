package com.johadahl.app1.quiet;


import android.content.Context;
import android.util.Log;

import org.quietmodem.Quiet.FrameReceiver;
import org.quietmodem.Quiet.FrameReceiverConfig;

import java.net.SocketTimeoutException;

import rx.Observable;
import rx.Subscription;


/**
 * Created by thoma on 11.03.2018.
 */

public class FrameReceiverObservable {

    private static final int TIEMOUT = 30;
    private static final int BUFFER_SIZE = 1024;
    private static final String LOG_TAG = FrameReceiverObservable.class.getCanonicalName();


    public static Observable<byte[]> create(Context context, String profile) {
        return Observable.create(subscriber -> {
            try {
                Log.d(LOG_TAG, "Observable started");
                FrameReceiverConfig receiverConfig = new FrameReceiverConfig(context, profile);
                final FrameReceiver frameReceiver = new FrameReceiver(receiverConfig);
                frameReceiver.setBlocking(TIEMOUT, 0);
                final byte[] buf = new byte[BUFFER_SIZE];

                subscriber.add(new Subscription() {
                    public boolean isUnsubscribed;

                    @Override
                    public void unsubscribe() {
                        frameReceiver.close();
                        isUnsubscribed = true;

                        Log.d(LOG_TAG, "Observable unsubsribed");
                    }

                    @Override
                    public boolean isUnsubscribed() {
                        return isUnsubscribed;
                    }
                });

                while(!subscriber.isUnsubscribed()) {
                    long recvLen = 0;
                    try {
                        recvLen = frameReceiver.receive(buf);
                        byte[] immutableBuf = java.util.Arrays.copyOf(buf, (int)recvLen);
                        subscriber.onNext(immutableBuf);
                    } catch (SocketTimeoutException e ) {
                        // ignore timeouts - attempt new receive
                    }
                }

            } catch (Exception e) {
                subscriber.onError(e);
            }

        });
    }
}
