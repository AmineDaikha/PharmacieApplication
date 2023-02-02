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
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.pharmacieapplication.Activities.ConversationActivity;
import com.example.pharmacieapplication.Activities.OffreActivity;
import com.example.pharmacieapplication.Models.Message;
import com.example.pharmacieapplication.R;

import java.util.ArrayList;

/**
 * Created by Aws on 11/03/2018.
 */

public class RecyclerMessageAdapter extends RecyclerView.Adapter<RecyclerMessageAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<Message> messageList;
    //RequestOptions option;
    private String type;
    private Activity activity;


    public RecyclerMessageAdapter(Context mContext, ArrayList<Message> messageList, String type) {
        this.mContext = mContext;
        this.messageList = messageList;
        this.type = type;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {

        View view;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        view = inflater.inflate(R.layout.item_message, parent, false);
        final MyViewHolder viewHolder = new MyViewHolder(view);
        viewHolder.view_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, ConversationActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("message", messageList.get(viewHolder.getAdapterPosition()));
                intent.putExtras(bundle);
                mContext.startActivity(intent);
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.subjectMessage.setText(messageList.get(position).getSubject());
        holder.contentMessage.setText(messageList.get(position).getContent());
        holder.dateMessage.setText(messageList.get(position).getDate());
        if (messageList.get(position).isByMe())
            holder.byMe.setVisibility(View.VISIBLE);
        if (messageList.get(position).isRead())
            holder.newReplay.setVisibility(View.GONE);
        else {
            holder.newReplay.setVisibility(View.VISIBLE);
            holder.newReplay.setText("1");
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.fade_trans_anim);
        holder.view_container.startAnimation(animation);
        //holder.image.setImageResource(foodList.get(position).getImg());

        // Load Image from the internet and set it into Imageview using Glide
        //Glide.with(mContext).load(mData.get(position).getImage_url()).apply(option).into(holder.layout_thumbnail);
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView subjectMessage, contentMessage, dateMessage, newReplay, byMe;
        RelativeLayout view_container;

        public MyViewHolder(View itemView) {
            super(itemView);
            view_container = itemView.findViewById(R.id.container);
            subjectMessage = itemView.findViewById(R.id.subjectMessage);
            byMe = itemView.findViewById(R.id.byMe);
            contentMessage = itemView.findViewById(R.id.contentMessage);
            dateMessage = itemView.findViewById(R.id.dateMessage);
            newReplay = itemView.findViewById(R.id.newReplay);
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
