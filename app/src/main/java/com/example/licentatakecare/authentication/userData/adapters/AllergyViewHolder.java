package com.example.licentatakecare.authentication.userData.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.licentatakecare.authentication.model.Allergy;
import com.example.licentatakecare.R;

class AllergyViewHolder extends RecyclerView.ViewHolder {
    private TextView textView;
    private TextView type;
    private final Context mContext;

    public AllergyViewHolder(@NonNull View itemView, Context context) {
        super(itemView);
        textView = itemView.findViewById(R.id.tvAllergyName);
        mContext = context;
        //type = itemView.findViewById(R.id.ivAllergyImage);
    }

    public void bind(Allergy allergy) {
        textView.setText(allergy.getName());

        // Set the background color based on the AllergyType
        LinearLayout linearLayout = itemView.findViewById(R.id.linear_layout);

        switch (allergy.getType()) {
            case MEDICINE:
                linearLayout.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.medicine));
                break;
            case SEASONAL:
                linearLayout.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.seasonal));
                break;
            case FOOD:
                linearLayout.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.food));
                break;
            case ANIMAL:
                linearLayout.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.animal));
                break;
            default:
                // Set a default color if necessary
                linearLayout.setBackgroundColor(Color.WHITE);
                break;
        }
        //  type.setText(allergy.getType());
    }
}
