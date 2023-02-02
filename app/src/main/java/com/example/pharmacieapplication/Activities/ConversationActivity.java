package com.example.pharmacieapplication.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.pharmacieapplication.Adapters.RecyclerConversationAdapter;
import com.example.pharmacieapplication.Models.Message;
import com.example.pharmacieapplication.Models.Replay;
import com.example.pharmacieapplication.Models.StaticVariable;
import com.example.pharmacieapplication.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class ConversationActivity extends AppCompatActivity {

    EditText txtMsg;
    ImageView sendMsg;
    RecyclerView recyclerConversation;
    Message message;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        txtMsg = findViewById(R.id.txtMsg);
        sendMsg = findViewById(R.id.sendMsg);
        recyclerConversation = findViewById(R.id.conversation);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        message = (Message) bundle.getSerializable("message");
        getSupportActionBar().setTitle(message.getSubject());

        txtMsg.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (txtMsg.getText().toString().length() == 0)
                    sendMsg.setVisibility(View.GONE);
                else
                    sendMsg.setVisibility(View.VISIBLE);
                return false;
            }
        });
        sendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage4(txtMsg.getText().toString());
            }
        });
        getReplays();
    }

    private void getReplays() {
        String JSON_URL = "https://holoola-z.com/projects/Pharmacie_Offers/php/messages/message?msgId=" + message.getId();
        StringRequest request = new StringRequest(Request.Method.GET, JSON_URL, response -> {
            Log.i("VOLLEY RESPONSE ", "response : " + response);
            try {
                JSONObject jsonObject = new JSONObject(response);
                Log.i("VOLLEY RESPONSE ", "message : " + jsonObject.get("message"));
                Log.i("VOLLEY RESPONSE ", "code : " + jsonObject.getString("code"));
                if (jsonObject.getString("code").equals("200")) {
                    //JSONObject j = new JSONObject(jsonObject.getJSONArray("messages"));
                    JSONArray replies = new JSONArray(jsonObject.getString("replies"));
                    for (int i = 0; i < replies.length(); i++) {
                        JSONObject rep = new JSONObject(String.valueOf(replies.getJSONObject(i)));
                        Replay replay = new Replay();
                        replay.setIdMsg(rep.getInt("msgId"));
                        replay.setReplay(rep.getString("reply"));
                        replay.setReciverEmail(rep.getString("replierEmail"));
                        message.getReplays().add(replay);
                    }
                    LinearLayoutManager llm = new LinearLayoutManager(this);
                    llm.setOrientation(LinearLayoutManager.VERTICAL);
                    RecyclerConversationAdapter recyclerMessageAdapter = new RecyclerConversationAdapter(this, message.getReplays(), "");
                    recyclerConversation.setLayoutManager(new LinearLayoutManager(this));
                    recyclerConversation.setLayoutManager(llm);
                    recyclerConversation.setAdapter(recyclerMessageAdapter);
                    recyclerConversation.scrollToPosition(message.getReplays().size() - 1);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //message.getText().clear();
            //Toast.makeText(AddOffreActivity.this, "Insription terminé avec succès !", Toast.LENGTH_SHORT).show();
        }, error ->

        {
            System.out.println("errrrrrrrrrrr " + error.getMessage());
            Log.e("VOLLEY ERROR : ", "message : " + error.networkResponse);
            error.printStackTrace();
            AlertDialog.Builder adb = new AlertDialog.Builder(this);
            adb.setTitle("Erreur");
            adb.setMessage("Pas de connexion !");
            adb.setPositiveButton("Ok",
                    new AlertDialog.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }).show();
            //Toast.makeText(AddOffreActivity.this, "Error !! ", Toast.LENGTH_SHORT).show();
        });
        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }

    void sendMessage4(String body) {
        try {
            body = new String(body.getBytes(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Replay replay = new Replay();
        replay.setReplay(body);
        replay.setSenderEmail(StaticVariable.user.getEmail());
        replay.setIdMsg(message.getId());
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String URL = "https://holoola-z.com/projects/Pharmacie_Offers/php/messages/reply";
        String finalBody = body;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, response -> {
            Log.i("VOLLEY RESPONSE ", "response : " + response);
            try {
                JSONObject res = new JSONObject(response);
                Log.i("VOLLEY RESPONSE : ", "message : " + res.getString("message"));
                Log.i("VOLLEY RESPONSE : ", " replay code : " + res.getString("code"));
                if (res.getString("code").equals("200"))
                    txtMsg.getText().clear();
                message.getReplays().add(replay);
                recyclerConversation.getAdapter().notifyDataSetChanged();
                recyclerConversation.scrollToPosition(message.getReplays().size() - 1);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //Toast.makeText(ConversationActivity.this, "Message envoyé avec succès !", Toast.LENGTH_SHORT).show();
        }, error -> {
            System.out.println("errrrrrrrrrrr " + error.getMessage());
            error.printStackTrace();
            Toast.makeText(ConversationActivity.this, "Error !! ", Toast.LENGTH_SHORT).show();
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<>();
                parameters.put("msgId", String.valueOf(message.getId()));
                parameters.put("username", "haloce.dark@gmail.com");//
                parameters.put("reply", finalBody);
                return parameters;
            }
        };
        requestQueue.add(stringRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem item = menu.findItem(R.id.addOffre);
        item.setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }
}