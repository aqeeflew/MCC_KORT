package com.group2.kort;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import androidx.core.app.NotificationCompat;

public class AlarmNotificationService extends IntentService { // [cite: 712]

    public AlarmNotificationService() {
        super("AlarmNotificationService"); // [cite: 718]
    }

    @Override
    protected void onHandleIntent(Intent intent) { // [cite: 720]
        // Get the custom message passed from the Receiver
        String notifyMsg = intent.getExtras().getString("msg"); // [cite: 722]
        sendNotification(notifyMsg); // [cite: 723]
    }

    private void sendNotification(String msg) {
        NotificationManager notificationManager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE); // [cite: 727, 728]

        String NOTIFICATION_CHANNEL_ID = "01"; // [cite: 729]

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // [cite: 730]
            NotificationChannel notificationChannel = new NotificationChannel(
                    NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_MAX); // [cite: 732, 733]

            notificationChannel.setDescription("Sample Channel description"); // [cite: 735]
            notificationChannel.enableLights(true); // [cite: 736]
            notificationChannel.setLightColor(Color.RED); // [cite: 737]
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000}); // [cite: 738]
            notificationChannel.enableVibration(true); // [cite: 739]
            notificationManager.createNotificationChannel(notificationChannel); // [cite: 740]
        }

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE); // [cite: 742, 743]

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID); // [cite: 744, 745]

        notificationBuilder.setAutoCancel(true) // [cite: 746]
                .setDefaults(Notification.DEFAULT_ALL) // [cite: 747]
                .setWhen(System.currentTimeMillis()) // [cite: 748]
                .setSmallIcon(R.mipmap.ic_launcher_round) // [cite: 749]
                .setContentTitle("Reminder") // [cite: 750]
                .setContentText(msg) // [cite: 751]
                .setContentIntent(contentIntent);

        notificationManager.notify(1, notificationBuilder.build()); // [cite: 752]
    }
}