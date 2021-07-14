package com.example.pickup;

import android.app.Application;

import com.parse.Parse;

public class PickUpApplication extends Application {

    // Initializes Parse SDK as soon as the application is created
    @Override
    public void onCreate() {
        super.onCreate();

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("jtG8WfhyT0Sx2YbeFGHNGt3oQckjgtGUN1EOlqIf")
                .clientKey("nGo9Rwkxslzb9De6MSUjKdVFzZK1y0PAJu6Dz11D")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}
