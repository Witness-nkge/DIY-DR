package com.wintech.diydr.Model

class Comment {
    var id: String? = null
    var comment: String? = null
    var publisher: String? = null

    constructor() {}
    constructor(id: String?, comment: String?, publisher: String?) {
        this.id = id
        this.comment = comment
        this.publisher = publisher
    }
}