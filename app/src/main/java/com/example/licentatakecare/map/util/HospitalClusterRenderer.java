package com.example.licentatakecare.map.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.Pair;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HospitalClusterRenderer extends DefaultClusterRenderer<ClusterMarker> {
    private final Context mContext;
    private List<Hospital> hospitals = new ArrayList<>();
    private Section mSection=Section.ALL;

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

        // If the number of available places is greater than 0, display it in a text overlay
        if (clusterItem.getNumAvailablePlaces() > 0) {
            marker.setIcon(getMarkerIcon(clusterItem));
        }
    }

    private Bitmap drawableToBitmap(Drawable drawable, int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public void updateMarker(Section section, HashMap<ClusterMarker,Hospital> markers,ClusterManager<ClusterMarker> mClusterManager) {
        for (Map.Entry<ClusterMarker, Hospital> entry : markers.entrySet()) {
            ClusterMarker clusterMarker = entry.getKey();
            Hospital hospital = entry.getValue();
            mSection=section;
            int numAvailablePlaces = hospital.getNumAvailablePlaces(section);
            clusterMarker.setmNumAvailablePlaces(numAvailablePlaces);
            Marker marker = getMarker(clusterMarker);
            if (marker != null) {
                marker.setIcon(getMarkerIcon(clusterMarker));
            }
        }
        mClusterManager.cluster();
    }
    public void updateSection(Section section) {
        mSection = section;
    }
    private BitmapDescriptor getMarkerIcon(ClusterMarker clusterMarker) {
        TextDrawable drawable = TextDrawable.builder()
                .beginConfig()
                .textColor(Color.WHITE)
                .useFont(Typeface.DEFAULT_BOLD)
                .fontSize(50)
                .bold()
                .endConfig()
                .buildRound(Integer.toString(clusterMarker.getNumAvailablePlaces()), getMarkerColor(clusterMarker));
        int width = drawable.getIntrinsicWidth() + 130;
        int height = drawable.getIntrinsicHeight() + 130;
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }




    private int getMarkerColor(ClusterMarker clusterItem) {
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
