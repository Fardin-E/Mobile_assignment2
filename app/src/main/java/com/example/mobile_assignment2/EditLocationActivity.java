package com.example.mobile_assignment2;

import androidx.appcompat.app.AppCompatActivity;
import android.content.ContentValues;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class EditLocationActivity extends AppCompatActivity {

    private EditText latitudeInput;
    private EditText longitudeInput;
    private Button saveButton;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_location);

        dbHelper = new DatabaseHelper(this);
        dbHelper.getWritableDatabase();

        latitudeInput = findViewById(R.id.editTextLatitude);
        latitudeInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
        longitudeInput = findViewById(R.id.editTextLongitude);
        longitudeInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);

        saveButton = findViewById(R.id.buttonSave);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveLocation();
            }
        });
    }

    private void saveLocation() {
        String latitudeText = latitudeInput.getText().toString();
        String longitudeText = longitudeInput.getText().toString();

        if (latitudeText.isEmpty() || longitudeText.isEmpty()) {
            Toast.makeText(this, "Please enter latitude and longitude.", Toast.LENGTH_SHORT).show();
            return;
        }

        double latitude = Double.parseDouble(latitudeText);
        double longitude = Double.parseDouble(longitudeText);

        String address = getAddressFromCoordinates(latitude, longitude);

        // Check for duplicates in the database
        if (dbHelper.isDuplicate(address,latitude, longitude)) {
            Toast.makeText(this, "Location already exists in the database.", Toast.LENGTH_SHORT).show();
        } else {

            if (address != null) {
                // Insert the address, latitude, and longitude into the database
                dbHelper.insertLocation(address, latitude, longitude);
                Toast.makeText(this, "Location saved successfully.", Toast.LENGTH_SHORT).show();
                finish(); // Finish the activity after saving
            } else {
                Toast.makeText(this, "Unable to retrieve address for the given coordinates.", Toast
                        .LENGTH_SHORT).show();
            }
        }
    }


    private String getAddressFromCoordinates(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);

            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                return address.getAddressLine(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
