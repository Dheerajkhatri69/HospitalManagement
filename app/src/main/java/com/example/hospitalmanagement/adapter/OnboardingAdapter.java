package com.example.hospitalmanagement.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hospitalmanagement.OnboardingActivity;
import com.example.hospitalmanagement.R;
import com.example.hospitalmanagement.model.ScreenItem;
import java.util.List;

public class OnboardingAdapter extends RecyclerView.Adapter<OnboardingAdapter.ViewHolder> {

    private List<ScreenItem> screenItems;
    private OnboardingActivity context;

    public OnboardingAdapter(List<ScreenItem> screenItems, OnboardingActivity context) {
        this.screenItems = screenItems;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_onboarding, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ScreenItem item = screenItems.get(position);

        holder.headingText.setText(item.getHeading());
        holder.descriptionText.setText(item.getDescription());
        holder.profileImage.setImageResource(item.getImageResId());

        // You can customize based on position if needed
        if (position == 0) {
            // First screen customization
        } else if (position == screenItems.size() - 1) {
            // Last screen customization
        }
    }

    @Override
    public int getItemCount() {
        return screenItems.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView profileCard;
        ImageView profileImage;
        TextView headingText;
        TextView descriptionText;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            profileCard = itemView.findViewById(R.id.profileCard);
            profileImage = itemView.findViewById(R.id.profileImage);
            headingText = itemView.findViewById(R.id.headingText);
            descriptionText = itemView.findViewById(R.id.descriptionText);
        }
    }
}