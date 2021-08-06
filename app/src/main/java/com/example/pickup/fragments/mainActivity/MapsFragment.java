package com.example.pickup.fragments.mainActivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.os.Build;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class MapsFragment extends Fragment{
    
    private static final String TAG = "MapsFragment";

    List<Game> gameList;
    ParseUser user;
    Game currentGame;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

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
                if(currentGame != null && game.getObjectId().equals(currentGame.getObjectId())) {
                    Bitmap bitmap = getBitmapFromVectorDrawable(getContext(), R.drawable.ic_basketball_map_marker_icon_2);
                    googleMap.addMarker(
                            new MarkerOptions()
                                    .position(gameLocation)
                                    .title(game.getGameType())
                                    .snippet("@" + game.getLocationName())
                                    .icon(BitmapDescriptorFactory.fromBitmap(bitmap)));
                }
                else {
                    Bitmap bitmap = getBitmapFromVectorDrawable(getContext(), R.drawable.ic_map_marker3);
                    googleMap.addMarker(
                            new MarkerOptions()
                                    .position(gameLocation)
                                    .title(game.getGameType())
                                    .snippet("@" + game.getLocationName())
                                    .icon(BitmapDescriptorFactory.fromBitmap(bitmap)));
                }
            }

            //Zooming in camera
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
        }
    };

    private Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

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

        user = ParseUser.getCurrentUser();
        currentGame = (Game) user.getParseObject("currentGame");
        try {
            if(currentGame != null) {
                currentGame.fetchIfNeeded();
            }
        } catch (ParseException e) {
            Log.e(TAG, "onViewCreated: Cannot fetch current game", e);
        }
        gameList = HomeFragment.gamesFeed;

        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }
}