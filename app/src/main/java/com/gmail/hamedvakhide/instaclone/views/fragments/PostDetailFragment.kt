package com.gmail.hamedvakhide.instaclone.views.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gmail.hamedvakhide.instaclone.R
import com.gmail.hamedvakhide.instaclone.adapter.PostAdapter
//import com.gmail.hamedvakhide.instaclone.adapter.PostAdapter
import com.gmail.hamedvakhide.instaclone.model.Post
import com.gmail.hamedvakhide.instaclone.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
//import com.gmail.hamedvakhide.instaclone.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.fragment_post_detail.view.*
import kotlinx.coroutines.ExperimentalCoroutinesApi


@AndroidEntryPoint
class PostDetailFragment : Fragment() {
    private val viewModel : MainViewModel by viewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var postAdapter: PostAdapter
    private var postList: MutableList<Post> = ArrayList()
    private var userId: String =""
    private var position: Int = 0
    private var firstRun: Boolean = true

    @ExperimentalCoroutinesApi
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_post_detail, container, false)

        ///////////////////////
        userId = viewModel.readBackUserIdFromPref()
        position = viewModel.readPositionFromPref()
        //////////////////////////

        firstRun = true

        ///// back button onClick action
        view.btn_back_post_detail.setOnClickListener {
            ///////////////////
            viewModel.saveProfileIdToPref(userId)
            ////////////////////
            showFragment(ProfileFragment())
        }

        recyclerView = view.recycler_view_post_detail
        val linearLayoutManager = LinearLayoutManager(context)
        ///show new post at top
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        recyclerView.layoutManager = linearLayoutManager

        postAdapter = context?.let { PostAdapter(
            activity,
            it, postList as ArrayList<Post>,
            viewModel.isPostsLikedByCurrentUserMutableLiveDataList,
            viewModel.postsLikesNumberMutableLiveDataList,
            viewModel.postsCommentsNumberMutableLiveDataList,
            viewModel.postsPublishersInfoMutableLiveDataList,
            viewModel,
            viewLifecycleOwner
        ) }!!

        recyclerView.adapter = postAdapter

        //// observe and add data to postList
        viewModel.userPostMutableLiveDataList.observe(viewLifecycleOwner, {
            if (it != null) {
                postList.clear()
                postList.addAll(it)
                //// go to clicked post position
                if(firstRun) {
                    recyclerView.scrollToPosition(position)
                    firstRun = false
                }
                viewModel.getPostsPublishersInfo(postList)//////

                viewModel.getPostsCommentsNumber(postList)
                viewModel.getPostsLikesNumber(postList)

                viewModel.isPostsLiked(postList)
                postAdapter.notifyDataSetChanged()
            }
        })

        viewModel.getUserPosts(userId)

        return view
    }

    private fun showFragment(fragment: Fragment) {
        val fragmentTransX = activity?.supportFragmentManager?.beginTransaction();
        fragmentTransX?.replace(
            R.id.fragment_container,
            fragment
        )?.commit()
    }
}