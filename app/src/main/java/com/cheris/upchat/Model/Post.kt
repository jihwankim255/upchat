package com.cheris.upchat.Model

class Post {
    var postId // 올린 사람 Id
            : String? = null
    var postImage // 올린 이미지
            : String? = null
    var postedBy // 올린 사람
            : String? = null
    var postDescription // 올린 글
            : String? = null
    var postedAt // 올린 날짜
            : Long = 0
    var postLike //글에 달린 좋아요
            = 0
    var commentCount //글에 달린 댓글 수
            = 0

    constructor(
        postId: String?,
        postImage: String?,
        postedBy: String?,
        postDescription: String?,
        postedAt: Long
    ) {
        this.postId = postId
        this.postImage = postImage
        this.postedBy = postedBy
        this.postDescription = postDescription
        this.postedAt = postedAt
    }

    constructor() {}
}