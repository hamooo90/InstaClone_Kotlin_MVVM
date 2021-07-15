package com.gmail.hamedvakhide.instaclone.model

class User {
    private var username: String = ""
    private var fullname: String = ""
    private var email: String = ""
    private var uid: String = ""
    private var image: String = ""
    private var bio: String = ""

    constructor()

    constructor(
        username: String,
        fullname: String,
        email: String,
        uid: String,
        image: String,
        bio: String
    ) {
        this.username = username
        this.fullname = fullname
        this.email = email
        this.uid = uid
        this.image = image
        this.bio = bio
    }

    fun getUserName(): String{
        return username
    }
    fun setUserName(username:String){
        this.username= username
    }
    fun getFullName(): String{
        return fullname
    }
    fun setFullName(fullname:String){
        this.fullname= fullname
    }
    fun getEmail(): String{
        return email
    }
    fun setEmail(email:String){
        this.email= email
    }
    fun getBio(): String{
        return bio
    }
    fun setBio(bio:String){
        this.bio= bio
    }
    fun getUid(): String{
        return uid
    }
    fun setUid(uid:String){
        this.uid= uid
    }
    fun getImage(): String{
        return image
    }
    fun setImage(image:String){
        this.image= image
    }
}