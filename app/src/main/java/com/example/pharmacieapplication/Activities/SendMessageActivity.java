package com.example.pharmacieapplication.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.pharmacieapplication.Models.StaticVariable;
import com.example.pharmacieapplication.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class SendMessageActivity extends AppCompatActivity {

    EditText subject;
    EditText txtMsg;
    Button send;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);
        subject = findViewById(R.id.subject);
        txtMsg = findViewById(R.id.txtMsg);
        send = findViewById(R.id.send);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String subMsg = subject.getText().toString();
                String body = txtMsg.getText().toString();
                if (subMsg.equals("")) {
                    AlertDialog alertDialog = new AlertDialog.Builder(SendMessageActivity.this).create();
                    alertDialog.setTitle("Erreur !");
                    alertDialog.setMessage("Svp, tapez le sujet d'abord !");
                    alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                } else if (body.equals("")) {
                    AlertDialog alertDialog = new AlertDialog.Builder(SendMessageActivity.this).create();
                    alertDialog.setTitle("Erreur !");
                    alertDialog.setMessage("Svp, tapez le message ! ");
                    alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                } else {
                    sendMessage4(subMsg, body);
                }
            }
        });
    }

    void sendMessage4(String subMsg, String body) {
        try {
            body = new String(body.getBytes(), "UTF-8");
            subMsg = new String(subMsg.getBytes(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String URL = "https://holoola-z.com/projects/Pharmacie_Offers/php/messages/add";
        String finalBody = body;
        String finalSubMsg = subMsg;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, response -> {
            Log.i("VOLLEY RESPONSE ", "response : " + response);
            try {
                JSONObject res = new JSONObject(response);
                Log.i("VOLLEY RESPONSE : ", "message : " + res.getString("message"));
                Log.i("VOLLEY RESPONSE : ", "code : " + res.getString("code"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            subject.getText().clear();
            txtMsg.getText().clear();
            Toast.makeText(SendMessageActivity.this, "Message envoyé avec succès !", Toast.LENGTH_SHORT).show();
        }, error -> {
            System.out.println("errrrrrrrrrrr " + error.getMessage());
            error.printStackTrace();
            Toast.makeText(SendMessageActivity.this, "Error !! ", Toast.LENGTH_SHORT).show();
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<>();
                parameters.put("sender", StaticVariable.db.getOneUser().getEmail());
                parameters.put("receiver", "haloce.dark@gmail.com");
                parameters.put("subject", finalSubMsg);
                parameters.put("body", finalBody);
                return parameters;
            }
        };
        requestQueue.add(stringRequest);
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