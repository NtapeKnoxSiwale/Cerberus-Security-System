package dev.knox.cerberus;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import dev.knox.cerberus.databinding.ActivityMainBinding;
import dev.knox.cerberus.ui.dashboard.AlarmSystem;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private NavController navController;
    private AlarmSystem alarmSystem; // Initialize the AlarmSystem

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize the AlarmSystem
        alarmSystem = new AlarmSystem();

        // Obtain the NavController
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);

        // Set up the BottomNavigationView with the NavController
        NavigationUI.setupWithNavController(binding.navView, navController);

        // Start the background service
        alarmSystem.startBackgroundService(this);
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp() || super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        if (!navController.popBackStack()) {
            super.onBackPressed();
        }
    }
}
