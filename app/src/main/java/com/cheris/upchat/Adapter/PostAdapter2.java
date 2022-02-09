package com.cheris.upchat.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cheris.upchat.CommentActivity;
import com.cheris.upchat.Model.Post;
import com.cheris.upchat.Model.User;
import com.cheris.upchat.R;
import com.cheris.upchat.databinding.RvSamplePostBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;


public class PostAdapter2 extends  RecyclerView.Adapter<PostAdapter2.viewHolder>{

    // Initialize variable
    ArrayList<Post> list;
    Context context;

    FirebaseAuth auth;
    FirebaseStorage storage;
    FirebaseDatabase database;

    // Create constructor
    public PostAdapter2(ArrayList<Post> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Initialize view
        View view = LayoutInflater.from(context).inflate(R.layout.rv_sample_post, parent, false);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        // Initialize main data

        Post model = list.get(position);
//        Picasso.get()
        try {
            if (model.getPostImage() != null) {
                Glide.with(context)
                        .load(model.getPostImage())
                        .placeholder(R.drawable.placeholder)
                        .into(holder.binding.postImage);
            } else {
                holder.binding.postImage.setVisibility(View.GONE);
            }
        } catch (Exception e) {}

        holder.binding.like.setText(model.getPostLike()+"");
        holder.binding.comment.setText(model.getCommentCount()+"");
        String description = model.getPostDescription();
        if (description.trim().length() < 5){
            holder.binding.postDescription.setVisibility(View.GONE);

        } else {
            holder.binding.postDescription.setText(model.getPostDescription());
            holder.binding.postDescription.setVisibility(View.VISIBLE);
        }
        //  포스트 작성자의 프로필
        FirebaseDatabase.getInstance().getReference().child("Users")  //
                .child(model.getPostedBy()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    User user = snapshot.getValue(User.class);
//                Picasso.get()
                    Glide.with(context)
                            .load(user.getProfile())
                            .placeholder(R.drawable.placeholder)
                            .into(holder.binding.notificationProfile);
                    holder.binding.userName.setText(user.getName());
                    holder.binding.bio.setText(user.getProfession());
                } catch (Exception e) {
                    Glide.with(context)
                            .load("https://firebasestorage.googleapis.com/v0/b/upchat-a0789.appspot.com/o/profile_image%2Fdefault_profile.jpg?alt=media&token=e96d4a33-cc51-4f47-9097-b349735488de")
                            .placeholder(R.drawable.placeholder)
                            .into(holder.binding.notificationProfile);
                    holder.binding.userName.setText("(Deleted user)");
                    holder.binding.userName.setTypeface(holder.binding.userName.getTypeface(), Typeface.NORMAL);
                    holder.binding.bio.setText("");
                    holder.binding.postImage.setVisibility(View.GONE);
                    holder.binding.postDescription.setText("");
                    holder.binding.like.setClickable(false);
                    holder.binding.comment.setClickable(false);
                    holder.binding.share.setClickable(false);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //   색깔을 정함
        FirebaseDatabase.getInstance().getReference()
                .child("posts")
                .child(model.getPostId())
                .child("likes")
                .child(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){  // 처음 불러올때 좋아요 데이터가있으면
                    holder.binding.like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like_2,0,0,0);
                } else { //없으면
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        // 더보기 버튼
        holder.binding.btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(context,v);
                popupMenu.getMenuInflater().inflate(R.menu.post_menu,popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.report:
                                Toast.makeText(context,"신고",Toast.LENGTH_SHORT).show();

                                break;
                            case R.id.hide:
                                Toast.makeText(context,"채팅하기",Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.delete:
                                Toast.makeText(context,"삭제",Toast.LENGTH_SHORT).show();
                                AlertDialog alertbox = new AlertDialog.Builder(context)
                                        .setMessage("Do you want to delete this post?")
                                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                return;
                                            }
                                        }).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                database.getReference().child("posts").child(model.getPostId()).removeValue();
                                                try {
                                                    storage.getReference().child("posts").child(""+model.getPostedAt()).delete();
                                                } catch (Exception e){

                                                }


                                                notifyItemRemoved(holder.getAdapterPosition());
                                            }
                                        }).show();
                                break;
                            default:
                                break;
                        }
                        return false;
                    }
                });
                // 글쓴이와 로그인유저가 동일하면 삭제하기, 다르면 신고하기
                if (auth.getUid().equals( model.getPostedBy())) {
                    popupMenu.getMenu().getItem(0).setVisible(false);
                    popupMenu.getMenu().getItem(1).setVisible(false);
                } else  {
                    popupMenu.getMenu().getItem(2).setVisible(false);
                }
                popupMenu.show();//Popup Menu 보이기

            }
        });

        // 커멘트 Activity로 이동하는 버튼
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
        holder.binding.postDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CommentActivity.class);
                intent.putExtra("postId",model.getPostId());
                intent.putExtra("postedBy", model.getPostedBy());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
        holder.binding.empty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CommentActivity.class);
                intent.putExtra("postId",model.getPostId());
                intent.putExtra("postedBy", model.getPostedBy());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

        holder.binding.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!model.getLikes().containsKey(auth.getUid())){
                    Toast.makeText(context, ""+ model.getLikes(), Toast.LENGTH_SHORT).show();
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
                                    .setValue(model.getPostLike() + 1);

                        }
                    });
                } else {
                    FirebaseDatabase.getInstance().getReference()
                            .child("posts")
                            .child(model.getPostId())
                            .child("likes")
                            .child(FirebaseAuth.getInstance().getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            FirebaseDatabase.getInstance().getReference()
                                    .child("posts")
                                    .child(model.getPostId())
                                    .child("postLike")
                                    .setValue(model.getPostLike() - 1);
                        }
                    });
                }
            }
        });

    }


    @Override
    public int getItemCount() {
        return list != null ?list.size() : 0;
    }

    public class viewHolder extends RecyclerView.ViewHolder{

        RvSamplePostBinding binding;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            // 이부분 안쓰면 nullPointerException 뜸. rv_sample과 연결해주는 부분
            binding = RvSamplePostBinding.bind(itemView);


        }
    }
}

//.addOnSuccessListener(new OnSuccessListener<Void>() {
//@Override
//public void onSuccess(Void unused) {
//                                Notification notification = new Notification();
//                                notification.setNotificationBy(FirebaseAuth.getInstance().getUid());
//                                notification.setNotificationAt(new Date().getTime());
//                                notification.setPostID(model.getPostId());
//                                notification.setPostedBy(model.getPostedBy());
//                                notification.setType("like");
//
//                                FirebaseDatabase.getInstance().getReference()
//                                        .child("notification")
//                                        .child(model.getPostedBy())
//                                        .push()
//                                        .setValue(notification);

//        }
//        });