package com.example.licentatakecare.Authentication.userData.Adapters;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.licentatakecare.Authentication.LogEntry;
import com.example.licentatakecare.R;

public class LogViewHolder extends RecyclerView.ViewHolder {

    private TextView entryTimestampTextView;
    private TextView exitTimestampTextView;
    private TextView sectionTextView;
    private TextView hospitalTextView;
    private TextView timeTextView;

    public LogViewHolder(@NonNull View itemView) {
        super(itemView);

        entryTimestampTextView = itemView.findViewById(R.id.entry_timestamp);
        exitTimestampTextView = itemView.findViewById(R.id.exit_timestamp);
        sectionTextView = itemView.findViewById(R.id.section);
        hospitalTextView = itemView.findViewById(R.id.hospital);
        timeTextView=itemView.findViewById(R.id.tv_time);
    }

    public void bind(LogEntry logEntry) {
        entryTimestampTextView.setText("Entry: " + logEntry.getEntryTimestamp());
        exitTimestampTextView.setText("Exit: " + logEntry.getExitTimestamp());
        sectionTextView.setText("Section: " + logEntry.getSection());
        hospitalTextView.setText(logEntry.getHospital());
        timeTextView.setText("Stayed for: "+logEntry.getTimeDifference());
    }
}