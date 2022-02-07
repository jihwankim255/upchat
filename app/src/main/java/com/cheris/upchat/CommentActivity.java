package com.cheris.upchat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.cheris.upchat.Adapter.CommentAdapter;
import com.cheris.upchat.Model.Comment;
import com.cheris.upchat.Model.Notification;
import com.cheris.upchat.Model.Post;
import com.cheris.upchat.Model.User;
import com.cheris.upchat.databinding.ActivityCommentBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

public class CommentActivity extends AppCompatActivity {
    Context context;
    ActivityCommentBinding binding;
    Intent intent;
    String postId;
    String postedBy;
    FirebaseDatabase database;
    FirebaseAuth auth;
    ArrayList<Comment> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCommentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        intent = getIntent();

        setSupportActionBar(binding.toolbar2);
        CommentActivity.this.setTitle("Comments");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        postId = intent.getStringExtra("postId");
        postedBy = intent.getStringExtra("postedBy");

        database.getReference()
                .child("posts")
                .child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Post post = snapshot.getValue(Post.class);
//                Picasso.get()
                if (post != null) {
                    Glide.with(CommentActivity.this)
                            .load(post.getPostImage())
                            .placeholder(R.drawable.placeholder)
                            .into(binding.postImage);
                    binding.description.setText(post.getPostDescription());
                    binding.like.setText(post.getPostLike()+"");
                    binding.comment.setText(post.getCommentCount()+"");

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //
        database.getReference()
                .child("Users")
                .child(postedBy).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
//                Picasso.get()
                try {
                    Glide.with(CommentActivity.this)
                            .load(user.getProfile())
                            .placeholder(R.drawable.placeholder)
                            .into(binding.notificationProfile); // 왜이러는지모름
                    binding.name.setText(user.getName());
                } catch (Exception e) {
                    //???
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.commentPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Comment comment = new Comment();
                comment.setCommentBody(binding.commentET.getText().toString());
                comment.setCommentedAt(new Date().getTime());
                comment.setCommentedBy(FirebaseAuth.getInstance().getUid());
                if (binding.commentET.getText().toString().length() == 0 ) {
                    Toast.makeText(CommentActivity.this, "Please write a comment.", Toast.LENGTH_SHORT).show();
                } else {
                    database.getReference()
                            .child("posts")
                            .child(postId)
                            .child("comments")
                            .push()
                            .setValue(comment).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            database.getReference()
                                    .child("posts")
                                    .child(postId)
                                    .child("commentCount").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    int commentCount = 0;
                                    if (snapshot.exists()){
                                        commentCount = snapshot.getValue(Integer.class);
                                    }
                                    database.getReference()
                                            .child("posts")
                                            .child(postId)
                                            .child("commentCount")
                                            .setValue(commentCount + 1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            binding.commentET.setText("");
                                            Toast.makeText(CommentActivity.this, "Commented", Toast.LENGTH_SHORT).show();

                                            Notification notification = new Notification();
                                            notification.setNotificationBy(FirebaseAuth.getInstance().getUid());
                                            notification.setNotificationAt(new Date().getTime());
                                            notification.setPostID(postId);
                                            notification.setPostedBy(postedBy);
                                            notification.setType("comment");

                                            FirebaseDatabase.getInstance().getReference()
                                                    .child("notification")
                                                    .child(postedBy)
                                                    .push()
                                                    .setValue(notification);
                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    });
                }


            }
        });
        CommentAdapter adapter = new CommentAdapter(this, list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.commentRv.setLayoutManager(layoutManager);
        binding.commentRv.setAdapter(adapter);

        database.getReference()
                .child("posts")
                .child(postId)
                .child("comments").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Comment comment = dataSnapshot.getValue(Comment.class);
                    list.add(comment);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
}