package com.example.hospitalmanagement.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.hospitalmanagement.api.RetrofitClient;
import com.example.hospitalmanagement.model.AppointmentResponse;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Response;

public class PatientNotificationWorker extends Worker {

    public PatientNotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        SessionManager sessionManager = new SessionManager(getApplicationContext());
        Integer patientId = sessionManager.getPatientId();

        if (patientId == null || patientId == -1) {
            return Result.failure();
        }

        try {
            Response<List<AppointmentResponse>> response = RetrofitClient.getApiService()
                    .getAppointmentsByPatient(patientId)
                    .execute();

            if (response.isSuccessful() && response.body() != null) {
                List<AppointmentResponse> appointments = response.body();
                String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

                for (AppointmentResponse appointment : appointments) {
                    if (appointment.getScheduledDate().equals(todayDate) && "confirmed".equalsIgnoreCase(appointment.getStatus())) {
                        if (shouldNotify()) {
                            NotificationHelper.showPatientNotification(
                                    getApplicationContext(),
                                    "Appointment Reminder",
                                    "You have an appointment today with " + appointment.getDoctorName() + " at " + appointment.getScheduledTime(),
                                    3001
                            );
                            updateLastNotifyTime();
                        }
                        break;
                    }
                }
            }
            return Result.success();
        } catch (IOException e) {
            e.printStackTrace();
            return Result.retry();
        }
    }

    private boolean shouldNotify() {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("worker_prefs", Context.MODE_PRIVATE);
        long lastTime = prefs.getLong("last_notified_time", 0);
        return System.currentTimeMillis() - lastTime > 4 * 60 * 60 * 1000; // 4 hours
    }

    private void updateLastNotifyTime() {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("worker_prefs", Context.MODE_PRIVATE);
        prefs.edit().putLong("last_notified_time", System.currentTimeMillis()).apply();
    }
}
