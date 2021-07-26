package com.example.pickup.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

// Android Dependencies
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

// Google Maps Dependencies
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

// Parse Dependencies
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;

// Java dependencies
import java.util.List;

import com.example.pickup.R;
import com.example.pickup.fragments.mainActivity.GameFragment;
import com.example.pickup.fragments.mainActivity.HomeFragment;
import com.example.pickup.fragments.mainActivity.MapsFragment;
import com.example.pickup.fragments.mainActivity.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int REQUEST_LOCATION = 1;

    FragmentManager fragmentManager;
    public static BottomNavigationView bottomNavigationView;
    Location location;
    LocationManager locationManager;
    ParseGeoPoint userLocation;
    ParseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialize location manager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //Get user's location
        saveCurrentUserLocation();

        //Find components
        fragmentManager = getSupportFragmentManager();
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        //Define fragments
        final Fragment fragment1 = new HomeFragment();
        final Fragment fragment2 = new GameFragment();
        final Fragment fragment3 = new MapsFragment();
        final Fragment fragment4 = new ProfileFragment();

        //Handle navigation selection
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;
                switch (item.getItemId()) {
                    case R.id.action_home:
                        fragment = fragment1;
                        //Get user's location
                        saveCurrentUserLocation();
                        Log.i(TAG, "onNavigationItemSelected: Home");
                        break;
                    case R.id.action_games:
                        fragment = fragment2;
                        Log.i(TAG, "onNavigationItemSelected: Games");
                        break;
                    case R.id.action_map:
                        fragment = fragment3;
                        Log.i(TAG, "onNavigationItemSelected: Map");
                        break;
                    case R.id.action_profile:
                        fragment = fragment4;
                        Log.i(TAG, "onNavigationItemSelected: Profile");
                        break;
                    default:
                        fragment = fragment1;
                        break;
                }
                fragmentManager.beginTransaction().replace(R.id.flContainerMain, fragment).commit();
                return true;
            }
        });

        // Set default selection
        bottomNavigationView.setSelectedItemId(R.id.action_home);
    }

    public void saveCurrentUserLocation() {
        //Ask for permission if not already granted
        if(ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        }
        else {
            // getting gps status
            boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if(isGPSEnabled) {
                Log.d(TAG, "saveCurrentUserLocation: GPS enabled");
                //Update location
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 5, new LocationListener() {
                    @Override
                    public void onLocationChanged(@NonNull Location location) {
                        //Log.d(TAG, "onLocationChanged: location changed " + location.toString());
                    }
                });

                //Getting last known user's location
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                //Update user's location
                updateUserLocation();
            }
            else {
                Log.d(TAG, "saveCurrentUserLocation: Network is not enabled");
            }
        }
    }

    private void updateUserLocation() {
        //Check if location is null
        if(location != null) {
            //If location isn't null, save it to the backend
            //Initialize user's GeoPoint location
            userLocation = new ParseGeoPoint(location.getLatitude(), location.getLongitude());

            user = ParseUser.getCurrentUser();

            if(user != null) {
                user.put("playerLocation", userLocation);
                user.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if( e == null) {
                            Log.i(TAG, "done: Updated user's location");
                        }
                        else {
                            Log.e(TAG, "done: Error updating user's location", e);
                        }
                    }
                });
            }
            else {
                Log.i(TAG, "saveCurrentUserLocation: User is null");
            }
        }
        else {
            Log.i(TAG, "saveCurrentUserLocation: Location is null");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == REQUEST_LOCATION) {
            saveCurrentUserLocation();
        }
    }

    @Override
    public void onBackPressed() {
        if(bottomNavigationView.getSelectedItemId() != R.id.action_home) {
            MainActivity.bottomNavigationView.setSelectedItemId(R.id.action_home);
            return;
        }
        super.onBackPressed();
    }
}