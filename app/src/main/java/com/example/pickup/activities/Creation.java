package com.example.pickup.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pickup.R;
import com.example.pickup.fragments.games.CurrentGameFragment;
import com.example.pickup.fragments.games.RecentGamesFragment;
import com.example.pickup.fragments.userAuthentication.LoginFragment;
import com.example.pickup.fragments.userAuthentication.SignupFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class Creation extends AppCompatActivity {

    private static final String TAG = "Creation";
    public static final int REQUEST_CODE = 1;
    public static final String KEY_LOCATION = "location";

    Toolbar toolbar;
    EditText etLocation;
    AutoCompleteTextView actvGameType;
    EditText etPlayerLimit;
    EditText etScoreLimit;
    CheckBox cbWinBy2;
    TextView tvWinBy2Text;
    Button btnCancel;
    Button btnCreate;
    ImageView ivSelectLocation;
    ActivityResultLauncher<Intent> autoCompleteResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creation);

        //Find components
        toolbar = findViewById(R.id.tbCreation);
        etLocation = findViewById(R.id.etLocationCreation);
        actvGameType = findViewById(R.id.actvGameTypeCreation);
        etPlayerLimit = findViewById(R.id.etPlayerLimitCreation);
        etScoreLimit = findViewById(R.id.etScoreLimitCreation);
        cbWinBy2 = findViewById(R.id.cbWinBy2Creation);
        tvWinBy2Text = findViewById(R.id.tvWinBy2TextCreation);
        btnCancel = findViewById(R.id.btnCancelCreation);
        btnCreate = findViewById(R.id.btnCreateCreation);
        ivSelectLocation = findViewById(R.id.ivSelectLocation);

        //Toolbar
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        //Cancel Button
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //Create Button
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //Select location button
        ivSelectLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Go to map activity
                Log.d(TAG, "onClick: Started map");
                Intent intent = new Intent(Creation.this, MapsActivity.class);
                intent.putExtra(KEY_LOCATION, etLocation.getText());

                autoCompleteResultLauncher.launch(intent);
            }
        });

        //Create launcher (startActivityForResult is deprecated)
        autoCompleteResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        Intent data = result.getData();
                        //Log.d(TAG, "onActivityResult: " + data.getStringExtra(KEY_LOCATION));
                        if(result.getResultCode() == RESULT_OK) {
                            //Set text
                            Log.d(TAG, "onActivityResult: Recieved data");
                            etLocation.setText(data.getStringExtra(KEY_LOCATION));

                        }
                        else {
                            //Error handling
                            Log.d(TAG, "onActivityResult: Error getting place. " + result.getResultCode());
                        }
                        Creation.super.onActivityResult(REQUEST_CODE, result.getResultCode(), data);
                    }
                }
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.creation, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.d(TAG, "onActivityResult: got data from map");
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            // Extract name value from result extras
            String location = data.getExtras().getString(KEY_LOCATION);
            etLocation.setText(location);
        }
        else {
            Log.d(TAG, "onActivityResult: Error");
        }

    }
}