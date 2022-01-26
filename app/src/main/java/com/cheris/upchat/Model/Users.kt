package com.cheris.upchat.Model

// ChatFragment 유저 목록
class Users {
    //    //SignUp Constructor  이부분 필요없는거같음
    //    public Users(String userName, String mail, String password) {
    //        this.userName = userName;
    //        this.mail = mail;
    //        this.password = password;
    //    }
    var profilepic: String? = null
    var userName: String? = null
    var mail: String? = null
    var password: String? = null
    var userId: String? = null
    var lastMessage: String? = null

    constructor(
        profilepic: String?,
        userName: String?,
        mail: String?,
        password: String?,
        userId: String?,
        lastMessage: String?
    ) {
        this.profilepic = profilepic
        this.userName = userName
        this.mail = mail
        this.password = password
        this.userId = userId
        this.lastMessage = lastMessage
    }

    constructor() {}
}