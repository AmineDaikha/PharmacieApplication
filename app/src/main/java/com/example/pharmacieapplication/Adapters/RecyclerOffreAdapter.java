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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.pharmacieapplication.Activities.OffreActivity;
import com.example.pharmacieapplication.Models.Offre;
import com.example.pharmacieapplication.Models.StaticVariable;
import com.example.pharmacieapplication.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Aws on 11/03/2018.
 */

public class RecyclerOffreAdapter extends RecyclerView.Adapter<RecyclerOffreAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<Offre> offreList;
    //RequestOptions option;
    private String type;
    private Activity activity;


    public RecyclerOffreAdapter(Context mContext, ArrayList<Offre> offreList, String type) {
        this.mContext = mContext;
        this.offreList = offreList;
        this.type = type;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {

        View view;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        view = inflater.inflate(R.layout.item_offre, parent, false);
        final MyViewHolder viewHolder = new MyViewHolder(view);
        viewHolder.view_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, OffreActivity.class);
                Bundle bundle = new Bundle();
                offreList.get(viewHolder.getAdapterPosition()).setType(type);
                bundle.putSerializable("offre", offreList.get(viewHolder.getAdapterPosition()));
                intent.putExtras(bundle);
                mContext.startActivity(intent);
            }
        });
        if (!type.equals("my")) {
            viewHolder.fav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (offreList.get(viewHolder.getAdapterPosition()).isFav()) {
                        viewHolder.fav.setImageResource(R.drawable.ic_favorite_border);
                        offreList.get(viewHolder.getAdapterPosition()).setFav(false);
                        StaticVariable.db.deleteOffre(offreList.get(viewHolder.getAdapterPosition()).getOfferId());
                    } else {
                        viewHolder.fav.setImageResource(R.drawable.ic_favorite);
                        offreList.get(viewHolder.getAdapterPosition()).setFav(true);
                        StaticVariable.db.insertOffre(offreList.get(viewHolder.getAdapterPosition()));
                    }
                }
            });
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Picasso.get().load(offreList.get(position).getOfferImage()).into(holder.image);
        if (type.equals("my"))
            holder.fav.setVisibility(View.GONE);
        else
            holder.fav.setVisibility(View.VISIBLE);
        holder.nameOffre.setText(offreList.get(position).getOfferName());
        holder.descOffre.setText(offreList.get(position).getOfferDesc());
        holder.priceOffre.setText(offreList.get(position).getOfferPrice());
        holder.dateOffre.setText(offreList.get(position).getOfferStartDate());
        if (!offreList.get(position).isFav())
            holder.fav.setImageResource(R.drawable.ic_favorite_border);
        else
            holder.fav.setImageResource(R.drawable.ic_favorite);

        Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.fade_trans_anim);
        holder.view_container.startAnimation(animation);
        //holder.image.setImageResource(foodList.get(position).getImg());

        // Load Image from the internet and set it into Imageview using Glide
        //Glide.with(mContext).load(mData.get(position).getImage_url()).apply(option).into(holder.layout_thumbnail);
    }

    @Override
    public int getItemCount() {
        return offreList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView nameOffre, descOffre, priceOffre, dateOffre;
        ImageView image, fav;
        LinearLayout view_container;

        public MyViewHolder(View itemView) {
            super(itemView);
            view_container = itemView.findViewById(R.id.container);
            nameOffre = itemView.findViewById(R.id.nameOffre);
            descOffre = itemView.findViewById(R.id.descOffre);
            priceOffre = itemView.findViewById(R.id.priceOffre);
            image = itemView.findViewById(R.id.imgOffre);
            dateOffre = itemView.findViewById(R.id.dateEndOffre);
            fav = itemView.findViewById(R.id.fav);
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
