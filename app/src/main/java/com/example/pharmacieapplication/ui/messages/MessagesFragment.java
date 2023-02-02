package com.example.pharmacieapplication.ui.messages;

import static com.example.pharmacieapplication.Models.StaticVariable.sendNotification;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.pharmacieapplication.Activities.AddOffreActivity;
import com.example.pharmacieapplication.Activities.SendMessageActivity;
import com.example.pharmacieapplication.Adapters.RecyclerMessageAdapter;
import com.example.pharmacieapplication.Models.Message;
import com.example.pharmacieapplication.Models.Replay;
import com.example.pharmacieapplication.Models.StaticVariable;
import com.example.pharmacieapplication.R;
import com.example.pharmacieapplication.databinding.FragmentMessagesBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MessagesFragment extends Fragment {

    private MessagesViewModel messagesViewModel;
    private FragmentMessagesBinding binding;
    RecyclerView recyclerMessages;
    TextView empty;
    FloatingActionButton addMessage;
    JsonObjectRequest request;
    private RequestQueue requestQueue;
    ArrayList<Message> messages = new ArrayList<>();


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        messagesViewModel =
                new ViewModelProvider(this).get(MessagesViewModel.class);

        binding = FragmentMessagesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        recyclerMessages = root.findViewById(R.id.listMessages);
        empty = root.findViewById(R.id.empty);
//        Message m = new Message();
//        m.setSubject("Subject");
//        m.setContent("Heloo !!");
//        m.setDate("2021-12-08");
//        m.setNbReply(4);
//        messages.add(m);
        addMessage = root.findViewById(R.id.addMessage);
        addMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (StaticVariable.user.isApproved()) {
                    startActivity(new Intent(getContext(), SendMessageActivity.class));
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
        getMessages();
        return root;
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
                    empty.setVisibility(View.GONE);
                    recyclerMessages.setVisibility(View.VISIBLE);
                    messages.clear();
                    //JSONObject j = new JSONObject(jsonObject.getJSONArray("messages"));
                    JSONObject j = new JSONObject(jsonObject.getString("messages"));
                    JSONArray msgs = new JSONArray(j.getString("sent"));
                    Replay replay;
                    for (int i = 0; i < msgs.length(); i++) {
                        JSONObject msg = new JSONObject(String.valueOf(msgs.getJSONObject(i)));
                        Message message = new Message();
                        replay = new Replay();
                        message.setId(msg.getInt("msgId"));
                        message.setSenderId(msg.getString("userId"));
                        message.setSenderEmail(msg.getString("userEmail"));
                        message.setSubject(msg.getString("msgSubject"));
                        message.setContent(msg.getString("msgBody"));
                        message.setDate(msg.getString("msgDate"));
                        if (msg.getString("isRead").equals("0"))
                            message.setRead(false);
                        else if (msg.getString("isRead").equals("1"))
                            message.setRead(true);
                        message.setByMe(true);
                        replay.setIdMsg(message.getId());
                        replay.setSenderEmail(message.getSenderEmail());
                        replay.setReplay(message.getContent());
                        message.getReplays().add(replay);
                        messages.add(message);
                    }
                    msgs = new JSONArray(j.getString("inbox"));
                    for (int i = 0; i < msgs.length(); i++) {
                        JSONObject msg = new JSONObject(String.valueOf(msgs.getJSONObject(i)));
                        Message message = new Message();
                        replay = new Replay();
                        message.setId(msg.getInt("msgId"));
                        message.setSenderId(msg.getString("userId"));
                        message.setSenderEmail(msg.getString("userEmail"));
                        message.setSubject(msg.getString("msgSubject"));
                        message.setContent(msg.getString("msgBody"));
                        message.setDate(msg.getString("msgDate"));
                        replay.setIdMsg(message.getId());
                        replay.setSenderEmail(message.getSenderEmail());
                        replay.setReplay(message.getContent());
                        if (msg.getString("isRead").equals("0"))
                            message.setRead(false);
                        else if (msg.getString("isRead").equals("1"))
                            message.setRead(true);
                        message.getReplays().add(replay);
                        messages.add(message);
                    }
                    Collections.sort(messages, new Comparator<Message>() {
                        @Override
                        public int compare(Message lhs, Message rhs) {
                            //return lhs.getId().compareTo(rhs.getId());
                            return rhs.getId() - lhs.getId();
                        }
                    });
                    //sendNotification(getActivity(), 1, "message");
                    //Collections.reverse(messages);
                    LinearLayoutManager llm = new LinearLayoutManager(MessagesFragment.this.getContext());
                    llm.setOrientation(LinearLayoutManager.VERTICAL);
                    RecyclerMessageAdapter recyclerMessageAdapter = new RecyclerMessageAdapter(getContext(), messages, "");
                    recyclerMessages.setLayoutManager(new LinearLayoutManager(getContext()));
                    recyclerMessages.setLayoutManager(llm);
                    recyclerMessages.setAdapter(recyclerMessageAdapter);
                } else if (jsonObject.getString("code").equals("404")) {
                    recyclerMessages.setVisibility(View.GONE);
                    empty.setVisibility(View.VISIBLE);
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
            AlertDialog.Builder adb = new AlertDialog.Builder(getContext());
            adb.setTitle("Erreur");
            adb.setMessage("Pas de connexion !");
            adb.setPositiveButton("Ok",
                    new AlertDialog.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }).show();
            //Toast.makeText(AddOffreActivity.this, "Error !! ", Toast.LENGTH_SHORT).show();
        });
        requestQueue = Volley.newRequestQueue(

                getContext());
        requestQueue.add(request);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        MenuItem item = menu.findItem(R.id.addOffre);
        item.setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onResume() {
        super.onResume();
        getMessages();
    }
}