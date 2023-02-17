package com.example.licentatakecare.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;

import com.airbnb.lottie.utils.Utils;
import com.amulyakhare.textdrawable.TextDrawable;
import com.example.licentatakecare.models.ClusterMarker;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

public class HospitalClusterRenderer extends DefaultClusterRenderer<ClusterMarker> {
    private final Context mContext;

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
        if (clusterItem.getNumAvailablePlaces() > -1) {
            TextPaint textPaint = new TextPaint();
            textPaint.setColor(Color.WHITE);
            textPaint.setTypeface(Typeface.DEFAULT_BOLD);
            textPaint.setTextSize(50);

            Rect bounds = new Rect();
            textPaint.getTextBounds(Integer.toString(clusterItem.getNumAvailablePlaces()), 0, Integer.toString(clusterItem.getNumAvailablePlaces()).length(), bounds);
            int width = bounds.width()+70;
            int height = bounds.height()+70;

            TextDrawable drawable = TextDrawable.builder()
                    .beginConfig()
                    .textColor(Color.WHITE)
                    .useFont(Typeface.DEFAULT_BOLD)
                    .fontSize(50)
                    .bold()
                    .endConfig()
                    .buildRound(Integer.toString(clusterItem.getNumAvailablePlaces()), Color.parseColor("#02457A"));
            marker.setIcon(BitmapDescriptorFactory.fromBitmap(drawableToBitmap(drawable, width, height)));
        }
    }

    private Bitmap drawableToBitmap(Drawable drawable, int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

}

