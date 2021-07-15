package com.gmail.hamedvakhide.instaclone.views.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gmail.hamedvakhide.instaclone.R
import com.gmail.hamedvakhide.instaclone.adapter.PostAdapter
import com.gmail.hamedvakhide.instaclone.model.Post
import com.gmail.hamedvakhide.instaclone.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.fragment_post_detail.view.*


class PostDetailFragment : Fragment() {
    private lateinit var mainViewModel: MainViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var postAdapter: PostAdapter
    private var postList: MutableList<Post> = ArrayList()
    private var userId: String =""
    private var position: Int = 0
    private var firstRun: Boolean = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_post_detail, container, false)

        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        if (pref != null) {
            userId = pref.getString("userId", "").toString()
            position = pref.getInt("position",0)
        }

        firstRun = true

        ///// back button onClick action
        view.btn_back_post_detail.setOnClickListener {
            val prefsEdit = pref!!.edit()
            prefsEdit.putString("profileId",userId)
            prefsEdit.apply()
            showFragment(ProfileFragment())
        }

        recyclerView = view.recycler_view_post_detail
        val linearLayoutManager = LinearLayoutManager(context)
        ///show new post at top
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        recyclerView.layoutManager = linearLayoutManager

        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)/////////

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

        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        //// observe and add data to postList
        mainViewModel.getUserPostMutableLiveDataList().observe(viewLifecycleOwner, {
            if (it != null) {
                postList.clear()
                postList.addAll(it)
                //// go to clicked post position
                if(firstRun) {
                    recyclerView.scrollToPosition(position)
                    firstRun = false
                }
                ///
                mainViewModel.getPostsCommentsNumber(postList)
                mainViewModel.getPostsLikesNumber(postList)

                mainViewModel.isPostsLiked(postList)
                postAdapter.notifyDataSetChanged()
            }
        })
        mainViewModel.isLikeMutableLiveDataList.observe(viewLifecycleOwner,{
            mainViewModel.getPostsPublishersInfo(postList)
        })

        mainViewModel.getUserPosts(userId)

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