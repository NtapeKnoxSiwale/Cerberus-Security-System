package dev.knox.cerberus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    private TextView room1WeightTextView, room2WeightTextView;
    private DatabaseReference room1Ref, room2Ref;
    private DecimalFormat decimalFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI elements
        room1WeightTextView = findViewById(R.id.room1_weight);
        room2WeightTextView = findViewById(R.id.room2_weight);

        // Initialize Firebase database references
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        room1Ref = rootRef.child("Room1");
        room2Ref = rootRef.child("Room2");

        // Initialize Decimal Formatter for weight format
        decimalFormat = new DecimalFormat("0.00");

        // Attach event listeners to database references
        room1Ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Double weight = snapshot.getValue(Double.class);
                if (weight != null) {
                    String weightText = decimalFormat.format(weight) + " kg";
                    room1WeightTextView.setText(weightText);
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
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }
}
