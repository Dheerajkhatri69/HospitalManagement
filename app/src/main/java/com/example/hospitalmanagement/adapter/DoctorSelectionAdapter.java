package com.example.hospitalmanagement.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hospitalmanagement.R;
import com.example.hospitalmanagement.model.DoctorResponse;

import java.util.List;

public class DoctorSelectionAdapter extends RecyclerView.Adapter<DoctorSelectionAdapter.ViewHolder> {

    private List<DoctorResponse> doctorList;
    private int selectedPosition = -1;
    private OnDoctorSelectedListener listener;

    public interface OnDoctorSelectedListener {
        void onDoctorSelected(DoctorResponse doctor);
    }

    public DoctorSelectionAdapter(List<DoctorResponse> doctorList, OnDoctorSelectedListener listener) {
        this.doctorList = doctorList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_doctor_selection, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DoctorResponse doctor = doctorList.get(position);

        holder.doctorName.setText(doctor.getFullName());

        // Show first specialization or "General" if none
        String specialization = "General";
        if (doctor.getSpecializations() != null && !doctor.getSpecializations().isEmpty()) {
            specialization = doctor.getSpecializations().get(0);
        }
        holder.doctorSpecialization.setText(specialization);

        // Show experience
        String experience = doctor.getYearsExperience() != null ? doctor.getYearsExperience() + " years experience"
                : "N/A";
        holder.doctorExperience.setText(experience);

        // Update selection indicator
        if (position == selectedPosition) {
            holder.selectionIndicator.setBackgroundResource(R.drawable.circle_filled);
        } else {
            holder.selectionIndicator.setBackgroundResource(R.drawable.circle_outline);
        }

        // Click listener
        holder.itemView.setOnClickListener(v -> {
            int previousPosition = selectedPosition;
            selectedPosition = holder.getAdapterPosition();

            // Notify changes
            if (previousPosition != -1) {
                notifyItemChanged(previousPosition);
            }
            notifyItemChanged(selectedPosition);

            // Callback
            if (listener != null) {
                listener.onDoctorSelected(doctor);
            }
        });
    }

    @Override
    public int getItemCount() {
        return doctorList.size();
    }

    public DoctorResponse getSelectedDoctor() {
        if (selectedPosition >= 0 && selectedPosition < doctorList.size()) {
            return doctorList.get(selectedPosition);
        }
        return null;
    }

    public void updateList(List<DoctorResponse> newList) {
        this.doctorList = newList;
        this.selectedPosition = -1;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView doctorName;
        TextView doctorSpecialization;
        TextView doctorExperience;
        View selectionIndicator;

        ViewHolder(View itemView) {
            super(itemView);
            doctorName = itemView.findViewById(R.id.doctor_name);
            doctorSpecialization = itemView.findViewById(R.id.doctor_specialization);
            doctorExperience = itemView.findViewById(R.id.doctor_experience);
            selectionIndicator = itemView.findViewById(R.id.selection_indicator);
        }
    }
}
