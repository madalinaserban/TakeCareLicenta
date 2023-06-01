package com.example.licentatakecare.map;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.licentatakecare.R;

public class ErrorFragment extends Fragment {

    private OnRetryClickListener retryClickListener;

    public static ErrorFragment newInstance() {
        return new ErrorFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_error, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

       // Button retryButton = view.findViewById(R.id.retryButton);
//        retryButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (retryClickListener != null) {
//                    retryClickListener.onRetryClick();
//                }
//            }
//        });
    }

    public void setOnRetryClickListener(OnRetryClickListener listener) {
        this.retryClickListener = listener;
    }

    public interface OnRetryClickListener {
        void onRetryClick();
    }
}
