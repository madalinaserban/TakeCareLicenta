package com.example.licentatakecare.map;

import static com.example.licentatakecare.models.Hospital.getHospitalList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.example.licentatakecare.R;
import com.example.licentatakecare.models.ClusterMarker;
import com.example.licentatakecare.models.Hospital;
import com.example.licentatakecare.util.HospitalClusterRenderer;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int REQUEST_CODE = 101;
    private GoogleMap mGoogleMap;
    private Location currentLocation;
    private FusedLocationProviderClient fusedClient;
    private FirebaseFirestore db;
    private List<Hospital> hospitals = new ArrayList<>();
    private ClusterManager<ClusterMarker> mClusterManager;
    private SwipeRefreshLayout swipeRefreshLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Get database
        db = FirebaseFirestore.getInstance();
        fusedClient = LocationServices.getFusedLocationProviderClient(this);
        getLocation();
        //code to refresh
        // Initialize the SwipeRefreshLayout
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Reload the map
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
        } else {
            Task<Location> task = fusedClient.getLastLocation();
            task.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        currentLocation = location;
                        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.maps);
                        assert supportMapFragment != null;
                        supportMapFragment.getMapAsync(MapsActivity.this);
                    }
                }
            });
        }
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mGoogleMap=googleMap;
        mClusterManager = new ClusterManager<>(this, googleMap);
        mClusterManager.setRenderer(new HospitalClusterRenderer(this, googleMap, mClusterManager));


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request for permission
            mGoogleMap.setMyLocationEnabled(true);
        }

        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("My Current Location");
        // Add my location to the map
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
        googleMap.addMarker(markerOptions);

        // Assign the GoogleMap object to the member variable
        mGoogleMap = googleMap;

        // Get the hospital locations from Firestore and add markers to the map
        Hospital.getHospitalList(new HospitalsCallback() {
            @Override
            public void onHospitalsRetrieved(List<Hospital> hospitals) {
                Log.d("MapsActivity", "Hospitals retrieved: " + hospitals.size());
                addHospitalsToMap(hospitals);
                // Display clusters on map
                mClusterManager.cluster();
            }
        });
    }


    public void addHospitalsToMap(List<Hospital> hospitals) {
        for (Hospital hospital : hospitals) {
//            LatLng hospitalLatLng = new LatLng(hospital.getGeoPoint().getLatitude(), hospital.getGeoPoint().getLongitude());
//            MarkerOptions markerOptions = new MarkerOptions().position(hospitalLatLng).title(hospital.getName());
//            mGoogleMap.addMarker(markerOptions);
            ClusterMarker marker = new ClusterMarker(hospital.getGeoPoint().getLatitude(), hospital.getGeoPoint().getLongitude(), hospital.getName(), null,hospital.getEmpty_spots());
            mClusterManager.addItem(marker);

        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                getLocation();
        }
    }

}