package com.gmail.hamedvakhide.instaclone.repository

import com.gmail.hamedvakhide.instaclone.model.Comment
import com.gmail.hamedvakhide.instaclone.model.Post
import com.gmail.hamedvakhide.instaclone.model.User
import kotlinx.coroutines.flow.Flow

interface FirebaseRepoInterface {
    fun getPostComments(postId: String): Flow<Result<List<Comment>>>
    fun getCurrentUserProfilePic(): Flow<Result<String>>
    fun getUserInfo(userId: String): Flow<Result<User>>
    fun getUserFollowers(userId: String) : Flow<Result<List<String>>>
    fun getUserFollowings(userId: String) : Flow<Result<List<String>>>
    fun getUserPosts(userId: String) : Flow<Result<List<Post>>>
    fun searchUser(text: String) : Flow<Result<List<User>>>
    fun isUserFollowed(userId: String) : Flow<Result<Boolean>>
}