package com.example.unnamedapp.main.social_webs;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.unnamedapp.R;
import com.example.unnamedapp.main.MainActivity;
import com.example.unnamedapp.main.SubscriptionsListAdapter;
import com.example.unnamedapp.model.AvatarTransformation;
import com.example.unnamedapp.model.Constants;
import com.example.unnamedapp.model.data.SubscriptionData;
import com.example.unnamedapp.new_subscription.NewSubscriptionActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ChooseSubscriptionAdapter extends RecyclerView.Adapter {

    private int mSocialWebId;
    private String mUserSocialId;
    private String mToken;
    private Context mContext;
    private ArrayList<SubscriptionData> mList;

    public ChooseSubscriptionAdapter(Context context, String token, ArrayList<SubscriptionData> list, int socialWebId, String userSocialId) {
        mContext = context;
        mToken = token;
        mList = list;
        mSocialWebId = socialWebId;
        mUserSocialId = userSocialId;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.list_item_subscription_without_editor, viewGroup, false);
        return new ChooseSubscriptionAdapter.SubscriptionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
        ChooseSubscriptionAdapter.SubscriptionViewHolder holder = (ChooseSubscriptionAdapter.SubscriptionViewHolder) viewHolder;
        holder.textViewName.setText(mList.get(i).name);
        Picasso.with(mContext)
                .load(mList.get(i).image)
                .transform(new AvatarTransformation())
                .into(holder.imageViewAvatar);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, NewSubscriptionActivity.class);
                intent.putExtra("token", mToken);
                intent.putExtra("editor", true);
                intent.putExtra("id", mList.get(i).id);
                intent.putExtra("name", mList.get(i).name);
                intent.putExtra("image", mList.get(i).image);
                if(mSocialWebId == Constants.INSTAGRAM_ID){
                    intent.putExtra("instagram_id", mUserSocialId);
                }else {
                    intent.putExtra("instagram_id", mList.get(i).instagram_id);
                }
                if(mSocialWebId == Constants.TWITTER_ID) {
                    intent.putExtra("twitter_id", mUserSocialId);
                }else{
                    intent.putExtra("twitter_id", mList.get(i).twitter_id);
                }
                if(mSocialWebId == Constants.YOUTUBE_ID){
                    intent.putExtra("youtube_id", mUserSocialId);
                }else{
                    intent.putExtra("youtube_id", mList.get(i).youtube_id);
                }
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount()  {
        return mList.size();
    }

    public static class SubscriptionViewHolder extends RecyclerView.ViewHolder{

        TextView textViewName;
        ImageView imageViewAvatar;

        public SubscriptionViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = (TextView) itemView.findViewById(R.id.textViewSubscriptionName);
            imageViewAvatar = (ImageView) itemView.findViewById(R.id.imageViewSubscriptionAvatar);
        }
    }
}
