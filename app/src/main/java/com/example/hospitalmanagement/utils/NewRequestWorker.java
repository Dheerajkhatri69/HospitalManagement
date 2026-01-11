package com.example.hospitalmanagement.utils;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.hospitalmanagement.api.RetrofitClient;
import com.example.hospitalmanagement.model.AppointmentRequestResponse;

import java.io.IOException;
import java.util.List;

import retrofit2.Response;

public class NewRequestWorker extends Worker {

    public NewRequestWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            // Check for pending appointment requests
            Response<List<AppointmentRequestResponse>> response = RetrofitClient.getApiService()
                    .getAllAppointmentRequests("pending")
                    .execute();

            if (response.isSuccessful() && response.body() != null) {
                List<AppointmentRequestResponse> requests = response.body();
                if (!requests.isEmpty()) {
                    NotificationHelper.showAdminNotification(
                            getApplicationContext(),
                            "New Appointment Requests",
                            "There are " + requests.size() + " pending appointment requests waiting for approval.",
                            4001
                    );
                }
            }
            return Result.success();
        } catch (IOException e) {
            e.printStackTrace();
            return Result.retry();
        }
    }
}
