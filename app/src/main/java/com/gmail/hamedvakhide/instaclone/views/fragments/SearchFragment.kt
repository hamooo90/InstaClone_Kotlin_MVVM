package com.gmail.hamedvakhide.instaclone.views.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gmail.hamedvakhide.instaclone.R
import com.gmail.hamedvakhide.instaclone.adapter.UserAdapter
//import com.gmail.hamedvakhide.instaclone.adapter.UserAdapter
import com.gmail.hamedvakhide.instaclone.model.User
import com.gmail.hamedvakhide.instaclone.utils.KeyboardUtil
import com.gmail.hamedvakhide.instaclone.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
//import com.gmail.hamedvakhide.instaclone.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.fragment_search.view.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.util.*
import kotlin.collections.ArrayList


@AndroidEntryPoint
class SearchFragment : Fragment() {
    private val viewModel : MainViewModel by viewModels()

    private var recyclerView: RecyclerView? = null
    private lateinit var userAdapter: UserAdapter
    private lateinit var mUser: MutableList<User>

    override fun onStop() {
        super.onStop()
        activity?.let { KeyboardUtil().hideKeyboard(it) }
    }

    override fun onPause() {
        super.onPause()
        activity?.let { KeyboardUtil().hideKeyboard(it) }
    }

    override fun onDestroy() {
        super.onDestroy()
        activity?.let { KeyboardUtil().hideKeyboard(it) }
    }

    @ExperimentalCoroutinesApi
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_search, container, false)

        //show keyboard at fragment start
        view.edit_text_search_search.requestFocus()
        context?.let { KeyboardUtil().showKeyboard(it) }

        recyclerView = view.recycler_view_search
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = LinearLayoutManager(context)
        mUser = ArrayList()

        userAdapter = context?.let {
                UserAdapter(activity,it, mUser as ArrayList<User>,viewModel)
        }!!
        recyclerView?.adapter = userAdapter

        //// observe to refresh recyclerView
        viewModel.searchUserMutableLiveDataList.observe(viewLifecycleOwner, {
            if(it != null){
                mUser.clear()
                mUser.addAll(it)
                userAdapter.notifyDataSetChanged()
            }
        })

        /// search on editText changed
        view.edit_text_search_search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (view.edit_text_search_search.text.toString() == "") {
                    recyclerView?.visibility = View.GONE

                } else {
                    /// fix previous search visible for a second bug ///
                    mUser.clear()
                    userAdapter.notifyDataSetChanged()
                    ////////////////////////////////////////////////////
                    recyclerView?.visibility = View.VISIBLE
                    viewModel.searchUser(s.toString().toLowerCase(Locale.getDefault()))
                }
            }
            override fun afterTextChanged(s: Editable?) {
            }
        })

        return view
    }


}