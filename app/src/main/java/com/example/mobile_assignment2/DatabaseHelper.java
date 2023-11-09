package com.example.mobile_assignment2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "location.db";
    public static final String location = "LOCATION";
    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_ADDRESS = "ADDRESS";

    public static final String COLUMN_LATITUDE = "LATITUDE";

    public static final String COLUMN_LONGITUDE = "LONGITUDE";

    SQLiteDatabase db;

    AddressModel addressModel = new AddressModel();
    double latitude = addressModel.getLatitude();
    double longitude = addressModel.getLongitude();

    private static final String TAG = "MainActivity";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableStatement = "CREATE TABLE " + location + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_ADDRESS + " TEXT, " + COLUMN_LATITUDE + " REAL, " + COLUMN_LONGITUDE + " REAL)";

        db.execSQL(createTableStatement);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public void insertLocation(String address, double latitude, double longitude) {
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(this.COLUMN_ADDRESS, address);
        values.put(this.COLUMN_LATITUDE, latitude);
        values.put(this.COLUMN_LONGITUDE, longitude);
        long newRowId = db.insert(this.location, null, values);
        Log.d(TAG, "Inserted address into the database with ID: " + newRowId);
    }
    public boolean isDuplicate(String address, double latitude, double longitude) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                location,  // Table name
                new String[]{COLUMN_ID},  // Columns to return (you can use other columns for more specific checks)
                COLUMN_ADDRESS + " = ? AND " + COLUMN_LATITUDE + " = ? AND " + COLUMN_LONGITUDE + " = ?",  // Selection criteria
                new String[]{address, String.valueOf(latitude), String.valueOf(longitude)},  // Selection arguments
                null,  // Group by
                null,  // Having
                null   // Order by
        );

        boolean isDuplicate = cursor.getCount() > 0;
        cursor.close();

        return isDuplicate;
    }


    public void deleteLocation(String address) {
        SQLiteDatabase db = this.getWritableDatabase();

        String selection = COLUMN_ADDRESS + " = ?";
        String[] selectionArgs = { address };

        int deletedRows = db.delete(location, selection, selectionArgs);
        db.close();

        if (deletedRows > 0) {
            Log.d(TAG, "Deleted location from the database: " + address);
        }
    }

    public void deleteAllDatabaseEntries() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(location, null, null);
        db.close();
    }


}
