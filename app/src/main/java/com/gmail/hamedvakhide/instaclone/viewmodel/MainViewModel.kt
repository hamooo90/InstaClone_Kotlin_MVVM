package com.gmail.hamedvakhide.instaclone.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gmail.hamedvakhide.instaclone.model.Comment
import com.gmail.hamedvakhide.instaclone.model.Post
import com.gmail.hamedvakhide.instaclone.model.User
import com.gmail.hamedvakhide.instaclone.repository.FirebaseRepository
import com.gmail.hamedvakhide.instaclone.repository.PreferencesRepository
import com.google.firebase.auth.FirebaseAuthException
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    @ApplicationContext private val application: Context,
    private val repository: FirebaseRepository,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    val postsMutableLiveDataList = repository.postsMutableLiveDataList
    val isPostsLikedByCurrentUserMutableLiveDataList = repository
        .isPostsLikedByCurrentUserMutableLiveDataList
    val postsLikesNumberMutableLiveDataList = repository.postsLikesNumberMutableLiveDataList
    val postsCommentsNumberMutableLiveDataList = repository.postsCommentsNumberMutableLiveDataList
    val postsPublishersInfoMutableLiveDataList = repository.postsPublishersInfoMutableLiveDataList

    val commentsMutableLiveDataList = MutableLiveData<MutableList<Comment>>()
    var currentUserProfilePic = MutableLiveData<String>()
    val commentsPublishersInfoMutableLiveDataList = repository.commentsPublishersInfoMutableLiveDataList
    var addCommentDoneMutableLiveData: MutableLiveData<String> = MutableLiveData()
    var userInfoMutableLiveData = MutableLiveData<User>()
    var followerListMutableLiveData = MutableLiveData<MutableList<String>>()
    var followingListMutableLiveData = MutableLiveData<MutableList<String>>()
    val userPostMutableLiveDataList = MutableLiveData<MutableList<Post>>()
    val searchUserMutableLiveDataList = MutableLiveData<MutableList<User>>()
    var isUserFollowedMutableLiveData = MutableLiveData<Boolean>()
    var profileUpdateDoneMutableLiveData: MutableLiveData<String> = MutableLiveData()
    var addPostDoneMutableLiveData: MutableLiveData<String> = MutableLiveData()

    var userMutableLiveData: MutableLiveData<String> = MutableLiveData()

    var stateMutableLiveData: MutableLiveData<String> = MutableLiveData()

    ////////////// shared preferences  ///////////////
    fun readProfileIdFromPref(): String {
        return  preferencesRepository.readProfileId()
    }
    fun readBackUserIdFromPref(): String {
        return  preferencesRepository.readBackUserId()
    }
    fun readPositionFromPref(): Int {
        return  preferencesRepository.readPosition()
    }
    fun saveProfileIdToPref(uid: String) {
        preferencesRepository.saveProfileId(uid)
    }
    fun saveBackUserIdToPref(uid: String) {
        preferencesRepository.saveBackUserId(uid)
    }
    fun savePositionToPref(pos: Int) {
        preferencesRepository.savePosition(pos)
    }
    ////////////// shared preferences  ///////////////

    //////////////// firebase auth  //////////////////
    fun login(email: String, password: String) {
        stateMutableLiveData.postValue("loading")
        viewModelScope.launch {
            try {
                repository.login(email, password).let {
                    stateMutableLiveData.postValue("done")
                    saveProfileIdToPref(getUserId())
                }
            } catch (e: FirebaseAuthException) {
                stateMutableLiveData.postValue(e.message)
            }

        }
    }

    fun register(fullName: String, userName: String, email: String, password: String) {
        stateMutableLiveData.postValue("loading")
        viewModelScope.launch {
            try {
            repository.register(fullName, userName, email, password).let {
                stateMutableLiveData.postValue("done")
                userMutableLiveData.postValue(it)
                saveProfileIdToPref(getUserId())

            }
            } catch (e: Exception) {
                stateMutableLiveData.postValue(e.message)
            }

        }
    }

    fun logOut() {
        repository.signOut()
        stateMutableLiveData.postValue("Signed out")

    }

    fun getUserId(): String = repository.getUserId()
    //////////////// firebase auth ///////////////////

    //////////////// firebase read ///////////////////
    fun getPosts() {
        repository.getPosts()
    }

    fun isPostsLiked(postsList: MutableList<Post>) {
        repository.isPostsLiked(postsList)
    }

    fun getPostsLikesNumber(postsList: MutableList<Post>) {
        repository.getPostsLikesNumber(postsList)
    }

    fun getPostsCommentsNumber(postsList: MutableList<Post>) {
        repository.getPostsCommentsNumber(postsList)
    }

    fun getPostsPublishersInfo(postsList: MutableList<Post>) {
        repository.getPostsPublishersInfo(postsList)
    }

    @ExperimentalCoroutinesApi
    fun getPostComments(postId: String){
        viewModelScope.launch {
            repository.getPostComments(postId).collect {
                when {
                    it.isSuccess -> {
                        val list = it.getOrNull()
                        if (list != null) {
                            commentsMutableLiveDataList.postValue(list as MutableList<Comment>?)
                        }
                    }
                    it.isFailure -> {
                        it.exceptionOrNull()?.printStackTrace()
                    }
                }
            }
        }
    }

    @ExperimentalCoroutinesApi
    fun getCurrentUserProfilePic(){
        viewModelScope.launch {
            repository.getCurrentUserProfilePic().collect {
                when {
                    it.isSuccess -> {
                        val img = it.getOrNull()
                        if (img != null) {
                            currentUserProfilePic.postValue(img!!)
                        }
                    }
                    it.isFailure -> {
                        it.exceptionOrNull()?.printStackTrace()
                    }
                }
            }
        }
    }

    fun getCommentsPublisherInfo(commentsList: MutableList<Comment>){
        repository.getCommentsPublisherInfo(commentsList)
    }

    @ExperimentalCoroutinesApi
    fun getUserInfo(userId: String){
        viewModelScope.launch {
            repository.getUserInfo(userId).collect {
                when {
                    it.isSuccess -> {
                        val user = it.getOrNull()
                        if(user != null){
                            userInfoMutableLiveData.postValue(user!!)
                        }
                    }
                    it.isFailure -> {
                        it.exceptionOrNull()?.printStackTrace()
                    }
                }
            }
        }
    }

    @ExperimentalCoroutinesApi
    fun getUserFollowers(userId: String){
        viewModelScope.launch {
            repository.getUserFollowers(userId).collect {
                when {
                    it.isSuccess -> {
                        val list = it.getOrNull()
                        if (list != null) {
//                            for(a in list)
//                            Log.d("DAGDAG", "getUserFollowers: ${list.size}")
                            followerListMutableLiveData.postValue(list as MutableList<String>)
                        }
                    }
                    it.isFailure -> {
                        it.exceptionOrNull()?.printStackTrace()
                    }
                }
            }
        }
    }

    @ExperimentalCoroutinesApi
    fun getUserFollowings(userId: String){
        viewModelScope.launch {
            repository.getUserFollowings(userId).collect {
                when {
                    it.isSuccess -> {
                        val list = it.getOrNull()
                        if (list != null) {
//                            for(a in list)
//                            Log.d("DAGDAG", "getUserFollowers: ${list.size}")
                            followingListMutableLiveData.postValue(list as MutableList<String>)
                        }
                    }
                    it.isFailure -> {
                        it.exceptionOrNull()?.printStackTrace()
                    }
                }
            }
        }
    }

    @ExperimentalCoroutinesApi
    fun getUserPosts(userId: String){
        viewModelScope.launch {
            repository.getUserPosts(userId).collect {
                when {
                    it.isSuccess -> {
                        val list = it.getOrNull()
                        userPostMutableLiveDataList.postValue(list as MutableList<Post>)
                    }
                    it.isFailure -> {
                        it.exceptionOrNull()?.printStackTrace()
                    }
                }
            }
        }
    }

    @ExperimentalCoroutinesApi
    fun searchUser(text: String){
//        searchUserMutableLiveDataList.postValue(MutableList<User>)
        viewModelScope.launch {
            repository.searchUser(text).collect {
                when {
                    it.isSuccess -> {
                        val list = it.getOrNull()
                        searchUserMutableLiveDataList.postValue(list as MutableList<User>)
                    }
                    it.isFailure -> {
                        it.exceptionOrNull()?.printStackTrace()
                    }
                }
            }
        }
    }

    @ExperimentalCoroutinesApi
    fun isUserFollowed(userId: String){
        viewModelScope.launch {
            repository.isUserFollowed(userId).collect {
                when {
                    it.isSuccess -> {
                        val isFollowed = it.getOrNull()
                        isUserFollowedMutableLiveData.postValue(isFollowed!!)

                    }
                    it.isFailure -> {
                        it.exceptionOrNull()?.printStackTrace()
                    }
                }
            }
        }
    }
    //////////////// firebase read ///////////////////

    //////////////// firebase write //////////////////
    fun likePost(postId: String){
        viewModelScope.launch {
            repository.likePost(postId)
        }
    }

    fun unLikePost(postId: String){
        viewModelScope.launch {
            repository.unLikePost(postId)
        }
    }

    fun follow(userId: String){
        viewModelScope.launch {
            repository.follow(userId)
        }
    }

    fun unFollow(userId: String){
        viewModelScope.launch {
            repository.unFollow(userId)
        }
    }

    fun updateUserProfileInfo(fullName: String, userName: String, bio: String){
        profileUpdateDoneMutableLiveData.postValue("updating")
        viewModelScope.launch {
            val isUpdated = repository.updateUserProfileInfo(fullName, userName, bio)
            if(isUpdated){
                profileUpdateDoneMutableLiveData.postValue("done")
            } else {
                profileUpdateDoneMutableLiveData.postValue("failed")
            }
        }
    }

    fun updateUserProfileInfoWithImage(fullName: String, userName: String, bio: String, imageUri: Uri){
        profileUpdateDoneMutableLiveData.postValue("updating")
        viewModelScope.launch {
            val url = repository.uploadImage(imageUri,false)
            if (url != ""){
                val isUpdated = repository.updateUserProfileInfoWithImage(fullName, userName, bio, url)
                if(isUpdated){
                    profileUpdateDoneMutableLiveData.postValue("done")
                } else {
                    profileUpdateDoneMutableLiveData.postValue("failed")
                }
            } else {
                profileUpdateDoneMutableLiveData.postValue("failed")
            }
        }
    }

    fun addNewPost(caption: String, imageUri: Uri){
        addPostDoneMutableLiveData.postValue("adding")
        viewModelScope.launch {
            val url = repository.uploadImage(imageUri,true)
            if (url != ""){
                val isUpdated = repository.addNewPost(caption, url)
                if(isUpdated){
                    addPostDoneMutableLiveData.postValue("done")
                } else {
                    addPostDoneMutableLiveData.postValue("failed")
                }
            } else {
                addPostDoneMutableLiveData.postValue("failed")
            }
        }
    }

    fun addComment(comment: String, postId: String){
        if(comment.trim().isNotEmpty()) {
            addCommentDoneMutableLiveData.postValue("adding")
            viewModelScope.launch {
                repository.addComment(comment, postId).let {
                    addCommentDoneMutableLiveData.postValue(it)
                }
            }
        }
    }
    //////////////// firebase write //////////////////

}