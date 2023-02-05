package com.example.licentatakecare.log;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.licentatakecare.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    Context context;

    public MyAdapter(Context context, ArrayList<Log> list) {
        this.context = context;
        this.list = list;
    }

    ArrayList<Log> list;

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.item,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
   Log log=list.get(position);
   holder.entry_timestamp.setText(log.getEntry_timestamp());
   holder.exit_timestamp.setText(log.getExit_timestamp());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView entry_timestamp,exit_timestamp;

        public MyViewHolder(@NotNull View itemView)
        {super(itemView);
            entry_timestamp=itemView.findViewById(R.id.entry_timestamp);
            entry_timestamp=itemView.findViewById(R.id.exit_timestamp);
        }
    }

}
