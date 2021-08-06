package com.gmail.hamedvakhide.instaclone.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.gmail.hamedvakhide.instaclone.R
import com.gmail.hamedvakhide.instaclone.adapter.CommentAdapter
import com.gmail.hamedvakhide.instaclone.model.Comment
import com.gmail.hamedvakhide.instaclone.utils.KeyboardUtil
import com.gmail.hamedvakhide.instaclone.viewmodel.MainViewModel
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_comments.*
import kotlinx.coroutines.ExperimentalCoroutinesApi

@AndroidEntryPoint
class CommentsActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

    private lateinit var commentAdapter: CommentAdapter
    private lateinit var commentsList: MutableList<Comment>

    private var publisherId = ""
    private var postId = ""
    private var imageUrlProfPost = ""
    private var publisherName = ""
    private var caption = ""

    @ExperimentalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments)

        // get intent extras sent from PostAdapter
        val intent = intent
        publisherId = intent.getStringExtra("publisherId").toString()
        postId = intent.getStringExtra("postId").toString()
        imageUrlProfPost = intent.getStringExtra("imageUrl").toString()
        publisherName = intent.getStringExtra("publisherName").toString()
        caption = intent.getStringExtra("caption").toString()

        Picasso.get().load(imageUrlProfPost).placeholder(R.drawable.profile)
            .into(circle_image_post_user_prof_comment)
        txt_publisher_comment.text = publisherName
        txt_caption_comment.text = caption

        val recyclerView = recycler_view_comment
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        recyclerView.layoutManager = linearLayoutManager
        commentsList = ArrayList()
        commentAdapter = CommentAdapter(
            this,
            commentsList,
            viewModel.commentsPublishersInfoMutableLiveDataList,
            this
        )
        recyclerView.adapter = commentAdapter

        ///////// show current user profile pic
        viewModel.currentUserProfilePic.observe(this, {
            if (it != null || it != "") {
                Picasso.get().load(it).placeholder(R.drawable.profile)
                    .into(circle_image_current_user_prof_comment)
            }
        })

        /// observe if comment is successfully added to db or not
        viewModel.addCommentDoneMutableLiveData.observe(this, {
            if (it != null) {
                if (it == "failed") {
                    Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
                } else if (it == "done") {
                    edit_text_comment.text.clear()
                    KeyboardUtil().hideKeyboard(this)
                }
            }
        })

        btn_close_comment.setOnClickListener {
            finish()
        }

        btn_post_comment_comment.setOnClickListener {
            val comment = edit_text_comment.text.toString()
            viewModel.addComment(comment, postId)
        }

        /// observe if a comment is added and refresh recyclerView
        viewModel.commentsMutableLiveDataList.observe(this, Observer {
            if (it != null) {
                commentsList.clear()
                commentsList.addAll(it)

                viewModel.getCommentsPublisherInfo(it)
                commentAdapter.notifyDataSetChanged()
                /////scroll to top of recycler view
                if (commentsList.size != 0) {
                    recyclerView.smoothScrollToPosition(commentsList.size - 1)
                }
            }
        })
//
//
        viewModel.getCurrentUserProfilePic()
        viewModel.getPostComments(postId)
    }
}