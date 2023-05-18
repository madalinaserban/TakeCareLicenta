package com.example.licentatakecare.map;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

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
    private String s_hospitalGoogleId;
    private String s_hospitalTime;

    private HospitalDetailsGenerator hospitalDetailsGenerator;

    public DirectionsPanelFragment() {
        // Required empty public constructor
    }

    public static DirectionsPanelFragment newInstance(String hospitalGoogle_Id,String hospitalTime) {
        DirectionsPanelFragment fragment = new DirectionsPanelFragment();
        Bundle args = new Bundle();
        args.putString("hospitalGoogleId", hospitalGoogle_Id);
        args.putString("hospitalTime", hospitalTime);
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
        hospitalTimeToGetThere = view.findViewById(R.id.hospital_time_to_get_there);
        hospitalNumber=view.findViewById(R.id.hospital_phone);
        hospitalAdress=view.findViewById(R.id.hospital_address);

        // hospitalDirections = view.findViewById(R.id.hospital_directions);

        // Retrieve the hospital name from arguments bundle
        Bundle args = getArguments();
        if (args != null) {
            s_hospitalGoogleId = args.getString("hospitalGoogleId");
            s_hospitalTime = args.getString("hospitalTime");
            fetchHospitalDetails(s_hospitalGoogleId,s_hospitalTime);
        }

        return view;
    }

    private void fetchHospitalDetails(String s_hospitalGoogleId, String s_hospitalTime ) {
        hospitalDetailsGenerator = new HospitalDetailsGenerator();

        // Make the API call to fetch hospital details
        // Create a Hospital object with the name
        hospitalDetailsGenerator.displayHospitalDetails(getContext(), s_hospitalGoogleId, s_hospitalTime,this);
    }

    @Override
    public void onDetailsReceived(String name, String address, String phone, String website, double rating, boolean isOpen, String photoUrl) {
        // Update the UI with the retrieved data
        hospitalName.setText(name);
        hospitalRating.setRating((float) rating);
        hospitalOpeningHours.setText(isOpen ? "Open Now: Yes" : "Open Now: No");
        hospitalTimeToGetThere.setText("Time :"+s_hospitalTime);
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
                        return false;
                    }
                })
                .into(hospitalImage);


    }

    @Override
    public void onError(String errorMessage) {

    }
}
