package com.gmail.hamedvakhide.instaclone.repository

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.gmail.hamedvakhide.instaclone.model.Comment
import com.gmail.hamedvakhide.instaclone.model.Post
import com.gmail.hamedvakhide.instaclone.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class FirebaseRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseDatabase: FirebaseDatabase,
    private val firebaseStorage: FirebaseStorage
) : FirebaseRepoInterface {

    val postsMutableLiveDataList =
        MutableLiveData<MutableList<Post>>() // contains list of all posts that current user follow their publisher
    val isPostsLikedByCurrentUserMutableLiveDataList =
        MutableLiveData<MutableList<Boolean>>() // a list that shows posts are liked by user or not
    val postsLikesNumberMutableLiveDataList = MutableLiveData<MutableList<Long>>()
    val postsCommentsNumberMutableLiveDataList = MutableLiveData<MutableList<Long>>()
    val postsPublishersInfoMutableLiveDataList = MutableLiveData<MutableList<User>>()
    val commentsPublishersInfoMutableLiveDataList = MutableLiveData<MutableList<User>>()


    /////////////////////// auth //////////////////////////
    suspend fun login(email: String, password: String): String {
        firebaseAuth.signInWithEmailAndPassword(email, password).await()
        return firebaseAuth.currentUser?.email ?: throw FirebaseAuthException("", "")
    }

    suspend fun register(
        fullName: String,
        userName: String,
        email: String,
        password: String
    ): String {
        return try {
            val data = firebaseAuth.createUserWithEmailAndPassword(email, password).await()

            val currentUserId = getUserId()

            val userMap = HashMap<String, Any>()
            userMap["uid"] = currentUserId
            userMap["fullname"] = fullName.toLowerCase(Locale.getDefault())
            userMap["username"] = userName.toLowerCase(Locale.getDefault())
            userMap["email"] = email
            userMap["bio"] = "This bio is empty"
            userMap["image"] =
                "https://firebasestorage.googleapis.com/v0/b/instaclone-365b0.appspot.com/o/Default%20images%2Fprofile.png?alt=media&token=3ea0b3bc-710d-4ae1-bd1c-ae3668e25de3"

            /////add user info in db
            firebaseDatabase.reference.child("Users").child(currentUserId).setValue(userMap).await()
            ////add user in user following list!!
            ////see your post in home fragment
            firebaseDatabase.reference.child("Follow")
                .child(currentUserId)
                .child("Following").child(currentUserId)
                .setValue(true)

            data.user?.email.toString()
        } catch (e: Exception) {
            firebaseAuth.signOut()
            e.message.toString()
        }

    }

    fun signOut() {
        firebaseAuth.signOut()
    }

    fun getUserId(): String{
        return firebaseAuth.currentUser!!.uid
    }
    /////////////////////// auth //////////////////////////

    //////////////// firebase read ///////////////////
    fun getPosts() {

        val followingList: MutableList<String> = ArrayList()

        var postList: MutableList<Post> = ArrayList()

        val postsRef = firebaseDatabase
            .reference
            .child("Posts")

        val followingRef = firebaseDatabase
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
                                    postsMutableLiveDataList.postValue(postList)
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
    
    fun isPostsLiked(postsList: MutableList<Post>) {
        val postLikedList: MutableList<Boolean> = ArrayList()
        postLikedList.clear()
        var counter = 0
        for (post in postsList) {
            counter++
            val likeRef = firebaseDatabase.reference
                .child("Likes")
                .child(post.getPostId())
                .child(getUserId())

            likeRef.addValueEventListener(object : ValueEventListener {
                val position = counter - 1
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        if (postLikedList.size < postsList.size) {
                            postLikedList.add(true)
                        } else {
                            postLikedList[position] = true
                        }
                    } else {
                        if (postLikedList.size < postsList.size) {
                            postLikedList.add(false)
                        } else {
                            postLikedList[position] = false
                        }
                    }
                    isPostsLikedByCurrentUserMutableLiveDataList.postValue(postLikedList)
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
        }

    }

    fun getPostsLikesNumber(postsList: MutableList<Post>) {
        val postsLikesNumberList: MutableList<Long> = ArrayList()
        postsLikesNumberList.clear()
        var counter = 0
        for (post in postsList) {
            counter++
            val likeRef = firebaseDatabase.reference
                .child("Likes")
                .child(post.getPostId())

            likeRef.addValueEventListener(object : ValueEventListener {
                val position = counter - 1
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        if (postsLikesNumberList.size < postsList.size) {
                            postsLikesNumberList.add(snapshot.childrenCount)
                        } else {
                            postsLikesNumberList[position] = snapshot.childrenCount
                        }
                    } else {
                        if (postsLikesNumberList.size < postsList.size) {
                            postsLikesNumberList.add(0)
                        } else {
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

    fun getPostsCommentsNumber(postsList: MutableList<Post>) {
        val postsCommentsNumberList: MutableList<Long> = ArrayList()
        postsCommentsNumberList.clear()
        var counter = 0
        for (post in postsList) {
            counter++
            val commentsRef = firebaseDatabase.reference
                .child("comments")
                .child(post.getPostId())

            commentsRef.addValueEventListener(object : ValueEventListener {
                val position = counter - 1
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        if (postsCommentsNumberList.size < postsList.size) {
                            postsCommentsNumberList.add(snapshot.childrenCount)
                        } else {
                            postsCommentsNumberList[position] = snapshot.childrenCount
                        }
                    } else {
                        if (postsCommentsNumberList.size < postsList.size) {
                            postsCommentsNumberList.add(0)
                        } else {
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

    fun getPostsPublishersInfo(postsList: MutableList<Post>) {
        val postsPublishersInfoList: MutableList<User> = ArrayList()
        postsPublishersInfoList.clear()

        var counter = 0
        for (post in postsList) {
            if (postsPublishersInfoList.size < postsList.size) {
                postsPublishersInfoList.add(User())
            }
            counter++
            val userRef = firebaseDatabase.reference
                .child("Users")
                .child(post.getPublisher())

            userRef.addValueEventListener(object : ValueEventListener {
                var position = counter - 1
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
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

    @ExperimentalCoroutinesApi
    override fun getPostComments(postId: String) = callbackFlow<Result<List<Comment>>> {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val items = snapshot.children.map { ds ->
                    ds.getValue(Comment::class.java)
                }
                this@callbackFlow.trySendBlocking<Result<List<Comment>>>(Result.success(items.filterNotNull()))
            }

            override fun onCancelled(error: DatabaseError) {
                this@callbackFlow.trySendBlocking<Result<List<Comment>>>(Result.failure(error.toException()))
            }
        }
        firebaseDatabase
            .reference
            .child("comments")
            .child(postId).addValueEventListener(listener)

        awaitClose {
            firebaseDatabase
                .reference
                .child("comments")
                .child(postId).removeEventListener(listener)
        }
    }

    @ExperimentalCoroutinesApi
    override fun getCurrentUserProfilePic() = callbackFlow<Result<String>> {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var userImg = ""
                if (snapshot.exists()) {
                    val user = snapshot.getValue<User>(User::class.java)
                    userImg = user!!.getImage()
                }
                this@callbackFlow.sendBlocking(Result.success(userImg))
            }

            override fun onCancelled(error: DatabaseError) {
                this@callbackFlow.sendBlocking(Result.failure(error.toException()))
            }
        }
        val singleUserRef = firebaseDatabase.reference.child("Users").child(firebaseAuth.currentUser!!.uid)
        singleUserRef.addListenerForSingleValueEvent(listener)

        awaitClose {
            singleUserRef.removeEventListener(listener)
        }
    }

    fun getCommentsPublisherInfo(commentsList: MutableList<Comment>){
        val commentsPublisherInfoList : MutableList<User> = ArrayList()
        commentsPublisherInfoList.clear()
        var counter = 0
        for (comment in commentsList) {
            counter++
            val userRef = firebaseDatabase.reference
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

    @ExperimentalCoroutinesApi
    override fun getUserInfo(userId: String) = callbackFlow<Result<User>> {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var user = User()
                if (snapshot.exists()) {
                    user = snapshot.getValue<User>(User::class.java)!!
                }
                this@callbackFlow.trySendBlocking<Result<User>>(Result.success(user))
            }

            override fun onCancelled(error: DatabaseError) {
                this@callbackFlow.trySendBlocking<Result<User>>(Result.failure(error.toException()))
            }
        }
        val singleUserRef = firebaseDatabase.reference.child("Users").child(userId)
        singleUserRef.addValueEventListener(listener)

        awaitClose {
            singleUserRef.removeEventListener(listener)
        }
    }

    @ExperimentalCoroutinesApi
    override fun getUserFollowers(userId: String) = callbackFlow<Result<List<String>>> {
        val followerList : List<String> = ArrayList()

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                (followerList as ArrayList<String>).clear()
                if(snapshot.exists()){
                    for (item in snapshot.children) {
                        item.key?.let { (followerList as ArrayList<String>).add(it) }
                    }
                }
                this@callbackFlow.trySendBlocking<Result<List<String>>>(Result.success(followerList))
            }

            override fun onCancelled(error: DatabaseError) {
                this@callbackFlow.trySendBlocking<Result<List<String>>>(Result.failure(error.toException()))
            }
        }
        val followerRef = firebaseDatabase
            .reference.child("Follow").child(userId).child("Follower")
        followerRef.addValueEventListener(listener)

        awaitClose {
            followerRef.removeEventListener(listener)
        }

    }

    @ExperimentalCoroutinesApi
    override fun getUserFollowings(userId: String) =  callbackFlow<Result<List<String>>> {
        val followingList : List<String> = ArrayList()

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                (followingList as ArrayList<String>).clear()
                if(snapshot.exists()){
                    for (item in snapshot.children) {
                        item.key?.let { (followingList as ArrayList<String>).add(it) }
                    }
                }
                this@callbackFlow.trySendBlocking<Result<List<String>>>(Result.success(followingList))
            }

            override fun onCancelled(error: DatabaseError) {
                this@callbackFlow.trySendBlocking<Result<List<String>>>(Result.failure(error.toException()))
            }
        }
        val followerRef = firebaseDatabase
            .reference.child("Follow").child(userId).child("Following")
        followerRef.addListenerForSingleValueEvent(listener)

        awaitClose {
            followerRef.removeEventListener(listener)
        }

    }

    @ExperimentalCoroutinesApi
    override fun getUserPosts(userId: String) = callbackFlow<Result<List<Post>>> {
        val postList: List<Post> = ArrayList()

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                (postList as ArrayList<Post>).clear()
                if(snapshot.exists()){
                    for(item in snapshot.children){
                        val post = item.getValue(Post::class.java)

                        if (post!!.getPublisher() == userId) {
                            postList.add(post)
                        }
                    }
                }
                this@callbackFlow.trySendBlocking<Result<List<Post>>>(Result.success(postList))
            }

            override fun onCancelled(error: DatabaseError) {
                this@callbackFlow.trySendBlocking<Result<List<Post>>>(Result.failure(error.toException()))
            }
        }

        val postsRef = firebaseDatabase
            .reference
            .child("Posts")
        postsRef.addValueEventListener(listener)

        awaitClose {
            postsRef.removeEventListener(listener)
        }
    }

    @ExperimentalCoroutinesApi
    override fun searchUser(text: String) = callbackFlow<Result<List<User>>> {
        val userList: List<User> = ArrayList()

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                (userList as ArrayList<User>).clear()
                if(snapshot.exists()){
                    for(item in snapshot.children){
                        val user = item.getValue(User::class.java)
                        if (user != null && user.getUid() != getUserId()) {
                            userList.add(user)
                        }
                    }
                }
                this@callbackFlow.trySendBlocking<Result<List<User>>>(Result.success(userList))
            }

            override fun onCancelled(error: DatabaseError) {
                this@callbackFlow.trySendBlocking<Result<List<User>>>(Result.failure(error.toException()))
            }
        }
        val query = firebaseDatabase.reference
            .child("Users")
            .orderByChild("fullname")
            .startAt(text).endAt(text+"\uf8ff")
        query.addValueEventListener(listener)
        awaitClose {
            query.removeEventListener(listener)
        }
    }

    @ExperimentalCoroutinesApi
    override fun isUserFollowed(userId: String) = callbackFlow<Result<Boolean>>{

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var isFollowed = false
                if (snapshot.child(userId).exists()) {
                    isFollowed = true
                }
                this@callbackFlow.trySendBlocking<Result<Boolean>>(Result.success(isFollowed))
            }

            override fun onCancelled(error: DatabaseError) {
                this@callbackFlow.trySendBlocking<Result<Boolean>>(Result.failure(error.toException()))
            }
        }

        val followingRef = firebaseDatabase.reference
                .child("Follow")
                .child(getUserId())
                .child("Following")
        followingRef.addValueEventListener(listener)
        awaitClose {
            followingRef.removeEventListener(listener)
        }

    }
    //////////////// firebase read ///////////////////

    //////////////// firebase write //////////////////
    suspend fun likePost(postId: String) {
        firebaseDatabase.reference
            .child("Likes")
            .child(postId)
            .child(firebaseAuth.currentUser!!.uid)
            .setValue(true)
            .await()
    }

    suspend fun unLikePost(postId: String) {
        firebaseDatabase.reference
            .child("Likes")
            .child(postId)
            .child(firebaseAuth.currentUser!!.uid)
            .removeValue()
            .await()
    }

    suspend fun follow(userId: String) {
        try {
            firebaseDatabase.reference
                .child("Follow")
                .child(getUserId())
                .child("Following")
                .child(userId)
                .setValue(true)
                .await()

            firebaseDatabase.reference
                .child("Follow")
                .child(userId)
                .child("Follower")
                .child(getUserId())
                .setValue(true)
                .await()
        } catch (e: Exception){
            e.printStackTrace()
        }

    }

    suspend fun unFollow(userId: String) {
        try {
            firebaseDatabase.reference
                .child("Follow")
                .child(getUserId())
                .child("Following")
                .child(userId)
                .removeValue()
                .await()

            firebaseDatabase.reference
                .child("Follow")
                .child(userId)
                .child("Follower")
                .child(getUserId())
                .removeValue()
                .await()
        } catch (e: Exception){
            e.printStackTrace()
        }
    }

    suspend fun updateUserProfileInfo(fullName: String, userName: String, bio: String): Boolean{
        val userMap = HashMap<String, Any>()
        userMap["fullname"] = fullName
        userMap["username"] = userName
        userMap["bio"] = bio
        ///
        return try {
            firebaseDatabase.reference.child("Users").child(getUserId()).updateChildren(userMap).await()
            true
        } catch (e: Exception){
            e.printStackTrace()
            false
        }

    }

    suspend fun updateUserProfileInfoWithImage(fullName: String, userName: String, bio: String, imageUrl: String): Boolean{
        val userMap = HashMap<String, Any>()
        userMap["fullname"] = fullName
        userMap["username"] = userName
        userMap["bio"] = bio
        userMap["image"] = imageUrl

        ///
        return try {
            firebaseDatabase.reference.child("Users").child(getUserId()).updateChildren(userMap).await()
            true
        } catch (e: Exception){
            e.printStackTrace()
            false
        }

    }

    suspend fun uploadImage(imageUri: Uri, isPost: Boolean): String{
        var storageProfilePicsRef = firebaseStorage.reference.child("ProfilePics")
        val userId = getUserId()
        var picRef = storageProfilePicsRef.child("$userId.jpg")
        if(isPost) {
            storageProfilePicsRef =  firebaseStorage.reference.child("PostPics")
            picRef = storageProfilePicsRef.child(System.currentTimeMillis().toString() + ".jpg")

        }


        return try {
            // upload image
            val taskUpload = picRef.putFile(imageUri).await()
            // get uploaded image url
            val dlUrl = taskUpload.task.snapshot.storage.downloadUrl.await()

            dlUrl.toString()

        } catch (e:Exception){
            e.printStackTrace()

            ""
        }

    }

    suspend fun addNewPost(caption: String, imageUrl: String): Boolean{
        val postsRef = firebaseDatabase.reference
            .child("Posts")

        /// Creates a random key
        val postId = postsRef.push().key

        val postMap = HashMap<String, Any>()
        postMap["postId"] = postId!!
        postMap["caption"] = caption
        postMap["publisher"] = getUserId()
        postMap["image"] = imageUrl

        return try {
            postsRef.child(postId).updateChildren(postMap).await()
            true
        } catch (e: Exception){
            e.printStackTrace()
            false
        }
    }

    suspend fun addComment(comment: String, postId: String) : String {
        val commentRef = firebaseDatabase.reference
            .child("comments")
            .child(postId)
        val commentMap = HashMap<String, Any>()
        val commentId = commentRef.push().key
        commentMap["comment"] = comment
        commentMap["publisher"] = firebaseAuth.currentUser!!.uid
        commentMap["commentId"] = commentId!!

        return try {
            commentRef.child(commentId).setValue(commentMap).await()
            "done"
        } catch (e: Exception) {
            "failed"
        }
    }
    //////////////// firebase write //////////////////

}