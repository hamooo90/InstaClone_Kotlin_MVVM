package com.gmail.hamedvakhide.instaclone.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.NonNull
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.gmail.hamedvakhide.instaclone.R
import com.gmail.hamedvakhide.instaclone.model.Post
import com.gmail.hamedvakhide.instaclone.viewmodel.MainViewModel
import com.gmail.hamedvakhide.instaclone.views.fragments.PostDetailFragment
import com.squareup.picasso.Picasso

class UserPostAdapter(
    private val activity: FragmentActivity?,
    private val mContext: Context,
    private val mPost: MutableList<Post>,
    private val viewModel: MainViewModel///

    ) :
    RecyclerView.Adapter<UserPostAdapter.ViewHolder>() {
    class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {
        var postImage: ImageView = itemView.findViewById(R.id.post_image_item_profile)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.user_post_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Picasso.get().load(mPost[position].getImage()).placeholder(R.drawable.image_placeholder).into(holder.postImage)

        holder.itemView.setOnClickListener {
            ///// fix some bugs when trying to show list in reverse
            val pos = mPost.size-1-position
            ///////////////////////////////////////////////////////

            viewModel.saveBackUserIdToPref(mPost[pos].getPublisher())
            viewModel.savePositionToPref(pos)

            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.replace(R.id.fragment_container, PostDetailFragment())?.commit()


        }
    }

    override fun getItemCount(): Int {
        return mPost.size
    }
}