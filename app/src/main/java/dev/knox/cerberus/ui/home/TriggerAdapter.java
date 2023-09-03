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

    public TriggerAdapter(List<Trigger> triggerList) {
        this.triggerList = triggerList;
    }

    @NonNull
    @Override
    public TriggerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.trigger_item, parent, false);
        return new TriggerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TriggerViewHolder holder, int position) {
        Trigger trigger = triggerList.get(position);
        holder.bind(trigger);
    }

    @Override
    public int getItemCount() {
        return triggerList.size();
    }

    public void setTriggerList(List<Trigger> triggerList) {
        this.triggerList = triggerList;
        notifyDataSetChanged(); // Notify the adapter that the data has changed
    }

    // Add a method to set cached trigger data
    public void setCachedTriggerList(List<Trigger> cachedTriggerList) {
        this.triggerList = cachedTriggerList;
        notifyDataSetChanged(); // Notify the adapter that the data has changed
    }

    public class TriggerViewHolder extends RecyclerView.ViewHolder {
        private TextView triggerNameTextView;
        private TextView roomNumberTextView;
        private TextView maxInputTextView;
        private TextView minInputTextView;
        private TextView notificationTypeTextView;
        private TextView alertTypeTextView;

        public TriggerViewHolder(@NonNull View itemView) {
            super(itemView);
            triggerNameTextView = itemView.findViewById(R.id.triggerNameTextView);
            roomNumberTextView = itemView.findViewById(R.id.roomTextView);
            maxInputTextView = itemView.findViewById(R.id.maxTextView);
            minInputTextView = itemView.findViewById(R.id.minTextView);
            notificationTypeTextView = itemView.findViewById(R.id.notificationTypeTextView);
            alertTypeTextView = itemView.findViewById(R.id.alertTypeTextView);
        }

        public void bind(Trigger trigger) {
            triggerNameTextView.setText("Trigger Name: " + trigger.getTriggerName());
            roomNumberTextView.setText("Room Number: " + trigger.getRoomNumber());
            maxInputTextView.setText("Max Input: " + trigger.getMaxInput());
            minInputTextView.setText("Min Input: " + trigger.getMinInput());
            notificationTypeTextView.setText("Notification Type: " + trigger.getNotificationType());
            alertTypeTextView.setText("Alert Type: " + trigger.getAlertType());
        }
    }
}
