package com.cheris.upchat.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cheris.upchat.ChatDetailActivity;
import com.cheris.upchat.Model.User;
import com.cheris.upchat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

        FirebaseDatabase.getInstance().getReference().child("chats")
                .child(FirebaseAuth.getInstance().getUid() + user.getUserID())
                .orderByChild("timestamp")
                .limitToLast(1)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChildren()) {
                            for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                holder.lastMessage.setText(snapshot1.child("message").getValue().toString());
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                String blockUser = context.getResources().getString(R.string.block);
                String reportUser = context.getResources().getString(R.string.report);
                final CharSequence[] items = {blockUser, reportUser}; //, "(나가기)" "(즐겨찾기)","(알림 끄기)" ,

                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());

                builder.setTitle(user.getName());
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        switch (item) {
                            case 0:
                                AlertDialog.Builder builderBlock = new AlertDialog.Builder(v.getContext());
                                builderBlock.setTitle(R.string.block).setMessage(String.format(context.getResources().getString(R.string.blockDialog),user.getName())).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                }).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                }).show();

                                break;
                            case 1:

                                break;
                            default:
                                break;
                        }

                    }
                });
                builder.show();
                return true;
            }
        });

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
