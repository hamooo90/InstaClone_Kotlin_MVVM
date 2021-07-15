package com.gmail.hamedvakhide.instaclone.views.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gmail.hamedvakhide.instaclone.R
import com.gmail.hamedvakhide.instaclone.adapter.PostAdapter
import com.gmail.hamedvakhide.instaclone.model.Post
import com.gmail.hamedvakhide.instaclone.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.fragment_home.view.*


class HomeFragment : Fragment() {

    private lateinit var mainViewModel: MainViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var postAdapter: PostAdapter
    private lateinit var postList: MutableList<Post>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_home, container, false)

        recyclerView = view.recycler_view_home
        val linearLayoutManager = LinearLayoutManager(context)
        ///show new post at top
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        recyclerView.layoutManager = linearLayoutManager

        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)/////////


        postList = ArrayList()
//        likedList = ArrayList()
//        likesNumberList = ArrayList()

        postAdapter = context?.let { PostAdapter(
            it, postList as ArrayList<Post>,
            mainViewModel.isLikeMutableLiveDataList,
            mainViewModel.postsLikesNumberMutableLiveDataList,
            mainViewModel.postsCommentsNumberMutableLiveDataList,
            mainViewModel.postsPublishersInfoMutableLiveDataList,
            mainViewModel,
            viewLifecycleOwner
        ) }!!

        recyclerView.adapter = postAdapter

        /////////
        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        mainViewModel.getPostMutableLiveDataList().observe(viewLifecycleOwner, {
            if (it != null) {
                postList.clear()
                postList.addAll(it)

                mainViewModel.getPostsCommentsNumber(postList)
                mainViewModel.getPostsLikesNumber(postList)

                mainViewModel.isPostsLiked(postList)
                postAdapter.notifyDataSetChanged()
            }
        })
        mainViewModel.isLikeMutableLiveDataList.observe(viewLifecycleOwner, {
            if(it!=null){
                mainViewModel.getPostsPublishersInfo(postList)
            }
        })


        mainViewModel.getPosts()



        return view
    }

}