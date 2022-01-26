package com.cheris.upchat.Model

class User {
    var name: String? = null
    var profession: String? = null
    var email: String? = null
    var password: String? = null

    // default cover photo
    var coverPhoto: String? = null
        get() = if (field != null) {
            field
        } else { // default cover photo
            "https://firebasestorage.googleapis.com/v0/b/upchat-a0789.appspot.com/o/profile_image%2Fdefault_cover_photo.jpg?alt=media&token=d8df274f-454f-490d-9f22-fc5f09061942"
        }
    var userID: String? = null
    var followerCount = 0

    // 프사 설정 안했을 때
    var profile: String? = null
        get() = if (field != null) {
            field
        } else {  // 프사 설정 안했을 때
            "https://firebasestorage.googleapis.com/v0/b/upchat-a0789.appspot.com/o/profile_image%2Fdefault_profile.jpg?alt=media&token=5e28932f-3872-4121-a975-051f597e8599"
        }
    var lastMessage: String? = null
    var blockUser: String? = null

    constructor() {}
    constructor(name: String?, profession: String?, email: String?, password: String?) {
        this.name = name
        this.profession = profession
        this.email = email
        this.password = password
    }
}