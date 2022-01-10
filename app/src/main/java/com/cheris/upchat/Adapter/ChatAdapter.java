package com.cheris.upchat.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cheris.upchat.ChatDetailActivity;
import com.cheris.upchat.Model.User;
import com.cheris.upchat.R;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {


    ArrayList<User> list;
    Context context;

    public ChatAdapter(ArrayList<User> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_rv_sample, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = list.get(position);
        Glide.with(context).load(user.getProfile()).placeholder(R.drawable.ic_profile).into(holder.image);
        holder.chatUserName.setText(user.getName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatDetailActivity.class);
                intent.putExtra("userId",user.getUserID());
                intent.putExtra("profilePic",user.getProfile());
                intent.putExtra("userName",user.getName());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView chatUserName, lastMessage;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.profile_image);
            chatUserName = itemView.findViewById(R.id.chatUserName);
            lastMessage = itemView.findViewById(R.id.lastMessage);
        }
    }
}
