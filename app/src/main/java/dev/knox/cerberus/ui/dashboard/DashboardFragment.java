package dev.knox.cerberus.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import dev.knox.cerberus.databinding.FragmentDashboardBinding;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textHome;
        dashboardViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}


//package dev.knox.cerberus;
//
//        import android.content.Intent;
//        import android.os.Bundle;
//        import android.view.View;
//        import android.widget.Button;
//        import android.widget.TextView;
//
//        import androidx.annotation.NonNull;
//        import androidx.appcompat.app.AppCompatActivity;
//        import androidx.navigation.NavController;
//        import androidx.navigation.Navigation;
//        import androidx.navigation.ui.AppBarConfiguration;
//        import androidx.navigation.ui.NavigationUI;
//
//        import com.google.android.material.bottomnavigation.BottomNavigationView;
//        import com.google.firebase.auth.FirebaseAuth;
//        import com.google.firebase.auth.FirebaseUser;
//        import com.google.firebase.database.DataSnapshot;
//        import com.google.firebase.database.DatabaseError;
//        import com.google.firebase.database.DatabaseReference;
//        import com.google.firebase.database.FirebaseDatabase;
//        import com.google.firebase.database.ValueEventListener;
//
//        import java.text.DecimalFormat;
//
//        import dev.knox.cerberus.auth.AuthActivity;
//        import dev.knox.cerberus.databinding.ActivityMainBinding;
//
//
//
//public class MainActivity extends AppCompatActivity {
//    // UI elements
//    private TextView room1WeightTextView;
//    private TextView room2WeightTextView;
//    private Button stopAlarmButton;
//
//    // Decimal Formatter for weight format
//    private final DecimalFormat decimalFormat = new DecimalFormat("0.00");
//
//    // Alarm system instance
//    private AlarmSystem alarmSystem;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        dev.knox.cerberus.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
//
//        BottomNavigationView navView = findViewById(R.id.nav_view);
//
//        // Passing each menu ID as a set of Ids because each
//        // menu should be considered as top level destinations.
//        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications, R.id.navigation_more)
//                .build();
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main2);
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
//        NavigationUI.setupWithNavController(binding.navView, navController);
//
//        // Initialize UI elements
//        room1WeightTextView = findViewById(R.id.room1_weight);
//        room2WeightTextView = findViewById(R.id.room2_weight);
//        stopAlarmButton = findViewById(R.id.stop_alarm_button);
//
//        // Initialize Firebase database references
//        initializeFirebase();
//
//        // Initialize Alarm system instance
//        alarmSystem = new AlarmSystem(this, stopAlarmButton, 1);
//
//        Button logoutButton = findViewById(R.id.logout);
//        logoutButton.setOnClickListener(v -> {
//            FirebaseAuth.getInstance().signOut();
//            Intent intent = new Intent(this, AuthActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            startActivity(intent);
//            finish();
//        });
//
//
//        // Attach onClick listener to stop alarm button
//        stopAlarmButton.setOnClickListener(v -> alarmSystem.stopAlarm());
//    }
//
//    private void initializeFirebase() {
//        // Firebase user object
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        if (user == null) {
//            // User is not signed in
//            return;
//        }
//
//        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
//        DatabaseReference userRef = rootRef.child("users").child(user.getUid());
//        // Firebase database references
//        DatabaseReference room1Ref = userRef.child("rooms").child("room1");
//        DatabaseReference room2Ref = userRef.child("rooms").child("room2");
//
//        ValueEventListener valueEventListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                String roomName = snapshot.getKey();
//                Double weightValue = snapshot.getValue(Double.class);
//                if (weightValue != null) {
//                    CharSequence weightText = decimalFormat.format(weightValue) + " kg";
//                    assert roomName != null;
//                    if (roomName.equals("room1")) {
//                        room1WeightTextView.setText(weightText);
//                        alarmSystem.checkWeightAndNotify("room1", weightValue);
//                    } else if (roomName.equals("room2")) {
//                        room2WeightTextView.setText(weightText);
//                        alarmSystem.checkWeightAndNotify("room2", weightValue);
//                    }
//                    if (weightValue > 50) {
//                        stopAlarmButton.setVisibility(View.VISIBLE);
//                    } else {
//                        stopAlarmButton.setVisibility(View.GONE);
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                // Handle error
//            }
//        };
//
//        room1Ref.addValueEventListener(valueEventListener);
//        room2Ref.addValueEventListener(valueEventListener);
//    }
//}