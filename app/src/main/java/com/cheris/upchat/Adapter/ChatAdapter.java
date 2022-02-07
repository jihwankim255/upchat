package com.cheris.upchat.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cheris.upchat.ChatDetailActivity;
import com.cheris.upchat.Model.User;
import com.cheris.upchat.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {


    ArrayList<User> list;
    Context context;

    // For block user
    FirebaseAuth auth;
    FirebaseDatabase database;

    public ChatAdapter(ArrayList<User> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rv_sample_chat, parent, false);
        auth = FirebaseAuth.getInstance();  // 할당부분이 맞는지 부정확
        database = FirebaseDatabase.getInstance();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = list.get(position);
        Glide.with(context).load(user.getProfile()).placeholder(R.drawable.ic_profile).into(holder.image);
        holder.chatUserName.setText(user.getName());

        // 최근 메세지
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
                                        blockUser(user.getUserID());
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

    private void blockUser(String Uid) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("uid",Uid);
        database.getReference().child("Users").child(auth.getUid()).child("BlockedUsers").child(Uid).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {  // 차단 성공
                        Toast.makeText(context, "Blocked Successfully.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {  // 차단 실패
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Failed: "+e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }
    private void unBlockUser(String Uid) {
        database.getReference().child("Users").child(auth.getUid()).child("BlockedUsers").orderByChild(Uid).equalTo(Uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {  // BlockedUsers리스트에 있는 해당 Uid의 값을 불러온다
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                            if (dataSnapshot.exists()) {
                                dataSnapshot.getRef().removeValue()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(context, "Unblocked successfully.", Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(context, "Failed: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

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
