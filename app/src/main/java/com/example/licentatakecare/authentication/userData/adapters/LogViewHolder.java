package com.example.licentatakecare.authentication.userData.adapters;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.RecyclerView;

import com.example.licentatakecare.authentication.model.LogEntry;
import com.example.licentatakecare.R;
import com.example.licentatakecare.map.util.clusters.ESection;

import java.util.Locale;

public class LogViewHolder extends RecyclerView.ViewHolder {

    private TextView entryTimestampTextView;
    private TextView exitTimestampTextView;
    private TextView sectionTextView;
    private TextView hospitalTextView;
    private TextView timeTextView;
    private LinearLayout linear_background;
    private Context mContext;


    public LogViewHolder(@NonNull View itemView, Context context) {
        super(itemView);

        entryTimestampTextView = itemView.findViewById(R.id.entry_timestamp);
        exitTimestampTextView = itemView.findViewById(R.id.exit_timestamp);
        sectionTextView = itemView.findViewById(R.id.tv_log_section);
        hospitalTextView = itemView.findViewById(R.id.tv_log_hospital);
        timeTextView = itemView.findViewById(R.id.stayed_for_duration);
        linear_background = itemView.findViewById(R.id.linear_background);
        mContext = context;
    }

    public void bind(LogEntry logEntry) {
        entryTimestampTextView.setText(logEntry.getEntryTimestamp());
        exitTimestampTextView.setText(logEntry.getExitTimestamp());
        sectionTextView.setText(logEntry.getSection());
        hospitalTextView.setText(logEntry.getHospital());
        timeTextView.setText(logEntry.getTimeDifference());
        ESection section = ESection.valueOf(logEntry.getSection().toUpperCase(Locale.ROOT));
        int backgroundColor = getBackgroundColorForSection(section);

        // Create a ShapeDrawable with rounded corners
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setCornerRadii(new float[]{90, 90, 90, 90, 90, 90, 90, 90});
        drawable.setColor(backgroundColor);

        // Set the ShapeDrawable as the background of the LinearLayout
        linear_background.setBackground(drawable);
    }

    private int getBackgroundColorForSection(ESection section) {
        int color = ContextCompat.getColor(mContext, R.color.btn_all); // default color
        switch (section) {
            case EMERGENCY:
                color = ContextCompat.getColor(mContext, R.color.btn_emergency);
                break;
            case RADIOLOGY:
                color = ContextCompat.getColor(mContext, R.color.btn_radiology);
                break;
            case CARDIOLOGY:
                color = ContextCompat.getColor(mContext, R.color.btn_cardiology);
                break;
            case PEDIATRICS:
                color = ContextCompat.getColor(mContext, R.color.btn_pediatrics);
                break;
            case PULMONARY:
                color = ContextCompat.getColor(mContext, R.color.btn_pulmonary);
                break;
            case LABORATORY:
                color = ContextCompat.getColor(mContext, R.color.btn_laboratory);
                break;
            case ALL:
                color = ContextCompat.getColor(mContext, R.color.btn_all);
                break;
        }
        int transparentcolor = ColorUtils.setAlphaComponent(color, 178);
        return transparentcolor;
    }
}