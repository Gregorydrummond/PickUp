package com.example.pickup.fragments.userAuthentication;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.pickup.R;
import com.example.pickup.activities.MainActivity;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignupFragment extends Fragment {

    private static final String TAG = "SignupFragment";

    EditText etUsername;
    EditText etPassword;
    Button btnSignup;


    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public SignupFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static SignupFragment newInstance(String param1, String param2) {
        SignupFragment fragment = new SignupFragment();
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
        return inflater.inflate(R.layout.fragment_signup, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Find components
        etUsername = view.findViewById(R.id.etUsername);
        etPassword = view.findViewById(R.id.etPassword);
        btnSignup = view.findViewById(R.id.btnSignup);

        //Sign user up
        btnSignup.setOnClickListener(v -> {
            String username = etUsername.getText().toString();
            String password = etPassword.getText().toString();

            if(username.isEmpty()) {
                Toast.makeText(getContext(), "Username required", Toast.LENGTH_SHORT).show();
                Log.i(TAG, "onClick: Username is empty");
                return;
            }

            if(password.isEmpty()) {
                Toast.makeText(getContext(), "Password required", Toast.LENGTH_SHORT).show();
                Log.i(TAG, "onClick: Password is empty");
                return;
            }

            signupUser(username, password);
        });
    }

    private void signupUser(String username, String password) {
        //Create the ParseUser & set properties
        ParseUser user = new ParseUser();
        user.setUsername(username);
        user.setPassword(password);

        //Sign in background
        user.signUpInBackground(e -> {
            if(e != null) {
                Toast.makeText(getContext(), "Issue signing up, try a different username.", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "done: Issue with signup", e);
            }
            else {
                Log.i(TAG, "done: User signed up!");
                Toast.makeText(getContext(), "Sign up successful", Toast.LENGTH_SHORT).show();
                loginUser(username, password);
            }
        });
    }

    private void loginUser(String username, String password) {
        ParseUser.logInInBackground(username, password, (user, e) -> {
            //If there's an error with the user login
            if(e != null) {
                Toast.makeText(getContext(), "Incorrect password/username", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "done: Issue with login", e);
            }
            else {
                //Else navigate to the main activity if the user has signed in properly
                Log.i(TAG, "done: User logged in");
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

    }
}