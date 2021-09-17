package com.wintech.diydr.Model

class Notification {
    var userid: String? = null
    var text: String? = null
    var postid: String? = null
    var isIsPost = false
        private set

    constructor() {}
    constructor(userid: String?, text: String?, postid: String?, isPost: Boolean) {
        this.userid = userid
        this.text = text
        this.postid = postid
        isIsPost = isPost
    }

    fun setIsPost(post: Boolean) {
        isIsPost = post
    }
}