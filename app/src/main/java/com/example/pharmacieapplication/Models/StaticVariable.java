package com.example.pharmacieapplication.Models;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

import com.example.pharmacieapplication.DBLocal.DatabaseHalper;
import com.example.pharmacieapplication.R;
import com.example.pharmacieapplication.custom.MsgPushService;
import com.example.pharmacieapplication.custom.MyReceiver;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;

public class StaticVariable {

    public static final int IMG_REQUEST_GALLERY = 1;
    public static final int IMG_REQUEST_CAMERA = 2;
    public static DatabaseHalper db;
    public static int ok = 0;
    public static Client user;

    public static String toArabic(String txt) {
        try {
            return new String(txt.getBytes(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void sendNotification(Activity activity, int type, int nb, String title) {
        //String LOG_TAG = activity.getClass().getSimpleName();
        Toast.makeText(activity, "in send ...", Toast.LENGTH_SHORT).show();
        Intent notifyIntent = new Intent(activity, MyReceiver.class);
        Toast.makeText(activity, "in send 0 ...", Toast.LENGTH_SHORT).show();
        notifyIntent.putExtra("broadcast_type", MyReceiver.BROADCAST_NOTIFICATION);

        Toast.makeText(activity, "in send 1...", Toast.LENGTH_SHORT).show();
        int folderPosition = activity.getIntent().getIntExtra("folder_position", -1);
        int notePosition = activity.getIntent().getIntExtra("note_position", -1);
        int notificationId = type;
        System.out.println("type is ; " + type);
        Log.i("LOG_TAG", "unix the note id is : " + notePosition + "   and the notification id is : " + notificationId);
        Log.i("LOG_TAG", "unix the note title is : " + title);

        Toast.makeText(activity, "in send 2 ...", Toast.LENGTH_SHORT).show();
        notifyIntent.putExtra("notification_id", notificationId);
        notifyIntent.putExtra("note_position", notePosition);
        notifyIntent.putExtra("folder_position", folderPosition);
        notifyIntent.putExtra("type", type);
        notifyIntent.putExtra("nb", nb);
        notifyIntent.putExtra("note_title", title);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(activity, notificationId, notifyIntent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);

        Toast.makeText(activity, "in send 3 ...", Toast.LENGTH_SHORT).show();
        // TODO: create a new variable to store the sum result before insert it inside the method
        long time = (Calendar.getInstance().getTimeInMillis());
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, time, pendingIntent);
        Toast.makeText(activity, "in send 4 ...", Toast.LENGTH_SHORT).show();
    }
}
