package dev.knox.cerberus.ui.home;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import dev.knox.cerberus.R;

public class EditTriggerFragment extends DialogFragment {
    private View rootView;
    private EditText maxInput;
    private EditText minInput;
    private TextView triggerIdText;
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
        triggerIdText = rootView.findViewById(R.id.triggerId);
        notificationTypeRadioGroup = rootView.findViewById(R.id.editNotificationTypeRadioGroup);
        alertTypeRadioGroup = rootView.findViewById(R.id.editAlertTypeRadioGroup);

        Button saveButton = rootView.findViewById(R.id.saveEditButton);
        Button deleteButton = rootView.findViewById(R.id.deleteButton);

        // Retrieve trigger properties from arguments
        Bundle args = getArguments();
        if (args != null) {
            String initialMax = args.getString("max");
            String initialMin = args.getString("min");
            String initialNotificationType = args.getString("notificationType");
            String initialAlertType = args.getString("alertType");
            String roomNode = args.getString("triggerId");
            String triggerId = args.getString("triggerNode");

            // Check for null values before populating the UI
            if (initialMax != null) {
                maxInput.setText(initialMax);
            }
            if (initialMin != null) {
                minInput.setText(initialMin);
            }
            if (initialNotificationType != null) {
                setRadioGroupSelection(notificationTypeRadioGroup, initialNotificationType);
            }
            if (initialAlertType != null) {
                setRadioGroupSelection(alertTypeRadioGroup, initialAlertType);
            }
            if (triggerId != null) {
                triggerIdText.setText(triggerId);
            }

            saveButton.setOnClickListener(v -> {
                String newMax = maxInput.getText().toString();
                String newMin = minInput.getText().toString();
                String selectedNotificationType = getSelectedRadioText(notificationTypeRadioGroup);
                String selectedAlertType = getSelectedRadioText(alertTypeRadioGroup);

                if (updateTriggerInFirebase(roomNode, triggerId, newMax, newMin, selectedNotificationType, selectedAlertType)) {
                    // Update successful, dismiss the fragment
                    dismiss();
                } else {
                    // Handle the failure to update (e.g., show an error message)
                    Toast.makeText(getContext(), "Failed to update trigger.", Toast.LENGTH_SHORT).show();
                }
            });

            deleteButton.setOnClickListener(v -> {
                // Confirm the user wants to delete the trigger
                new AlertDialog.Builder(requireContext())
                        .setTitle("Confirm Deletion")
                        .setMessage("Are you sure you want to delete this trigger?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            // Call a method to delete the trigger from Firebase
                            if (deleteTriggerFromFirebase(roomNode, triggerId)) {
                                // Deletion successful, dismiss the fragment
                                dismiss();
                            } else {
                                // Handle deletion failure (e.g., show an error message)
                                Toast.makeText(getContext(), "Failed to delete trigger.", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("No", (dialog, which) -> {
                            // User canceled the deletion, do nothing
                        })
                        .show();
            });

        }

        // Initialize Firebase references
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            usersRef = FirebaseDatabase.getInstance().getReference("users");
        }

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

    // Method to update the trigger data in Firebase and return success status
    private boolean updateTriggerInFirebase(String roomNode, String triggerId, String newMax, String newMin, String selectedNotificationType, String selectedAlertType) {
//        // Log values for debugging
//        Log.d("EditTriggerFragment", "updateTriggerInFirebase: currentUser = " + currentUser);
//        Log.d("EditTriggerFragment", "updateTriggerInFirebase: roomNode = " + roomNode);
//        Log.d("EditTriggerFragment", "updateTriggerInFirebase: triggerId = " + triggerId);
        // Check if the user is authenticated
        if (currentUser != null) {
            DatabaseReference triggerRef = usersRef.child(currentUser.getUid()).child(roomNode).child(triggerId);

            // Create a map to hold the updated trigger properties
            Map<String, Object> updatedProperties = new HashMap<>();
            updatedProperties.put("maxInput", newMax);
            updatedProperties.put("minInput", newMin);
            updatedProperties.put("notificationType", selectedNotificationType);
            updatedProperties.put("alertType", selectedAlertType);

            try {
                triggerRef.updateChildren(updatedProperties).addOnSuccessListener(aVoid -> {
                    // Update successful
                }).addOnFailureListener(e -> {
                    // Handle the failure to update
                    Log.e("EditTriggerFragment", "Failed to update trigger in Firebase: " + e.getMessage());
                });
                return true;
            } catch (Exception e) {
                // Handle any exceptions
                Log.e("EditTriggerFragment", "Exception: " + e.getMessage());
            }
        }
        return false;
    }

    private boolean deleteTriggerFromFirebase(String roomNode, String triggerId) {
//        // Log values for debugging
//        Log.d("EditTriggerFragment", "deleteTriggerFromFirebase: currentUser = " + currentUser);
//        Log.d("EditTriggerFragment", "deleteTriggerFromFirebase: roomNode = " + roomNode);
//        Log.d("EditTriggerFragment", "deleteTriggerFromFirebase: triggerId = " + triggerId);
        // Check if the user is authenticated
        if (currentUser != null) {
            DatabaseReference triggerRef = usersRef.child(currentUser.getUid()).child(roomNode).child(triggerId);

            try {
                triggerRef.removeValue().addOnSuccessListener(aVoid -> {
                    // Deletion successful
                }).addOnFailureListener(e -> {
                    // Handle the failure to delete
                    Log.e("EditTriggerFragment", "Failed to delete trigger in Firebase: " + e.getMessage());
                });
                return true;
            } catch (Exception e) {
                // Handle any exceptions
                Log.e("EditTriggerFragment", "Exception: " + e.getMessage());
            }
        }
        return false;
    }

}
