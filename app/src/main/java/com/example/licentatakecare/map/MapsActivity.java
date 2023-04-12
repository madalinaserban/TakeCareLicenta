package com.example.licentatakecare.map;

import static com.example.licentatakecare.map.util.ESection.ALL;
import static com.example.licentatakecare.map.util.ESection.CARDIOLOGY;
import static com.example.licentatakecare.map.util.ESection.EMERGENCY;
import static com.example.licentatakecare.map.util.ESection.RADIOLOGY;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.licentatakecare.R;
import com.example.licentatakecare.databinding.ActivityMapsBinding;
import com.example.licentatakecare.map.models.ClusterMarker;
import com.example.licentatakecare.map.models.Hospital;
import com.example.licentatakecare.map.models.HospitalDistanceCalculator;
import com.example.licentatakecare.map.util.Directions.DirectionsApiInterface;
import com.example.licentatakecare.map.util.Directions.DirectionsResponse;
import com.example.licentatakecare.map.util.Directions.Leg;
import com.example.licentatakecare.map.util.Directions.Route;
import com.example.licentatakecare.map.util.Directions.Step;
import com.example.licentatakecare.map.util.ESection;
import com.example.licentatakecare.map.util.FirestoreUpdateListener;
import com.example.licentatakecare.map.util.HospitalClusterRenderer;
import com.example.licentatakecare.map.util.HospitalsCallback;
import com.example.licentatakecare.map.util.HospitalsDao;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, RadioGroup.OnCheckedChangeListener, HospitalsCallback {

    private static final int REQUEST_CODE = 101;
    private GoogleMap mGoogleMap;
    private RadioButton btn_all;
    private Location currentLocation;
    private FusedLocationProviderClient fusedClient;
    private FirebaseFirestore db;
    private List<Hospital> mHospitals = new ArrayList<>();
    private ClusterManager<ClusterMarker> mClusterManager;
    private HospitalClusterRenderer mHospitalClusterRenderer;
    private ESection mSection = ALL;
    private ActivityMapsBinding binding;
    private List<ClusterMarker> mClusterMarkers = new ArrayList<>();
    HospitalDistanceCalculator calculator = new HospitalDistanceCalculator();
    // Declare a boolean to keep track of whether the app is waiting for a permission
    private boolean mWaitingForPermission = false;

    // Declare a Bundle to save the state of the activity
    private Bundle mSavedInstanceState;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        RadioGroup radioGroup = findViewById(R.id.radio_group);
        btn_all = findViewById(R.id.button_all);
        btn_all.setChecked(true);
        radioGroup.setOnCheckedChangeListener(this);
        // Get database
        db = FirebaseFirestore.getInstance();

        fusedClient = LocationServices.getFusedLocationProviderClient(this);
        HospitalsDao.getHospitalList(this);
        mSavedInstanceState = savedInstanceState;
        //Get location
        getLocation();
    }

    public void findNearestHospital(View view) {
        displayDirectionsToClosestHospital();
    }


    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.button_radiology:
                mSection = RADIOLOGY;
                Log.d("RadioGroup", mSection.toString());
                break;
            case R.id.button_cardiology:
                mSection = CARDIOLOGY;
                Log.d("RadioGroup", mSection.toString());
                break;
            case R.id.button_emergency:
                mSection = EMERGENCY;
                Log.d("RadioGroup", mSection.toString());
                break;
            case R.id.button_all:
                mSection = ALL;
                Log.d("RadioGroup", mSection.toString());
                break;
        }
        mHospitalClusterRenderer.updateMarker(mSection, mClusterMarkers);
    }

    public void getLocation() {
        // Check if the app is already waiting for a permission
        if (mWaitingForPermission) {
            return;
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            {
                mWaitingForPermission = true;
                ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            }

        } else {
            Task<Location> task = fusedClient.getLastLocation();
            task.addOnSuccessListener(location -> {
                if (location != null) {
                    currentLocation = location;
                    SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.maps);
                    assert supportMapFragment != null;
                    supportMapFragment.getMapAsync(MapsActivity.this);
                }
            });
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        Log.d("MapsActivity", "onMapReady");
        mGoogleMap = googleMap;
        mClusterManager = new ClusterManager<>(this, googleMap);
        // Set the distance threshold for clustering (in pixels)
        // mClusterManager.setDistanceThreshold(100);

        mClusterManager.setRenderer(new HospitalClusterRenderer(this, mGoogleMap, mClusterManager));
        mHospitalClusterRenderer = new HospitalClusterRenderer(getApplicationContext(), mGoogleMap, mClusterManager);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request for permission
            mGoogleMap.setMyLocationEnabled(true);
        }

        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("My Current Location");
        // Add my location to the map
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        googleMap.addMarker(markerOptions);
        mClusterManager.cluster(); // cluster once at the end

        // Assign the GoogleMap object to the member variable
        mGoogleMap = googleMap;
    }

    public void addHospitalsToMap(List<Hospital> hospitals) {
        for (Hospital hospital : hospitals) {
            ClusterMarker marker = new ClusterMarker(hospital, hospital.getAvailability(ALL));
            mClusterMarkers.add(marker);
            Log.d("Cluster add", "" + marker.getTitle());
            mClusterManager.addItem(marker);
        }
        mClusterManager.cluster(); // cluster once at the end
    }


    public void onHospitalsRetrieved(List<Hospital> hospitals) {
        mHospitals = hospitals;
        addHospitalsToMap(mHospitals);
    }

    public void onHospitalUpdated(Hospital hospital) {
        for (ClusterMarker clusterMarker : mClusterMarkers) {
            if (clusterMarker.getHospital().getId().equals(hospital.getId())) {
                clusterMarker.setmNumAvailablePlaces(hospital.getAvailability(mSection));
                mHospitalClusterRenderer.updateHospitalChanged(clusterMarker);
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        // Check if the app was waiting for a permission
        if (mWaitingForPermission) {
            // Restore the instance state
            if (mSavedInstanceState != null) {
                onRestoreInstanceState(mSavedInstanceState);
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Reset the flag
        mWaitingForPermission = false;
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                getLocation();
        }
    }

    private void displayDirectionsToClosestHospital() {
        // Check if the user's location is available
        if (currentLocation == null) {
            Toast.makeText(this, "Please wait for your location to be determined", Toast.LENGTH_SHORT).show();
            return;
        }

        // Find the closest hospital to the user's current location
        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        TreeMap<Double, Hospital> hospitalsByDistance = calculator.getHospitalsByDistance(latLng,mHospitals);
        Hospital closestHospital = hospitalsByDistance.firstEntry().getValue();

        // Use the Google Maps API to draw a route from the user's current location to the closest hospital
        LatLng hlatLng = new LatLng(closestHospital.getGeoPoint().getLatitude(), closestHospital.getGeoPoint().getLongitude());
        getDirections(latLng, hlatLng);

        // Display a dialog box to the user with the directions and the distance to the hospital
//        double distance = hospitalsByDistance.firstKey();
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setMessage("The closest hospital is " + closestHospital.getName() + " which is " + distance + " meters away. Do you want directions to this hospital?");
//        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int id) {
//                //  getDirections();
//            }
//        });
//        builder.setNegativeButton("No", null);
//        builder.show();
    }

    private void getDirections(LatLng origin, LatLng destination) {
        String url = "https://maps.googleapis.com/maps/api/directions/json?" +
                "origin=" + origin.latitude + "," + origin.longitude +
                "&destination=" + destination.latitude + "," + destination.longitude +
                "&key=" + getResources().getString(R.string.google_maps_key);


        createDirectionsApi().getDirections(url).enqueue(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                        if (!response.isSuccessful()) {
                            // Handle non-successful response here
                            Log.e("", "Direction response not successful: " + response.code());
                            return;
                        }

                        Log.e("", "Origin: " + origin.longitude+" "+origin.latitude);
                        Log.e("", "Destination: " + destination.longitude+" "+destination.latitude);

                        DirectionsResponse directionsResponse = response.body();
                        if (directionsResponse == null) {
                            // Handle null response here
                            Log.e("", "Direction response body is null");
                            return;
                        }

                        List<Route> routes = directionsResponse.getRoutes();
                        if (routes.isEmpty()) {
                            // Handle empty routes here
                            Log.e("", "Direction response has no routes");
                            return;
                        }

                        // Get the first route
                        Route route = routes.get(0);


                        // Loop through each leg of the route
                        for (Leg leg : route.getLegs()) {

                            // Loop through each step of the leg
                            for (Step step : leg.getSteps()) {

                                // Get the polyline of the step and decode it to a list of LatLng points
                                List<LatLng> points = PolyUtil.decode(String.valueOf(step.getPolyline().getPoints()));

                                // Draw the polyline on the map
                                PolylineOptions polylineOptions = new PolylineOptions()
                                        .addAll(points)
                                        .color(Color.RED)
                                        .width(10);
                                mGoogleMap.addPolyline(polylineOptions);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                        // Handle failure here
                        Log.e("A", "Direction API request failed", t);
                    }
                });
    }


    private DirectionsApiInterface createDirectionsApi() {
        return new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(DirectionsApiInterface.class);
    }


}