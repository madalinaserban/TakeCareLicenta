package com.example.licentatakecare.Authentication.userData.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.licentatakecare.Authentication.userData.Allergy;
import com.example.licentatakecare.R;

import java.util.List;

public class AllergiesAdapter extends RecyclerView.Adapter<AllergyViewHolder> {
    private List<Allergy> allergies;

    public AllergiesAdapter(List<Allergy> allergies) {
        this.allergies = allergies;
    }

    @NonNull
    @Override
    public AllergyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_allergy, parent, false);
        return new AllergyViewHolder(view, parent.getContext());
    }

    @Override
    public void onBindViewHolder(@NonNull AllergyViewHolder holder, int position) {
        Allergy allergy = allergies.get(position);
        holder.bind(allergy);
    }

    @Override
    public int getItemCount() {
        return allergies.size();
    }

}

