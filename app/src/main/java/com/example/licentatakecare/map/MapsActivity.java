package com.example.licentatakecare.map;

import static com.example.licentatakecare.map.util.clusters.ESection.ALL;
import static com.example.licentatakecare.map.util.clusters.ESection.CARDIOLOGY;
import static com.example.licentatakecare.map.util.clusters.ESection.EMERGENCY;
import static com.example.licentatakecare.map.util.clusters.ESection.RADIOLOGY;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
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
import com.example.licentatakecare.map.models.cluster.ClusterMarker;
import com.example.licentatakecare.map.models.hospital.Hospital;
import com.example.licentatakecare.map.util.directions.HospitalDistanceCalculator;
import com.example.licentatakecare.map.models.directions.DirectionsApiInterface;
import com.example.licentatakecare.map.models.directions.DirectionsResponse;
import com.example.licentatakecare.map.models.directions.Leg;
import com.example.licentatakecare.map.models.directions.Route;
import com.example.licentatakecare.map.models.directions.Step;
import com.example.licentatakecare.map.util.clusters.ESection;
import com.example.licentatakecare.map.util.clusters.HospitalClusterRenderer;
import com.example.licentatakecare.map.util.directions.HospitalRouteGenerator;
import com.example.licentatakecare.map.util.directions.callback.DirectionsCallback;
import com.example.licentatakecare.map.util.hospitals.HospitalsCallback;
import com.example.licentatakecare.map.util.hospitals.HospitalsDao;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;
import java.util.List;
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
    private HospitalRouteGenerator mRouteGenerator;
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
        mRouteGenerator = new HospitalRouteGenerator();
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

    public void showRouteToNearestHospital(View view) {
            mRouteGenerator.displayDirectionsToClosestHospital(this, currentLocation, mHospitals, new DirectionsCallback() {
                @Override
                public void onSuccess(Route route) {
                    // Loop through each leg of the route
                    for (Leg leg : route.getLegs()) {

                        // Loop through each step of the leg
                        for (Step step : leg.getSteps()) {

                            // Get the polyline of the step and decode it to a list of LatLng points
                            List<LatLng> points = PolyUtil.decode(String.valueOf(step.getPolyline().getPoints()));

                            // Draw the polyline on the map
                            PolylineOptions polylineOptions = new PolylineOptions()
                                    .addAll(points)
                                    .color(Color.GREEN)
                                    .width(5);
                            mGoogleMap.addPolyline(polylineOptions);
                        }
                    }
                }

                @Override
                public void onFailure() {
                    // Handle the error here
                }
            });

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


}