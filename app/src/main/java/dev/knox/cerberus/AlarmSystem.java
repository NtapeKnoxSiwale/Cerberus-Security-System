package dev.knox.cerberus;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import androidx.core.app.NotificationCompat;

public class AlarmSystem {

    private static final String CHANNEL_ID = "alarm_channel";
    private static final String CHANNEL_NAME = "Alarm Channel";
    private static final int NOTIFICATION_ID = 1;

    private Context context;

    public AlarmSystem(Context context) {
        this.context = context;
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription(context.getString(R.string.notification_channel_description));
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    public void checkWeightAndNotify(String roomName, double weight) {
        if (weight >= 0 && weight <= 20) {
            String message = context.getString(R.string.notification_message, weight, roomName);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.notification_icon)
                    .setContentTitle(context.getString(R.string.notification_title))
                    .setContentText(message)
                    .setChannelId(CHANNEL_ID)
                    .setAutoCancel(true);
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (roomName.equals("Room1")) {
                manager.notify(NOTIFICATION_ID, builder.build());
            } else if (roomName.equals("Room2")) {
                manager.notify(NOTIFICATION_ID + 1, builder.build());
            }
        }
    }

}
