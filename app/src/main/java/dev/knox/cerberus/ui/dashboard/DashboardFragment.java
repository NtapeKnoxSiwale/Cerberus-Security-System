package dev.knox.cerberus.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;

import dev.knox.cerberus.R;

public class DashboardFragment extends Fragment {

    // UI elements
    private TextView room1WeightTextView;
    private TextView room2WeightTextView;
    private Button stopAlarmButton;

    // Decimal Formatter for weight format
    private final DecimalFormat decimalFormat = new DecimalFormat("0.00");
    //notification
    private AlarmSystem alarmSystem;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);

        // Initialize UI elements
        room1WeightTextView = rootView.findViewById(R.id.room1_weight);
        room2WeightTextView = rootView.findViewById(R.id.room2_weight);
        stopAlarmButton = rootView.findViewById(R.id.stop_alarm_button);

        // Initialize Alarm system instance (Assuming this is already defined somewhere)
        alarmSystem = new AlarmSystem(requireContext(), stopAlarmButton, 1);

        // Attach onClick listener to stop alarm button
        stopAlarmButton.setOnClickListener(v -> alarmSystem.stopAlarm());

        // Initialize Firebase database references
        initializeFirebase();

        return rootView;
    }

    private void initializeFirebase() {
        // Firebase user object
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            // User is not signed in
            return;
        }

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference userRef = rootRef.child("users").child(user.getUid());
        // Firebase database references
        DatabaseReference room1Ref = userRef.child("rooms").child("room1");
        DatabaseReference room2Ref = userRef.child("rooms").child("room2");

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String roomName = snapshot.getKey();
                Double weightValue = snapshot.getValue(Double.class);
                if (weightValue != null) {
                    CharSequence weightText = decimalFormat.format(weightValue) + " kg";
                    assert roomName != null;
                    if (roomName.equals("room1")) {
                        room1WeightTextView.setText(weightText);

                        alarmSystem.checkWeightAndNotify("room1", weightValue);
                    } else if (roomName.equals("room2")) {
                        room2WeightTextView.setText(weightText);
                        alarmSystem.checkWeightAndNotify("room2", weightValue);
                    }
                    if (weightValue > 50) {
                        stopAlarmButton.setVisibility(View.VISIBLE);
                    } else {
                        stopAlarmButton.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        };

        room1Ref.addValueEventListener(valueEventListener);
        room2Ref.addValueEventListener(valueEventListener);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Clean up references to avoid memory leaks
        room1WeightTextView = null;
        room2WeightTextView = null;
        stopAlarmButton = null;
    }
}
