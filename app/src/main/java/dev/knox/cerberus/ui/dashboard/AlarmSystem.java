package dev.knox.cerberus.ui.dashboard;

import android.content.Context;
import android.content.Intent;

public class AlarmSystem {
    public void startBackgroundService(Context context) {
        Intent serviceIntent = new Intent(context, WeightDataUpdateService.class);
        context.startService(serviceIntent);
    }
}