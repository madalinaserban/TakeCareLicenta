package com.example.licentatakecare.map;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.licentatakecare.R;
import com.example.licentatakecare.map.models.hospital.Hospital;
import com.example.licentatakecare.map.util.directions.HospitalDetailsGenerator;
import com.example.licentatakecare.map.util.directions.callback.DetailsCallback;

public class DirectionsPanelFragment extends Fragment implements DetailsCallback {

    private ImageView hospitalImage;
    private TextView hospitalName;
    private RatingBar hospitalRating;
    private TextView hospitalOpeningHours;
    private TextView hospitalTimeToGetThere;
    private TextView hospitalNumber;
    private TextView hospitalAdress;
    private TextView hospitalDistance;
    private String s_hospitalGoogleId;
    private String s_hospitalDistance;
    private String s_hospitalTime;
    private TextView tvTimeToGetThere;
    private TextView tvDistance;
    public static Hospital mHospital;
    private ProgressBar prograssBar;

    private HospitalDetailsGenerator hospitalDetailsGenerator;

    public DirectionsPanelFragment() {
        // Required empty public constructor
    }

    public static DirectionsPanelFragment newInstance(Hospital hospital) {
        DirectionsPanelFragment fragment = new DirectionsPanelFragment();
        Bundle args = new Bundle();
        args.putString("hospitalGoogleId", hospital.getGoogle_id());
        args.putString("hospitalTime", hospital.getTimeToGetThere());
        args.putString("hospitalDistance", String.valueOf(hospital.getDistance() / 1000.0));
        mHospital = hospital;
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_directions_panel, container, false);

        // Initialize views
        hospitalImage = view.findViewById(R.id.hospital_image);
        hospitalName = view.findViewById(R.id.hospital_name);
        hospitalRating = view.findViewById(R.id.hospital_rating);
        hospitalOpeningHours = view.findViewById(R.id.hospital_opening_hours);
        hospitalTimeToGetThere = view.findViewById(R.id.time_to_get_there_value);
        hospitalNumber = view.findViewById(R.id.hospital_phone);
        hospitalAdress = view.findViewById(R.id.address_value);
        hospitalDistance = view.findViewById(R.id.distance_value);
        tvTimeToGetThere=view.findViewById(R.id.hospital_time_to_get_there);
        tvDistance=view.findViewById(R.id.hospital_distance);
        prograssBar = view.findViewById(R.id.progress_bar);

        // hospitalDirections = view.findViewById(R.id.hospital_directions);

        // Retrieve the hospital name from arguments bundle
        Bundle args = getArguments();
        if (args != null) {
            s_hospitalGoogleId = args.getString("hospitalGoogleId");
            s_hospitalTime = args.getString("hospitalTime");
            s_hospitalDistance = args.getString("hospitalDistance");
            fetchHospitalDetails(s_hospitalGoogleId, s_hospitalTime);
        }
        ImageButton directionsButton = view.findViewById(R.id.directions_button);
        directionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Launch Google Maps with directions intent
                String latitude = String.valueOf(mHospital.getGeoPoint().getLatitude());
                String longitude = String.valueOf(mHospital.getGeoPoint().getLongitude());
                String label = mHospital.getName();

                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latitude + "," + longitude + "&label=" + label);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");

                if (mapIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
                    startActivity(mapIntent);
                } else {
                    Toast.makeText(getContext(), "Google maps is not installed", Toast.LENGTH_LONG).show();
                }
            }
        });
        showLoadingScreen();
        return view;
    }

    private void fetchHospitalDetails(String s_hospitalGoogleId, String s_hospitalTime) {
        hospitalDetailsGenerator = new HospitalDetailsGenerator();
        hospitalDetailsGenerator.displayHospitalDetails(getContext(), s_hospitalGoogleId, s_hospitalTime, this);
    }

    @Override
    public void onDetailsReceived(String name, String address, String phone, String website, double rating, boolean isOpen, String photoUrl) {
        // Update the UI with the retrieved data
        hospitalName.setText(name);
        hospitalRating.setRating((float) rating);
        hospitalTimeToGetThere.setText(s_hospitalTime);
        hospitalDistance.setText(s_hospitalDistance + " km");
        hospitalAdress.setText(address);
        hospitalNumber.setText(phone);

        Glide.with(requireContext())
                .load(photoUrl)
                .placeholder(R.drawable.logo_light)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Log.e("Glide", "Image load failed: " + e.getMessage());
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        Log.d("Glide", "Image loaded successfully");
                        // Hide the loading screen and show the content views
                        hideLoadingScreen();
                        showContentViews();
                        return false;
                    }
                })
                .into(hospitalImage);
    }

    @Override
    public void onError(String errorMessage) {
        // Hide the loading screen
        hideLoadingScreen();

        // Show an error message (e.g., using Toast)
        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
    }

    private void hideLoadingScreen() {
        prograssBar.setVisibility(View.GONE);
    }

    private void showContentViews() {
        hospitalImage.setVisibility(View.VISIBLE);
        hospitalName.setVisibility(View.VISIBLE);
        hospitalRating.setVisibility(View.VISIBLE);
        hospitalOpeningHours.setVisibility(View.VISIBLE);
        hospitalTimeToGetThere.setVisibility(View.VISIBLE);
        hospitalNumber.setVisibility(View.VISIBLE);
        hospitalAdress.setVisibility(View.VISIBLE);
        tvDistance.setVisibility(View.VISIBLE);
        tvTimeToGetThere.setVisibility(View.VISIBLE);
        hospitalDistance.setVisibility(View.VISIBLE);
    }

    private void showLoadingScreen() {
        // Hide the content views
        hospitalImage.setVisibility(View.GONE);
        hospitalName.setVisibility(View.GONE);
        hospitalRating.setVisibility(View.GONE);
        hospitalOpeningHours.setVisibility(View.GONE);
        hospitalTimeToGetThere.setVisibility(View.GONE);
        hospitalNumber.setVisibility(View.GONE);
        hospitalAdress.setVisibility(View.GONE);
        hospitalDistance.setVisibility(View.GONE);
        tvDistance.setVisibility(View.GONE);
        tvTimeToGetThere.setVisibility(View.GONE);

        prograssBar.setVisibility(View.VISIBLE);
    }

}
