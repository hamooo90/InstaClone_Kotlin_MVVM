package com.gmail.hamedvakhide.instaclone.views.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.gmail.hamedvakhide.instaclone.views.AccountSettingActivity
import com.gmail.hamedvakhide.instaclone.R
import com.gmail.hamedvakhide.instaclone.adapter.UserPostAdapter
//import com.gmail.hamedvakhide.instaclone.adapter.UserPostAdapter
import com.gmail.hamedvakhide.instaclone.model.Post
import com.gmail.hamedvakhide.instaclone.viewmodel.MainViewModel
//import com.gmail.hamedvakhide.instaclone.viewmodel.MainViewModel
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import kotlinx.coroutines.ExperimentalCoroutinesApi

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private val viewModel : MainViewModel by viewModels()

    private lateinit var profileId: String
    private var postList: MutableList<Post> = ArrayList()
    private var userPostAdapter: UserPostAdapter? = null
    private lateinit var userId : String


    override fun onStop() {
        super.onStop()
        viewModel.saveProfileIdToPref(userId)
    }

    override fun onPause() {
        super.onPause()
        viewModel.saveProfileIdToPref(userId)

    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.saveProfileIdToPref(userId)
    }

    @ExperimentalCoroutinesApi
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        /// observe and show following number
        viewModel.followingListMutableLiveData
            .observe(viewLifecycleOwner, Observer {
                if (it != null) {
                    view.text_view_following_number_profile.text = (it.size-1).toString()
                } else {
                    view.text_view_following_number_profile.text = "0"
                }
            })
        /// observe and show followers number
        viewModel.followerListMutableLiveData
            .observe(viewLifecycleOwner, Observer {
                if (it != null) {
                    view.text_view_followers_number_profile.text = it.size.toString()
                } else {
                    view.text_view_followers_number_profile.text = "0"
                }
            })

        /// observe and show user info
        viewModel.userInfoMutableLiveData.observe(viewLifecycleOwner, {
            Picasso.get().load(it.getImage()).placeholder(R.drawable.profile).into(view.circle_Image_profile)
            view.txt_full_name_profile.text = it.getFullName()
            view.text_view_username_profile.text = it.getUserName()
            view.txt_bio_profile.text = it.getBio()
        })
        //////////

        /// check which user profile we want to see//////
        /////////////////////////
        profileId = viewModel.readProfileIdFromPref()
        ////////////////////////


        var recyclerView = view.recycler_view_profile
        recyclerView.isNestedScrollingEnabled = false
        recyclerView.setHasFixedSize(true)
        val linearLayoutManager : LinearLayoutManager = GridLayoutManager(context,3)
//        linearLayoutManager.reverseLayout = true
        recyclerView.layoutManager = linearLayoutManager

        userPostAdapter = context?.let { UserPostAdapter(activity,it, postList as ArrayList,viewModel) }
        recyclerView.adapter = userPostAdapter

        ///////change button text////////////////////////////////////////
        //////if the clicked user from search is the same as current user
        userId = viewModel.getUserId()
        if (profileId == userId) {
            view.btn_edit_setting_profile.text = "Edit Profile"
        } else if (profileId != userId) {
            viewModel.isUserFollowedMutableLiveData.observe(viewLifecycleOwner,{
                if(it){
                    view.btn_edit_setting_profile.text = "Following"
                }else{
                    view.btn_edit_setting_profile.text = "Follow"
                }
            })
            viewModel.isUserFollowed(profileId)
        }

        /////////////edit profile or follow unfollow action
        view.btn_edit_setting_profile.setOnClickListener {


            val btnText = btn_edit_setting_profile.text.toString()
            when (btnText) {
                "Edit Profile" -> startActivity(Intent(context, AccountSettingActivity::class.java))
                "Follow" -> {
                    viewModel.follow(profileId)
                }
                "Following" -> {
                    viewModel.unFollow(profileId)
                }
            }
        }
//
        viewModel.userPostMutableLiveDataList.observe(viewLifecycleOwner, Observer {
            if(it!=null){
                text_view_post_number_profile.text = it.size.toString()
                postList.clear()
                postList.addAll(it)
                postList.reverse()
                userPostAdapter!!.notifyDataSetChanged()
            }

        })
//
        viewModel.getUserFollowings(profileId)
        viewModel.getUserFollowers(profileId)
        viewModel.getUserInfo(profileId)
//
        viewModel.getUserPosts(profileId)



        return view
    }


}