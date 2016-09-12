package com.example.arsh.geofencing.services;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;

import java.util.ArrayList;

/**
 * Created by arsh on 9/9/16.
 */

public class GeoFenceService extends Service {

    MyServiceHandler myServiceHandler;
    GoogleApiClient mGoogleApiClient;
    ArrayList<Geofence> geofences;



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        HandlerThread thread = new HandlerThread("geoFencing",Thread.MAX_PRIORITY);
        thread.start();

        myServiceHandler = new MyServiceHandler(thread.getLooper());

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Message msg = myServiceHandler.obtainMessage();
        myServiceHandler.handleMessage(msg);


        return START_STICKY;
    }


    private class MyServiceHandler extends Handler {

         MyServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);



//            geofences.add(new Geofence.Builder()
//                    // Set the request ID of the geofence. This is a string to identify this
//                    // geofence.
//                    .setRequestId(entry.getKey())
//
//                    .setCircularRegion(
//                            entry.getValue().latitude,
//                            entry.getValue().longitude,
//                            Constants.GEOFENCE_RADIUS_IN_METERS
//                    )
//                    .setExpirationDuration(Constants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)
//                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
//                            Geofence.GEOFENCE_TRANSITION_EXIT)
//                    .build());


        }
    }


}
