package com.example.arsh.geofencing.dialog;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.arsh.geofencing.R;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

/**
 * Created by arsh on 13/9/16.
 */

public class MyAlertDialog extends Dialog {

    OnTagAdded onTagAdded;

    EditText etLat, etLong, etLocationTag;

    GoogleApiClient mGoogleApiClient;

    Button btnAddLocation ;

    Location currentLocation;

    public MyAlertDialog(@NonNull Context context,GoogleApiClient googleApiClient) {
        super(context);
        this.mGoogleApiClient = googleApiClient;

    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_location);

        etLat = (EditText)findViewById(R.id.etLat);
        etLong = (EditText)findViewById(R.id.etLong);
        etLocationTag = (EditText)findViewById(R.id.etLocationTag);
        btnAddLocation = (Button)findViewById(R.id.btnAddLocation);

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        currentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);


        etLat.setText(currentLocation.getLatitude() + "");
        etLong.setText(currentLocation.getLongitude() + "");

        btnAddLocation.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {


                if (currentLocation == null) {
                    Toast.makeText(getContext(), "Enter Location", Toast.LENGTH_SHORT).show();
                    return;
                }
                String tag = etLocationTag.getText().toString();

                if (TextUtils.isEmpty(tag)) {

                    Toast.makeText(getContext(), "Enter the Tag first", Toast.LENGTH_SHORT).show();
                    return;

                }



                if(onTagAdded !=null)
                    onTagAdded.onTagAdded(currentLocation,tag);

                dismiss();

            }

        });


    }

    public void onTagAddedListener(OnTagAdded onTagAdded){

        this.onTagAdded = onTagAdded;

    }


    public interface OnTagAdded{

        void onTagAdded(Location location, String tag);

    }



}
