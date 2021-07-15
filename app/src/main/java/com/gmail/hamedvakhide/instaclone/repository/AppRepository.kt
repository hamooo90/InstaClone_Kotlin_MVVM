package com.gmail.hamedvakhide.instaclone.repository

import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.gmail.hamedvakhide.instaclone.model.Comment
import com.gmail.hamedvakhide.instaclone.model.Post
import com.gmail.hamedvakhide.instaclone.model.User
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_account_setting.*
import kotlinx.android.synthetic.main.activity_add_new_post.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class AppRepository(application: Application) {
    private val application: Application

    private lateinit var userMutableLiveData: MutableLiveData<FirebaseUser>
    private lateinit var firebaseAuth: FirebaseAuth
    private val loggedOutMutableLiveData: MutableLiveData<Boolean>
    private var loading: MutableLiveData<Boolean> = MutableLiveData()

    private val rootDB: FirebaseDatabase
    private val userRef: DatabaseReference

    private lateinit var followingList: MutableList<String>
    private lateinit var followerList: MutableList<String>
    private var followingListMutableLiveData = MutableLiveData<MutableList<String>>()
    private var followerListMutableLiveData = MutableLiveData<MutableList<String>>()

    private val postMutableLiveDataList = MutableLiveData<MutableList<Post>>()
    private val userPostMutableLiveDataList = MutableLiveData<MutableList<Post>>()

    private val searchUserMutableLiveDataList = MutableLiveData<MutableList<User>>()

    private var userInfoMutableLiveData = MutableLiveData<User>()

    private var isUserFollowedMutableLiveData = MutableLiveData<Boolean>()

    var profileUpdateDoneMutableLiveData: MutableLiveData<String> = MutableLiveData()

    var addPostDoneMutableLiveData: MutableLiveData<String> = MutableLiveData()

    var currentUserProfilePic = MutableLiveData<String>()

    var addcommentDoneMutableLiveData: MutableLiveData<String> = MutableLiveData()

    var isPostLikedMutableLiveData: MutableLiveData<Boolean> = MutableLiveData()

     val isLikeMutableLiveDataList = MutableLiveData<MutableList<Boolean>>()
    val postsLikesNumberMutableLiveDataList = MutableLiveData<MutableList<Long>>()
    val postsCommentsNumberMutableLiveDataList = MutableLiveData<MutableList<Long>>()

    val postsPublishersInfoMutableLiveDataList = MutableLiveData<MutableList<User>>()
    val commentsPublishersInfoMutableLiveDataList = MutableLiveData<MutableList<User>>()

    val commentsMutableLiveDataList = MutableLiveData<MutableList<Comment>>()



    val postLikedList : MutableList<Boolean> = ArrayList()
    val postsLikesNumberList : MutableList<Long> = ArrayList()
    val postsCommentsNumberList : MutableList<Long> = ArrayList()

    private val postsPublishersInfoList : MutableList<User> = ArrayList()
    private val commentsPublisherInfoList : MutableList<User> = ArrayList()

    private val dbUrl = ""



    init {
        this.application = application

        firebaseAuth = FirebaseAuth.getInstance()
        userMutableLiveData = MutableLiveData()
        loggedOutMutableLiveData = MutableLiveData()

        rootDB = FirebaseDatabase.getInstance(dbUrl)
        userRef = rootDB.reference.child("Users")

        loading.postValue(false)
        profileUpdateDoneMutableLiveData.postValue("")
        addPostDoneMutableLiveData.postValue("")
        addcommentDoneMutableLiveData.postValue("")

        isPostLikedMutableLiveData.postValue(false)


    }

    ///////////////////////////////////////////////////////////////////////////////////////
    /////////////////////// Authentication ////////////////////////////////////////////////
    fun register(fullName: String, userName: String, email: String, password: String) {
        //////////
        loading.postValue(true)
        //create a user
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    /// if registration successful add information to db
                    saveUserInfo(fullName, userName, email)
                } else {
                    val message = task.exception.toString()
                    Toast.makeText(
                        application,
                        "Error: $message",
                        Toast.LENGTH_LONG
                    ).show()
                }
                loading.postValue(false)

            }

    }

    fun login(email: String, password: String) {
        //////
        loading.postValue(true)
        //////
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    val pref =
                        application.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
                    pref?.putString("profileId", FirebaseAuth.getInstance().currentUser?.uid)
                    pref?.apply()

                    userMutableLiveData.postValue(firebaseAuth.currentUser)
                } else {

                    val message = task.exception.toString()
                    Toast.makeText(
                        application,
                        "Error: $message",
                        Toast.LENGTH_LONG
                    ).show()

                }
                loading.postValue(false)

            }

    }

    fun logOut() {
        firebaseAuth.signOut()
        loggedOutMutableLiveData.postValue(true)
    }

    fun isUserLoggedIn():Boolean{
        return firebaseAuth.currentUser != null
    }

    fun getUserId(): String{
        return firebaseAuth.currentUser!!.uid
    }
    /////////////////////// Authentication ////////////////////////////////////////////////

    //////////////////////////////////////////////////////////
    ////////////////// Db Write///////////////////////////////
    private fun saveUserInfo(fullName: String, userName: String, email: String) {
        val currentUserId = firebaseAuth.currentUser!!.uid

        val userMap = HashMap<String, Any>()
        userMap["uid"] = currentUserId
        userMap["fullname"] = fullName.toLowerCase(Locale.getDefault())
        userMap["username"] = userName.toLowerCase(Locale.getDefault())
        userMap["email"] = email
        userMap["bio"] = "This bio is empty"
        userMap["image"] =
            "https://firebasestorage.googleapis.com/v0/b/instaclone-365b0.appspot.com/o/Default%20images%2Fprofile.png?alt=media&token=3ea0b3bc-710d-4ae1-bd1c-ae3668e25de3"

        /////add user info in db
        userRef.child(currentUserId).setValue(userMap)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        application,
                        "Account has been created",
                        Toast.LENGTH_LONG
                    ).show()

                    ////add user in user following list!!
                    ////see your post in home fragment
                    rootDB.reference
                        .child("Follow")
                        .child(currentUserId)
                        .child("Following").child(currentUserId)
                        .setValue(true)


                    val pref =
                        application.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
                    pref?.putString("profileId", currentUserId)
                    pref?.apply()

                    ////////// set current user in mutableLiveData
                    userMutableLiveData.postValue(firebaseAuth.currentUser)

                } else {

                    val message = task.exception!!.message.toString()
                    Toast.makeText(
                        application,
                        "Error: $message",
                        Toast.LENGTH_LONG
                    ).show()
                    FirebaseAuth.getInstance().signOut()
                }

                loading.postValue(false)

            }

    }

    fun follow(userId: String) {

        firebaseAuth.currentUser?.uid.let { it ->
            ///add to following list
            rootDB.reference
                .child("Follow")
                .child(it.toString())
                .child("Following")
                .child(userId)
                .setValue(true).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        ///add to follower list
                        rootDB.reference
                            .child("Follow")
                            .child(userId)
                            .child("Follower")
                            .child(it.toString())
                            .setValue(true).addOnCompleteListener { task ->
                                if (task.isSuccessful) {

                                }
                            }
                    }
                }
        }
    }

    fun unFollow(userId: String) {
        firebaseAuth.currentUser?.uid.let { it ->
            rootDB.reference
                .child("Follow")
                .child(it.toString())
                .child("Following")
                .child(userId)
                .removeValue().addOnCompleteListener { task ->
                    if (task.isSuccessful) {

                            rootDB.reference
                                .child("Follow")
                                .child(userId)
                                .child("Follower")
                                .child(it.toString())
                                .removeValue().addOnCompleteListener { task ->
                                    if (task.isSuccessful) {

                                    }
                                }
                    }
                }
        }
    }

    fun likePost(postId: String){
        rootDB.reference
            .child("Likes")
            .child(postId)
            .child(firebaseAuth.currentUser!!.uid)
            .setValue(true)
    }

    fun unLikePost(postId: String){
        rootDB.reference
            .child("Likes")
            .child(postId)
            .child(firebaseAuth.currentUser!!.uid)
            .removeValue()
    }

    fun updateUserProfileInfo(fullName: String, userName: String, bio: String){
        val userMap = HashMap<String, Any>()
        userMap["fullname"] = fullName
        userMap["username"] = userName
        userMap["bio"] = bio
        ///
        profileUpdateDoneMutableLiveData.postValue("updating")
        firebaseAuth.currentUser?.uid.let {
            userRef.child(it.toString()).updateChildren(userMap).addOnCompleteListener { task ->
                if(task.isSuccessful){
                    profileUpdateDoneMutableLiveData.postValue("done")
                }else{
                    profileUpdateDoneMutableLiveData.postValue("failed")
                }
            }
        }

    }

    fun updateUserProfileInfoWithImage(fullName: String, userName: String, bio: String, imageUri: Uri){
        val storageProfilePicsRef = FirebaseStorage.getInstance().reference.child("ProfilePics")
        val userId = firebaseAuth.currentUser!!.uid
        val picRef = storageProfilePicsRef.child("$userId.jpg")

        profileUpdateDoneMutableLiveData.postValue("updating")

        var uploadTask: StorageTask<*>
        uploadTask = picRef.putFile(imageUri)

        uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    profileUpdateDoneMutableLiveData.postValue("failed")
                    throw it
                }
            }
            return@Continuation picRef.downloadUrl
        }).addOnCompleteListener(OnCompleteListener<Uri> { task ->
            if (task.isSuccessful) {
                val downloadUrl = task.result
                val myUrl = downloadUrl.toString()

                val userMap = HashMap<String, Any>()
                userMap["fullname"] =
                    fullName
                userMap["username"] =
                    userName
                userMap["bio"] =
                    bio
                userMap["image"] = myUrl

                userRef.child(userId).updateChildren(userMap)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            profileUpdateDoneMutableLiveData.postValue("done")
                        }
                        else{
                            profileUpdateDoneMutableLiveData.postValue("failed")
                        }

                    }

            }
            else{
                profileUpdateDoneMutableLiveData.postValue("failed")
            }
        })
    }

    fun addNewPost(caption: String, imageUri: Uri){

        addPostDoneMutableLiveData.postValue("adding")

        val picRef = FirebaseStorage.getInstance().reference.child("PostPics")
            .child(System.currentTimeMillis().toString() + ".jpg")
        var uploadTask: StorageTask<*>
        uploadTask = picRef.putFile(imageUri)
        uploadTask.continueWithTask (Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
            if(!task.isSuccessful){
                task.exception?.let {
                    addPostDoneMutableLiveData.postValue("failed")
                    throw it
                }
            }
            return@Continuation picRef.downloadUrl
        }).addOnCompleteListener ( OnCompleteListener<Uri> { task ->
            if(task.isSuccessful){
                val downloadUrl = task.result
                val myUrl = downloadUrl.toString()

                val postsRef = rootDB.reference
                    .child("Posts")

                /// Creates a random key
                val postId = postsRef.push().key

                val postMap = HashMap<String, Any>()
                postMap["postId"] = postId!!
                postMap["caption"] = caption
                postMap["publisher"] = firebaseAuth.currentUser!!.uid
                postMap["image"] = myUrl

                postsRef.child(postId).updateChildren(postMap).addOnCompleteListener { task ->
                    if(task.isSuccessful){
                        addPostDoneMutableLiveData.postValue("done")
                    }else{
                        addPostDoneMutableLiveData.postValue("failed")
                    }
                }

            }else{
                addPostDoneMutableLiveData.postValue("failed")
            }
        } )


    }

    fun addComment(comment: String, postId: String){
        val commentRef = rootDB.reference
            .child("comments")
            .child(postId)

        val commentMap = HashMap<String, Any>()
        val commentId = commentRef.push().key
        commentMap["comment"] = comment
        commentMap["publisher"] = firebaseAuth.currentUser!!.uid
        commentMap["commentId"] = commentId!!
        addcommentDoneMutableLiveData.postValue("adding")
        commentRef.child(commentId).setValue(commentMap)
            .addOnCompleteListener {
                if (it.isSuccessful){
                    addcommentDoneMutableLiveData.postValue("done")
                } else{
                    addcommentDoneMutableLiveData.postValue("failed")
                }
            }

    }
    ////////////////// Db Write///////////////////////////////

    /////////////////////////////////////////////////////////
    ////////////////// Db Read //////////////////////////////
    fun isUserFollowed(userId: String){
        val followingRef = firebaseAuth.currentUser?.uid.let { it ->
            rootDB.reference
                .child("Follow")
                .child(it.toString())
                .child("Following")
        }

        ///is the current user follow this person?
        if (followingRef != null) {
            followingRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.child(userId).exists()) {
                        isUserFollowedMutableLiveData.postValue(true)
                    } else {
                        isUserFollowedMutableLiveData.postValue(false)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
        }
    }

    fun isPostsLiked(postsList: MutableList<Post>){
        postLikedList.clear()
        var counter = 0
        for (post in postsList) {
            counter++
            val likeRef = rootDB.reference
                .child("Likes")
                .child(post.getPostId())
                .child(firebaseAuth.currentUser!!.uid)

            likeRef.addValueEventListener(object :ValueEventListener{
                val position = counter-1
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        if(postLikedList.size < postsList.size){
                            postLikedList.add(true)
                        }else{
                            postLikedList[position] = true
                        }
                    } else {
                        if(postLikedList.size < postsList.size){
                            postLikedList.add(false)
                        }else{
                            postLikedList[position] = false
                        }
                    }
                    isLikeMutableLiveDataList.postValue(postLikedList)
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
        }

    }

    fun getUserFollowings(userId: String) {
        followingList = ArrayList()

        val followingRef = rootDB
            .reference.child("Follow").child(userId).child("Following")
        followingRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    (followingList as ArrayList<String>).clear()
                    for (item in snapshot.children) {
                        item.key?.let { (followingList as ArrayList<String>).add(it) }
                    }
                } else {
                    (followingList as ArrayList<String>).clear()
                }
                followingListMutableLiveData.postValue(followingList)
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    fun getUserFollowers(userId: String) {
        followerList = ArrayList()

        val followerRef = rootDB
            .reference.child("Follow").child(userId).child("Follower")
        followerRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    (followerList as ArrayList<String>).clear()
                    for (item in snapshot.children) {
                        item.key?.let { (followerList as ArrayList<String>).add(it) }
                    }
                }else{
                    (followerList as ArrayList<String>).clear()
                }
                followerListMutableLiveData.postValue(followerList)
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    fun searchUser(text: String){
        val userList: MutableList<User> = ArrayList()
        val query = rootDB.reference
            .child("Users")
            .orderByChild("fullname")
            .startAt(text).endAt(text+"\uf8ff")

        query.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()

                for(shot in snapshot.children){
                    val user = shot.getValue(User::class.java)
                    if(user != null){
                        userList.add(user)
                    }
                }

                searchUserMutableLiveDataList.postValue(userList)
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    /// get all posts that current user follow their publisher
    fun getPosts() {

        followingList = ArrayList()

        var postList: MutableList<Post> = ArrayList()

        val postsRef = rootDB
            .reference
            .child("Posts")

        val followingRef = rootDB
            .reference.child("Follow").child(firebaseAuth.currentUser!!.uid).child("Following")
        followingRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    (followingList as ArrayList<String>).clear()
                    for (item in snapshot.children) {
                        item.key?.let { (followingList as ArrayList<String>).add(it) }
                    }

                    //////////get posts////////
                    postsRef.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            postList.clear()

                            for (item in snapshot.children) {
                                val post = item.getValue(Post::class.java)

                                for (userId in followingList!!) {
                                    if (post!!.getPublisher() == userId) {
                                        postList.add(post)
                                    }
                                    postMutableLiveDataList.postValue(postList)
                                }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                        }
                    })

                }

            }

            override fun onCancelled(error: DatabaseError) {
            }
        })


    }

    fun getUserInfo(userId: String){
        val singleUserRef =
            userRef.child(userId)
        singleUserRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val user = snapshot.getValue<User>(User::class.java)
                    userInfoMutableLiveData.postValue((user))
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

    }

    fun getCurrentUserProfilePic(){
        val singleUserRef =
            userRef.child(firebaseAuth.currentUser!!.uid)
        singleUserRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val user = snapshot.getValue<User>(User::class.java)
                    currentUserProfilePic.postValue(user!!.getImage())
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    fun getUserPosts(userId: String) {

        val postList: MutableList<Post> = ArrayList()

        val postsRef = rootDB
            .reference
            .child("Posts")
        postsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                postList.clear()

                for (item in snapshot.children) {
                    val post = item.getValue(Post::class.java)

                    if (post!!.getPublisher() == userId) {
                        postList.add(post)
                        userPostMutableLiveDataList.postValue(postList)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    fun getPostsCommentsNumber(postsList: MutableList<Post>){
        postsCommentsNumberList.clear()
        var counter = 0
        for (post in postsList) {
            counter++
            val commentsRef = rootDB.reference
                .child("comments")
                .child(post.getPostId())

            commentsRef.addValueEventListener(object :ValueEventListener{
                val position = counter-1
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        if(postsCommentsNumberList.size < postsList.size){
                            postsCommentsNumberList.add(snapshot.childrenCount)
                        }else{
                            postsCommentsNumberList[position] = snapshot.childrenCount
                        }
                    } else {
                        if(postsCommentsNumberList.size < postsList.size){
                            postsCommentsNumberList.add(0)
                        }else{
                            postsCommentsNumberList[position] = 0
                        }
                    }
                    postsCommentsNumberMutableLiveDataList.postValue(postsCommentsNumberList)
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
        }
    }

    fun getPostsLikesNumber(postsList: MutableList<Post>){
        postsLikesNumberList.clear()
        var counter = 0
        for (post in postsList) {
            counter++
            val likeRef = rootDB.reference
                .child("Likes")
                .child(post.getPostId())

            likeRef.addValueEventListener(object :ValueEventListener{
                val position = counter-1
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        if(postsLikesNumberList.size < postsList.size){
                            postsLikesNumberList.add(snapshot.childrenCount)
                        }else{
                            postsLikesNumberList[position] = snapshot.childrenCount
                        }
                    } else {
                        if(postsLikesNumberList.size < postsList.size){
                            postsLikesNumberList.add(0)
                        }else{
                            postsLikesNumberList[position] = 0
                        }
                    }
                    postsLikesNumberMutableLiveDataList.postValue(postsLikesNumberList)
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
        }
    }

    fun getPostsPublishersInfo(postsList: MutableList<Post>){
        postsPublishersInfoList.clear()

        var counter = 0
        for (post in postsList) {
            if(postsPublishersInfoList.size<postsList.size){
                postsPublishersInfoList.add(User())
            }
            counter++
            val userRef = rootDB.reference
                .child("Users")
                .child(post.getPublisher())

            userRef.addValueEventListener(object :ValueEventListener{
                var position = counter-1
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        val user = snapshot.getValue<User>(User::class.java)
                        postsPublishersInfoList[position] = user!!
                    }
                    postsPublishersInfoMutableLiveDataList.postValue(postsPublishersInfoList)

                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
        }
    }

    fun getPostComments(postId: String){
        val commentsList: MutableList<Comment> = ArrayList()

        val commentsRef = rootDB.reference
            .child("comments")
            .child(postId)

        commentsRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                commentsList.clear()
                if (snapshot.exists()){
                    for(item in snapshot.children){
                        val comment = item.getValue(Comment::class.java)
                        commentsList.add(comment!!)
                    }
                }
                commentsMutableLiveDataList.postValue(commentsList)
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    fun getCommentsPublisherInfo(commentsList: MutableList<Comment>){
        commentsPublisherInfoList.clear()
        var counter = 0
        for (comment in commentsList) {
            counter++
            val userRef = rootDB.reference
                .child("Users")
                .child(comment.getPublisher())

            userRef.addValueEventListener(object :ValueEventListener{
                val position = counter-1
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        val user = snapshot.getValue<User>(User::class.java)
                        if(commentsPublisherInfoList.size < commentsList.size){
                            commentsPublisherInfoList.add(user!!)
                        }else{
                            commentsPublisherInfoList[position] = user!!
                        }
                    }
                    commentsPublishersInfoMutableLiveDataList.postValue(commentsPublisherInfoList)

                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
        }
    }
    ////////////////// Db Read //////////////////////////////

    ///////////////////////////////////////////////////////////////
    ////////////////// Get LiveData ///////////////////////////////
    fun getUserMutableLiveData(): MutableLiveData<FirebaseUser> {
        return userMutableLiveData
    }

    fun getLoggedOutMutableLiveData(): MutableLiveData<Boolean> {
        return loggedOutMutableLiveData
    }

    fun getLoadingMutableLiveData(): MutableLiveData<Boolean> {
        return loading
    }

    fun getUserfollowingMutableLiveList(): MutableLiveData<MutableList<String>> {
        return followingListMutableLiveData
    }

    fun getUserfollowerMutableLiveList(): MutableLiveData<MutableList<String>> {
        return followerListMutableLiveData
    }

    fun getPostMutableLiveDataList(): MutableLiveData<MutableList<Post>> {
        return postMutableLiveDataList
    }

    fun getUserPostMutableLiveDataList(): MutableLiveData<MutableList<Post>> {
        return userPostMutableLiveDataList
    }

    fun getSearchedUserMutableLiveDataList(): MutableLiveData<MutableList<User>>{
        return searchUserMutableLiveDataList
    }

    fun getUserInfoMutableLiveData(): MutableLiveData<User>{
        return userInfoMutableLiveData
    }

    fun isFollowedMutableLiveData(): MutableLiveData<Boolean>{
        return isUserFollowedMutableLiveData
    }
    ////////////////// Get LiveData ///////////////////////////////

}