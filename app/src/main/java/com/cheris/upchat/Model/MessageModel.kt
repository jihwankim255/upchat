package com.cheris.upchat.Model

class MessageModel {
    var uId: String? = null
    var message: String? = null
    var timestamp: Long? = null

    constructor(uId: String?, message: String?, timestamp: Long?) {
        this.uId = uId
        this.message = message
        this.timestamp = timestamp
    }

    constructor(uId: String?, message: String?) {
        this.uId = uId
        this.message = message
    }

    constructor() {}

    fun getuId(): String? {
        return uId
    }

    fun setuId(uId: String?) {
        this.uId = uId
    }
}