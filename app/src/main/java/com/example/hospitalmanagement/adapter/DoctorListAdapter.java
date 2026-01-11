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

public class DoctorListAdapter extends RecyclerView.Adapter<DoctorListAdapter.ViewHolder> {

    private List<DoctorResponse> doctorList;

    private OnDoctorClickListener listener;

    public interface OnDoctorClickListener {
        void onDoctorClick(DoctorResponse doctor);
        void onViewAppointments(DoctorResponse doctor);
        void onEditDoctor(DoctorResponse doctor);
    }

    public DoctorListAdapter(List<DoctorResponse> doctorList, OnDoctorClickListener listener) {
        this.doctorList = doctorList;
        this.listener = listener;
    }

    // Constructor for backward compatibility if needed, or update usage
    public DoctorListAdapter(List<DoctorResponse> doctorList) {
        this.doctorList = doctorList;
    }
    
    public void setOnDoctorClickListener(OnDoctorClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_doctor_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DoctorResponse doctor = doctorList.get(position);

        holder.doctorName.setText(doctor.getFullName());
        
        String specialization = "General";
        if (doctor.getSpecializations() != null && !doctor.getSpecializations().isEmpty()) {
            specialization = doctor.getSpecializations().get(0);
        }
        holder.doctorSpecialization.setText(specialization);
        
        holder.doctorExperience.setText(doctor.getYearsExperience() + " years experience");

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDoctorClick(doctor);
            }
        });

        holder.btnAppointments.setOnClickListener(v -> {
            if (listener != null) {
                listener.onViewAppointments(doctor);
            }
        });

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditDoctor(doctor);
            }
        });
    }

    @Override
    public int getItemCount() {
        return doctorList.size();
    }

    public void updateList(List<DoctorResponse> newList) {
        this.doctorList = newList;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView doctorName;
        TextView doctorSpecialization;
        TextView doctorExperience;
        View btnAppointments;
        View btnEdit;

        ViewHolder(View itemView) {
            super(itemView);
            doctorName = itemView.findViewById(R.id.doctor_name);
            doctorSpecialization = itemView.findViewById(R.id.doctor_specialization);
            doctorExperience = itemView.findViewById(R.id.doctor_experience);
            btnAppointments = itemView.findViewById(R.id.btn_appointments);
            btnEdit = itemView.findViewById(R.id.btn_edit);
        }
    }
}
