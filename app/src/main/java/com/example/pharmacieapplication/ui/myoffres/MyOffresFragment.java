package com.example.pharmacieapplication.ui.myoffres;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.example.pharmacieapplication.Activities.AddOffreActivity;
import com.example.pharmacieapplication.Adapters.RecyclerOffreAdapter;
import com.example.pharmacieapplication.Models.Offre;
import com.example.pharmacieapplication.Models.StaticVariable;
import com.example.pharmacieapplication.R;
import com.example.pharmacieapplication.databinding.FragmentMyOffresBinding;
import com.example.pharmacieapplication.databinding.FragmentOffresBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MyOffresFragment extends Fragment {

    //private HomeViewModel homeViewModel;
    private FragmentMyOffresBinding binding;
    RecyclerView recyclerOffre;
    FloatingActionButton addOffre;
    TextView empty;
    ArrayList<Offre> offres = new ArrayList<>();
    //private JsonArrayRequest request;
    private JsonRequest jsonRequest;
    JsonObjectRequest request;
    private RequestQueue requestQueue;
    private String mParam1;
    private String mParam2;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);


        binding = FragmentMyOffresBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
//        TextView title = root.findViewById(R.id.titleOfFragment);
//        title.setText("Les offres");
        recyclerOffre = root.findViewById(R.id.listOffres);
        empty = root.findViewById(R.id.empty);
        addOffre = root.findViewById(R.id.addOffre);
        addOffre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (StaticVariable.user.isApproved()) {
                    startActivity(new Intent(getContext(), AddOffreActivity.class));
                } else {
                    AlertDialog.Builder adb = new AlertDialog.Builder(getContext());
                    adb.setTitle("Information");
                    adb.setMessage("Ce compte n'est pas encore Approuvée par l'admin !");
                    adb.setPositiveButton("Ok",
                            new AlertDialog.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    return;
                                }
                            }).show();
                }
            }
        });
        updateOffres();
//        final TextView textView = binding.textHome;
//        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
        return root;
    }

    private void updateOffres() {
        String JSON_URL = "https://holoola-z.com/projects/Pharmacie_Offers/php/customers/offers?userId=" + StaticVariable.user.getId();
        //RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        //CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, JSON_URL, params, this.createRequestSuccessListener(), this.createRequestErrorListener());
        //requestQueue.add(jsObjRequest);
        request = new JsonObjectRequest(Request.Method.GET, JSON_URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                offres.clear();
                //System.out.println("its working : " + response);
                try {
                    JSONObject res = new JSONObject(response.toString());
                    if (res.getString("code").equals("404")) {
                        recyclerOffre.setVisibility(View.GONE);
                        empty.setVisibility(View.VISIBLE);
                    } else {
                        empty.setVisibility(View.GONE);
                        recyclerOffre.setVisibility(View.VISIBLE);
                        JSONArray data = response.getJSONArray("offers");
                        offres.clear();
                        for (int i = 0; i < data.length(); i++) {
                            Offre offre = new Offre();
                            JSONObject jsonobject = data.getJSONObject(i);
                            offre.setOfferId(jsonobject.getInt("offerId"));
                            offre.setOfferName(jsonobject.getString("offerName"));
                            offre.setOfferDesc(jsonobject.getString("offerDesc"));
                            offre.setOfferImage(jsonobject.getString("offerImage"));
                            offre.setOfferStartDate(jsonobject.getString("offerStartDate"));
                            offre.setOfferEndDate(jsonobject.getString("offerEndDate"));
                            offre.setOfferPrice(jsonobject.getString("offerPrice"));
                            offre.setOfferQuantity(jsonobject.getString("offerQuantity"));
                            offres.add(offre);
                        }
                        LinearLayoutManager llm = new LinearLayoutManager(MyOffresFragment.this.getContext());
                        llm.setOrientation(LinearLayoutManager.VERTICAL);
                        RecyclerOffreAdapter recyclerOffreAdapter = new RecyclerOffreAdapter(getContext(), offres, "my");
                        recyclerOffre.setLayoutManager(new LinearLayoutManager(getContext()));
                        recyclerOffre.setLayoutManager(llm);
                        recyclerOffre.setAdapter(recyclerOffreAdapter);
                        //recyclerOffreAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("errrrrrrrrrrrrrr " + error.getMessage());
                Toast.makeText(getContext(), "Pas de connexion !", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(request);
    }

//    private void updateOffres() {
//        String JSON_URL = "https://holoola-z.com/projects/Pharmacie_Offers/php/offers/";
//        request = new JsonArrayRequest(JSON_URL, new Response.Listener<JSONArray>() {
//            @Override
//            public void onResponse(JSONArray response) {
//                offres.clear();
//                JSONObject jsonObject = null;
//                if (response.length() == 0) {
//                    AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
//                    alertDialog.setTitle("Alerte");
//                    alertDialog.setMessage("Pas des offres !");
//                    alertDialog.setIcon(android.R.drawable.ic_dialog_info);
//                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
//                            new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int which) {
//                                    dialog.dismiss();
//                                }
//                            });
//                    alertDialog.show();
//                } else
//                    for (int i = 0; i < response.length(); i++) {
//                        try {
//                            jsonObject = response.getJSONObject(i);
//                            Offre offre = new Offre();
//                            JSONArray data = jsonObject.getJSONArray("data");
//                            for (int j = 0; j < data.length(); j++) {
//                                JSONObject jsonobject = data.getJSONObject(j);
//                                offre.setOfferId(jsonobject.getInt("offerId"));
//                                offre.setOfferName(jsonobject.getString("offerName"));
//                                offre.setOfferDesc(jsonobject.getString("offerDesc"));
//                                offre.setOfferImage(jsonobject.getString("offerImage"));
//                                offre.setOfferStartDate(jsonobject.getString("offerStartDate"));
//                                offre.setOfferEndDate(jsonobject.getString("offerEndDate"));
//                                offre.setOfferPrice(jsonobject.getString("offerPrice"));
//                                offre.setOfferQuantity(jsonobject.getString("offerQuantity"));
//                                offres.add(offre);
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                System.out.println("errrrrrrrrrrrrrr " + error.getMessage());
//                Toast.makeText(getContext(), "Pas de connexion !", Toast.LENGTH_SHORT).show();
//            }
//        });
//        requestQueue = Volley.newRequestQueue(getContext());
//        requestQueue.add(request);
//        RecyclerOffreAdapter recyclerOffreAdapter = new RecyclerOffreAdapter(getContext(), offres, "");
//        recyclerOffre.setLayoutManager(new LinearLayoutManager(getContext()));
//        recyclerOffre.setAdapter(recyclerOffreAdapter);
//    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}