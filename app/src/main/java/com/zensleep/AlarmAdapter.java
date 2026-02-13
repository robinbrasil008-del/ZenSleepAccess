package com.zensleep;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.Holder> {

    public interface Listener {
        void onToggle(AlarmItem item, boolean enabled);
        void onDelete(AlarmItem item);
    }

    private final List<AlarmItem> list;
    private final Listener listener;

    public AlarmAdapter(List<AlarmItem> list, Listener listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_alarm, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder h, int position) {
        AlarmItem item = list.get(position);

        h.txtTime.setText(item.timeText());
        h.txtLabel.setText(item.label == null || item.label.trim().isEmpty() ? "Alarme" : item.label);

        h.switchEnabled.setOnCheckedChangeListener(null);
        h.switchEnabled.setChecked(item.enabled);

        h.switchEnabled.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (listener != null) listener.onToggle(item, isChecked);
        });

        h.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDelete(item);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class Holder extends RecyclerView.ViewHolder {

        TextView txtTime, txtLabel, btnDelete;
        Switch switchEnabled;

        Holder(@NonNull View itemView) {
            super(itemView);
            txtTime = itemView.findViewById(R.id.txtTime);
            txtLabel = itemView.findViewById(R.id.txtLabel);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            switchEnabled = itemView.findViewById(R.id.switchEnabled);
        }
    }
}
