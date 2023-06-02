package com.example.licentatakecare.authentication.userData.Adapters;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.licentatakecare.R;
import com.example.licentatakecare.authentication.model.LogEntry;
import java.util.List;

public class LogAdapter extends RecyclerView.Adapter<LogViewHolder> {

    private List<LogEntry> logList;
    private RecyclerView recyclerView;

    public LogAdapter(List<LogEntry> logList, RecyclerView recyclerView) {
        this.logList = logList;
        this.recyclerView = recyclerView;
    }

    @NonNull
    @Override
    public LogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.log_item_layout, parent, false);
        return new LogViewHolder(view, parent.getContext());
    }

    @Override
    public void onBindViewHolder(@NonNull LogViewHolder holder, int position) {
        LogEntry logEntry = logList.get(position);
        holder.bind(logEntry);
    }

    public List<LogEntry> getLogList() {
        return logList;
    }

    public void setLogList(List<LogEntry> logList) {
        this.logList = logList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return logList.size();
    }

}