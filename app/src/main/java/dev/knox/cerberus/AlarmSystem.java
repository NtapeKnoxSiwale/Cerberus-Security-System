package dev.knox.cerberus;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.Button;

import androidx.core.app.NotificationCompat;

public class AlarmSystem {

    private static final String CHANNEL_ID = "alarm_channel";
    private static final String CHANNEL_NAME = "Alarm Channel";
    private static int NOTIFICATION_ID = 1;

    private final Context context;
    private final Vibrator vibrator;
    private MediaPlayer mediaPlayer;
    private boolean isPlayingAlarm;

    private final Button stopAlarmButton;

    public AlarmSystem(Context context, Button stopAlarmButton, int notificationId) {
        this.context = context;
        this.stopAlarmButton = stopAlarmButton;
        NOTIFICATION_ID = notificationId;
        createNotificationChannel();
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        mediaPlayer = MediaPlayer.create(context, R.raw.alarm_sound_1);
        isPlayingAlarm = false;
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
        // User set trigger 1: notification
        if (weight > 0 && weight <= 20) {
            // Grey out the button and disable it
            stopAlarmButton.setEnabled(false);

            String message = context.getString(R.string.notification_message, weight, roomName);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.notification_icon)
                    .setContentTitle(context.getString(R.string.notification_title))
                    .setContentText(message)
                    .setChannelId(CHANNEL_ID)
                    .setAutoCancel(true);
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            manager.notify(NOTIFICATION_ID, builder.build());
            vibrate();
        }
        // user set trigger 2: notification + sound alarm
        // password promote
        else if (weight > 20 && weight < 50) {
            stopAlarmButton.setEnabled(true);
            stopAlarm();
        } else if (isPlayingAlarm) {
            stopAlarmButton.setEnabled(true);
            stopAlarm();
        }

        // Override AlertSystem: promote password
        else if (weight >= 50) {
            soundAlarm();
            vibrate();
            stopAlarmButton.setEnabled(true);
        }
    }

    private void soundAlarm() {
        if (!isPlayingAlarm) {
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
            isPlayingAlarm = true;
            stopAlarmButton.setEnabled(true);
        } else {
            stopAlarmButton.setEnabled(true);
        }
    }

    private void vibrate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            vibrator.vibrate(1000);
        }
    }



    void stopAlarm() {
        if (isPlayingAlarm) {
            mediaPlayer.setLooping(false);
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = MediaPlayer.create(context, R.raw.alarm_sound_2);
            isPlayingAlarm = false;
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            manager.cancel(NOTIFICATION_ID);
        }
    }

}
