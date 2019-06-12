package com.example.unnamedapp.main;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.unnamedapp.R;
import com.example.unnamedapp.model.AvatarTransformation;
import com.example.unnamedapp.model.data.SubscriptionData;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SubscriptionsListAdapter extends RecyclerView.Adapter {

    private MainActivity mActivity;
    private int mSelectedIndex;
    private Context mContext;
    private ArrayList<SubscriptionData> mList;

    public SubscriptionsListAdapter(Context context, MainActivity activity) {
        mContext = context;
        this.mList = new ArrayList<SubscriptionData>();
        mSelectedIndex = -1;
        mActivity = activity;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.list_item_subscription, viewGroup, false);
        return new SubscriptionsListAdapter.SubscriptionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
        SubscriptionViewHolder holder = (SubscriptionViewHolder) viewHolder;
        holder.textViewName.setText(mList.get(i).name);
        Picasso.with(mContext)
                .load(mList.get(i).image)
                .transform(new AvatarTransformation())
                .into(holder.imageViewAvatar);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.hideDrawerLayout();
                mSelectedIndex = i;
                notifyDataSetChanged();
            }
        });
        if(mSelectedIndex == i && mSelectedIndex > -1) {
            holder.itemView.setBackgroundColor(mContext.getResources().getColor(R.color.colorPrimary));
            holder.textViewName.setTextColor(Color.parseColor("#ffffff"));
        }
    }

    public void resetSelectedIndex(){
        mSelectedIndex = -1;
        notifyDataSetChanged();
    }

    public void addSubscriptions(ArrayList<SubscriptionData> subscriptions){
        mList.addAll(subscriptions);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class SubscriptionViewHolder extends RecyclerView.ViewHolder{

        TextView textViewName;
        ImageView imageViewAvatar;
        ConstraintLayout layout;

        public SubscriptionViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = (TextView) itemView.findViewById(R.id.textViewSubscriptionName);
            imageViewAvatar = (ImageView) itemView.findViewById(R.id.imageViewSubscriptionAvatar);
            layout = (ConstraintLayout) itemView.findViewById(R.id.item_post);
        }
    }


}
