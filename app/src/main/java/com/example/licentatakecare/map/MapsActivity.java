package com.example.licentatakecare.map;

import static com.example.licentatakecare.map.util.clusters.ESection.ALL;
import static com.example.licentatakecare.map.util.clusters.ESection.CARDIOLOGY;
import static com.example.licentatakecare.map.util.clusters.ESection.EMERGENCY;
import static com.example.licentatakecare.map.util.clusters.ESection.RADIOLOGY;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.licentatakecare.R;
import com.example.licentatakecare.databinding.ActivityMapsBinding;
import com.example.licentatakecare.map.models.cluster.ClusterMarker;
import com.example.licentatakecare.map.models.hospital.Hospital;
import com.example.licentatakecare.map.util.timeAndDistance.CalculateDistancesCallback;
import com.example.licentatakecare.map.util.directions.HospitalDistanceCalculator;
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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
    private LatLng latLng;
    private Hospital closestHospital;
    List<Hospital> hospitalsByDistance = new ArrayList<>();
    private ActivityMapsBinding binding;
    private List<ClusterMarker> mClusterMarkers = new ArrayList<>();
    HospitalDistanceCalculator calculator = new HospitalDistanceCalculator();
    // Declare a boolean to keep track of whether the app is waiting for a permission
    private boolean mWaitingForPermission = false;
    private List<Polyline> mRoutePolylines = new ArrayList<>();


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
        FrameLayout directionsPanelContainer = findViewById(R.id.directionsPanelContainer);
        fusedClient = LocationServices.getFusedLocationProviderClient(this);
        HospitalsDao.getHospitalList(this);
        mSavedInstanceState = savedInstanceState;
        //Get location
        getLocation();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.directions_button:
                        showDirectionsPanel(closestHospital);
                        return true;
                    // handle other items here
                    default:
                        return false;
                }
            }
        });

    }

    public void showDirectionsPanel(Hospital hospital) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.directionsPanelContainer);
//        if (currentFragment != null) {
//            // The directions panel is already open, so we need to collapse it
//            ObjectAnimator animator = ObjectAnimator.ofFloat(findViewById(R.id.directionsPanelContainer), "translationY", 0);
//            animator.setDuration(700);
//            animator.start();
//            fragmentManager.popBackStack();
//        } else {
        DirectionsPanelFragment directionsPanelFragment = DirectionsPanelFragment.newInstance(hospital.getGoogle_id(), hospital.getTimeToGetThere());
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.directionsPanelContainer, directionsPanelFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void showRouteChosenHospital(Hospital hospital) {
        // Clear existing hospital routes from the map
        for (Polyline polyline : mRoutePolylines) {
            if (polyline.getColor() == Color.BLUE)
                polyline.remove();
        }
        if (!(hospital.equals(closestHospital))) {
            mRouteGenerator.displayDirectionsToHospital(this, currentLocation, hospital, new DirectionsCallback() {
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
                                    .color(Color.BLUE)
                                    .width(10);
                            Polyline polyline = mGoogleMap.addPolyline(polylineOptions);
                            mRoutePolylines.add(polyline); // Add the polyline to the list
                        }
                    }
                }

                @Override
                public void onFailure() {
                    // Handle the error here
                }
            });
        }

    }

    public void showRouteToNearestHospital() {
        // Clear existing hospital routes from the map
        for (Polyline polyline : mRoutePolylines) {
            polyline.remove();
        }
        mRoutePolylines.clear();
        Hospital closestGreenHospital = hospitalsByDistance.get(0);
        closestHospital = hospitalsByDistance.get(0);
        for (Hospital hospital : hospitalsByDistance) {
            if (hospital.getAvailability(mSection) > 0.15 * hospital.getTotalSpaces(mSection)) {
                closestGreenHospital = hospital;
                break; // exit the loop once a hospital with availability is found
            }
        }
        if (!(closestGreenHospital.equals(closestHospital))) {
            mRouteGenerator.displayDirectionsToHospital(this, currentLocation, closestHospital, new DirectionsCallback() {
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
                                    .color(Color.RED)
                                    .width(10);
                            Polyline polyline = mGoogleMap.addPolyline(polylineOptions);
                            mRoutePolylines.add(polyline); // Add the polyline to the list
                        }
                    }
                }

                @Override
                public void onFailure() {
                    // Handle the error here
                }
            });
        }

        mRouteGenerator.displayDirectionsToHospital(this, currentLocation, closestGreenHospital, new DirectionsCallback() {
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
                                .width(10);
                        Polyline polyline = mGoogleMap.addPolyline(polylineOptions);
                        mRoutePolylines.add(polyline); // Add the polyline to the list
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

        // Redraw the hospital routes
        showRouteToNearestHospital();
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
        mClusterManager.setRenderer(new HospitalClusterRenderer(getApplicationContext(), mGoogleMap, mClusterManager, this));
        mHospitalClusterRenderer = new HospitalClusterRenderer(getApplicationContext(), mGoogleMap, mClusterManager, this);

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
        latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

        calculator.calculateDistancesToHospitals(currentLocation, hospitals, new CalculateDistancesCallback() {

            @Override
            public void onDistancesCalculated(List<Hospital> hospitalsWithDistances) {

                Collections.sort(hospitalsWithDistances, new Comparator<Hospital>() {
                    @Override
                    public int compare(Hospital h1, Hospital h2) {
                        return Double.compare(h1.getDistance(), h2.getDistance());
                    }
                });


                hospitalsByDistance = hospitalsWithDistances;
                showRouteToNearestHospital();

            }

            @Override
            public void onDistancesCalculationFailed() {

                Log.d("Hospital", " FAILED " + " - Distance: " + " FAILED");
            }


        });
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