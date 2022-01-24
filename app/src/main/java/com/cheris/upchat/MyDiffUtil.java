package com.cheris.upchat;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import com.cheris.upchat.Model.Post;

import java.util.ArrayList;

public class MyDiffUtil extends DiffUtil.Callback {

    private final ArrayList<Post> mOldPostList;
    private final ArrayList<Post> mNewPostList;

    public MyDiffUtil(ArrayList<Post> mOldPostList, ArrayList<Post> mNewPostList) {
        this.mOldPostList = mOldPostList;
        this.mNewPostList = mNewPostList;
    }

    @Override
    public int getOldListSize() {
        return mOldPostList.size();
    }

    @Override
    public int getNewListSize() {
        return mNewPostList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return mOldPostList.get(oldItemPosition).getPostId() == mNewPostList.get(
                newItemPosition).getPostId();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        final Post oldPost = mOldPostList.get(oldItemPosition);
        final Post newPost = mNewPostList.get(newItemPosition);

        return oldPost.getPostDescription().equals(newPost.getPostDescription());
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        // Implement method if you're going to use ItemAnimator
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}