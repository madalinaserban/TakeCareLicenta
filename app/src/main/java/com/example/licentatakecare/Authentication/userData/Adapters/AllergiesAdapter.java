package com.example.licentatakecare.Authentication.userData.Adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.licentatakecare.Authentication.userData.Allergy;
import com.example.licentatakecare.R;

import java.util.List;

public class AllergiesAdapter extends RecyclerView.Adapter<AllergiesAdapter.AllergyViewHolder> {
    private List<Allergy> allergies;

    public AllergiesAdapter(List<Allergy> allergies) {
        this.allergies = allergies;
    }

    @NonNull
    @Override
    public AllergyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_allergy, parent, false);
        return new AllergyViewHolder(view);
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

    static class AllergyViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;
        private TextView type;

        public AllergyViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tvAllergyName);
            //type = itemView.findViewById(R.id.ivAllergyImage);
        }

        public void bind(Allergy allergy) {
            textView.setText(allergy.getName());

            // Set the background color based on the AllergyType
            switch (allergy.getType()) {
                case MEDICINE:
                    itemView.setBackgroundColor(Color.RED);
                    break;
                case SEASONAL:
                    itemView.setBackgroundColor(Color.GREEN);
                    break;
                case FOOD:
                    itemView.setBackgroundColor(Color.BLUE);
                    break;
                case SEAFOOD:
                    itemView.setBackgroundColor(Color.YELLOW);
                    break;
                default:
                    // Set a default color if necessary
                    itemView.setBackgroundColor(Color.WHITE);
                    break;
            }
            //  type.setText(allergy.getType());
        }
    }
}

