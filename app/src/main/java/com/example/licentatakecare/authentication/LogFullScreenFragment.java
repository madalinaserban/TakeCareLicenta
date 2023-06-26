package com.example.licentatakecare.authentication;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.licentatakecare.R;
import com.example.licentatakecare.authentication.model.LogEntry;
import com.example.licentatakecare.authentication.userData.adapters.LogAdapter;
import com.example.licentatakecare.map.MapsActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class LogFullScreenFragment extends Fragment {

    private RecyclerView recyclerView;
    private List mLogList;
    private LogAdapter logAdapter;

    public LogFullScreenFragment(List<LogEntry> logList) {
        mLogList = logList;

    }

    public LogFullScreenFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_log_full_screen, container, false);
        recyclerView = view.findViewById(R.id.logRecyclerView);

        // Retrieve the logList from the arguments
        Bundle args = getArguments();
        if (args != null) {
            List<LogEntry> logList = (List<LogEntry>) args.getSerializable("logList");
            logAdapter = new LogAdapter(logList, recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(logAdapter);
        }
        ImageButton backToMapButton = view.findViewById(R.id.backToProfileButton);
        backToMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateFromFullScreenFragment();
            }
        });

        return view;
    }

    private void animateFromFullScreenFragment() {
        NavController navController = Navigation.findNavController(requireActivity(), R.id.fragmentContainerView);
        navController.popBackStack(R.id.profileFragment, false);
    }


}