package com.example.unnamedapp.main;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.unnamedapp.R;
import com.example.unnamedapp.model.AvatarTransformation;
import com.example.unnamedapp.model.data.SubscriptionData;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SubscriptionsListAdapter extends RecyclerView.Adapter {

    private MainActivity mActivity;
    private Context mContext;
    private ArrayList<SubscriptionData> mList;

    public SubscriptionsListAdapter(Context context, MainActivity activity) {
        mContext = context;
        this.mList = new ArrayList<SubscriptionData>();
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
        if(mList.get(i).image == null) {
            Picasso.with(mContext)
                    .load(R.drawable.default_photo)
                    .transform(new AvatarTransformation())
                    .into(holder.imageViewAvatar);
        }else{
            Picasso.with(mContext)
                    .load(mList.get(i).image)
                    .transform(new AvatarTransformation())
                    .into(holder.imageViewAvatar);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.hideDrawerLayout();
                //mSelectedIndex = i;
                notifyDataSetChanged();
                mActivity.showWall();
                mActivity.createWallAdapter();
                mActivity.fetchPosts(mList.get(i));
                mActivity.showLoading();
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //mActivity.deleteSubscription(mList.get(i).id);
                onCreateDialog(i).show();
                return true;
            }
        });
        holder.imageViewEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCreateDialog(i).show();
            }
        });
    }


    public void addSubscriptions(ArrayList<SubscriptionData> subscriptions){
        mList.clear();
        mList.addAll(subscriptions);
        notifyDataSetChanged();
    }

    protected Dialog onCreateDialog(final int i) {
        final String[] choiсe = new  String[]{mActivity.getResources().getString(R.string.edit),
        mActivity.getResources().getString(R.string.delete)};
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setItems(choiсe,
                new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                switch (item){
                    case 0:
                        mActivity.openEditActivity(mList.get(i));
                        break;
                    case 1:
                        mActivity.deleteSubscription(mList.get(i).id);
                        break;
                }
                dialog.cancel();
            }
        });
        //builder.setCancelable(false);
        return builder.create();
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class SubscriptionViewHolder extends RecyclerView.ViewHolder{

        TextView textViewName;
        ImageView imageViewAvatar;
        ImageView imageViewEdit;
        ConstraintLayout layout;

        public SubscriptionViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = (TextView) itemView.findViewById(R.id.textViewSubscriptionName);
            imageViewAvatar = (ImageView) itemView.findViewById(R.id.imageViewSubscriptionAvatar);
            imageViewEdit = (ImageView) itemView.findViewById(R.id.imageViewEdit);
            layout = (ConstraintLayout) itemView.findViewById(R.id.item_post);
        }
    }


}
