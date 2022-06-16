package com.example.taskmanager.broadcastReciever;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;


import com.example.taskmanager.R;
import com.example.taskmanager.activity.AlarmActivity;

// Broadcast Receiver - это механизм для отсылки и получения сообщений в Android
public class AlarmBroadcastReceiver extends BroadcastReceiver {

    String title2, desc, date, time;
    @Override
    public void onReceive(Context context, Intent intent) {

        title2 = intent.getStringExtra("TITLE");
        desc = intent.getStringExtra("DESC");
        date = intent.getStringExtra("DATE");
        time = intent.getStringExtra("TIME");


        Intent i = new Intent(context, AlarmActivity.class);
        i.putExtra("TITLE", title2);
        i.putExtra("DESC", desc);
        i.putExtra("DATE", date);
        i.putExtra("TIME", time);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }
}