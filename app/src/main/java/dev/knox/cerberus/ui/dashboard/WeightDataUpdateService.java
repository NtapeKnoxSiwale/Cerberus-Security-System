package dev.knox.cerberus.ui.dashboard;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class WeightDataUpdateService extends Service {

    private DatabaseReference userRef;

    @Override
    public void onCreate() {
        super.onCreate();
        // Get the current user's UID
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            String userUid = user.getUid();
            // Initialize Firebase user reference
            userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userUid);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("WeightDataUpdateService", "Service started");
        // Read weights from Firebase in the background
        ValueEventListener roomWeightListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Log when data is collected
                Log.d("WeightDataUpdateService", "Data collected: " + dataSnapshot.toString());

                // Handle changes in room weight here
                if (dataSnapshot.exists()) {
                    Double roomWeight = dataSnapshot.getValue(Double.class);
                    if (roomWeight != null) {
                        Log.d("WeightDataUpdateService", "Room Weight: " + roomWeight);
                        // Determine which room you are reading (room 1 or room 2) and set the corresponding weight
                        if (dataSnapshot.getKey().equals("room_1_weight")) {
                            WeightData.getInstance().addRoom1Weight(roomWeight);
                        } else if (dataSnapshot.getKey().equals("room_2_weight")) {
                            WeightData.getInstance().addRoom2Weight(roomWeight);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors
                Log.e("WeightDataUpdateService", "Error reading room weight: " + databaseError.getMessage());
            }
        };

        // Attach the listener to room 1 weight
        DatabaseReference room1Ref = userRef.child("room_1_weight");
        room1Ref.addValueEventListener(roomWeightListener);

        // Attach the listener to room 2 weight
        DatabaseReference room2Ref = userRef.child("room_2_weight");
        room2Ref.addValueEventListener(roomWeightListener);

        return START_STICKY; // Service will be restarted if it's killed by the system
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
