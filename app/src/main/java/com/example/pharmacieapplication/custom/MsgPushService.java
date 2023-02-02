package com.example.pharmacieapplication.custom;

import static com.example.pharmacieapplication.Models.StaticVariable.db;
import static com.example.pharmacieapplication.Models.StaticVariable.sendNotification;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.pharmacieapplication.DBLocal.DatabaseHalper;
import com.example.pharmacieapplication.MainActivity;
import com.example.pharmacieapplication.Models.Notification;
import com.example.pharmacieapplication.Models.StaticVariable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class MsgPushService extends Service {

    Timer timer;
    TimerTask timerTask;
    String TAG = "Timers";
    int Your_X_SECS = 5;
    //we are going to use a handler to be able to run in our TimerTask
    final Handler handler = new Handler();
    private RequestQueue requestQueue;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
        StaticVariable.db = new DatabaseHalper(getApplicationContext());
        startTimer();
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        stoptimertask();
        Toast.makeText(this, "Service Destroy", Toast.LENGTH_LONG).show();
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }


    public void startTimer() {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, after the first 5000ms the TimerTask will run every 10000ms
        timer.schedule(timerTask, 5000, Your_X_SECS * 1000); //
        //timer.schedule(timerTask, 5000,1000); //
    }

    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public void initializeTimerTask() {

        timerTask = new TimerTask() {
            public void run() {

                //use a handler to run a toast that shows the current timestamp
                handler.post(new Runnable() {
                    public void run() {

                        //TODO CALL NOTIFICATION FUNC
                        //Toast.makeText(MsgPushService.this, "test test !!", Toast.LENGTH_SHORT).show();
                        getMessages();
                        getOffres();
                    }
                });
            }
        };
    }

    private void getOffres() {
        String JSON_URL = "https://holoola-z.com/projects/Pharmacie_Offers/php/offers/";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, JSON_URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject res = new JSONObject(response.toString());
                    if (res.getString("code").equals("200")) {
                        JSONArray data = response.getJSONArray("data");
                        int compar = data.length() - StaticVariable.db.getAllNotifs().get(0).getNbOffre();
                        if (compar > 0) {
                            Notification notification = StaticVariable.db.getAllNotifs().get(0);
                            notification.setNbOffre(data.length());
                            StaticVariable.db.updateNotifOffre(notification);
                            sendNotification(MsgPushService.this, 1, compar, "offre");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(MsgPushService.this, "err offre " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("errrrrrrrrrrrrrr " + error.getMessage());
            }
        });
        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }

    private void getMessages() {
        String JSON_URL = "https://holoola-z.com/projects/Pharmacie_Offers/php/messages/?username=" + StaticVariable.db.getOneUser().getUserName()
                + "&unread=false";
        //RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        //CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, JSON_URL, params, this.createRequestSuccessListener(), this.createRequestErrorListener());
        //requestQueue.add(jsObjRequest);
        StringRequest request = new StringRequest(Request.Method.GET, JSON_URL, response -> {
            Log.i("VOLLEY RESPONSE ", "response : " + response);
            try {
                JSONObject jsonObject = new JSONObject(response);
                Log.i("VOLLEY RESPONSE ", "message : " + jsonObject.get("message"));
                Log.i("VOLLEY RESPONSE ", "code : " + jsonObject.getString("code"));
                if (jsonObject.getString("code").equals("200")) {
                    //JSONObject j = new JSONObject(jsonObject.getJSONArray("messages"));
                    JSONObject j = new JSONObject(jsonObject.getString("messages"));
                    JSONArray msgs = new JSONArray(j.getString("inbox"));
                    int compar = msgs.length() - StaticVariable.db.getAllNotifs().get(0).getNbMsg();
                    System.out.println("msgs : " + msgs.length() + " and msgsBD : " + StaticVariable.db.getAllNotifs().get(0).getNbMsg());
                    //Toast.makeText(MsgPushService.this, "msgs : " + msgs.length() + " and msgsBD : " + StaticVariable.db.getAllNotifs().get(0).getNbMsg(), Toast.LENGTH_SHORT).show();
                    if (compar > 0) {
                        Notification notification = StaticVariable.db.getAllNotifs().get(0);
                        notification.setNbMsg(msgs.length());
                        StaticVariable.db.updateNotifMessage(notification);
                        System.out.println("msgs : " + msgs.length() + " and msgsBD : " + StaticVariable.db.getAllNotifs().get(0).getNbMsg());
                        sendNotification(this, 2, compar, "message");
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "err " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            //message.getText().clear();
            //Toast.makeText(AddOffreActivity.this, "Insription terminé avec succès !", Toast.LENGTH_SHORT).show();
        }, error ->

        {
            Log.e("VOLLEY ERROR : ", "message : " + error.networkResponse);
            error.printStackTrace();
            //Toast.makeText(AddOffreActivity.this, "Error !! ", Toast.LENGTH_SHORT).show();
        });
        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }

    public void sendNotification(MsgPushService activity, int type, int nb, String title) {
        //String LOG_TAG = activity.getClass().getSimpleName();
        Intent notifyIntent = new Intent(activity, MyReceiver.class);
        notifyIntent.putExtra("broadcast_type", MyReceiver.BROADCAST_NOTIFICATION);

//        int folderPosition = activity.getIntent().getIntExtra("folder_position", -1);
//        int notePosition = activity.getIntent().getIntExtra("note_position", -1);
        int notificationId = type;
//        Log.i("LOG_TAG", "unix the note id is : " + notePosition + "   and the notification id is : " + notificationId);
        Log.i("LOG_TAG", "unix the note title is : " + title);

        notifyIntent.putExtra("notification_id", notificationId);
//        notifyIntent.putExtra("note_position", notePosition);
//        notifyIntent.putExtra("folder_position", folderPosition);
        notifyIntent.putExtra("type", type);
        notifyIntent.putExtra("nb", nb);
        notifyIntent.putExtra("note_title", title);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(activity, notificationId, notifyIntent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
        // TODO: create a new variable to store the sum result before insert it inside the method
        long time = (Calendar.getInstance().getTimeInMillis());
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, time, pendingIntent);
    }
}
