package com.cheris.upchat.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cheris.upchat.CommentActivity;
import com.cheris.upchat.Model.Notification;
import com.cheris.upchat.Model.Post;
import com.cheris.upchat.Model.User;
import com.cheris.upchat.R;
import com.cheris.upchat.databinding.DashboardRvSampleBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;


public class PostAdapter extends  RecyclerView.Adapter<PostAdapter.viewHolder>{

    // Initialize variable
    ArrayList<Post> list;
    Context context;

    // Create constructor
    public PostAdapter(ArrayList<Post> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Initialize view
        View view = LayoutInflater.from(context).inflate(R.layout.dashboard_rv_sample, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        // Initialize main data

        Post model = list.get(position);
//        Picasso.get()
        if (model.getPostImage() != null ){
            Glide.with(context)
                    .load(model.getPostImage())
                    .placeholder(R.drawable.placeholder)
                    .into(holder.binding.postImage);
        } else {
            holder.binding.postImage.setVisibility(View.GONE);
        }

        holder.binding.like.setText(model.getPostLike()+"");
        holder.binding.comment.setText(model.getCommentCount()+"");
        String description = model.getPostDescription();
        if (description.equals("")){
            holder.binding.postDescription.setVisibility(View.GONE);
        } else {
            holder.binding.postDescription.setText(model.getPostDescription());
            holder.binding.postDescription.setVisibility(View.VISIBLE);
        }
        // 알림
        FirebaseDatabase.getInstance().getReference().child("Users")
                .child(model.getPostedBy()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
//                Picasso.get()
                Glide.with(context)
                        .load(user.getProfile())
                        .placeholder(R.drawable.placeholder)
                        .into(holder.binding.notificationProfile);
                holder.binding.userName.setText(user.getName());
                holder.binding.bio.setText(user.getProfession());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //
        FirebaseDatabase.getInstance().getReference()
                .child("posts")
                .child(model.getPostId())
                .child("likes")
                .child(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    holder.binding.like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like_2,0,0,0);
                } else {
                    holder.binding.like.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FirebaseDatabase.getInstance().getReference()
                                    .child("posts")
                                    .child(model.getPostId())
                                    .child("likes")
                                    .child(FirebaseAuth.getInstance().getUid())
                                    .setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    FirebaseDatabase.getInstance().getReference()
                                            .child("posts")
                                            .child(model.getPostId())
                                            .child("postLike")
                                            .setValue(model.getPostLike() + 1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            holder.binding.like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like_2,0,0,0);

                                            Notification notification = new Notification();
                                            notification.setNotificationBy(FirebaseAuth.getInstance().getUid());
                                            notification.setNotificationAt(new Date().getTime());
                                            notification.setPostID(model.getPostId());
                                            notification.setPostedBy(model.getPostedBy());
                                            notification.setType("like");

                                            FirebaseDatabase.getInstance().getReference()
                                                    .child("notification")
                                                    .child(model.getPostedBy())
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

        holder.binding.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CommentActivity.class);
                intent.putExtra("postId",model.getPostId());
                intent.putExtra("postedBy", model.getPostedBy());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{

        DashboardRvSampleBinding binding;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            // 이부분 안쓰면 nullPointerException 뜸. rv_sample과 연결해주는 부분
            binding = DashboardRvSampleBinding.bind(itemView);


        }
    }
}