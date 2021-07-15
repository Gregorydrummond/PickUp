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

        //Initialize placeClient
        placesClient = Places.createClient(this);

        //Components
        //svLocation = findViewById(R.id.svLocation);

//        //Query listener
//        svLocation.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                String location = svLocation.getQuery().toString();
//                if(location != null || !location.isEmpty()) {
//                    Geocoder geocoder = new Geocoder(MapsActivity.this);
//                    try {
//                        addressList = geocoder.getFromLocationName(location, 1);
//                    } catch (IOException e) {
//                        Log.e(TAG, "onQueryTextSubmit: Error with query submit", e);
//                    }
//                    Address address = addressList.get(0);
//                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
//                    mMap.addMarker(new MarkerOptions().position(latLng).title(location));
//                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));
//                }
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
////                //Initialize place field list
////                List<Place.Field> fieldList = Arrays.asList(Place.Field.ID, Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME);
////
////                //Create Intent
////                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldList).build(MapsActivity.this);
//
////                //Start activity result
////                autoCompleteResultLauncher.launch(intent);
//                return false;
//            }
//        });
//        final AutocompleteSupportFragment autocompleteSupportFragment =
//                (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.autoComplete_fragment);
//
//        //What fields we'll get
//        autocompleteSupportFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.LAT_LNG, Place.Field.NAME));
//
//        //
//        autocompleteSupportFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
//            @Override
//            public void onPlaceSelected(@NonNull Place place) {
//                final LatLng latLng = place.getLatLng();
//                Log.i(TAG, "onPlaceSelected: " + latLng.toString());
//            }
//
//            @Override
//            public void onError(@NonNull Status status) {
//                Log.i(TAG, "onError: Error with autocomplete");
//            }
//        });

        //Search view listener
//        svLocation.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                //API call
//                //Use the builder to create a FindAutocompletePredictionsRequest.
//                FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
//                        .setQuery(newText)
//                        .build();
//                placesClient.findAutocompletePredictions(request).addOnSuccessListener((response) -> {
//                    for(AutocompletePrediction prediction : response.getAutocompletePredictions()) {
//                        Log.i(TAG, "onQueryTextChange: " + prediction.getPlaceId() + ", " + prediction.getPrimaryText(null).toString());
//                    }
//                }).addOnFailureListener((exception) -> {
//                    if(exception instanceof ApiException) {
//                        ApiException apiException = (ApiException) exception;
//                        Log.e(TAG, "onQueryTextChange: Place not found: " + apiException.getStatusCode());
//                    }
//                });
//                return false;
//            }
//        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

//        Create launcher (startActivityForResult is deprecated)
//        autoCompleteResultLauncher = registerForActivityResult(
//                new ActivityResultContracts.StartActivityForResult(),
//                new ActivityResultCallback<ActivityResult>() {
//                    @Override
//                    public void onActivityResult(ActivityResult result) {
//                        Intent data = result.getData();
//                        if(result.getResultCode() == RESULT_OK) {
//                            //Get place info with the data returned
//                            Place place = Autocomplete.getPlaceFromIntent(data);
//                            Log.i(TAG, "onActivityResult: Place: " + place.getName() + ", " + place.getId() + ", " + place.getLatLng().toString());
//                        }
//                        else if(result.getResultCode() == AutocompleteActivity.RESULT_ERROR) {
//                            //Error handling
//                            Log.d(TAG, "onActivityResult: Error getting place. "  + Autocomplete.getStatusFromIntent(data).getStatusMessage());
//                        }
//                        else if(result.getResultCode() == AutocompleteActivity.RESULT_CANCELED) {
//                            //User canceled
//                            Log.i(TAG, "onActivityResult: User canceled search");
//                        }
//                        MapsActivity.super.onActivityResult(AUTOCOMPLETE_REQUEST_CODE, result.getResultCode(), data);
//                    }
//                }
//        );
    }

    //Launch activity for result
//    public void openSomeActivityForResult() {
//        // Set the fields to specify which types of place data to return after the user has made a selection.
//        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);
//
//        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields).build(this);
//        autoCompleteResultLauncher.launch(intent);
//    }

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

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                googleMap.addMarker(new MarkerOptions().position(latLng).title("Your game location"));
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                gameLocation = latLng;
                Log.i(TAG, "onMapClick: Added marker at " + latLng.toString());
                Log.d(TAG, "onStop: Sending back data");
                //Create an intent which which will contain the results
                Intent intent = new Intent();

                //Pass the data (results of editing)
                String location = "Lat: " + gameLocation.latitude + ", Long: " + gameLocation.longitude;
                intent.putExtra(Creation.KEY_LOCATION, location);

                //Set the result of the intent
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

}