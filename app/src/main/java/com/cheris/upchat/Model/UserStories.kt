package com.cheris.upchat.Model

class UserStories {
    var image: String? = null
    var storyAt: Long = 0

    constructor(image: String?, storyAt: Long) {
        this.image = image
        this.storyAt = storyAt
    }

    constructor() {}
}