package com.cheris.upchat.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cheris.upchat.Model.Follow;
import com.cheris.upchat.Model.Notification;
import com.cheris.upchat.Model.User;
import com.cheris.upchat.R;
import com.cheris.upchat.databinding.UserSampleBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.viewHolder>{

    Context context;
    ArrayList<User> list;

    public UserAdapter(Context context, ArrayList<User> list) {
        this.context = context;
        this.list = list;
    }


    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_sample, parent,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        User user = list.get(position);
//        Picasso.get()
        Glide.with(context)
                .load(user.getProfile())
                .placeholder(R.drawable.placeholder)
                .into(holder.binding.notificationProfile);
        holder.binding.name.setText(user.getName());
        holder.binding.profession.setText(user.getProfession());

        // followed되고 다른화면 갔다왔는데 다시 follow로 바뀐것을 막기위해 미리 follow 목록을 확인시킨다
        FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(user.getUserID())
                .child("followers")
                .child(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    holder.binding.followBtn.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.follow_active_btn));
                    holder.binding.followBtn.setText("Following");
                    holder.binding.followBtn.setTextColor(context.getResources().getColor(R.color.grey));
                    holder.binding.followBtn.setEnabled(false);
                } else {
                    // follow 버튼이 눌렸을 때
                    holder.binding.followBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Follow follow = new Follow();
                            follow.setFollowedBy(FirebaseAuth.getInstance().getUid());
                            follow.setFollowedAt(new Date().getTime());

                            FirebaseDatabase.getInstance().getReference()
                                    .child("Users")
                                    .child(user.getUserID())
                                    .child("followers")
                                    .child(FirebaseAuth.getInstance().getUid())
                                    .setValue(follow).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    FirebaseDatabase.getInstance().getReference()
                                            .child("Users")
                                            .child(user.getUserID())
                                            .child("followerCount")
                                            .setValue(user.getFollowerCount() + 1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            holder.binding.followBtn.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.follow_active_btn));
                                            holder.binding.followBtn.setText("Following");
                                            holder.binding.followBtn.setTextColor(context.getResources().getColor(R.color.grey));
                                            holder.binding.followBtn.setEnabled(false);

                                            Toast.makeText(context, "You Followed " + user.getName(), Toast.LENGTH_SHORT).show();

                                            Notification notification = new Notification();
                                            notification.setNotificationBy(FirebaseAuth.getInstance().getUid());
                                            notification.setNotificationAt(new Date().getTime());
                                            notification.setType("follow");

                                            FirebaseDatabase.getInstance().getReference()
                                                    .child("notification")
                                                    .child(user.getUserID())
                                                    .push()
                                                    .setValue(notification);
                                        }
                                    });
                                }
                            });
                        }
                    });

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

    public class viewHolder extends RecyclerView.ViewHolder{
        UserSampleBinding binding;
        public viewHolder(@NonNull View itemView) {
            super(itemView);

            binding = UserSampleBinding.bind(itemView);
        }
    }
}