package com.example.hospitalmanagement.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hospitalmanagement.R;
import com.example.hospitalmanagement.model.AppointmentRequestResponse;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class AppointmentRequestAdapter extends RecyclerView.Adapter<AppointmentRequestAdapter.ViewHolder> {

    private List<AppointmentRequestResponse> requestList;
    private OnRequestActionListener listener;

    public interface OnRequestActionListener {
        void onApprove(AppointmentRequestResponse request);

        void onReject(AppointmentRequestResponse request);

        void onRequestClick(AppointmentRequestResponse request);
    }

    public AppointmentRequestAdapter(List<AppointmentRequestResponse> requestList, OnRequestActionListener listener) {
        this.requestList = requestList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_appointment_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AppointmentRequestResponse request = requestList.get(position);

        holder.patientName.setText(request.getPatientName());
        holder.specialization.setText(request.getSpecialization());

        // Show doctor name if available
        if (request.getDoctorName() != null && !request.getDoctorName().isEmpty()) {
            holder.doctorName.setText("Requested: " + request.getDoctorName());
            holder.doctorName.setVisibility(View.VISIBLE);
        } else {
            holder.doctorName.setVisibility(View.GONE);
        }

        // Format date and time
        String dateTime = request.getPreferredDate() + " - " + request.getPreferredTime();
        holder.preferredDateTime.setText(dateTime);

        // Set status badge
        String status = request.getStatus().toUpperCase();
        holder.statusBadge.setText(status);

        // Set badge background based on status
        switch (request.getStatus().toLowerCase()) {
            case "pending":
                holder.statusBadge.setBackgroundResource(R.drawable.status_badge_pending);
                holder.actionButtons.setVisibility(View.VISIBLE);
                break;
            case "approved":
                holder.statusBadge.setBackgroundResource(R.drawable.status_badge_approved);
                holder.actionButtons.setVisibility(View.GONE);
                break;
            case "rejected":
                holder.statusBadge.setBackgroundResource(R.drawable.status_badge_rejected);
                holder.actionButtons.setVisibility(View.GONE);
                break;
        }

        // Show notes if available
        if (request.getNotes() != null && !request.getNotes().isEmpty()) {
            holder.notes.setText(request.getNotes());
            holder.notes.setVisibility(View.VISIBLE);
        } else {
            holder.notes.setVisibility(View.GONE);
        }

        // Button listeners
        holder.btnApprove.setOnClickListener(v -> {
            if (listener != null)
                listener.onApprove(request);
        });

        holder.btnReject.setOnClickListener(v -> {
            if (listener != null)
                listener.onReject(request);
        });

        holder.itemView.setOnClickListener(v -> {
            if (listener != null)
                listener.onRequestClick(request);
        });
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    public void updateList(List<AppointmentRequestResponse> newList) {
        this.requestList = newList;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView patientName;
        TextView specialization;
        TextView doctorName;
        TextView preferredDateTime;
        TextView statusBadge;
        TextView notes;
        View actionButtons;
        MaterialButton btnApprove;
        MaterialButton btnReject;

        ViewHolder(View itemView) {
            super(itemView);
            patientName = itemView.findViewById(R.id.patient_name);
            specialization = itemView.findViewById(R.id.specialization);
            doctorName = itemView.findViewById(R.id.doctor_name);
            preferredDateTime = itemView.findViewById(R.id.preferred_datetime);
            statusBadge = itemView.findViewById(R.id.status_badge);
            notes = itemView.findViewById(R.id.notes);
            actionButtons = itemView.findViewById(R.id.action_buttons);
            btnApprove = itemView.findViewById(R.id.btn_approve);
            btnReject = itemView.findViewById(R.id.btn_reject);
        }
    }
}
