package com.example.arsh.geofencing;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.location.Geofence;

import java.util.ArrayList;

/**
 * Created by arsh on 10/9/16.
 */

public class SecondActivity extends AppCompatActivity {

    ArrayList<Geofence> geofences;

    ListView lvGeoList;
    MyAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.second_activity);

        lvGeoList = (ListView) findViewById(R.id.lvGeoList);

        mAdapter = new MyAdapter(this,geofences);

        lvGeoList.setAdapter(mAdapter);


    }


    public static class MyAdapter extends BaseAdapter {

        Context mContext;

        ArrayList<Geofence> geofences;

        MyAdapter(Context mContext, ArrayList<Geofence> geofences) {
            this.mContext = mContext;
            this.geofences = geofences;
        }

        @Override
        public int getCount() {
            return geofences.size();
        }

        @Override
        public Geofence getItem(int i) {
            return geofences.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            TextView textView = new TextView(mContext);
            textView.setText(getItem(i).getRequestId());
            return textView;
        }
    }

}
