package com.example.pharmacieapplication.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.pharmacieapplication.DBLocal.DatabaseHalper;
import com.example.pharmacieapplication.MainActivity;
import com.example.pharmacieapplication.Models.Client;
import com.example.pharmacieapplication.Models.StaticVariable;
import com.example.pharmacieapplication.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    Button login, createAccount;
    EditText userName, password;
    TextView forgetPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login = findViewById(R.id.button_login);
        createAccount = findViewById(R.id.to_new_account);
        userName = findViewById(R.id.edit_user_name);
        password = findViewById(R.id.edit_password);
        forgetPassword = findViewById(R.id.forgetPassword);
        DatabaseHalper db = new DatabaseHalper(this);
        db.setActivity(this);
        if (db.getAllUsers() > 0) {
            Client client = db.getOneUser();
            StaticVariable.user = client;
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("client", client);
            intent.putExtras(bundle);
            finish();
            startActivity(intent);
        }
        if (StaticVariable.ok == 1) {
            StaticVariable.ok = 0;
            AlertDialog.Builder adb = new AlertDialog.Builder(LoginActivity.this);
            adb.setTitle("Information");
            adb.setMessage("Félicitations ! l'insription est terminé avec succès, un e-mail sera envoyé à votre boîte pour valider votre compte.");
            adb.setPositiveButton("Ok",
                    new AlertDialog.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }).show();
        }
        try {
            db.insertAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
        StaticVariable.db = db;
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userName.getText().toString().equals("") || password.getText().toString().equals(""))
                    Toast.makeText(LoginActivity.this, "Des champs vides !", Toast.LENGTH_SHORT).show();
                else {
                    Client client = new Client();
                    client.setUserName(StaticVariable.toArabic(userName.getText().toString()));
                    client.setPassword(StaticVariable.toArabic(password.getText().toString()));
                    demandeAccess(client);
                }
            }
        });

        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, NewAccountActivity.class);
                startActivity(intent);
            }
        });
    }

    private void demandeAccess(Client client) {
        String URL = "https://holoola-z.com/projects/Pharmacie_Offers/php/users/login";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, response -> {
            Log.i("VOLLEY RESPONSE ", "response login : " + response);
            try {
                JSONObject res = new JSONObject(response);
                Log.i("VOLLEY RESPONSE : ", "message : " + res.getString("message"));
                Log.i("VOLLEY RESPONSE : ", "code : " + res.getString("code"));
                if (res.getString("code").equals("200")) {
//                    if (res.getString("isApproved").equals("0")) {
//
//                    } else

                    JSONObject signedIn = res.getJSONObject("signedIn");
                    Log.i("VOLLEY RESPONSE : ", "code : " + signedIn.getString("isVerified"));
                    if (signedIn.getString("isVerified").equals("0")) {
                        AlertDialog.Builder adb = new AlertDialog.Builder(LoginActivity.this);
                        adb.setTitle("Erreur");
                        adb.setMessage("Ce compte n'est pas encore validé ! Il faut valider votre compte via notre message envoyé à votre adresse e-mail.");
                        adb.setPositiveButton("Ok",
                                new AlertDialog.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        return;
                                    }
                                }).show();
                    } else {
                        client.setId(signedIn.getString("userId"));
                        client.setEmail(signedIn.getString("userEmail"));
                        client.setName(signedIn.getString("firstName") + " " + signedIn.getString("lastName"));
                        client.setUserName(signedIn.getString("userName"));
                        client.setWilaya(signedIn.getString("state"));
                        client.setCommune(signedIn.getString("municipal"));
                        client.setVerified(true);
                        if (signedIn.getString("isApproved").equals("0"))
                            client.setApproved(false);
                        else
                            client.setApproved(true);
                        System.out.println("insert : " + StaticVariable.db.insertUser(client));
                        finish();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("client", client);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        return;
                    }
                } else if (res.getString("code").equals("404")) {
                    AlertDialog.Builder adb = new AlertDialog.Builder(LoginActivity.this);
                    adb.setTitle("Erreur");
                    adb.setMessage("Nom d'utilisateur ou mot de passe incorrecte");
                    adb.setPositiveButton("Ok",
                            new AlertDialog.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    return;
                                }
                            }).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //message.getText().clear();
            //Toast.makeText(AddOffreActivity.this, "Insription terminé avec succès !", Toast.LENGTH_SHORT).show();
        }, error -> {
            System.out.println("errrrrrrrrrrr " + error.getMessage());
            Log.e("VOLLEY ERROR : ", "message : " + error.networkResponse);
            error.printStackTrace();
            AlertDialog.Builder adb = new AlertDialog.Builder(LoginActivity.this);
            adb.setTitle("Erreur");
            adb.setMessage("Pas de connexion !");
            adb.setPositiveButton("Ok",
                    new AlertDialog.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }).show();
            //Toast.makeText(AddOffreActivity.this, "Error !! ", Toast.LENGTH_SHORT).show();
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<>();
                parameters.put("type", "CUSTOMER");
                parameters.put("username", client.getUserName());
                parameters.put("password", client.getPassword());
                return parameters;
            }
        };
        requestQueue.add(stringRequest);
    }
}