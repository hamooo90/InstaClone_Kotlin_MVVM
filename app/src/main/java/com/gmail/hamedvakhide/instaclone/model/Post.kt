package com.gmail.hamedvakhide.instaclone.model

class Post {
    private var postId: String = ""
    private var publisher: String = ""
    private var image: String = ""
    private var caption: String = ""

    constructor()

    constructor(postId: String, publisher: String, image: String, caption: String) {
        this.postId = postId
        this.publisher = publisher
        this.image = image
        this.caption = caption
    }

    fun getPostId(): String{
        return this.postId
    }
    fun setPostId(postId: String){
        this.postId = postId
    }

    fun getImage(): String{
        return this.image
    }
    fun setImage(image: String){
        this.image = image
    }

    fun getPublisher(): String{
        return this.publisher
    }
    fun setPublisher(publisher: String){
        this.publisher = publisher
    }

    fun getCaption(): String{
        return this.caption
    }
    fun setCaption(caption: String){
        this.caption = caption
    }


}