package com.example.licentatakecare.map.util.clusters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.amulyakhare.textdrawable.TextDrawable;
import com.example.licentatakecare.R;
import com.example.licentatakecare.map.MapsActivity;
import com.example.licentatakecare.map.models.cluster.ClusterMarker;
import com.example.licentatakecare.map.models.hospital.Hospital;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.algo.GridBasedAlgorithm;
import com.google.maps.android.clustering.algo.NonHierarchicalDistanceBasedAlgorithm;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import android.os.Handler;


import java.util.List;

public class HospitalClusterRenderer extends DefaultClusterRenderer<ClusterMarker> implements GoogleMap.OnMarkerClickListener {
    private final Context mContext;
    private ESection mSection = ESection.ALL;
    private int currentZoomLevel;
    private ClusterManager<ClusterMarker> mClusterManager;
    private GoogleMap mMap;
    private MapsActivity mActivity;

    public HospitalClusterRenderer(Context context, GoogleMap map, ClusterManager<ClusterMarker> clusterManager,MapsActivity activity)
    // implements GoogleMap.OnCameraIdleListener
    {
        super(context, map, clusterManager);
        mContext = context;
        currentZoomLevel = (int) map.getCameraPosition().zoom;
        // map.setOnCameraIdleListener(this);
        mClusterManager = clusterManager;
        mActivity = activity;
        mMap = map;
    }

    @Override
    protected void onBeforeClusterItemRendered(ClusterMarker item, MarkerOptions markerOptions) {
        int transparency = 0; // 0 for completely transparent

// Set the hue value (range: 0.0 - 360.0)
        float hue = 30.0f;

// Create the marker color with transparency and hue
        int markerColor = Color.HSVToColor(Color.alpha(transparency), new float[]{hue, 1.0f, 1.0f});

        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
        markerOptions.title(item.getTitle());
        markerOptions.snippet(item.getSnippet());
        Marker marker = mMap.addMarker(markerOptions);
        marker.setTag(item); // Attach the ClusterMarker object to the marker
    }

    @Override
    protected void onClusterItemRendered(ClusterMarker clusterItem, Marker marker) {
        super.onClusterItemRendered(clusterItem, marker);
        // Set the Marker as the marker property of the corresponding ClusterMarker
        clusterItem.setMarker(marker);
        marker.setTag(clusterItem);
        // If the number of available places is greater than 0, display it in a text overlay
        if (clusterItem.getNumAvailablePlaces() >= 0) {
            marker.setIcon(getMarkerIcon(clusterItem, Color.WHITE));
        }
        mMap.setOnMarkerClickListener(this); // Set the click listener for the map

    }

    public void updateMarker(ESection esection, List<ClusterMarker> markers) {
        for (ClusterMarker clusterMarker : markers) {
            Hospital hospital = clusterMarker.getHospital();
            mSection = esection;
            int numAvailablePlaces = hospital.getAvailability(mSection);
            clusterMarker.setmNumAvailablePlaces(numAvailablePlaces);
            clusterMarker.setColor(getMarkerColor());
            Marker marker = clusterMarker.getMarker(); // get the Marker object from the ClusterMarker
            if (marker != null) {
                marker.setIcon(getMarkerIcon(clusterMarker, Color.WHITE));
            } else {
                Log.e("Marker update", "Marker not found for cluster marker");
            }
        }
    }


    public void updateHospitalChanged(ClusterMarker clusterMarker) {
        Marker marker = clusterMarker.getMarker();
        if (marker != null) {
            marker.setIcon(getMarkerIcon(clusterMarker, Color.RED));
            new Handler().postDelayed(() -> {
                marker.setIcon(getMarkerIcon(clusterMarker, Color.WHITE));

            }, 3000); // 3 seconds
        } else {
            Log.e("Marker update", "Marker not found for cluster marker");
        }
    }


    @Override
    protected boolean shouldRenderAsCluster(Cluster<ClusterMarker> cluster) {
        //always return false to prevent clustering
        return false;
    }

    private BitmapDescriptor getMarkerIcon(ClusterMarker clusterMarker, int color) {
        TextDrawable drawable = TextDrawable.builder()
                .beginConfig()
                .textColor(color)
                .useFont(Typeface.DEFAULT_BOLD)
                .fontSize(50)
                .bold()
                .endConfig()
                .buildRound(Integer.toString(clusterMarker.getNumAvailablePlaces()), getMarkerColor());
        int width = drawable.getIntrinsicWidth() + 130;
        int height = drawable.getIntrinsicHeight() + 130;
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }


    private int getMarkerColor() {
        int color = ContextCompat.getColor(mContext, R.color.btn_all); // default color
        switch (mSection) {
            case EMERGENCY:
                color = ContextCompat.getColor(mContext, R.color.btn_emergency);
                break;
            case RADIOLOGY:
                color = ContextCompat.getColor(mContext, R.color.btn_radiology);
                break;
            case CARDIOLOGY:
                color = ContextCompat.getColor(mContext, R.color.btn_cardiology);
                break;
            case ALL:
                color = ContextCompat.getColor(mContext, R.color.btn_all);
                break;
        }
        return color;
    }


    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        ClusterMarker clusterMarker = (ClusterMarker) marker.getTag();
       Hospital hospital = clusterMarker.getHospital();
        mActivity.showDirectionsPanel(hospital);


        return false;
    }
}