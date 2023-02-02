package com.example.pharmacieapplication.custom;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.pharmacieapplication.MainActivity;
import com.example.pharmacieapplication.R;


public class MyReceiver extends BroadcastReceiver {


    private static final String LOG_TAG = MyReceiver.class.getSimpleName();
    public static final int BROADCAST_NOTIFICATION = 1;


    @Override
    public void onReceive(Context context, Intent mainIntent) {

        sendNotification(context, mainIntent);

    }

    private void sendNotification(Context context, Intent mainIntent) {


        Log.i(LOG_TAG, "unix the method that the broadcast execute now is : sendNotification ");


        int notificationId = mainIntent.getIntExtra("notification_id", -1);
//        int notePosition = mainIntent.getIntExtra("note_position", -1);
//        int folderPosition = mainIntent.getIntExtra("folder_position", -1);
        int nb = mainIntent.getIntExtra("nb", -1);
        int type = mainIntent.getIntExtra("type", -1);
        String noteTitle = "";
        if (type == 1)
            noteTitle = nb + " nouveaux offres";
        else if (type == 2)
            noteTitle = nb + " nouveaux messages";


        Log.i("ContentNoteActivity", ":the notification id is  " + notificationId);
//        Log.i("ContentNoteActivity", "the note position is : " + notePosition);
//        Log.i("ContentNoteActivity", "the folder position is : " + folderPosition);


        Intent notifyIntent = new Intent(context, MainActivity.class);
        notifyIntent.putExtra("notification_id", notificationId);
//        notifyIntent.putExtra("note_position", notePosition);
//        notifyIntent.putExtra("folder_position", folderPosition);
        notifyIntent.setAction("" + Math.random());
        PendingIntent pendingIntent = PendingIntent.getActivity(context,
                notificationId + 100, notifyIntent, PendingIntent.FLAG_CANCEL_CURRENT);


        Notification notification = new NotificationCompat.Builder(context, App.CHANNEL_1_ID)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(context.getString(R.string.notification_content, noteTitle))
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setPriority(Notification.PRIORITY_HIGH)
                .setCategory(Notification.CATEGORY_MESSAGE)
                .setColor(context.getResources().getColor(R.color.white))
                //.setContentIntent(pendingIntent)
                .setSilent(true)
                .build();


        Uri ringtoneUri = Uri.parse(
                ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getPackageName() +
                        "/" + R.raw.notification);

        // to trigger the notification sound.
        Ringtone ringtone = RingtoneManager.getRingtone(context, ringtoneUri);
        ringtone.play();


        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        // TODO: the line below to cancel the previous notification for the same note.
        notificationManager.cancel(notificationId - 1);
        notificationManager.notify(notificationId, notification);

    }


}
