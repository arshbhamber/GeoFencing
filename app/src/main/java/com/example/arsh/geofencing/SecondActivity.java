package com.example.arsh.geofencing;

import android.Manifest;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.arsh.geofencing.db.DBHandler;
import com.example.arsh.geofencing.dialog.MyAlertDialog;
import com.example.arsh.geofencing.pojo.SimpleGeofence;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * Created by arsh on 10/9/16.
 */

public class SecondActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback, LocationListener {

    ArrayList<SimpleGeofence> geofences;

    ListView lvGeoList;
    MyAdapter mAdapter;

    DBHandler dbHandler;

    FloatingActionButton fabAddLocation;

    ArrayList<Geofence> geofenceArrayList;

    GoogleApiClient mGoogleApiClient;

    String tag = "";

    String[] projection = {
            DBHandler._ID,
            DBHandler.COLUMN_NAME_TITLE,
    };

    String selection = DBHandler.COLUMN_NAME_TITLE + " = ?";
    String[] selectionArgs = {"My Title"};


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.second_activity);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }


        lvGeoList = (ListView) findViewById(R.id.lvGeoList);

        fabAddLocation = (FloatingActionButton) findViewById(R.id.fabAddLocation);

        geofences = new ArrayList<>();

        geofenceArrayList = new ArrayList<>();

        dbHandler = new DBHandler(this);


        Gson gson = new Gson();

        SQLiteDatabase db = dbHandler.getReadableDatabase();

        Cursor c = db.rawQuery("select * from " + DBHandler.TABLE_NAME, null);

        c.moveToFirst();

        while (c.moveToNext()) {

            SimpleGeofence simpleGeofence = gson.fromJson(c.getString(c.getColumnIndex(DBHandler.COLUMN_NAME_TITLE)), SimpleGeofence.class);

            geofences.add(simpleGeofence);


            geofenceArrayList.add(new Geofence.Builder()

                    .setRequestId(tag)

                    .setCircularRegion(
                            simpleGeofence.getLatitude(),
                            simpleGeofence.getLongitude(),
                            simpleGeofence.getRadius()
                    )
                    .setExpirationDuration(simpleGeofence.getExpirationDuration())
                    .setTransitionTypes(simpleGeofence.getTransitionType())
                    .build());


        }

        c.close();

        mAdapter = new MyAdapter(this, geofences, geofenceArrayList, mGoogleApiClient, dbHandler);

        lvGeoList.setAdapter(mAdapter);


        fabAddLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (ActivityCompat.checkSelfPermission(SecondActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(SecondActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }


                MyAlertDialog dialog = new MyAlertDialog(SecondActivity.this, mGoogleApiClient);
                dialog.setTitle("Title");

                dialog.onTagAddedListener(new MyAlertDialog.OnTagAdded() {
                    @Override
                    public void onTagAdded(Location loc, String tag) {


                        Bundle extras = new Bundle();
                        extras.putString("tag", tag);
                        loc.setExtras(extras);

                        SQLiteDatabase db = dbHandler.getWritableDatabase();

                        Gson gson = new Gson();

                        ContentValues values = new ContentValues();

                        SimpleGeofence location = new SimpleGeofence(tag, loc.getLatitude(), loc.getLongitude(), 150, 1000000, Geofence.GEOFENCE_TRANSITION_ENTER |
                                Geofence.GEOFENCE_TRANSITION_EXIT);


                        values.put(DBHandler.COLUMN_NAME_TITLE, gson.toJson(location));

                        db.insert(DBHandler.TABLE_NAME, null, values);

                        geofences.add(location);

                        geofenceArrayList.add(new Geofence.Builder()

                                .setRequestId(tag)

                                .setCircularRegion(
                                        location.getLatitude(),
                                        location.getLongitude(),
                                        location.getRadius()
                                )
                                .setExpirationDuration(location.getExpirationDuration())
                                .setTransitionTypes(location.getTransitionType())
                                .build());

                        mAdapter.notifyDataSetChanged();

                        if (ActivityCompat.checkSelfPermission(SecondActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }

                        LocationServices.GeofencingApi.addGeofences(
                                mGoogleApiClient,
                                getGeofencingRequest(),
                                getGeofencePendingIntent()
                        ).setResultCallback(SecondActivity.this);


                    }
                });

                dialog.show();


            }
        });


    }


    @Override
    protected void onStart() {
        super.onStart();

        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        }else {

            LocationRequest locationRequest = new LocationRequest();
            locationRequest.setInterval(10000).setFastestInterval(5000).setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY).setSmallestDisplacement(50f);

            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED) {

            LocationRequest locationRequest = new LocationRequest();
            locationRequest.setInterval(10000).setFastestInterval(5000).setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY).setSmallestDisplacement(50f);

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);

        }



    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onResult(@NonNull Result result) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }


    public static class MyAdapter extends BaseAdapter {

        Context mContext;

        ArrayList<SimpleGeofence> geofences;

        ArrayList<Geofence> geofenceArrayList;

        GoogleApiClient mGoogleApiClient;
        DBHandler dbHandler;

        MyAdapter(Context mContext, ArrayList<SimpleGeofence> geofences,ArrayList<Geofence> geofenceArrayList, GoogleApiClient googleApiClient,DBHandler dbHandler) {
            this.mContext = mContext;
            this.geofences = geofences;
            this.mGoogleApiClient = googleApiClient;
            this.dbHandler = dbHandler;
            this.geofenceArrayList = geofenceArrayList;

        }

        @Override
        public int getCount() {
            return geofences.size();
        }

        @Override
        public SimpleGeofence getItem(int i) {
            return geofences.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {

            View v = LayoutInflater.from(mContext).inflate(R.layout.geo_list_item, null);
            TextView tvLocation = (TextView) v.findViewById(R.id.tvLocation);
            TextView tvLocCoords = (TextView) v.findViewById(R.id.tvLocCoords);
            final TextView tvRegister = (TextView) v.findViewById(R.id.tvRegister);
            TextView tvRemove = (TextView) v.findViewById(R.id.tvRemove);

            tvRegister.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    LocationServices.GeofencingApi.removeGeofences(mGoogleApiClient, getGeofencePendingIntent());

                    if (geofenceArrayList.size()>= i && geofenceArrayList.get(i).getRequestId().equalsIgnoreCase(getItem(i).getId())) {

                        geofenceArrayList.remove(i);


                        tvRegister.setText("Register");


                    }else{

                        tvRegister.setText("UnRegister");

                        geofenceArrayList.add(i,new Geofence.Builder()

                                .setRequestId(getItem(i).getId())

                                .setCircularRegion(
                                        getItem(i).getLatitude(),
                                        getItem(i).getLongitude(),
                                        getItem(i).getRadius()
                                )
                                .setExpirationDuration(getItem(i).getExpirationDuration())
                                .setTransitionTypes(getItem(i).getTransitionType())
                                .build());

                    }

                    if(geofenceArrayList.isEmpty())
                        return;

                    if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    LocationServices.GeofencingApi.addGeofences(
                            mGoogleApiClient,
                            getGeofencingRequest(),
                            getGeofencePendingIntent()
                    ).setResultCallback((SecondActivity)mContext);

                }


            });



            tvRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    LocationServices.GeofencingApi.removeGeofences(mGoogleApiClient, getGeofencePendingIntent());

                    geofenceArrayList.remove(i);
                    geofences.remove(i);

                    notifyDataSetChanged();

                    if(geofenceArrayList.isEmpty())
                        return;

                    if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    LocationServices.GeofencingApi.addGeofences(
                            mGoogleApiClient,
                            getGeofencingRequest(),
                            getGeofencePendingIntent()
                    ).setResultCallback((SecondActivity)mContext);

                    Gson gson = new Gson();


                    SQLiteDatabase db= dbHandler.getWritableDatabase();
                    db.execSQL("DELETE FROM "+DBHandler.TABLE_NAME+" WHERE "+DBHandler.COLUMN_NAME_TITLE+"='"+gson.toJson(getItem(i))+"'");
                    db.close();





            }
            });

            tvLocation.setText(getItem(i).getId());
            tvLocCoords.setText("(" + getItem(i).getLatitude() + "," + getItem(i).getLongitude() + ")");
            return v;
        }

        private GeofencingRequest getGeofencingRequest() {
            GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
            builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
            builder.addGeofences(geofenceArrayList);
            return builder.build();
        }

        private PendingIntent getGeofencePendingIntent() {

            Intent intent = new Intent(mContext, GeofenceTransitionsIntentService.class);

            return PendingIntent.getService(mContext, 0, intent, PendingIntent.
                    FLAG_UPDATE_CURRENT);
        }

    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(geofenceArrayList);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent() {

        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);

        return PendingIntent.getService(this, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
    }



}
