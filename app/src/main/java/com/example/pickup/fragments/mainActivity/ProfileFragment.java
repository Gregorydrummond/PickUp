package com.example.pickup.fragments.mainActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.pickup.pageTransformers.DepthPageTransformer;
import com.example.pickup.R;
import com.example.pickup.adapters.ProfileFragmentViewPageAdapter;
import com.example.pickup.pageTransformers.ZoomOutPageTransformer;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";
    public static final int GET_FROM_GALLERY = 1;

    ImageView ivProfilePicture;
    TextView tvUsername;
    TabLayout tabLayout;
    ViewPager2 viewPager2;
    FloatingActionButton fabAddPic;
    ProfileFragmentViewPageAdapter adapter;
    ParseFile photoFile;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ParseUser currentUser = ParseUser.getCurrentUser();

        //Find components
        ivProfilePicture = view.findViewById(R.id.ivProfilePictureProfile);
        tvUsername = view.findViewById(R.id.tvUsernameProfile);
        tabLayout = view.findViewById(R.id.tabLayout_profile);
        viewPager2 = view.findViewById(R.id.viewPager2_profile);
        fabAddPic = view.findViewById(R.id.fabAddPP);

        //Initialize adapter
        adapter = new ProfileFragmentViewPageAdapter(getActivity());

        //Set adapter
        viewPager2.setAdapter(adapter);

        //Tab Layout Mediator
        new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position) {
                    case 0:
                        tab.setText("Games");
                        break;
                    case 1:
                        tab.setText("Stats");
                        break;
                    default:
                        tab.setText("Settings");
                }
            }
        }).attach();

        //Animation
        viewPager2.setPageTransformer(new DepthPageTransformer());


        // Activity result launcher. OnActivityResult method is deprecated
        ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // There are no request codes
                            Intent data = result.getData();
                            //doSomeOperations();
                            Uri selectedImage = data.getData();
                            Bitmap bitmap = null;
                            if(Build.VERSION.SDK_INT < 28) {
                                Log.i(TAG, "onActivityResult: build version < 28");
                                try {
                                    bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);
                                    Glide.with(getContext())
                                            .load(selectedImage)
                                            .transform(new CircleCrop())
                                            .into(ivProfilePicture);
                                    Log.i(TAG, "onActivityResult: set image");
                                } catch (IOException e) {
                                    Log.e(TAG, "onActivityResult: Error getting image", e);
                                    return;
                                }
                            }
                            else {
                                Log.i(TAG, "onActivityResult: build version >= 28");
                                ImageDecoder.Source source = ImageDecoder.createSource(getActivity().getContentResolver(), selectedImage);
                                try {
                                    bitmap = ImageDecoder.decodeBitmap((ImageDecoder.Source) source);
                                    Glide.with(getContext())
                                            .load(selectedImage)
                                            .transform(new CircleCrop())
                                            .into(ivProfilePicture);
                                    Log.i(TAG, "onActivityResult: set image");
                                } catch (IOException e) {
                                    Log.e(TAG, "onActivityResult: Error getting image", e);
                                    return;
                                }

                            }

                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                            photoFile = new ParseFile("UserProfilePic.png", stream.toByteArray());
                            currentUser.put("profilePicture", photoFile);
                            currentUser.saveInBackground();
                        }
                        else {
                            Log.i(TAG, "onActivityResult: Result code not ok");
                        }
                    }
                });

        //Open gallery
        fabAddPic.setOnClickListener(v -> {
            Log.i(TAG, "onViewCreated: launching camera");
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            someActivityResultLauncher.launch(intent);
        });

        ivProfilePicture.bringToFront();
        ParseFile profilePicture = currentUser.getParseFile("profilePicture");
        if(profilePicture != null) {
            Glide.with(getContext())
                    .load(profilePicture.getUrl())
                    .transform(new CircleCrop())
                    .into(ivProfilePicture);
        }
        tvUsername.setText(currentUser.getUsername());
    }
}