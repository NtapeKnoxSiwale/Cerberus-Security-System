package dev.knox.cerberus.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import dev.knox.cerberus.R;
import dev.knox.cerberus.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private RecyclerView recyclerView;
    private TriggerAdapter adapter;
    private List<Trigger> cachedTriggerList; // Store cached data here

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        recyclerView = root.findViewById(R.id.triggersRecyclerView);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button addButton = view.findViewById(R.id.add_trigger);

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TriggerAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(adapter);

        addButton.setOnClickListener(v -> {
            if (isMaxTriggersReached()) {
                addButton.setEnabled(false);
                Toast.makeText(getContext(), "Maximum number of triggers reached for the selected room.", Toast.LENGTH_SHORT).show();
            } else {
                AddPopupFragment popupFragment = new AddPopupFragment();
                popupFragment.show(getParentFragmentManager(), "add_popup_fragment");
            }
        });

        // Read data from Firebase and update the adapter
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            // Get the UID of the currently logged-in user
            String uid = currentUser.getUid();

            // Reference to the user's rooms
            DatabaseReference userRoomsRef = FirebaseDatabase.getInstance().getReference("users").child(uid);

            userRoomsRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    List<Trigger> triggerList = new ArrayList<>();

                    // Check if "Room 1" exists in Firebase
                    if (dataSnapshot.hasChild("Room 1")) {
                        DataSnapshot room1Snapshot = dataSnapshot.child("Room 1");
                        for (DataSnapshot triggerSnapshot : room1Snapshot.getChildren()) {
                            Trigger trigger = triggerSnapshot.getValue(Trigger.class);

                            // Retrieve and set the trigger name from Firebase
                            String triggerName = triggerSnapshot.child("triggerName").getValue(String.class);
                            trigger.setTriggerName(triggerName);

                            triggerList.add(trigger);
                        }
                    }

                    // Check if "Room 2" exists in Firebase
                    if (dataSnapshot.hasChild("Room 2")) {
                        DataSnapshot room2Snapshot = dataSnapshot.child("Room 2");
                        for (DataSnapshot triggerSnapshot : room2Snapshot.getChildren()) {
                            Trigger trigger = triggerSnapshot.getValue(Trigger.class);

                            // Retrieve and set the trigger name from Firebase
                            String triggerName = triggerSnapshot.child("triggerName").getValue(String.class);
                            trigger.setTriggerName(triggerName);

                            triggerList.add(trigger);
                        }
                    }

                    // Update the adapter with the new data
                    adapter.updateTriggerList(triggerList);

                    // Store the data in the cache
                    cachedTriggerList = triggerList;
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle read data error
                }
            });
        }
    }

    // Add this method to check if the maximum number of triggers is reached for the selected room
    private boolean isMaxTriggersReached() {
        if (cachedTriggerList != null) {
            // Determine the maximum limit (e.g., 3 triggers per room)
            int maxLimitPerRoom = 3;

            // Count the triggers in the selected room
            String selectedRoom = getSelectedRoom(); // Implement this method to get the selected room
            int triggersInSelectedRoom = countTriggersInRoom(selectedRoom);

            // Check if the maximum limit is reached
            return triggersInSelectedRoom >= maxLimitPerRoom;
        }
        return false;
    }

    // Implement this method to get the selected room from your UI
    private String getSelectedRoom() {
        // Replace this with the logic to get the selected room from your UI
        return "Room 1"; // Example: Replace with your actual logic
    }

    // Add this method to count triggers in the selected room
    private int countTriggersInRoom(String roomName) {
        int count = 0;
        if (cachedTriggerList != null) {
            for (Trigger trigger : cachedTriggerList) {
                if (trigger.getRoomNumber().equals(roomName)) {
                    count++;
                }
            }
        }
        return count;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // Implement the onTriggerCardClick method to open the EditTriggerFragment
    public void onTriggerCardClick(Trigger trigger) {
        // Log a message to confirm that the method is being called
        Log.d("HomeFragment", "Trigger card clicked");

        // Create a bundle to pass trigger data to the EditTriggerFragment
        Bundle args = new Bundle();
        args.putString("max", trigger.getMaxInput());
        args.putString("min", trigger.getMinInput());
        args.putString("notificationType", trigger.getNotificationType());
        args.putString("alertType", trigger.getAlertType());

        EditTriggerFragment editTriggerFragment = new EditTriggerFragment();
        editTriggerFragment.setArguments(args);

        // Show the EditTriggerFragment
        editTriggerFragment.show(getParentFragmentManager(), "edit_trigger_fragment");
    }
}
