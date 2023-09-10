package dev.knox.cerberus.ui.dashboard;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.text.DecimalFormat;

import dev.knox.cerberus.R;

public class DashboardFragment extends Fragment implements WeightDataObserver {

    // Decimal Formatter for weight format
    private final DecimalFormat decimalFormat = new DecimalFormat("0.00");
    // UI elements
    private TextView room1WeightTextView;
    private TextView room2WeightTextView;

    @Override
    public void onWeightChanged() {
        // Access and log the updated weights when they change
        Double room1Weight = WeightData.getInstance().getRoom1Weights().get(0);
        Double room2Weight = WeightData.getInstance().getRoom2Weights().get(0);
        logWeights(room1Weight, room2Weight);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);

        // Initialize UI elements
        room1WeightTextView = rootView.findViewById(R.id.room1_weight);
        room2WeightTextView = rootView.findViewById(R.id.room2_weight);

        // Register this fragment as an observer of WeightData
        WeightData.getInstance().addObserver(this);

        return rootView;
    }
    // Log the weights
    private void logWeights(double room1Weight, double room2Weight) {
        if (!Double.isNaN(room1Weight)) {
            // Log room 1 weight
            String logMessageRoom1 = "Room 1 Weight: " + room1Weight + " kg";
            Log.d("DashboardFragment", logMessageRoom1);
        }

        if (!Double.isNaN(room2Weight)) {
            // Log room 2 weight
            String logMessageRoom2 = "Room 2 Weight: " + room2Weight + " kg";
            Log.d("DashboardFragment", logMessageRoom2);
        }
    }
}
