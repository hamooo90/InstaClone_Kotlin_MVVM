package com.gmail.hamedvakhide.instaclone.model

class Comment {
    private var comment: String = ""
    private var commentId: String = ""
    private var publisher: String = ""

    constructor()

    constructor(comment: String, commentId: String, publisher: String) {
        this.comment = comment
        this.commentId = commentId
        this.publisher = publisher
    }

    fun getComment(): String{
        return this.comment
    }

    fun setComment(comment: String){
        this.comment = comment
    }

    fun getCommentId(): String{
        return this.commentId
    }

    fun setCommentId(commentId: String){
        this.commentId = commentId
    }

    fun getPublisher(): String{
        return this.publisher
    }

    fun setPublisher(publisher: String){
        this.publisher = publisher
    }


}