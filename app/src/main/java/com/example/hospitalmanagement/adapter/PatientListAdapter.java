package com.example.hospitalmanagement.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hospitalmanagement.R;
import com.example.hospitalmanagement.model.PatientResponse;

import java.util.List;

public class PatientListAdapter extends RecyclerView.Adapter<PatientListAdapter.ViewHolder> {

    private List<PatientResponse> patientList;
    private OnPatientClickListener listener;

    public interface OnPatientClickListener {
        void onPatientClick(PatientResponse patient);
        void onViewAppointments(PatientResponse patient);
        void onEditPatient(PatientResponse patient);
    }

    public PatientListAdapter(List<PatientResponse> patientList, OnPatientClickListener listener) {
        this.patientList = patientList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_patient_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PatientResponse patient = patientList.get(position);

        holder.patientName.setText(patient.getFullName());
        holder.patientEmail.setText(patient.getEmail());
        holder.patientPhone.setText(patient.getPhone() != null ? patient.getPhone() : "N/A");

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onPatientClick(patient);
            }
        });

        holder.btnAppointments.setOnClickListener(v -> {
            if (listener != null) {
                listener.onViewAppointments(patient);
            }
        });

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditPatient(patient);
            }
        });
    }

    @Override
    public int getItemCount() {
        return patientList.size();
    }

    public void updateList(List<PatientResponse> newList) {
        this.patientList = newList;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView patientName;
        TextView patientEmail;
        TextView patientPhone;
        View btnAppointments;
        View btnEdit;

        ViewHolder(View itemView) {
            super(itemView);
            patientName = itemView.findViewById(R.id.patient_name);
            patientEmail = itemView.findViewById(R.id.patient_email);
            patientPhone = itemView.findViewById(R.id.patient_phone);
            btnAppointments = itemView.findViewById(R.id.btn_appointments);
            btnEdit = itemView.findViewById(R.id.btn_edit);
        }
    }
}