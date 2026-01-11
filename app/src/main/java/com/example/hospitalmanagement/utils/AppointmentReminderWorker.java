package com.example.hospitalmanagement.utils;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class AppointmentReminderWorker extends Worker {

    public AppointmentReminderWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        String patientName = getInputData().getString("patient_name");
        String time = getInputData().getString("time");

        NotificationHelper.showNotification(
                getApplicationContext(),
                "Upcoming Appointment",
                "Appointment with " + patientName + " at " + time,
                (int) System.currentTimeMillis()
        );

        return Result.success();
    }
}