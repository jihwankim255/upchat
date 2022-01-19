package com.cheris.upchat.Model;

public class User {
    private String name, profession, email, password;
    private String coverPhoto;
    private String userID;
    private int followerCount;
    private String profile;
    private String lastMessage;
    private String blockUser;

    public User() {

    }

    public String getCoverPhoto() {
        if (coverPhoto != null) {
            return coverPhoto;}
        else { // default cover photo
            return ("https://firebasestorage.googleapis.com/v0/b/upchat-a0789.appspot.com/o/profile_image%2Fdefault_cover_photo.jpg?alt=media&token=d8df274f-454f-490d-9f22-fc5f09061942");
        }
    }

    public void setCoverPhoto(String coverPhoto) {
        this.coverPhoto = coverPhoto;
    }

    public User(String name, String profession, String email, String password) {
        this.name = name;
        this.profession = profession;
        this.email = email;
        this.password = password;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProfile() {
        if (profile != null) {
            return profile;
        } else {  // 프사 설정 안했을 때
            return ("https://firebasestorage.googleapis.com/v0/b/upchat-a0789.appspot.com/o/profile_image%2Fdefault_profile.jpg?alt=media&token=5e28932f-3872-4121-a975-051f597e8599");
        }

    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public int getFollowerCount() {
        return followerCount;
    }

    public void setFollowerCount(int followerCount) {
        this.followerCount = followerCount;
    }

    public String getBlockUser() {
        return blockUser;
    }

    public void setBlockUser(String blockUser) {
        this.blockUser = blockUser;
    }

    public String getLastMessage() { return lastMessage; }

    public void setLastMessage(String lastMessage) { this.lastMessage = lastMessage; }

}