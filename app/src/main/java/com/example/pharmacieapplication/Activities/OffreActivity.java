package com.example.pharmacieapplication.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.pharmacieapplication.Connection.CustomRequest;
import com.example.pharmacieapplication.Models.Offre;
import com.example.pharmacieapplication.Models.StaticVariable;
import com.example.pharmacieapplication.R;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class OffreActivity extends AppCompatActivity {


    TextView title, nameOffre, descOffre, priceOffre, dateEndOffre, quantity, dateBegin;
    ImageView fav, imgOffre;
    Button send;
    EditText message;
    private Offre offre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offre);
        //toolbar = findViewById(R.id.main_page_toolbar);
        ///setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);//
//        upArrow.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
//        getSupportActionBar().setHomeAsUpIndicator(upArrow);//
        //title = findViewById(R.id.titleOfActivity);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        offre = (Offre) bundle.getSerializable("offre");
        //title.setText(offre.getOfferName());
        getSupportActionBar().setTitle(offre.getOfferName());
        if (offre.getType().equals("my"))
            send.setVisibility(View.GONE);
        nameOffre = findViewById(R.id.nameOffre);
        descOffre = findViewById(R.id.descOffre);
        priceOffre = findViewById(R.id.priceOffre);
        dateBegin = findViewById(R.id.dateBeginOffre);
        dateEndOffre = findViewById(R.id.dateEndOffre);
        send = findViewById(R.id.send);
        imgOffre = findViewById(R.id.imgOffre);
        message = findViewById(R.id.msg);
        fav = findViewById(R.id.fav);
        Picasso.get().load(offre.getOfferImage()).into(imgOffre);
        System.out.println("image : " + offre.getOfferImage());
        quantity = findViewById(R.id.quantity);
        quantity.setText(offre.getOfferQuantity());
        dateEndOffre.setText(offre.getOfferEndDate());
        dateBegin.setText(offre.getOfferStartDate());
        nameOffre.setText(offre.getOfferName());
        priceOffre.setText(offre.getOfferPrice() + "DZD");
        descOffre.setText(offre.getOfferDesc());
        if (!offre.isFav())
            fav.setImageResource(R.drawable.ic_favorite_border);
        else
            fav.setImageResource(R.drawable.ic_favorite);

        //send.setEnabled(false);
