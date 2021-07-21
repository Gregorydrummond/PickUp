package com.example.pickup.fragments.mainActivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pickup.R;
import com.example.pickup.activities.GameDetailsActivity;
import com.example.pickup.models.Game;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class MapsFragment extends Fragment{
    
    private static final String TAG = "MapsFragment";

    List<Game> gameList;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
            Log.d(TAG, "onMapReady: Map is loaded");

            //Getting user's location
            LatLng userLocation = new LatLng(ParseUser.getCurrentUser().getParseGeoPoint("playerLocation").getLatitude(), ParseUser.getCurrentUser().getParseGeoPoint("playerLocation").getLongitude());

            //Adding a marker at user's location
            googleMap.addMarker(new MarkerOptions().position(userLocation).title("Your location"));

            //Moving camera onto user's location
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation));

            //Add markers on game locations
            for(Game game : gameList) {
                ParseGeoPoint gameLocationPGP = game.getLocation();
                LatLng gameLocation = new LatLng(gameLocationPGP.getLatitude(), gameLocationPGP.getLongitude());
                googleMap.addMarker(
                        new MarkerOptions()
                                .position(gameLocation)
                                .title(game.getGameType())
                                .snippet("@" + game.getLocationName()));
            }

            //Zooming in camera
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.fragment_map);

        gameList = HomeFragment.gamesFeed;

        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }

    }
}