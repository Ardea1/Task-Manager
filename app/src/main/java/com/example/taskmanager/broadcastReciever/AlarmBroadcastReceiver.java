package com.example.taskmanager.broadcastReciever;

import static android.content.Context.NOTIFICATION_SERVICE;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.taskmanager.R;
import com.example.taskmanager.activity.AlarmActivity;
import com.example.taskmanager.activity.MainActivity;
import com.example.taskmanager.bottomSheetFragment.CreateTaskBottom;

// Broadcast Receiver - это механизм для отсылки и получения сообщений в Android
public class AlarmBroadcastReceiver extends BroadcastReceiver {

    String title, desc, date, time;
    AlarmService alarmService;
    private int lastId = 0; //постоянно увеличивающееся поле, уникальный номер каждого уведомления

    @Override
    public void onReceive(Context context, Intent intent) {

        title = intent.getStringExtra("TITLE");
        desc = intent.getStringExtra("DESC");
        date = intent.getStringExtra("DATE");
        time = intent.getStringExtra("TIME");

        Intent i = new Intent(context, AlarmActivity.class);
        i.putExtra("TITLE", title);
        i.putExtra("DESC", desc);
        i.putExtra("DATE", date);
        i.putExtra("TIME", time);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if(intent.getStringExtra("myAction") != null &&
                intent.getStringExtra("myAction").equals("notify")){
            NotificationManager manager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "YOUR_CHANNEL_ID")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("my title")
                    .setContentText("my message")
                    .setOngoing(false)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true);

            PendingIntent pendingIntent = PendingIntent.getActivity(context, lastId, i, PendingIntent.FLAG_MUTABLE);
            builder.setContentIntent(pendingIntent);
            manager.notify(lastId, builder.build());
            lastId++;
        }

        context.startActivity(i);
       Toast.makeText(context, "Будильник", Toast.LENGTH_SHORT).show();


    }
}
