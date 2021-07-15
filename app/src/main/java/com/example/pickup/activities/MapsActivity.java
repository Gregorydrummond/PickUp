package com.example.pickup.activities;

import androidx.fragment.app.FragmentActivity;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.example.pickup.R;
import com.example.pickup.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
//import com.example.pickup.activities.databinding.ActivityMapsBinding;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "MapsActivity";

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    LatLng location;

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

        //Zooming in camera
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 12));
    }
}