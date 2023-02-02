package com.example.pharmacieapplication.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.pharmacieapplication.Activities.ConversationActivity;
import com.example.pharmacieapplication.Models.Replay;
import com.example.pharmacieapplication.Models.StaticVariable;
import com.example.pharmacieapplication.R;

import java.util.ArrayList;

/**
 * Created by Aws on 11/03/2018.
 */

public class RecyclerConversationAdapter extends RecyclerView.Adapter<RecyclerConversationAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<Replay> chatList;
    //RequestOptions option;
    private String type;
    private Activity activity;


    public RecyclerConversationAdapter(Context mContext, ArrayList<Replay> messageList, String type) {
        this.mContext = mContext;
        this.chatList = messageList;
        this.type = type;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {

        View view;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        view = inflater.inflate(R.layout.item_chat_sender, parent, false);
        final MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        if (chatList.get(position).getSenderEmail() == null) {
            if (!StaticVariable.user.getEmail().equals(chatList.get(position).getReciverEmail())) {
                holder.replayBody.setText(chatList.get(position).getReplay());
                holder.sunder.setVisibility(View.VISIBLE);
                holder.reciver.setVisibility(View.GONE);
            } else {
                holder.replayBodyReciver.setText(chatList.get(position).getReplay());
                holder.reciver.setVisibility(View.VISIBLE);
                holder.sunder.setVisibility(View.GONE);
            }
        } else {
            if (StaticVariable.user.getEmail().equals(chatList.get(position).getSenderEmail())) {
                holder.replayBody.setText(chatList.get(position).getReplay());
                holder.sunder.setVisibility(View.VISIBLE);
                holder.reciver.setVisibility(View.GONE);
            } else {
                holder.replayBodyReciver.setText(chatList.get(position).getReplay());
                holder.reciver.setVisibility(View.VISIBLE);
                holder.sunder.setVisibility(View.GONE);
            }
        }
        Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.fade_trans_anim);
        holder.view_container.startAnimation(animation);
        //holder.image.setImageResource(foodList.get(position).getImg());

        // Load Image from the internet and set it into Imageview using Glide
        //Glide.with(mContext).load(mData.get(position).getImage_url()).apply(option).into(holder.layout_thumbnail);
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView replayBody, replayBodyReciver;
        RelativeLayout view_container, sunder, reciver;

        public MyViewHolder(View itemView) {
            super(itemView);
            view_container = itemView.findViewById(R.id.container);
            replayBody = itemView.findViewById(R.id.replayBody);
            replayBodyReciver = itemView.findViewById(R.id.replayBodyReciver);
            sunder = itemView.findViewById(R.id.sender);
            reciver = itemView.findViewById(R.id.reciver);

        }
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }


    //    @Override
//    public int getViewTypeCount() {
//        return getCount();
//    }
//
//    @Override
//    public int getItemViewType(int position) {
//        return position;
//    }
}
