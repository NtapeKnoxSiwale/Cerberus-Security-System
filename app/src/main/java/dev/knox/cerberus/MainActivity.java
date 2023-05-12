package dev.knox.cerberus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {
    // UI elements
    private TextView room1WeightTextView;
    private TextView room2WeightTextView;
    private Button stopAlarmButton;

    // Decimal Formatter for weight format
    private DecimalFormat decimalFormat;

    // Alarm system instance
    private AlarmSystem alarmSystem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI elements
        room1WeightTextView = findViewById(R.id.room1_weight);
        room2WeightTextView = findViewById(R.id.room2_weight);
        stopAlarmButton = findViewById(R.id.stop_alarm_button);
        stopAlarmButton.setVisibility(View.GONE);

        // Initialize Firebase database references
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        // Firebase database references
        DatabaseReference room1Ref = rootRef.child("Room1");
        DatabaseReference room2Ref = rootRef.child("Room2");

        // Initialize Decimal Formatter for weight format
        decimalFormat = new DecimalFormat("0.00");

        // Initialize Alarm system instance
        int notificationId = 1; // or any other desired value
        alarmSystem = new AlarmSystem(this, stopAlarmButton, notificationId);

        // Attach event listeners to database references
        room1Ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Double weight = snapshot.getValue(Double.class);
                if (weight != null) {
                    String weightText = decimalFormat.format(weight) + " kg";
                    room1WeightTextView.setText(weightText);
                    if (weight > 50) {
                        stopAlarmButton.setVisibility(View.VISIBLE);
                    }
                    alarmSystem.checkWeightAndNotify("Room1", weight);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });

        room2Ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Double weight = snapshot.getValue(Double.class);
                if (weight != null) {
                    String weightText = decimalFormat.format(weight) + " kg";
                    room2WeightTextView.setText(weightText);
                    if (weight > 50) {
                        stopAlarmButton.setVisibility(View.VISIBLE);
                    }
                    alarmSystem.checkWeightAndNotify("Room2", weight);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });

        // Attach onClick listener to stop alarm button
        stopAlarmButton.setOnClickListener(v -> {
            alarmSystem.stopAlarm();
            stopAlarmButton.setVisibility(View.GONE);
        });
    }
}

