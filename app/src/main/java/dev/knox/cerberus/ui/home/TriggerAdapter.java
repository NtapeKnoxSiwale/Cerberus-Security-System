package dev.knox.cerberus.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import dev.knox.cerberus.R;

public class TriggerAdapter extends RecyclerView.Adapter<TriggerAdapter.TriggerViewHolder> {

    private List<Trigger> triggerList;
    private HomeFragment homeFragment;

    public TriggerAdapter(List<Trigger> triggerList, HomeFragment homeFragment) {
        this.triggerList = triggerList;
        this.homeFragment = homeFragment;
    }

    public void updateTriggerList(List<Trigger> updatedList) {
        triggerList.clear();
        triggerList.addAll(updatedList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TriggerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trigger_item, parent, false);
        return new TriggerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TriggerViewHolder holder, int position) {
        Trigger trigger = triggerList.get(position);
        holder.bind(trigger);
    }

    public void setTriggerList(List<Trigger>triggerList){
        this.triggerList = triggerList;
        // Notify the adapter that the data has changed
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return triggerList.size();
    }

    public class TriggerViewHolder extends RecyclerView.ViewHolder {
        private final TextView triggerNameTextView;
        private final TextView roomNumberTextView;
        private final TextView maxTextView;
        private final TextView minTextView;
        private final TextView notificationTypeTextView;
        private final TextView alertTypeTextView;

        public TriggerViewHolder(@NonNull View itemView) {
            super(itemView);
            triggerNameTextView = itemView.findViewById(R.id.triggerNameTextView);
            roomNumberTextView = itemView.findViewById(R.id.roomTextView);
            maxTextView = itemView.findViewById(R.id.maxTextView);
            minTextView = itemView.findViewById(R.id.minTextView);
            notificationTypeTextView = itemView.findViewById(R.id.notificationTypeTextView);
            alertTypeTextView = itemView.findViewById(R.id.alertTypeTextView);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Trigger trigger = triggerList.get(position);
                    homeFragment.onTriggerCardClick(trigger);
                }
            });
        }

        public void bind(Trigger trigger) {
            triggerNameTextView.setText("Trigger Name: " + trigger.getTriggerName());
            roomNumberTextView.setText("Room: " + trigger.getRoomNumber());
            maxTextView.setText("Max Input: " + trigger.getMaxInput());
            minTextView.setText("Min Input: " + trigger.getMinInput());
            notificationTypeTextView.setText("Notification: " + trigger.getNotificationType());
            alertTypeTextView.setText("Alert: " + trigger.getAlertType());
        }
    }
}
