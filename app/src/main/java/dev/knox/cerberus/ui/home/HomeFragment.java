package dev.knox.cerberus.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

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
        adapter = new TriggerAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        addButton.setOnClickListener(v -> {
            AddPopupFragment popupFragment = new AddPopupFragment();
            popupFragment.show(getParentFragmentManager(), "add_popup_fragment");
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
                            triggerList.add(trigger);
                        }
                    }

                    // Check if "Room 2" exists in Firebase
                    if (dataSnapshot.hasChild("Room 2")) {
                        DataSnapshot room2Snapshot = dataSnapshot.child("Room 2");
                        for (DataSnapshot triggerSnapshot : room2Snapshot.getChildren()) {
                            Trigger trigger = triggerSnapshot.getValue(Trigger.class);
                            triggerList.add(trigger);
                        }
                    }

                    // Update the adapter with the new data
                    adapter.setTriggerList(triggerList);

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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
