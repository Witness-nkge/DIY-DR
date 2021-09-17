package com.wintech.diydr.Model

class Post {
    var description: String? = null
    var imageurl: String? = null
    var postid: String? = null
    var publisher: String? = null

    constructor() {}
    constructor(description: String?, imageurl: String?, postid: String?, publisher: String?) {
        this.description = description
        this.imageurl = imageurl
        this.postid = postid
        this.publisher = publisher
    }
}