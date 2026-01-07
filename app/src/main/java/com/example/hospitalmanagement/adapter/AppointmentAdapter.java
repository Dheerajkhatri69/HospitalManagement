package com.example.hospitalmanagement.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hospitalmanagement.R;
import com.example.hospitalmanagement.model.Appointment;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder> {

    private List<Appointment> appointmentList;
    private Context context;
    private boolean limitToThree; // Flag to limit to 3 items in main page
    private OnAppointmentClickListener listener;

    public interface OnAppointmentClickListener {
        void onAppointmentClick(Appointment appointment);
    }

    public AppointmentAdapter(List<Appointment> appointmentList, Context context, boolean limitToThree) {
        this.appointmentList = appointmentList;
        this.context = context;
        this.limitToThree = limitToThree;

        // Try to cast context to listener
        if (context instanceof OnAppointmentClickListener) {
            this.listener = (OnAppointmentClickListener) context;
        }
    }

    public AppointmentAdapter(List<Appointment> appointmentList, Context context,
            boolean limitToThree, OnAppointmentClickListener listener) {
        this.appointmentList = appointmentList;
        this.context = context;
        this.limitToThree = limitToThree;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_appointment, parent, false);
        return new AppointmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentViewHolder holder, int position) {
        if (appointmentList != null && position < appointmentList.size()) {
            Appointment appointment = appointmentList.get(position);

            // Set appointment data
            holder.patientName.setText(appointment.getPatientName());
            holder.appointmentTime.setText(appointment.getTime());
            holder.appointmentReason.setText(appointment.getReason());
            holder.appointmentStatus.setText(appointment.getStatus());

            // Set patient image (you can use Glide/Picasso for network images)
            holder.patientImage.setImageResource(appointment.getPatientImage());

            // Set status background color
            int statusBackground = getStatusBackground(appointment.getStatus());
            holder.appointmentStatus.setBackgroundResource(statusBackground);

            // Set status text color
            int statusTextColor = getStatusTextColor(appointment.getStatus());
            holder.appointmentStatus.setTextColor(context.getResources().getColor(statusTextColor));

            // Set click listener
            holder.itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onAppointmentClick(appointment);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (appointmentList == null) {
            return 0;
        }
        // If limitToThree is true, show max 3 items
        if (limitToThree) {
            return Math.min(appointmentList.size(), 3);
        }
        return appointmentList.size();
    }

    private int getStatusBackground(String status) {
        switch (status.toLowerCase()) {
            case "confirmed":
                return R.drawable.bg_status_confirmed;
            case "pending":
                return R.drawable.bg_status_pending;
            case "cancelled":
                return R.drawable.bg_status_cancelled;
            default:
                return R.drawable.bg_status_pending;
        }
    }

    private int getStatusTextColor(String status) {
        switch (status.toLowerCase()) {
            case "confirmed":
            case "pending":
            case "cancelled":
                return android.R.color.white;
            default:
                return android.R.color.white;
        }
    }

    static class AppointmentViewHolder extends RecyclerView.ViewHolder {
        ImageView patientImage;
        TextView patientName;
        TextView appointmentTime;
        TextView appointmentReason;
        TextView appointmentStatus;
        ImageView arrowIcon;

        AppointmentViewHolder(@NonNull View itemView) {
            super(itemView);
            patientImage = itemView.findViewById(R.id.patient_image);
            patientName = itemView.findViewById(R.id.patient_name);
            appointmentTime = itemView.findViewById(R.id.appointment_time);
            appointmentReason = itemView.findViewById(R.id.appointment_reason);
            appointmentStatus = itemView.findViewById(R.id.appointment_status);
            arrowIcon = itemView.findViewById(R.id.arrow_icon);
        }
    }

    // Update the appointment list
    public void updateAppointments(List<Appointment> newAppointments) {
        appointmentList.clear();
        appointmentList.addAll(newAppointments);
        notifyDataSetChanged();
    }
}