//        message.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View view, int i, KeyEvent keyEvent) {
//                if (message.getText().toString().equals(""))
//                    send.setEnabled(false);
//                else
//                    send.setEnabled(true);
//                return false;
//            }
//        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String body = message.getText().toString();
                if (body.equals("")) {
                    AlertDialog alertDialog = new AlertDialog.Builder(OffreActivity.this).create();
                    alertDialog.setTitle("Erreur !");
                    alertDialog.setMessage("Svp, entrez le message");
                    alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                } else {
                    sendMessage4(body);
                }
            }
        });
    }

    void sendMessage4(String body) {
        try {
            body = new String(body.getBytes(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String URL = "https://holoola-z.com/projects/Pharmacie_Offers/php/messages/add";
        String finalBody = body;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, response -> {
            Log.i("VOLLEY RESPONSE ", "response : " + response);
            try {
                JSONObject res = new JSONObject(response);
                Log.i("VOLLEY RESPONSE : ", "message : " + res.getString("message"));
                Log.i("VOLLEY RESPONSE : ", "code : " + res.getString("code"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            message.getText().clear();
            Toast.makeText(OffreActivity.this, "Message envoyé avec succès !", Toast.LENGTH_SHORT).show();
        }, error -> {
            System.out.println("errrrrrrrrrrr " + error.getMessage());
            error.printStackTrace();
            Toast.makeText(OffreActivity.this, "Error !! ", Toast.LENGTH_SHORT).show();
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<>();
                parameters.put("sender", StaticVariable.db.getOneUser().getEmail());
                parameters.put("receiver", "haloce.dark@gmail.com");
                parameters.put("subject", offre.getOfferName());
                parameters.put("body", finalBody);
                return parameters;
            }
        };
        requestQueue.add(stringRequest);
    }

//    void sendMessage3(String body) {
//        RequestQueue requestQueue = Volley.newRequestQueue(this);
//        String URL = "https://holoola-z.com/projects/Pharmacie_Offers/php/messages/add";
//        Map<String, String> parameters = new HashMap<>();
//        parameters.put("sender", "abss39.dz@gmail.com");
//        parameters.put("receiver", "haloce.dark@gmail.com");
//        parameters.put("subject", offre.getOfferName());
//        parameters.put("body", body);
//        System.out.println("object : " + parameters.toString());
//        CustomRequest customRequest = new CustomRequest(URL, parameters, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                Log.i("VOLLEY RESPONSE ", "response : " + response.toString());
//                try {
//                    Log.i("VOLLEY RESPONSE : ", "message : " + response.getString("message"));
//                    Log.i("VOLLEY RESPONSE : ", "code : " + response.getString("code"));
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                message.getText().clear();
//                Toast.makeText(OffreActivity.this, "Message envoyé avec succès !", Toast.LENGTH_SHORT).show();
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                System.out.println("errrrrrrrrrrr " + error.getMessage());
//                error.printStackTrace();
//                Toast.makeText(OffreActivity.this, "Error !! ", Toast.LENGTH_SHORT).show();
//            }
//        });
//        requestQueue.add(customRequest);
//    }
//
//    void sendMessage2(String body) {
//        try {
//            RequestQueue requestQueue = Volley.newRequestQueue(this);
//            String URL = "https://holoola-z.com/projects/Pharmacie_Offers/php/messages/add";
//            JSONObject parameters = new JSONObject();
//            parameters.put("sender", "abss39.dz@gmail.com");
//            parameters.put("receiver", "haloce.dark@gmail.com");
//            parameters.put("subject", offre.getOfferName());
//            parameters.put("body", body);
////            parameters.put("sender", "abss39.dz@gmail.com");
////            parameters.put("receiver", "haloce.dark@gmail.com");
////            parameters.put("subject", "offre.getOfferName()");
////            parameters.put("body", "body");
////            System.out.println("json : " + parameters.toString());
////            parameters = new JSONObject(s);
////            System.out.println("json : " + parameters.toString());
//            //JSONObject parameters = new JSONObject(String.valueOf(parameters));
//
//            JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, URL, parameters, new Response.Listener<JSONObject>() {
//                @Override
//                public void onResponse(JSONObject response) {
//                    Log.i("VOLLEY RESPONSE ", "response : " + response.toString());
//                    try {
//                        Log.i("VOLLEY RESPONSE : ", "message : " + response.getString("message"));
//                        Log.i("VOLLEY RESPONSE : ", "code : " + response.getString("code"));
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                    message.getText().clear();
//                    Toast.makeText(OffreActivity.this, "Message envoyé avec succès !", Toast.LENGTH_SHORT).show();
//                }
//            }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    error.printStackTrace();
//                }
//            });
//            //Volley.newRequestQueue(this).add(jsonRequest);
//            requestQueue.add(jsonRequest);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
//
//    void sendMessage(String body) {
//        try {
//            RequestQueue requestQueue = Volley.newRequestQueue(this);
//            String URL = "https://holoola-z.com/projects/Pharmacie_Offers/php/messages/add";
//            JSONObject jsonBody = new JSONObject();
//            jsonBody.put("sender", "abss39.dz@gmail.com");
//            jsonBody.put("receiver", "haloce.dark@gmail.com");
//            jsonBody.put("subject", offre.getOfferName());
//            jsonBody.put("body", body);
//            final String requestBody = jsonBody.toString();
//
//            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
//                @Override
//                public void onResponse(String response) {
//                    Log.i("VOLLEY RESPONSE : ", response);
//                    message.getText().clear();
//                    send.setEnabled(false);
//                    Toast.makeText(OffreActivity.this, "Message envoyé avec succès !", Toast.LENGTH_SHORT).show();
//                }
//            }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    Log.e("VOLLEY ERROR : ", error.toString());
//                    Toast.makeText(OffreActivity.this, "Pas de connexion pour envoyer ce message !", Toast.LENGTH_SHORT).show();
//                }
//            }) {
//                @Override
//                public String getBodyContentType() {
//                    return "application/json; charset=utf-8";
//                }
//
//                @Override
//                public byte[] getBody() throws AuthFailureError {
//                    try {
//                        return requestBody == null ? null : requestBody.getBytes("utf-8");
//                    } catch (UnsupportedEncodingException uee) {
//                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
//                        return null;
//                    }
//                }
//
//                public void onResponse(JSONObject response) {
//                    Log.i("VOLLEY RESPONSE ", "response : " + response.toString());
//                    try {
//                        Log.i("VOLLEY RESPONSE : ", "message : " + response.getString("message"));
//                        Log.i("VOLLEY RESPONSE : ", "code : " + response.getString("code"));
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                    message.getText().clear();
//                    send.setEnabled(false);
//                    Toast.makeText(OffreActivity.this, "Message envoyé avec succès !", Toast.LENGTH_SHORT).show();
//                }
//
//                @Override
//                protected Response<String> parseNetworkResponse(NetworkResponse response) {
//                    String responseString = "";
//                    if (response != null) {
//                        responseString = String.valueOf(response.statusCode);
//                        // can get more details such as response.headers
//                    }
//                    return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
//                }
//            };
//
//            requestQueue.add(stringRequest);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}