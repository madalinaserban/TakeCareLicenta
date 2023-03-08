package com.example.licentatakecare.map.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.amulyakhare.textdrawable.TextDrawable;
import com.example.licentatakecare.R;
import com.example.licentatakecare.map.models.ClusterMarker;
import com.example.licentatakecare.map.models.Hospital;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import java.util.ArrayList;
import java.util.List;

public class HospitalClusterRenderer extends DefaultClusterRenderer<ClusterMarker> {
    private final Context mContext;
    private List<Hospital> hospitals = new ArrayList<>();
    private ESection mSection = ESection.ALL;

    public HospitalClusterRenderer(Context context, GoogleMap map, ClusterManager<ClusterMarker> clusterManager) {
        super(context, map, clusterManager);
        mContext = context;
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
        if (clusterItem.getNumAvailablePlaces() > 0) {
            marker.setIcon(getMarkerIcon(clusterItem));
        }
    }

    public void updateMarker(ESection esection,List<ClusterMarker> markers) {
        for (ClusterMarker clusterMarker : markers) {
            Hospital hospital = clusterMarker.getHospital();
             mSection = esection;
            int numAvailablePlaces = hospital.getAvailability(mSection);
            clusterMarker.setmNumAvailablePlaces(numAvailablePlaces);
            Marker marker = clusterMarker.getMarker(); // get the Marker object from the ClusterMarker
            if (marker != null) {
                marker.setIcon(getMarkerIcon(clusterMarker));
            } else {
                Log.e("Marker update", "Marker not found for cluster marker");
            }
        }
    }

    private BitmapDescriptor getMarkerIcon(ClusterMarker clusterMarker) {
        TextDrawable drawable = TextDrawable.builder()
                .beginConfig()
                .textColor(Color.WHITE)
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
