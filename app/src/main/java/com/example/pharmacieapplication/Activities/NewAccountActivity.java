package com.example.pharmacieapplication.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.pharmacieapplication.Adapters.RecyclerOffreAdapter;
import com.example.pharmacieapplication.Models.Client;
import com.example.pharmacieapplication.Models.Commune;
import com.example.pharmacieapplication.Models.Offre;
import com.example.pharmacieapplication.Models.StaticVariable;
import com.example.pharmacieapplication.Models.Wilaya;
import com.example.pharmacieapplication.R;
import com.example.pharmacieapplication.ui.offres.OffresFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NewAccountActivity extends AppCompatActivity {

    Button login, ok;
    EditText email, password, repassword, name, lastName, pharmacieName, phoneNumber, edit_email, edit_user_name;
    Spinner wilaya, commune;

    ArrayList<Wilaya> wilayas;
    ArrayList<Commune> communes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_account);

        login = findViewById(R.id.to_login);
        ok = findViewById(R.id.button_new_account);
        email = findViewById(R.id.edit_email);
        password = findViewById(R.id.enter_password);
        repassword = findViewById(R.id.reenter_password);
        name = findViewById(R.id.name);
        lastName = findViewById(R.id.lastName);
        pharmacieName = findViewById(R.id.pharmacieName);
        phoneNumber = findViewById(R.id.phoneNumber);
        edit_email = findViewById(R.id.edit_email);
        wilaya = findViewById(R.id.wilaya);
        commune = findViewById(R.id.commune);
        chargeWilaya();
        chargeCommune();
        wilaya.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                chargeCommune();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        edit_user_name = findViewById(R.id.edit_user_name);
        //System.out.println("getAllUsers : " + StaticValues.db.getAllUsers().get(0).getEmail());
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!email.getText().toString().contains("@")) {
                    Toast.makeText(NewAccountActivity.this, "Svp, entrez un email valide !", Toast.LENGTH_SHORT).show();
                } else if (email.getText().toString().equals("") || password.getText().toString().equals("") || repassword.getText().toString().equals("") ||
                        name.getText().toString().equals("") || pharmacieName.getText().toString().equals("") ||
                        edit_email.getText().toString().equals("") || edit_user_name.getText().toString().equals(""))
                    Toast.makeText(NewAccountActivity.this, "Des champs vides !", Toast.LENGTH_SHORT).show();
                else if (password.getText().length() < 8)
                    Toast.makeText(NewAccountActivity.this, "mot de passe inférieur à 8 caractères !", Toast.LENGTH_SHORT).show();
                else if (!password.getText().toString().equals(repassword.getText().toString()))
                    Toast.makeText(NewAccountActivity.this, "Les deux mot de passes ne sont pas les mêmes !", Toast.LENGTH_SHORT).show();
//                else if (StaticValues.db.getOneUser(email.getText().toString()).size() != 0)
//                    Toast.makeText(NewAccountActivity.this, "هذا الحساب موجود بالفعل", Toast.LENGTH_SHORT).show();
                else {
                    AlertDialog.Builder adb = new AlertDialog.Builder(NewAccountActivity.this);
                    adb.setTitle("Confirmation");
                    adb.setMessage("Vous êtes sûr de créer ce compte ?");
                    adb.setNegativeButton("Cancel", null);
                    adb.setPositiveButton("Oui",
                            new AlertDialog.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Client client = new Client();
                                    String tab[] = name.getText().toString().split(" ");
                                    String last = "";
                                    for (int i = 1; i < tab.length; i++)
                                        last = last + tab[i];
                                    client.setName(tab[0]);
                                    client.setLastName(last);
                                    client.setPharmacieName(StaticVariable.toArabic(pharmacieName.getText().toString()));
                                    client.setPhoneNumber(StaticVariable.toArabic(phoneNumber.getText().toString()));
                                    client.setPassword(StaticVariable.toArabic(password.getText().toString()));
                                    client.setEmail(StaticVariable.toArabic(edit_email.getText().toString()));
                                    client.setUserName(StaticVariable.toArabic(edit_user_name.getText().toString()));
                                    client.setWilaya(StaticVariable.toArabic(wilayas.get(wilaya.getSelectedItemPosition()).getName()));
                                    client.setCommune(StaticVariable.toArabic(commune.getSelectedItem().toString()));
                                    insert(client);
                                }
                            }).show();
                }
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(NewAccountActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    void chargeWilaya() {
        wilayas = StaticVariable.db.getAllWilayas();
        //System.out.println("size is : " + wilayas.size());
        final String[] array = new String[wilayas.size()];
        for (int i = 0; i < wilayas.size(); i++)
            array[i] = wilayas.get(i).getCode() + "  " + wilayas.get(i).getName();
        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, array);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        wilaya.setAdapter(adapter);
        wilaya.setSelection(0);
    }

    void chargeCommune() {
        communes = StaticVariable.db.getAllCommunes(wilayas.get(wilaya.getSelectedItemPosition()).getName());
        final String[] array = new String[communes.size()];
        for (int i = 0; i < communes.size(); i++)
            array[i] = communes.get(i).getName();
        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, array);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        commune.setAdapter(adapter);
        commune.setSelection(0);
    }

    private void insert(Client client) {
        //RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        //CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, JSON_URL, params, this.createRequestSuccessListener(), this.createRequestErrorListener());
        //requestQueue.add(jsObjRequest);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String URL = "https://holoola-z.com/projects/Pharmacie_Offers/php/users/add";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, response -> {
            Log.i("VOLLEY RESPONSE ", "response : " + response);
            try {
                JSONObject res = new JSONObject(response);
                Log.i("VOLLEY RESPONSE : ", "message : " + res.getString("message"));
                Log.i("VOLLEY RESPONSE : ", "code : " + res.getString("code"));
                if (res.getString("code").equals("200")) {
                    finish();
                    StaticVariable.ok = 1;
                    startActivity(new Intent(NewAccountActivity.this, LoginActivity.class));
                    return;
                } else if (res.getString("code").equals("404")) {
                    AlertDialog.Builder adb = new AlertDialog.Builder(NewAccountActivity.this);
                    adb.setTitle("Erreur");
                    adb.setMessage("Ce compte est existe déjà !");
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
            AlertDialog.Builder adb = new AlertDialog.Builder(NewAccountActivity.this);
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
                parameters.put("lastname", client.getLastName());
                parameters.put("firstname", client.getName());
                parameters.put("username", client.getUserName());
                parameters.put("email", client.getEmail());
                parameters.put("password", client.getPassword());
                parameters.put("pharmacie", client.getPharmacieName());
                parameters.put("phone", client.getPhoneNumber());
                parameters.put("state", client.getWilaya());
                parameters.put("municipal", client.getCommune());
                return parameters;
            }
        };
        requestQueue.add(stringRequest);
        //requestQueue = Volley.newRequestQueue(this);
        //requestQueue.add(request);
    }
}