package com.example.pharmacieapplication;

import static com.example.pharmacieapplication.Models.StaticVariable.sendNotification;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.pharmacieapplication.Activities.AddOffreActivity;
import com.example.pharmacieapplication.Adapters.RecyclerMessageAdapter;
import com.example.pharmacieapplication.Adapters.RecyclerOffreAdapter;
import com.example.pharmacieapplication.Models.Client;
import com.example.pharmacieapplication.Models.Message;
import com.example.pharmacieapplication.Models.Notification;
import com.example.pharmacieapplication.Models.Offre;
import com.example.pharmacieapplication.Models.Replay;
import com.example.pharmacieapplication.Models.StaticVariable;
import com.example.pharmacieapplication.custom.MsgPushService;
import com.example.pharmacieapplication.ui.messages.MessagesFragment;
import com.example.pharmacieapplication.ui.offres.OffresFragment;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.pharmacieapplication.databinding.ActivityMainBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private Client client;
    private RequestQueue requestQueue;
    private Intent intentNotif = new Intent(this, MsgPushService.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null)
            client = (Client) bundle.getSerializable("client");
        else
            client = StaticVariable.db.getOneUser();
        if (client == null)
            client = StaticVariable.db.getOneUser();

        StaticVariable.user = client;
        if (StaticVariable.db.getAllNotifs().size() == 0)
            StaticVariable.db.insertNotif(new Notification(1, 0, 0));
//        if (getIntent().getExtras() != null) {
//            for (String key : getIntent().getExtras().keySet()) {
//                String value = getIntent().getExtras().getString(key);
//                Log.d("tag 1", "Key: " + key + " Value: " + value);
//            }
//        }
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();

        if (client.isApproved()) {
            binding = ActivityMainBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());
            setSupportActionBar(binding.appBarMain.toolbar);
            binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });
            DrawerLayout drawer = binding.drawerLayout;
            NavigationView navigationView = binding.navView;
            // Passing each menu ID as a set of Ids because each
            // menu should be considered as top level destinations.
            mAppBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.nav_offres, R.id.nav_messages, R.id.nav_fav)
                    .setOpenableLayout(drawer)
                    .build();
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
            NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
            NavigationUI.setupWithNavController(navigationView, navController);
            Notif notif = new Notif();
            notif.start();
        } else {
            AlertDialog.Builder adb = new AlertDialog.Builder(MainActivity.this);
            adb.setTitle("Information");
            adb.setMessage("Ce compte n'est pas encore Approuvée par l'admin !");
            adb.setPositiveButton("Ok",
                    new AlertDialog.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            return;
                        }
                    }).show();
        }

//        if (intentNotif != null)
//            stopService(intentNotif);
        startService(new Intent(this, MsgPushService.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if (intentNotif != null)
//            stopService(intentNotif);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addOffre:
                if (client.isApproved()) {
                    startActivity(new Intent(MainActivity.this, AddOffreActivity.class));
                } else {
                    AlertDialog.Builder adb = new AlertDialog.Builder(MainActivity.this);
                    adb.setTitle("Information");
                    adb.setMessage("Ce compte n'est pas encore Approuvée par l'admin !");
                    adb.setPositiveButton("Ok",
                            new AlertDialog.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    return;
                                }
                            }).show();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        if (client.isApproved()) {
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
            return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                    || super.onSupportNavigateUp();
        } else {
            AlertDialog.Builder adb = new AlertDialog.Builder(MainActivity.this);
            adb.setTitle("Information");
            adb.setMessage("Ce compte n'est pas encore Approuvée par l'admin !");
            adb.setPositiveButton("Ok",
                    new AlertDialog.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            return;
                        }
                    }).show();
            return false;
        }
    }

    private class Notif extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                getMessages();
                getOffres();
            }
//            getMessages();
//            getOffres();
        }
    }

    private void createNotifications() {
//        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
//        notificationManager.cancel(NOTIFICATION_ID);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            CharSequence name = "foxandroidRinderChannel";
            String description = "Channel for Alarm Manager";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("foxandroid", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            notificationManager.cancelAll();
        } else {
            System.out.println("impossible here !");
        }
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
                            sendNotification(MainActivity.this, 1, compar, "offre");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
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
                    if (compar > 0) {
                        Notification notification = StaticVariable.db.getAllNotifs().get(0);
                        notification.setNbMsg(msgs.length());
                        StaticVariable.db.updateNotifMessage(notification);
                        System.out.println("msgs : " + msgs.length() + " and msgsBD : " + StaticVariable.db.getAllNotifs().get(0).getNbMsg());
                        sendNotification(MainActivity.this, 2, compar, "message");
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
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

    @Override
    protected void onStop() {
        super.onStop();
        startService(new Intent(this, MsgPushService.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        startService(new Intent(this, MsgPushService.class));
    }

    public static boolean isApplicationSentToBackground(final Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }

        return false;
    }
}