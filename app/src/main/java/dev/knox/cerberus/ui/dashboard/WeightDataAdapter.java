package dev.knox.cerberus.ui.dashboard;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import dev.knox.cerberus.R;

public class WeightDataAdapter extends RecyclerView.Adapter<WeightDataAdapter.ViewHolder> {
    private List<Double> weightDataList; // A single list containing both room 1 and room 2 weights

    public WeightDataAdapter(List<Double> weightDataList) {
        this.weightDataList = weightDataList;
    }

    // Add this method to set the weight data
    public void setWeightDataList(List<Double> weightDataList) {
        this.weightDataList = weightDataList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate your list item layout here
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.weight_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Double weight = weightDataList.get(position);
        // Bind weight data to UI elements in the list item
        holder.room1WeightTextView.setText("Room 1 Weight: " + weight);
        holder.room2WeightTextView.setText("Room 2 Weight: " + weight);

        // Log the weight for debugging
        Log.d("WeightDataAdapter", "Weight at position " + position + ": " + weight);
    }

    @Override
    public int getItemCount() {
        return weightDataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView room1WeightTextView;
        TextView room2WeightTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            room1WeightTextView = itemView.findViewById(R.id.room1WeightTextView);
            room2WeightTextView = itemView.findViewById(R.id.room2WeightTextView);
        }
    }
}
