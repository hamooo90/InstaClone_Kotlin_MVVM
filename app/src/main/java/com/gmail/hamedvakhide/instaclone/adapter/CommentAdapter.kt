package com.gmail.hamedvakhide.instaclone.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.gmail.hamedvakhide.instaclone.R
import com.gmail.hamedvakhide.instaclone.model.Comment
import com.gmail.hamedvakhide.instaclone.model.User
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class CommentAdapter(
    private val mContext: Context,
    private val mComment: MutableList<Comment>,
    private val mCommenterInfo: MutableLiveData<MutableList<User>>,
    private val viewLifecycleOwner: LifecycleOwner
) : RecyclerView.Adapter<CommentAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.comment_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.commentTXT.text = mComment[position].getComment()

        ////observe publisherInfoList for change
        mCommenterInfo.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                val mPublisherInfo = it
                if (mPublisherInfo.size == mComment.size) {
                    val user = mPublisherInfo[position]
                    holder.userNameTXT.text = user.getUserName()
                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile).into(holder.userProfileImage)
                }
            }
        })
    }

    override fun getItemCount(): Int {
        return mComment.size
    }

    class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {
        var userNameTXT: TextView = itemView.findViewById(R.id.txt_user_name_item_comment)
        var commentTXT: TextView = itemView.findViewById(R.id.txt_comment_item_comment)
        var userProfileImage: CircleImageView = itemView.findViewById(R.id.circle_Image_item_comment)
    }
}