package dev.knox.cerberus.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.fragment.app.DialogFragment;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.Map;

import dev.knox.cerberus.R;

public class EditTriggerFragment extends DialogFragment {

    private View rootView;
    private EditText maxInput;
    private EditText minInput;
    private RadioGroup notificationTypeRadioGroup;
    private RadioGroup alertTypeRadioGroup;

    private DatabaseReference usersRef;
    private FirebaseUser currentUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragement_edit_trigger, container, false);

        // Initialize UI elements
        maxInput = rootView.findViewById(R.id.editMaxInput);
        minInput = rootView.findViewById(R.id.editMinInput);
        notificationTypeRadioGroup = rootView.findViewById(R.id.editNotificationTypeRadioGroup);
        alertTypeRadioGroup = rootView.findViewById(R.id.editAlertTypeRadioGroup);

        Button saveButton = rootView.findViewById(R.id.saveEditButton);

        // Retrieve trigger properties from arguments (you need to pass them when creating this fragment)
        Bundle args = getArguments();
        if (args != null) {
            String initialMax = args.getString("max");
            String initialMin = args.getString("min");
            String initialNotificationType = args.getString("notificationType");
            String initialAlertType = args.getString("alertType");

            // Populate the UI with initial trigger properties
            maxInput.setText(initialMax);
            minInput.setText(initialMin);
            setRadioGroupSelection(notificationTypeRadioGroup, initialNotificationType);
            setRadioGroupSelection(alertTypeRadioGroup, initialAlertType);
        }

        saveButton.setOnClickListener(v -> {
            // Retrieve the edited trigger properties from UI elements
            String newMax = maxInput.getText().toString();
            String newMin = minInput.getText().toString();
            String selectedNotificationType = getSelectedRadioText(notificationTypeRadioGroup);
            String selectedAlertType = getSelectedRadioText(alertTypeRadioGroup);

            // Update the trigger data in Firebase
            updateTriggerInFirebase(newMax, newMin, selectedNotificationType, selectedAlertType);
        });

        return rootView;
    }

    // Method to get the text of the selected radio button in a RadioGroup
    private String getSelectedRadioText(RadioGroup radioGroup) {
        int selectedId = radioGroup.getCheckedRadioButtonId();
        RadioButton radioButton = rootView.findViewById(selectedId);
        return radioButton.getText().toString();
    }

    // Method to set the selection of a RadioGroup based on text
    private void setRadioGroupSelection(RadioGroup radioGroup, String selectedText) {
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            RadioButton radioButton = (RadioButton) radioGroup.getChildAt(i);
            if (radioButton.getText().toString().equals(selectedText)) {
                radioButton.setChecked(true);
                break;
            }
        }
    }

    // Method to update the trigger data in Firebase
    private void updateTriggerInFirebase(String newMax, String newMin, String selectedNotificationType, String selectedAlertType) {
        // Check if the user is authenticated
        if (currentUser != null) {
            String currentUserId = currentUser.getUid();

            // Get a reference to the specific trigger you want to update
            DatabaseReference triggerRef = usersRef.child(currentUserId).child("your_trigger_node").child("trigger_id");

            // Create a map to hold the updated trigger properties
            Map<String, Object> updatedProperties = new HashMap<>();
            updatedProperties.put("max", newMax);
            updatedProperties.put("min", newMin);
            updatedProperties.put("notificationType", selectedNotificationType);
            updatedProperties.put("alertType", selectedAlertType);

            // Update the trigger properties in Firebase
            triggerRef.updateChildren(updatedProperties)
                    .addOnSuccessListener(aVoid -> {
                        // Update successful, dismiss the fragment
                        dismiss();
                    })
                    .addOnFailureListener(e -> {
                        // Handle the failure to update (e.g., show an error message)
                        // You can also add logging to help with debugging
                        Log.e("EditTriggerFragment", "Failed to update trigger in Firebase: " + e.getMessage());
                    });
        }
    }
}
