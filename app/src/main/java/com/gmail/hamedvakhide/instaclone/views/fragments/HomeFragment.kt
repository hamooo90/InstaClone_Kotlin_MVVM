package com.gmail.hamedvakhide.instaclone.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gmail.hamedvakhide.instaclone.R
import com.gmail.hamedvakhide.instaclone.adapter.PostAdapter
import com.gmail.hamedvakhide.instaclone.model.Post
import com.gmail.hamedvakhide.instaclone.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
//import com.gmail.hamedvakhide.instaclone.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.fragment_home.view.*

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var postAdapter: PostAdapter
    private lateinit var postList: MutableList<Post>

    private val viewModel : MainViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        recyclerView = view.recycler_view_home
        val linearLayoutManager = LinearLayoutManager(context)
        ///show new post at top
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        recyclerView.layoutManager = linearLayoutManager

        postList = ArrayList()
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

        viewModel.postsMutableLiveDataList.observe(viewLifecycleOwner, {
            if (it != null) {
                postList.clear()
                postList.addAll(it)
                viewModel.getPostsPublishersInfo(postList)//////

                viewModel.getPostsCommentsNumber(postList)
                viewModel.getPostsLikesNumber(postList)
                viewModel.isPostsLiked(postList)
                postAdapter.notifyDataSetChanged()
            }
        })


        viewModel.getPosts()


        return view
    }

}