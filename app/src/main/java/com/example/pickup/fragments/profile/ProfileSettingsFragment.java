package com.example.pickup.fragments.profile;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pickup.R;
import com.example.pickup.activities.UserAuthentication;
import com.parse.ParseUser;

public class ProfileSettingsFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private static final String TAG = "ProfileSettingsFragment";

    EditText etMaxDistance;
    Spinner spinnerGameTypeFilter;
    Button btnSignOut;
    Button btnSave;
    ArrayAdapter<CharSequence> adapter;
    ParseUser user = ParseUser.getCurrentUser();;

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public ProfileSettingsFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static ProfileSettingsFragment newInstance(String param1, String param2) {
        ProfileSettingsFragment fragment = new ProfileSettingsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Find components
        btnSignOut = view.findViewById(R.id.btnSignout);
        etMaxDistance = view.findViewById(R.id.etMaxDistancePS);
        spinnerGameTypeFilter = view.findViewById(R.id.spinnerGameTypePS);
        btnSave = view.findViewById(R.id.btnSave);

        //Spinner setup
        //Initialize an ArrayAdapter using the string array and a default spinner layout
        adapter = ArrayAdapter.createFromResource(getContext(), R.array.game_filter, android.R.layout.simple_spinner_item);

        //Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //Set adapter to spinner
        spinnerGameTypeFilter.setAdapter(adapter);

        //Set on item selected listener
        spinnerGameTypeFilter.setOnItemSelectedListener(this);

        //On save button click
        btnSave.setOnClickListener(v -> {
            //Save user settings
            if(!etMaxDistance.getText().toString().isEmpty()) {
                user.put("maxDistance", Double.parseDouble(etMaxDistance.getText().toString()));
            }
            user.saveInBackground();
            Toast.makeText(getContext(), "Settings saved", Toast.LENGTH_SHORT).show();
        });

        //On sign out button click
        btnSignOut.setOnClickListener(v -> {
            //Log user out
            ParseUser.logOutInBackground();
            Log.i(TAG, "onClick: User logged out");

            //Go to login page
            Log.i(TAG, "onClick: Back to login page");
            Intent intent = new Intent(getActivity(), UserAuthentication.class);
            startActivity(intent);
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //Set user's filter
        user.put("gameFilter", parent.getItemAtPosition(position));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}