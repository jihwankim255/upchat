package com.cheris.upchat.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cheris.upchat.Model.Follow;
import com.cheris.upchat.Model.User;
import com.cheris.upchat.R;
//import com.cheris.upchat.databinding.FriendRvSampleBinding;
import com.cheris.upchat.databinding.RvSampleFriendBinding;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FollowersAdapter extends RecyclerView.Adapter<FollowersAdapter.viewHolder> {

    ArrayList<Follow> list;
    Context context;

    // for deleted user
    FirebaseAuth auth;
    FirebaseDatabase database;

    public FollowersAdapter(ArrayList<Follow> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rv_sample_friend,parent,false);
        auth = FirebaseAuth.getInstance();  // 할당부분이 맞는지 부정확
        database = FirebaseDatabase.getInstance();
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        Follow follow = list.get(position);

        FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(follow.getFollowedBy()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
//                Picasso.get()
                try {
                    Glide.with(context)
                            .load(user.getProfile())
                            .placeholder(R.drawable.placeholder)
                            .into(holder.binding.notificationProfile);
                } catch (Exception e) {
                    // profile을 get할 수 없으면 해당 follower를 삭제
                    FirebaseDatabase.getInstance().getReference()
                            .child("Users").child(auth.getUid()).child("followers").child(follow.getFollowedBy()).removeValue();
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

    public class viewHolder extends RecyclerView.ViewHolder {


        RvSampleFriendBinding binding;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            binding = RvSampleFriendBinding.bind(itemView);

        }
    }
}
