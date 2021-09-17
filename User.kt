package com.wintech.diydr.Model

class User {
    var name: String? = null
    var email: String? = null
    var username: String? = null
    var bio: String? = null
    var imageurl: String? = null
    var id: String? = null

    constructor() {}
    constructor(name: String?, email: String?, username: String?, bio: String?, imageurl: String?, id: String?) {
        this.name = name
        this.email = email
        this.username = username
        this.bio = bio
        this.imageurl = imageurl
        this.id = id
    }
}