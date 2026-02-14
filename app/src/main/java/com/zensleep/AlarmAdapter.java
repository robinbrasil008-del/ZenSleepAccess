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
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_alarm, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder h, int position) {

        AlarmItem item = list.get(position);

        // 🔥 Horário
        h.txtTime.setText(item.timeText());

        // 🔥 Nome
        h.txtLabel.setText(
                item.label == null || item.label.trim().isEmpty()
                        ? "Alarme"
                        : item.label
        );

        // 🔥 Dias da semana
        h.txtDays.setText(formatDays(item.days));

        // 🔥 Som escolhido
        h.txtSound.setText(
                item.soundUri == null || item.soundUri.isEmpty()
                        ? "Som padrão"
                        : "Som personalizado"
        );

        // 🔥 Switch
        h.switchEnabled.setOnCheckedChangeListener(null);
        h.switchEnabled.setChecked(item.enabled);

        h.switchEnabled.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (listener != null) listener.onToggle(item, isChecked);
        });

        // 🔥 Excluir
        h.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDelete(item);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    // 🔥 FORMATA DIAS
    private String formatDays(boolean[] days) {

        if (days == null) return "Uma vez";

        String[] names = {"D", "S", "T", "Q", "Q", "S", "S"};
        StringBuilder sb = new StringBuilder();

        boolean any = false;

        for (int i = 0; i < 7; i++) {
            if (days[i]) {
                sb.append(names[i]).append(" ");
                any = true;
            }
        }

        if (!any) return "Uma vez";

        return sb.toString().trim();
    }

    static class Holder extends RecyclerView.ViewHolder {

        TextView txtTime, txtLabel, txtDays, txtSound, btnDelete;
        Switch switchEnabled;

        Holder(@NonNull View itemView) {
            super(itemView);

            txtTime = itemView.findViewById(R.id.txtTime);
            txtLabel = itemView.findViewById(R.id.txtLabel);
            txtDays = itemView.findViewById(R.id.txtDays);
            txtSound = itemView.findViewById(R.id.txtSound);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            switchEnabled = itemView.findViewById(R.id.switchEnabled);
        }
    }
}
