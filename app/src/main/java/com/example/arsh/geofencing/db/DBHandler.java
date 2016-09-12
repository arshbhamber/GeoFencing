package com.example.arsh.geofencing.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by arsh on 12/9/16.
 */

public class DBHandler extends SQLiteOpenHelper {


    public static final int DATABASE_VERSION = 1;
    public static final String  DATABASE_NAME = "GeoFence.db";
    public static final String TABLE_NAME = "geofences";
    public static final String _ID = "id";
    public static final String COLUMN_NAME_TITLE = "location";
    public static final String COLUMN_NAME_SUBTITLE = "subtitle";

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    _ID + " INTEGER PRIMARY KEY," +
                    COLUMN_NAME_TITLE + TEXT_TYPE +  " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES);


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }



    public void insertValues(){



    }


}
