package com.example.pharmacieapplication.Activities;

import static com.example.pharmacieapplication.Models.StaticVariable.IMG_REQUEST_CAMERA;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.pharmacieapplication.Connection.AppHelper;
import com.example.pharmacieapplication.Connection.VolleyMultipartRequest;
import com.example.pharmacieapplication.Connection.VolleySingleton;
import com.example.pharmacieapplication.Dialogs.DialogChoose;
import com.example.pharmacieapplication.Dialogs.DialogDate;
import com.example.pharmacieapplication.Models.Offre;
import com.example.pharmacieapplication.Models.StaticVariable;
import com.example.pharmacieapplication.R;

import org.joda.time.LocalDate;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AddOffreActivity extends AppCompatActivity {

    String dateStr1 = "";
    String dateStr2 = "";

    EditText nameOffre, descOffre, quantity, priceOffre;
    TextView dateBeginOffre, dateEndOffre;
    ImageView imageOffre, date1, date2;
    Button add;
    Bitmap bitmap;
    Offre offre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_offre);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Ajouter un offre");
        nameOffre = findViewById(R.id.nameOffre);
        descOffre = findViewById(R.id.descOffre);
        priceOffre = findViewById(R.id.priceOffre);
        dateBeginOffre = findViewById(R.id.dateBeginOffre);
        dateEndOffre = findViewById(R.id.dateEndOffre);
        add = findViewById(R.id.add);
        quantity = findViewById(R.id.quantity);
        imageOffre = findViewById(R.id.imageOffre);
        date1 = findViewById(R.id.date1);
        date2 = findViewById(R.id.date2);
        dateBeginOffre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogDate dialogDate = new DialogDate(AddOffreActivity.this, 1);
                dialogDate.setActivity(AddOffreActivity.this);
                dialogDate.show();
            }
        });

        date1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogDate dialogDate = new DialogDate(AddOffreActivity.this, 1);
                dialogDate.setActivity(AddOffreActivity.this);
                dialogDate.show();
            }
        });
        date2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogDate dialogDate = new DialogDate(AddOffreActivity.this, 2);
                dialogDate.setActivity(AddOffreActivity.this);
                dialogDate.show();
            }
        });

        dateEndOffre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogDate dialogDate = new DialogDate(AddOffreActivity.this, 2);
                dialogDate.setActivity(AddOffreActivity.this);
                dialogDate.show();
            }
        });

        imageOffre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogChoose dialogChoose = new DialogChoose(AddOffreActivity.this);
                dialogChoose.setActivity(AddOffreActivity.this);
                dialogChoose.show();
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BitmapDrawable drawable;
                try {
                    drawable = (BitmapDrawable) imageOffre.getDrawable();
                } catch (Exception e) {
                    AlertDialog.Builder adb = new AlertDialog.Builder(AddOffreActivity.this);
                    adb.setTitle("Erreur");
                    adb.setMessage("Ajouter l'image de l'offre !");
                    adb.setPositiveButton("Ok",
                            new AlertDialog.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            }).show();
                    return;
                }
                drawable = (BitmapDrawable) imageOffre.getDrawable();
                Bitmap bitmap = drawable.getBitmap();
                Bitmap bitmap2 = getBitmap(R.drawable.ic_image);
                if (bitmap.equals(bitmap2)) {
                    AlertDialog.Builder adb = new AlertDialog.Builder(AddOffreActivity.this);
                    adb.setTitle("Erreur");
                    adb.setMessage("Ajouter l'image de l'offre !");
                    adb.setPositiveButton("Ok",
                            new AlertDialog.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            }).show();
                    return;
                }
                if (nameOffre.getText().toString().equals("") || descOffre.getText().toString().equals("") ||
                        priceOffre.getText().toString().equals("") || quantity.getText().toString().equals("")) {
                    AlertDialog.Builder adb = new AlertDialog.Builder(AddOffreActivity.this);
                    adb.setTitle("Erreur");
                    adb.setMessage("Des champs vides !");
                    adb.setPositiveButton("Ok",
                            new AlertDialog.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            }).show();
                    return;
                }
                try {
                    LocalDate.parse(dateEndOffre.getText().toString());
                    LocalDate.parse(dateBeginOffre.getText().toString());
                } catch (Exception e) {
                    AlertDialog.Builder adb = new AlertDialog.Builder(AddOffreActivity.this);
                    adb.setTitle("Erreur");
                    adb.setMessage("Dates invalides !");
                    adb.setPositiveButton("Ok",
                            new AlertDialog.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).show();
                    return;
                }

                try {
                    double d = Double.parseDouble(priceOffre.getText().toString());
                    int i = Integer.parseInt(quantity.getText().toString());
                } catch (Exception e) {
                    AlertDialog.Builder adb = new AlertDialog.Builder(AddOffreActivity.this);
                    adb.setTitle("Erreur");
                    adb.setMessage("Prix ou quantité invalides !");
                    adb.setPositiveButton("Ok",
                            new AlertDialog.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            }).show();
                    return;
                }
                AlertDialog.Builder adb = new AlertDialog.Builder(AddOffreActivity.this);
                adb.setTitle("Confirmation");
                adb.setMessage("Vous êtes sûr de ajouter cet offre ?");
                adb.setNegativeButton("Cancel", null);
                adb.setPositiveButton("Oui",
                        new AlertDialog.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                offre = new Offre();
                                offre.setOfferName(StaticVariable.toArabic(nameOffre.getText().toString()));
                                offre.setOfferDesc(StaticVariable.toArabic(descOffre.getText().toString()));
                                offre.setOfferPrice(StaticVariable.toArabic(priceOffre.getText().toString()));
                                offre.setOfferQuantity(StaticVariable.toArabic(quantity.getText().toString()));
                                offre.setOfferEndDate(StaticVariable.toArabic(dateEndOffre.getText().toString()));
                                offre.setOfferStartDate(StaticVariable.toArabic(dateBeginOffre.getText().toString()));
                                sendMessage5(offre);
                            }
                        }).show();
            }
        });
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        if (!dateStr1.equals(""))
//            dateBeginOffre.setText(dateStr1);
//        if (!dateStr2.equals(""))
//            dateEndOffre.setText(dateStr2);
//    }

    void sendMessage5(Offre offre) {
        //String URL = "https://holoola-z.com/projects/Pharmacie_Offers/php/offers/uploadImage";
        String URL = "https://holoola-z.com/projects/Pharmacie_Offers/php/offers/add";
        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, URL, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.toString());
                    Log.i("VOLLEY RESPONSE : ", "message : " + jsonObject.getString("message"));
                    Log.i("VOLLEY RESPONSE : ", "code : " + jsonObject.getString("code"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String resultResponse = new String(response.data);
                // parse success output
                Log.i("VOLLEY RESPONSE : ", "message : " + resultResponse);
                //if
                Toast.makeText(AddOffreActivity.this, "Offre envoyé avec succès !", Toast.LENGTH_SHORT).show();
                refresh();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.e("VOLLEY RESPONSE : ", "message : " + error.getMessage());
                Toast.makeText(AddOffreActivity.this, "Error !", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                System.out.println("id user : " + StaticVariable.db.getOneUser().getId());
                params.put("userId", StaticVariable.db.getOneUser().getId());

                //params.put("offerImage", "abss39.dz@gmail.com");
                params.put("offerName", offre.getOfferName());
                params.put("offerDesc", offre.getOfferDesc());
                params.put("offerPrice", offre.getOfferPrice());
                params.put("offerQuantity", offre.getOfferQuantity());
                params.put("offerEndDate", offre.getOfferEndDate());
                params.put("offerStartDate", offre.getOfferStartDate());
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                // file name could found file base or direct access from real path
                // for now just get bitmap data from ImageView
                params.put("offerImage", new DataPart("file_avatar.jpg", AppHelper.getFileDataFromDrawable(getBaseContext(), imageOffre.getDrawable()), "image/jpeg"));
                //params.put("cover", new DataPart("file_cover.jpg", AppHelper.getFileDataFromDrawable(getBaseContext(), mCoverImage.getDrawable()), "image/jpeg"));
                return params;
            }
        };
        //requestQueue.add(multipartRequest);
        VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(multipartRequest);
    }

    void sendMessage4() {
//        file  = new Compressor(this).compressToFile(file);
//        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
//        // MultipartBody.Part is used to send also the actual filename
//        MultipartBody.Part body = MultipartBody.Part.createFormData("avatar", file.getName(), requestFile);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String URL = "https://holoola-z.com/projects/Pharmacie_Offers/php/offers/add";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, response -> {
            Log.i("VOLLEY RESPONSE ", "response : " + response);
            try {
                JSONObject res = new JSONObject(response);
                Log.i("VOLLEY RESPONSE : ", "message : " + res.getString("message"));
                Log.i("VOLLEY RESPONSE : ", "code : " + res.getString("code"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //message.getText().clear();
            Toast.makeText(AddOffreActivity.this, "Message envoyé avec succès !", Toast.LENGTH_SHORT).show();
        }, error -> {
            System.out.println("errrrrrrrrrrr " + error.getMessage());
            Log.e("VOLLEY ERROR : ", "message : " + error.networkResponse);
            error.printStackTrace();
            Toast.makeText(AddOffreActivity.this, "Error !! ", Toast.LENGTH_SHORT).show();
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<>();
                parameters.put("userId", "abss39.dz@gmail.com");
                parameters.put("offerImage", "abss39.dz@gmail.com");
                parameters.put("offerName", "haloce.dark@gmail.com");
                parameters.put("offerDesc", "haloce.dark@gmail.com");
                parameters.put("offerPrice", "200.00");
                parameters.put("offerQuantity", "2000");
                parameters.put("offerEndDate", "2022-01-01");
                parameters.put("offerStartDate", "2021-12-05");
                return parameters;
            }
        };
        requestQueue.add(stringRequest);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("pick : " + data);
        System.out.println("requestCode : " + requestCode);
        if (data == null)
            Toast.makeText(this, "Image non importé !", Toast.LENGTH_SHORT);
        if (requestCode == StaticVariable.IMG_REQUEST_GALLERY && data != null) {
            Uri path = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), path);
                imageOffre.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (requestCode == IMG_REQUEST_CAMERA && data != null) {
            Uri uri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                imageOffre.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Bitmap getBitmap(int res) {
        try {
            Bitmap bitmap;
            Drawable drawable = getDrawable(res);
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        } catch (OutOfMemoryError e) {
            // Handle the error
            return null;
        }
    }

    void refresh() {
        Intent intent = this.getIntent();
        finish();
        //startActivity(intent);
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

    public String getDateStr1() {
        return dateStr1;
    }

    public void setDateStr1(String dateStr1) {
        this.dateStr1 = dateStr1;
    }

    public String getDateStr2() {
        return dateStr2;
    }

    public void setDateStr2(String dateStr2) {
        this.dateStr2 = dateStr2;
    }

    public TextView getDateBeginOffre() {
        return dateBeginOffre;
    }

    public void setDateBeginOffre(TextView dateBeginOffre) {
        this.dateBeginOffre = dateBeginOffre;
    }

    public TextView getDateEndOffre() {
        return dateEndOffre;
    }

    public void setDateEndOffre(TextView dateEndOffre) {
        this.dateEndOffre = dateEndOffre;
    }
}