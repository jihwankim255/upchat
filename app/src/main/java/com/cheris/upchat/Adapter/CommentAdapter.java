package com.cheris.upchat.Adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cheris.upchat.GlideApp;
import com.cheris.upchat.Model.Comment;
import com.cheris.upchat.Model.User;
import com.cheris.upchat.MyGlideApp;
import com.cheris.upchat.R;
//import com.cheris.upchat.databinding.CommentSampleBinding;
import com.cheris.upchat.databinding.RvSampleCommentBinding;
import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;



import java.util.ArrayList;
// 글 클릭시 댓글
public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.viewHolder>{

    Context context;
    ArrayList<Comment> list;

    public CommentAdapter(Context context, ArrayList<Comment> list) {
        this.context = context;
        this.list = list;
    }


    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rv_sample_comment, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        Comment comment = list.get(position);

        String time = TimeAgo.using(comment.getCommentedAt());
        holder.binding.commentBody.setText(comment.getCommentBody());
        // comment.getCommentBody()
//        holder.binding.comment.setText(comment.getCommentBody());
        FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(comment.getCommentedBy()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
//                Picasso.get()
                try {
                    Glide.with(context)
                            .load(user.getProfile())
                            .into(holder.binding.notificationProfile); // 맞음
                    holder.binding.nameTime.setText(Html.fromHtml("<b>" + user.getName() + "</b>   " + time));
                } catch (Exception e) {
                    //???
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

        RvSampleCommentBinding binding;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            binding = RvSampleCommentBinding.bind(itemView);
        }
    }
}