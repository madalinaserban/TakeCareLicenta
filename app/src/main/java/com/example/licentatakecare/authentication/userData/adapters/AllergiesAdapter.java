package com.example.licentatakecare.authentication.userData.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.licentatakecare.authentication.model.Allergy;
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

