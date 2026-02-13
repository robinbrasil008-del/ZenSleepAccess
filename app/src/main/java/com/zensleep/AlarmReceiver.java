package com.zensleep;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent i = new Intent(context, AlarmRingingActivity.class);

        i.putExtra("alarm_id", intent.getIntExtra("alarm_id", -1));
        i.putExtra("alarm_label", intent.getStringExtra("alarm_label"));

        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                   Intent.FLAG_ACTIVITY_CLEAR_TOP);

        context.startActivity(i);
    }
}
