package com.group2.kort;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver { // [cite: 652]
    @Override
    public void onReceive(Context context, Intent intent) { // [cite: 654]
        // Lab 6 Requirement: Show Toast and Vibrate [cite: 643, 644]
        Toast.makeText(context, "Notification received!", Toast.LENGTH_LONG).show(); // [cite: 655]
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(2000); // [cite: 655]

        // Start the Notification Service to put the message in the tray
        String msg = intent.getStringExtra("msg");
        Intent serviceIntent = new Intent(context, AlarmNotificationService.class);
        serviceIntent.putExtra("msg", msg);
        context.startService(serviceIntent);
    }
}