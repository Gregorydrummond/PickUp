package com.example.pickup.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.SearchView;

import com.example.pickup.R;
import com.example.pickup.databinding.ActivityMapsBinding;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
//import com.example.pickup.activities.databinding.ActivityMapsBinding;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "MapsActivity";
    public static final int AUTOCOMPLETE_REQUEST_CODE = 1;

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    LatLng location;
    LatLng gameLocation;
    ActivityResultLauncher<Intent> autoCompleteResultLauncher;
    SearchView svLocation;
    List<Address> addressList;

    // Create a new PlacesClient instance
    PlacesClient placesClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Log.d(TAG, "onMapReady: Map is loaded");

        //Getting user's location
        ParseUser user = ParseUser.getCurrentUser();
        ParseGeoPoint userParseGeoPoint = user.getParseGeoPoint("playerLocation");
        if (userParseGeoPoint != null) {
            location = new LatLng(userParseGeoPoint.getLatitude(), userParseGeoPoint.getLongitude());
            Log.i(TAG, "onMapReady: Retrieved user's location");
        }
        else {
            Log.i(TAG, "onMapReady: Error getting user's geo point");
        }

        // Go to user's location
        mMap.moveCamera(CameraUpdateFactory.newLatLng(location));

        //Adding a marker at user's location
        googleMap.addMarker(new MarkerOptions().position(location).title("You"));


        //Zooming in camera
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15));

        mMap.setOnMapClickListener(latLng -> {
            googleMap.addMarker(new MarkerOptions().position(latLng).title("Your game location"));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            gameLocation = latLng;
            Log.i(TAG, "onMapClick: Added marker at " + latLng.toString());
            Log.d(TAG, "onStop: Sending back data");
            //Create an intent which which will contain the results
            Intent intent = new Intent();

            //Pass the data (results of editing)
            intent.putExtra(Creation.KEY_LOCATION_LAT, gameLocation.latitude);
            intent.putExtra(Creation.KEY_LOCATION_LONG, gameLocation.longitude);

            //Set the result of the intent
            setResult(RESULT_OK, intent);
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}