package dev.knox.cerberus.ui.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.ValueEventListener;

import dev.knox.cerberus.R;

public class AddPopupFragment extends DialogFragment {

    private View rootView;
    private EditText maxInput;
    private EditText minInput;
    private Spinner roomSpinner;
    private RadioGroup notificationTypeRadioGroup;
    private RadioGroup alertTypeRadioGroup;

    private DatabaseReference usersRef;
    private FirebaseUser currentUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_add_popup, container, false);

        // Initialize UI elements
        maxInput = rootView.findViewById(R.id.maxInput);
        minInput = rootView.findViewById(R.id.minInput);
        roomSpinner = rootView.findViewById(R.id.roomSpinner);
        notificationTypeRadioGroup = rootView.findViewById(R.id.notificationTypeRadioGroup);
        alertTypeRadioGroup = rootView.findViewById(R.id.alertTypeRadioGroup);
        Button testButton = rootView.findViewById(R.id.testButton);
        Button saveButton = rootView.findViewById(R.id.saveButton);

        // Create an input filter to restrict input to the range 0-100
        InputFilter filter = new InputFilter() {
            final int min = 0;
            final int max = 100;

            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                try {
                    String newValue = dest.subSequence(0, dstart) + source.toString() + dest.subSequence(dend, dest.length());
                    int input = Integer.parseInt(newValue);
                    if (isInRange(min, max, input)) {
                        return null;
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                return "";
            }

            private boolean isInRange(int a, int b, int c) {
                return b > a ? c >= a && c <= b : c >= b && c <= a;
            }
        };

        // Apply the input filter to both maxInput and minInput
        maxInput.setFilters(new InputFilter[]{filter});
        minInput.setFilters(new InputFilter[]{filter});

        // Find the Spinner view
        roomSpinner = rootView.findViewById(R.id.roomSpinner);

        // Create an ArrayAdapter using a string array of room options and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.room_options, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        roomSpinner.setAdapter(adapter);

        // Initialize Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            // Get the user's UID
            String uid = currentUser.getUid();
            // Set the database reference to the user's triggers
            usersRef = database.getReference("users").child(uid);
        }

        // Add change listeners to the radio groups to enable/disable buttons
        notificationTypeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            updateButtonStatus();
        });

        alertTypeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            updateButtonStatus();
        });

        // Initially, disable the "Save" and "Test" buttons
        saveButton.setEnabled(false);
        testButton.setEnabled(false);

        // Add text change listeners to the input fields to enable/disable buttons
        addTextChangeListeners(maxInput);
        addTextChangeListeners(minInput);

        roomSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateButtonStatus();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Add click listeners to the buttons
        testButton.setOnClickListener(v -> dismiss());
        saveButton.setOnClickListener(v -> handleSaveButtonClick());

        return rootView;
    }

    private void addTextChangeListeners(EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateButtonStatus();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void handleSaveButtonClick() {
        // Get the input values as strings
        String max = maxInput.getText().toString().trim(); // Trim to remove leading/trailing whitespace
        String min = minInput.getText().toString().trim(); // Trim to remove leading/trailing whitespace
        String selectedRoom = roomSpinner.getSelectedItem().toString();
        String selectedNotificationType = getSelectedNotificationType();
        String selectedAlertType = getSelectedAlertType();

        // Check if any of the fields are empty
        if (max.isEmpty() || min.isEmpty() || selectedRoom.isEmpty()) {
            Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if the user is authenticated
        if (currentUser != null) {
            String currentUserId = currentUser.getUid();

            // Create a reference to the room's triggers for the current user in Firebase
            DatabaseReference roomTriggersRef = usersRef.child(selectedRoom);

            // Parse max and min to integers, and handle any parsing errors
            int maxInt, minInt;
            try {
                maxInt = Integer.parseInt(max);
                minInt = Integer.parseInt(min);
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Please enter valid numbers for max and min", Toast.LENGTH_SHORT).show();
                return;
            }

            // Query the database to check the number of existing triggers for the selected room
            roomTriggersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    int numberOfTriggers = (int) dataSnapshot.getChildrenCount();
                    if (numberOfTriggers < 3) {
                        // If the room has fewer than 3 triggers, create a new one
                        int nextTriggerNumber = numberOfTriggers + 1;
                        String newTriggerKey = "trigger" + nextTriggerNumber;
                        DatabaseReference newTriggerRef = roomTriggersRef.child(newTriggerKey);
                        Trigger newTrigger = new Trigger(newTriggerKey, selectedRoom, String.valueOf(maxInt), String.valueOf(minInt), selectedNotificationType, selectedAlertType);

                        // Save to Firebase under the selected room
                        newTriggerRef.setValue(newTrigger);

                        // Save to Local Cache
                        saveTriggerToLocalCache(newTrigger);

                        // Now, you have successfully added the new trigger with the next trigger number for the current user and room
                        Toast.makeText(getContext(), "Trigger added successfully: " + newTriggerKey, Toast.LENGTH_SHORT).show();
                        dismiss(); // Close the dialog
                    } else {
                        // The selected room already has 3 triggers, show a message indicating the limit
                        Toast.makeText(getContext(), "Maximum number of triggers (3) reached for the selected room.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle errors here
                }
            });
        } else {
            // Handle the case where the user is not authenticated
            Toast.makeText(getContext(), "User is not authenticated", Toast.LENGTH_SHORT).show();
        }
    }

    private String getSelectedNotificationType() {
        // Determine the selected notification type based on the RadioGroup
        int selectedId = notificationTypeRadioGroup.getCheckedRadioButtonId();
        RadioButton radioButton = rootView.findViewById(selectedId);
        return radioButton.getText().toString();
    }

    private String getSelectedAlertType() {
        // Determine the selected alert type based on the RadioGroup
        int selectedId = alertTypeRadioGroup.getCheckedRadioButtonId();
        RadioButton radioButton = rootView.findViewById(selectedId);
        return radioButton.getText().toString();
    }

    // Method to update the button status based on input field values and radio button selections
    private void updateButtonStatus() {
        String max = maxInput.getText().toString().trim();
        String min = minInput.getText().toString().trim();
        String selectedRoom = roomSpinner.getSelectedItem().toString();

        // Check if any of the radio buttons in both groups are selected
        boolean isNotificationTypeSelected = notificationTypeRadioGroup.getCheckedRadioButtonId() != -1;
        boolean isAlertTypeSelected = alertTypeRadioGroup.getCheckedRadioButtonId() != -1;

        // Enable the buttons if all fields are filled and at least one radio button in each group is selected, otherwise disable them
        boolean enableButtons = !max.isEmpty() && !min.isEmpty() && !selectedRoom.isEmpty() && isNotificationTypeSelected && isAlertTypeSelected;
        rootView.findViewById(R.id.saveButton).setEnabled(enableButtons);
        rootView.findViewById(R.id.testButton).setEnabled(enableButtons);
    }

    // Method to save trigger data to local cache
    private void saveTriggerToLocalCache(Trigger trigger) {
        // Get the SharedPreferences instance
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("TriggerData", Context.MODE_PRIVATE);

        // Create an editor to modify SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Store the trigger data as key-value pairs
        editor.putString("room", trigger.getRoomNumber());
        editor.putString("max", trigger.getMaxInput());
        editor.putString("min", trigger.getMinInput());
        editor.putString("notificationType", trigger.getNotificationType());
        editor.putString("alertType", trigger.getAlertType());

        // Apply changes
        editor.apply();
    }
}
