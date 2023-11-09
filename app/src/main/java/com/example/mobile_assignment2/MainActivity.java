package com.example.mobile_assignment2;


import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private SearchView searchView;
    DatabaseHelper dbHelper;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create or open the database
        dbHelper = new DatabaseHelper(this);
        db = dbHelper.getWritableDatabase();

        searchView = findViewById(R.id.searchView);

        // Initialize the RecyclerView and its adapter
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        AddressAdapter addressAdapter = new AddressAdapter(this, new ArrayList<AddressModel>());
        recyclerView.setAdapter(addressAdapter);

        FloatingActionButton fabAddLocation = findViewById(R.id.fabAddLocation);
        fabAddLocation.setOnClickListener(view -> {
            // Launch the new activity to add, delete, or update entries
            Intent intent = new Intent(MainActivity.this, EditLocationActivity.class);
            startActivity(intent);
        });


        List<AddressModel> addressList = loadFromDatabase();

        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                List<AddressModel> filteredList = performSearch(newText);

                // Update the RecyclerView with the filtered list
                addressAdapter.updateData(filteredList);

                return true;
            }
        });

        // Load data from the database and update the RecyclerView


        try {
            AssetManager assetManager = getAssets();
            InputStream inputStream = assetManager.open("location_data.txt");
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] parts = line.split(",");
                double latitude = Double.parseDouble(parts[0]);
                double longitude = Double.parseDouble(parts[1]);

                // Log latitude and longitude
                Log.d(TAG, "Latitude: " + latitude + ", Longitude: " + longitude);

                // Reverse geocode the coordinates and log the address
                logAddressFromCoordinates(latitude, longitude);
            }
        } catch (IOException e) {
            e.printStackTrace();
            // Log the error message
            Log.e(TAG, "Error while reading location data: " + e.getMessage());
        }

        loadFromDatabase();
        addressAdapter.updateData(addressList);

    }

    private void logAddressFromCoordinates(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                String addressString = address.getAddressLine(0); // Get the first address line
                Log.d(TAG, "Address: " + addressString);

                if(!dbHelper.isDuplicate(addressString, latitude, longitude)) {
                    dbHelper.insertLocation(addressString, latitude, longitude);
                } else {
                    Log.d(TAG, "duplicated address");
                }

            } else {
                Log.d(TAG, "No address found for the given coordinates.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Error while reverse geocoding: " + e.getMessage());
        }
    }
    private List<AddressModel> loadFromDatabase() {
        List<AddressModel> addressList = new ArrayList<>();

        // Define the columns you want to retrieve (e.g., address, latitude, and longitude)
        String[] columns = {dbHelper.COLUMN_ADDRESS, dbHelper.COLUMN_LATITUDE, dbHelper.COLUMN_LONGITUDE};

        // Initialize the cursor
        Cursor cursor = null;

        try {
            // Perform a query to retrieve data
            cursor = db.query(DatabaseHelper.location, columns, null, null, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    // Retrieve data from the cursor
                    String address = cursor.getString(cursor.getColumnIndexOrThrow(dbHelper.COLUMN_ADDRESS));
                    double latitude = cursor.getDouble(cursor.getColumnIndexOrThrow(dbHelper.COLUMN_LATITUDE));
                    double longitude = cursor.getDouble(cursor.getColumnIndexOrThrow(dbHelper.COLUMN_LONGITUDE));

                    // Create an AddressModel object and add it to the list
                    AddressModel addressModel = new AddressModel(address, latitude, longitude);
                    addressList.add(addressModel);

                    // Log the data being loaded
                    Log.d(TAG, "Loaded from the database: Address: " + address + ", Latitude: " + latitude + ", Longitude: " + longitude);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Error while loading data from the database: " + e.getMessage());
        } finally {
            // Close the cursor if it's not null
            if (cursor != null) {
                cursor.close();
            }
        }

        return addressList;
    }

    private List<AddressModel> performSearch(String query) {
        List<AddressModel> filteredList = new ArrayList<>();

        if (query.isEmpty()) {
            // If the search query is empty, return the original list
            return loadFromDatabase();
        }

        // Iterate through the original list and add matching items to the filtered list
        for (AddressModel addressModel : loadFromDatabase()) {
            if (addressModel.getAddress().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(addressModel);
            }
        }

        return filteredList;
    }



}
