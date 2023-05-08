package com.example.licentatakecare.map.util.clusters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Looper;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.amulyakhare.textdrawable.TextDrawable;
import com.example.licentatakecare.R;
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

public class HospitalClusterRenderer extends DefaultClusterRenderer<ClusterMarker>{
    private final Context mContext;
    private ESection mSection = ESection.ALL;
    private int currentZoomLevel;
    private ClusterManager<ClusterMarker> mClusterManager;
    private GoogleMap mMap;

    public HospitalClusterRenderer(Context context, GoogleMap map, ClusterManager<ClusterMarker> clusterManager) {
        super(context, map, clusterManager);
        mContext = context;
        currentZoomLevel = (int) map.getCameraPosition().zoom;
       // map.setOnCameraIdleListener(this);
        mClusterManager = clusterManager;
        mMap = map;
    }

    @Override
    protected void onBeforeClusterItemRendered(ClusterMarker item, MarkerOptions markerOptions) {

        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
        markerOptions.title(item.getTitle());
        markerOptions.snippet(item.getSnippet());
    }

    @Override
    protected void onClusterItemRendered(ClusterMarker clusterItem, Marker marker) {
        super.onClusterItemRendered(clusterItem, marker);
        // Set the Marker as the marker property of the corresponding ClusterMarker
        clusterItem.setMarker(marker);
        // If the number of available places is greater than 0, display it in a text overlay
        if (clusterItem.getNumAvailablePlaces() >= 0) {
            marker.setIcon(getMarkerIcon(clusterItem, Color.WHITE));
        }
    }

    public void updateMarker(ESection esection, List<ClusterMarker> markers) {
        for (ClusterMarker clusterMarker : markers) {
            Hospital hospital = clusterMarker.getHospital();
            mSection = esection;
            int numAvailablePlaces = hospital.getAvailability(mSection);
            clusterMarker.setmNumAvailablePlaces(numAvailablePlaces);
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

//    @Override
//    protected String getClusterText(int bucket) {
//        if (bucket < 20) {
//            return String.valueOf(bucket);
//        } else {
//            return "+";
//        }
//    }



    @Override
    protected boolean shouldRenderAsCluster(Cluster<ClusterMarker> cluster) {
         //always return false to prevent clustering
         return false;
    }

//    @Override
//    public void onCameraIdle() {
//        int newZoomLevel = (int) mMap.getCameraPosition().zoom;
//        if (newZoomLevel != currentZoomLevel) {
//            currentZoomLevel = newZoomLevel;
//            if (newZoomLevel <= 40) {
//                mClusterManager.setAlgorithm(new NonHierarchicalDistanceBasedAlgorithm<ClusterMarker>());
//            } else {
//                mClusterManager.setAlgorithm(new GridBasedAlgorithm<ClusterMarker>());
//            }
//            mClusterManager.cluster();
//        }
//    }

//    @Override
//    protected void onClusterUpdated(Cluster<ClusterMarker> cluster, Marker marker) {
//        super.onClusterUpdated(cluster, marker);
//        marker.setTitle("CLUSTER");
//    }


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


}