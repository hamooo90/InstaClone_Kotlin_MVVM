package com.gmail.hamedvakhide.instaclone.views.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.gmail.hamedvakhide.instaclone.views.AccountSettingActivity
import com.gmail.hamedvakhide.instaclone.R
import com.gmail.hamedvakhide.instaclone.adapter.UserPostAdapter
import com.gmail.hamedvakhide.instaclone.model.Post
import com.gmail.hamedvakhide.instaclone.viewmodel.MainViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*


class ProfileFragment : Fragment() {

    private lateinit var mainViewModel: MainViewModel

    private lateinit var profileId: String
    private lateinit var firebaseUser: FirebaseUser

    private var postList: MutableList<Post> = ArrayList()
    private var userPostAdapter: UserPostAdapter? = null

    private lateinit var userId : String


    override fun onStop() {
        super.onStop()
        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", userId)
        pref?.apply()
    }

    override fun onPause() {
        super.onPause()
        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", userId)
        pref?.apply()
    }

    override fun onDestroy() {
        super.onDestroy()
        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", userId)
        pref?.apply()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        //////////////
        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        /// observe and show following number
        mainViewModel.getUserfollowingMutableLiveList()
            .observe(viewLifecycleOwner, Observer {
                if (it != null) {
                    view.text_view_following_number_profile.text = (it.size-1).toString()
                } else {
                    view.text_view_following_number_profile.text = "0"
                }
            })
        /// observe and show followers number
        mainViewModel.getUserfollowerMutableLiveList()
            .observe(viewLifecycleOwner, Observer {
                if (it != null) {
                    view.text_view_followers_number_profile.text = it.size.toString()
                } else {
                    view.text_view_followers_number_profile.text = "0"
                }
            })

        /// observe and show user info
        mainViewModel.getUserInfoMutableLiveData().observe(viewLifecycleOwner, {
            Picasso.get().load(it.getImage()).placeholder(R.drawable.profile).into(view.circle_Image_profile)
            view.txt_full_name_profile.text = it.getFullName()
            view.text_view_username_profile.text = it.getUserName()
            view.txt_bio_profile.text = it.getBio()
        })
        //////////

        /// check which user profile we want to see//////
        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        if (pref != null) {
            profileId = pref.getString("profileId", "").toString()
        }


        var recyclerView = view.recycler_view_profile
        recyclerView.isNestedScrollingEnabled = false
        recyclerView.setHasFixedSize(true)
        val linearLayoutManager : LinearLayoutManager = GridLayoutManager(context,3)
//        linearLayoutManager.reverseLayout = true
        recyclerView.layoutManager = linearLayoutManager

        userPostAdapter = context?.let { UserPostAdapter(it, postList as ArrayList) }
        recyclerView.adapter = userPostAdapter

        ///////change button text////////////////////////////////////////
        //////if the clicked user from search is the same as current user
        userId = mainViewModel.getUserId()
        if (profileId == userId) {
            view.btn_edit_setting_profile.text = "Edit Profile"
        } else if (profileId != userId) {
            mainViewModel.isFollowedMutableLiveData().observe(viewLifecycleOwner,{
                if(it){
                    view.btn_edit_setting_profile.text = "Following"
                }else{
                    view.btn_edit_setting_profile.text = "Follow"
                }
            })
            mainViewModel.isUserFollowed(profileId)
        }

        /////////////edit profile or follow unfollow action
        view.btn_edit_setting_profile.setOnClickListener {

            val btnText = btn_edit_setting_profile.text.toString()
            when (btnText) {
                "Edit Profile" -> startActivity(Intent(context, AccountSettingActivity::class.java))
                "Follow" -> {
                    mainViewModel.follow(profileId)
                }
                "Following" -> {
                    mainViewModel.unFollow(profileId)
                }
            }
        }

        mainViewModel.getUserPostMutableLiveDataList().observe(viewLifecycleOwner, Observer {
            if(it!=null){
                text_view_post_number_profile.text = it.size.toString()
                postList.clear()
                postList.addAll(it)
                postList.reverse()
                userPostAdapter!!.notifyDataSetChanged()
            }

        })

        mainViewModel.getUserFollowings(profileId)
        mainViewModel.getUserFollowers(profileId)
        mainViewModel.getUserInfo(profileId)

        mainViewModel.getUserPosts(profileId)

        return view
    }


}