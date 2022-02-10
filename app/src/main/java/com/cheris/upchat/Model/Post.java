package com.cheris.upchat.Model;

import java.util.HashMap;

public class Post {

    private String postId;     // 올린 사람 Id
    private String postImage;  // 올린 이미지
    private String postedBy;    // 올린 사람
    private String postDescription;  // 올린 글
    private long postedAt;        // 올린 날짜
    private int commentCount;     //글에 달린 댓글 수
    public int postLike = 0;         //글에 달린 좋아요
    public HashMap<String, Boolean> likes = new HashMap<>();          // 글에 좋아요 한 사람들



    public Post(String postId, String postImage, String postedBy, String postDescription, long postedAt) {
        this.postId = postId;
        this.postImage = postImage;
        this.postedBy = postedBy;
        this.postDescription = postDescription;
        this.postedAt = postedAt;
    }

    public Post() {
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getPostImage() {
        return postImage;
    }

    public void setPostImage(String postImage) {
        this.postImage = postImage;
    }

    public String getPostedBy() {
        return postedBy;
    }

    public void setPostedBy(String postedBy) {
        this.postedBy = postedBy;
    }

    public String getPostDescription() {
        return postDescription;
    }

    public void setPostDescription(String postDescription) {
        this.postDescription = postDescription;
    }

    public long getPostedAt() {
        return postedAt;
    }

    public void setPostedAt(long postedAt) {
        this.postedAt = postedAt;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public int getPostLike() {
        return postLike;
    }

    public void setPostLike(int postLike) {
        this.postLike = postLike;
    }

    public HashMap<String, Boolean> getLikes() {
        return likes;
    }

    public void setLikes(HashMap<String, Boolean> likes) {
        this.likes = likes;
    }
}
