package com.example.hospitalmanagement.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.hospitalmanagement.AdminActivity;
import com.example.hospitalmanagement.MainActivity;
import com.example.hospitalmanagement.PatientActivity;
import com.example.hospitalmanagement.R;

public class NotificationHelper {

    public static final String CHANNEL_ID_APPOINTMENTS = "appointments_channel";
    public static final String CHANNEL_ID_ADMIN = "admin_channel";
    public static final String CHANNEL_ID_LAB = "lab_channel";

    public static void createNotificationChannels(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel1 = new NotificationChannel(
                    CHANNEL_ID_APPOINTMENTS,
                    "Appointment Reminders",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel1.setDescription("Notifications for upcoming appointments");

            NotificationChannel channel2 = new NotificationChannel(
                    CHANNEL_ID_ADMIN,
                    "Admin Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel2.setDescription("Notifications for new requests");

            NotificationChannel channel3 = new NotificationChannel(
                    CHANNEL_ID_LAB,
                    "Lab Reports",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel3.setDescription("Notifications for lab reports");

            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel1);
                manager.createNotificationChannel(channel2);
                manager.createNotificationChannel(channel3);
            }
        }
    }

    public static void showNotification(Context context, String title, String message, int notificationId) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID_APPOINTMENTS)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        try {
            notificationManager.notify(notificationId, builder.build());
        } catch (SecurityException e) {
            // Handle permission denial
        }
    }
    
    public static void showAdminNotification(Context context, String title, String message, int notificationId) {
        Intent intent = new Intent(context, AdminActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID_ADMIN)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        try {
            notificationManager.notify(notificationId, builder.build());
        } catch (SecurityException e) {
            // Handle permission denial
        }
    }

    public static void showPatientNotification(Context context, String title, String message, int notificationId) {
        Intent intent = new Intent(context, PatientActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID_APPOINTMENTS)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        try {
            notificationManager.notify(notificationId, builder.build());
        } catch (SecurityException e) {
            // Handle permission denial
        }
    }

    public static void showLabNotification(Context context, String title, String message, int notificationId) {
        Intent intent = new Intent(context, MainActivity.class); 
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID_LAB)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        try {
            notificationManager.notify(notificationId, builder.build());
        } catch (SecurityException e) {
            // Handle permission denial
        }
    }
}
