package com.gmail.hamedvakhide.instaclone.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.gmail.hamedvakhide.instaclone.repository.AppRepository
import com.gmail.hamedvakhide.instaclone.model.Comment
import com.gmail.hamedvakhide.instaclone.model.Post
import com.gmail.hamedvakhide.instaclone.model.User
import com.google.firebase.auth.FirebaseUser

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val appRepository: AppRepository
    private val userMutableLiveData: MutableLiveData<FirebaseUser>
    private val loggedOutMutableLiveData: MutableLiveData<Boolean>
    private var loading: MutableLiveData<Boolean>


    private var followingListMutableLiveData: MutableLiveData<MutableList<String>>
    private var followerListMutableLiveData: MutableLiveData<MutableList<String>>
    private var postMutableLiveDataList: MutableLiveData<MutableList<Post>>
    private var userPostMutableLiveDataList = MutableLiveData<MutableList<Post>>()

    private var searchUserMutableLiveDataList : MutableLiveData<MutableList<User>>

    private var userInfoMutableLiveData : MutableLiveData<User>

    private var isUserFollowedMutableLiveData : MutableLiveData<Boolean>

    var profileUpdateDoneMutableLiveData: MutableLiveData<String> = MutableLiveData()
    var addPostDoneMutableLiveData: MutableLiveData<String> = MutableLiveData()

    var currentUserProfilePic : MutableLiveData<String> = MutableLiveData()

    var addcommentDoneMutableLiveData: MutableLiveData<String> = MutableLiveData()

    var isLikeMutableLiveDataList:MutableLiveData<MutableList<Boolean>> = MutableLiveData()
    var postsLikesNumberMutableLiveDataList : MutableLiveData<MutableList<Long>> = MutableLiveData()
    var postsCommentsNumberMutableLiveDataList : MutableLiveData<MutableList<Long>> = MutableLiveData()
    var postsPublishersInfoMutableLiveDataList : MutableLiveData<MutableList<User>> = MutableLiveData()
    var commentsMutableLiveDataList : MutableLiveData<MutableList<Comment>> = MutableLiveData()
    var commentsPublishersInfoMutableLiveDataList : MutableLiveData<MutableList<User>> = MutableLiveData()


    init {
        appRepository = AppRepository(application)
        userMutableLiveData = appRepository.getUserMutableLiveData()
        loggedOutMutableLiveData = appRepository.getLoggedOutMutableLiveData()
        loading = appRepository.getLoadingMutableLiveData()

        followingListMutableLiveData = appRepository.getUserfollowingMutableLiveList()
        followerListMutableLiveData = appRepository.getUserfollowerMutableLiveList()
        postMutableLiveDataList = appRepository.getPostMutableLiveDataList()
        userPostMutableLiveDataList =appRepository.getUserPostMutableLiveDataList()

        searchUserMutableLiveDataList = appRepository.getSearchedUserMutableLiveDataList()

        userInfoMutableLiveData = appRepository.getUserInfoMutableLiveData()

        isUserFollowedMutableLiveData = appRepository.isFollowedMutableLiveData()

        profileUpdateDoneMutableLiveData = appRepository.profileUpdateDoneMutableLiveData
        addPostDoneMutableLiveData = appRepository.addPostDoneMutableLiveData

        currentUserProfilePic = appRepository.currentUserProfilePic

        addcommentDoneMutableLiveData = appRepository.addcommentDoneMutableLiveData

        isLikeMutableLiveDataList = appRepository.isLikeMutableLiveDataList

        postsLikesNumberMutableLiveDataList = appRepository.postsLikesNumberMutableLiveDataList
        postsCommentsNumberMutableLiveDataList = appRepository.postsCommentsNumberMutableLiveDataList
        postsPublishersInfoMutableLiveDataList = appRepository.postsPublishersInfoMutableLiveDataList
        commentsMutableLiveDataList = appRepository.commentsMutableLiveDataList
        commentsPublishersInfoMutableLiveDataList = appRepository.commentsPublishersInfoMutableLiveDataList
    }

    ///////////////////////////////////////////////////////////////////////////////////////
    /////////////////////// Authentication ////////////////////////////////////////////////
    fun register(fullName: String, userName: String, email: String, password: String){
        appRepository.register(fullName, userName, email, password)
    }

    fun login(email: String, password: String){
        appRepository.login(email, password)
    }

    fun logOut(){
        appRepository.logOut()
    }

    fun isUserLoggedIn():Boolean {
        return appRepository.isUserLoggedIn()
    }

    fun getUserId(): String{
        return appRepository.getUserId()
    }
    /////////////////////// Authentication ////////////////////////////////////////////////

    //////////////////////////////////////////////////////////
    ////////////////// Db Write///////////////////////////////
    fun follow(userId: String){
        appRepository.follow(userId)
    }

    fun unFollow(userId: String){
        appRepository.unFollow(userId)
    }

    fun likePost(postId: String){
        appRepository.likePost(postId)
    }

    fun unLikePost(postId: String){
        appRepository.unLikePost(postId)
    }

    fun updateUserProfileInfo(fullName: String, userName: String, bio: String){
        appRepository.updateUserProfileInfo(fullName, userName, bio)
    }

    fun updateUserProfileInfoWithImage(fullName: String, userName: String, bio: String, imageUri: Uri) {
        appRepository.updateUserProfileInfoWithImage(fullName, userName, bio, imageUri)
    }

    fun addNewPost(caption: String, imageUri: Uri){
        appRepository.addNewPost(caption, imageUri)
    }

    fun addComment(comment: String, postId: String) {
        appRepository.addComment(comment, postId)
    }
    ////////////////// Db Write///////////////////////////////

    /////////////////////////////////////////////////////////
    ////////////////// Db Read //////////////////////////////

    fun isUserFollowed(userId: String){
        appRepository.isUserFollowed(userId)
    }

    fun isPostsLiked(postsList: MutableList<Post>){
        appRepository.isPostsLiked(postsList)
    }

    fun getUserFollowings(userId: String){
        appRepository.getUserFollowings(userId)
    }

    fun getUserFollowers(userId: String){
        appRepository.getUserFollowers(userId)
    }

    fun searchUser(text: String){
        appRepository.searchUser(text)
    }

    fun getPosts(){
        appRepository.getPosts()
    }

    fun getUserInfo(userId: String){
        appRepository.getUserInfo(userId)
    }

    fun getCurrentUserProfilePic(){
        appRepository.getCurrentUserProfilePic()
    }

    fun getUserPosts(userId: String) {
        appRepository.getUserPosts(userId)
    }

    fun getPostsCommentsNumber(postsList: MutableList<Post>){
        appRepository.getPostsCommentsNumber(postsList)
    }

    fun getPostsLikesNumber(postsList: MutableList<Post>){
        appRepository.getPostsLikesNumber(postsList)
    }

    fun getPostsPublishersInfo(postsList: MutableList<Post>){
        appRepository.getPostsPublishersInfo(postsList)
    }

    fun getPostComments(postId: String){
        appRepository.getPostComments(postId)
    }

    fun getCommentsPublisherInfo(commentsList: MutableList<Comment>) {
        appRepository.getCommentsPublisherInfo(commentsList)
    }
    ////////////////// Db Read //////////////////////////////

    ///////////////////////////////////////////////////////////////
    ////////////////// Get LiveData ///////////////////////////////
    fun getUserMutableLiveData():MutableLiveData<FirebaseUser>{
        return userMutableLiveData
    }

    fun getLoggedOutMutableLiveData():MutableLiveData<Boolean>{
        return loggedOutMutableLiveData
    }

    fun getLoadingMutableLiveData():MutableLiveData<Boolean>{
        return loading
    }

    fun getUserfollowingMutableLiveList(): MutableLiveData<MutableList<String>>{
        return followingListMutableLiveData
    }

    fun getUserfollowerMutableLiveList(): MutableLiveData<MutableList<String>>{
        return followerListMutableLiveData
    }

    fun getPostMutableLiveDataList(): MutableLiveData<MutableList<Post>>{
        return postMutableLiveDataList
    }

    fun getUserPostMutableLiveDataList(): MutableLiveData<MutableList<Post>>{
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