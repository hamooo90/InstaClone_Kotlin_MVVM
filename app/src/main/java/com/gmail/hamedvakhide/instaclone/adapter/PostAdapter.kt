package com.gmail.hamedvakhide.instaclone.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.gmail.hamedvakhide.instaclone.R
import com.gmail.hamedvakhide.instaclone.model.Post
import com.gmail.hamedvakhide.instaclone.model.User
import com.gmail.hamedvakhide.instaclone.viewmodel.MainViewModel
//import com.gmail.hamedvakhide.instaclone.viewmodel.MainViewModel
import com.gmail.hamedvakhide.instaclone.views.CommentsActivity
import com.gmail.hamedvakhide.instaclone.views.fragments.ProfileFragment
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class PostAdapter(
    private val activity: FragmentActivity?,
    private val mContext: Context, private val mPost: List<Post>,
    private val mIsLikedLiveData: MutableLiveData<MutableList<Boolean>>,
    private val mLikesNumberLiveData: MutableLiveData<MutableList<Long>>,
    private val mCommentsNumberLiveData: MutableLiveData<MutableList<Long>>,
    private val mPostPublisherInfoLiveData: MutableLiveData<MutableList<User>>,
    private val viewModel: MainViewModel,
    private val viewLifecycleOwner: LifecycleOwner
                  ) :
    RecyclerView.Adapter<PostAdapter.ViewHolder>() {


    inner class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {
        var circleProfileImage: CircleImageView =
            itemView.findViewById(R.id.circle_image_user_prof_home)
        var textUsername: TextView = itemView.findViewById(R.id.user_name_home)
        var postImage: ImageView = itemView.findViewById(R.id.post_image_home)
        var likeButton: ImageView = itemView.findViewById(R.id.btn_post_like_home)
        var commentButton: ImageView = itemView.findViewById(R.id.btn_post_comment_home)
        var textLikes: TextView = itemView.findViewById(R.id.txt_likes_home)
        var textPublisher: TextView = itemView.findViewById(R.id.txt_publisher_home)
        var textCaption: TextView = itemView.findViewById(R.id.txt_caption_home)
        var textComments: TextView = itemView.findViewById(R.id.txt_comment_home)

        var userTopBar: LinearLayout = itemView.findViewById(R.id.linear_top_bar_post_item_home)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.post_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        val post = mPost[position]
        //// show post image
        Picasso.get().load(post.getImage()).placeholder(R.drawable.progress_animation).into(holder.postImage)

        ////// show caption
        if(post.getCaption() == ""){
            holder.textCaption.visibility = View.GONE
        } else{
            holder.textCaption.visibility = View.VISIBLE
            holder.textCaption.text = post.getCaption()
        }

        /// Go to post publisher profile page on circleImage and top username click
        holder.userTopBar.setOnClickListener {
            viewModel.saveProfileIdToPref(post.getPublisher())

            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.replace(R.id.fragment_container, ProfileFragment())?.commit()

        }

        //////////// observe and change like button state/////////////////
        mIsLikedLiveData.observe(viewLifecycleOwner,{
            if(it!=null){
                val mIsLiked=it
                if(mIsLiked.size==mPost.size) {
                    if (mIsLiked[position]) {
                        holder.likeButton.setImageResource(R.drawable.ic_fill_favorite_24)
                        holder.likeButton.tag = "liked"
                    } else {
                        holder.likeButton.setImageResource(R.drawable.ic_outline_favorite_24)
                        holder.likeButton.tag = "like"
                    }
                }

            }
        })

        //// observe and change post likes number
        mLikesNumberLiveData.observe(viewLifecycleOwner, Observer {
            if(it!=null) {
                val mLikesNumber = it
                if (mLikesNumber.size == mPost.size) {
                    if (mLikesNumber[position] > 1) {
                        holder.textLikes.text = mLikesNumber[position].toString() + " likes"
                    } else {
                        holder.textLikes.text = mLikesNumber[position].toString() + " like"
                    }
                }
            }
        })

        //// observe and change post comments number
        mCommentsNumberLiveData.observe(viewLifecycleOwner, Observer {
            if(it!=null) {
                val mCommentsNumber = it
                if (mCommentsNumber.size == mPost.size) {
                    if(mCommentsNumber[position]>1) {
                        holder.textComments.visibility = View.VISIBLE
                        holder.textComments.text =
                            "View all " + mCommentsNumber[position].toString() + " comments"
                    } else if (mCommentsNumber[position]>0) {
                        holder.textComments.visibility = View.VISIBLE
                        holder.textComments.text =
                            "View " + mCommentsNumber[position].toString() + " comment"
                    } else {
                        holder.textComments.visibility = View.GONE
                    }
                }
            }
        })

        /////// like button action handle
        holder.likeButton.setOnClickListener {
            if(holder.likeButton.tag == "like"){
                viewModel.likePost(post.getPostId())
            } else{
                viewModel.unLikePost(post.getPostId())
            }
        }

        /// start comment activity on click
        holder.commentButton.setOnClickListener {
            val img = holder.circleProfileImage.tag.toString()
            val intentComment = Intent(mContext,CommentsActivity::class.java)
            intentComment.putExtra("publisherId", post.getPublisher())
            intentComment.putExtra("postId",post.getPostId())
            intentComment.putExtra("imageUrl",img)
            intentComment.putExtra("publisherName",holder.textPublisher.text.toString())
            intentComment.putExtra("caption",holder.textCaption.text.toString())

            mContext.startActivity(intentComment)
        }
        holder.textComments.setOnClickListener {
            val img = holder.circleProfileImage.tag.toString()
            val intentComment = Intent(mContext,CommentsActivity::class.java)
            intentComment.putExtra("publisherId", post.getPublisher())
            intentComment.putExtra("postId",post.getPostId())
            intentComment.putExtra("imageUrl",img)
            intentComment.putExtra("publisherName",holder.textPublisher.text.toString())
            intentComment.putExtra("caption",holder.textCaption.text.toString())

            mContext.startActivity(intentComment)
        }

        /////observe and add publisher info
        mPostPublisherInfoLiveData.observe(viewLifecycleOwner, {

            if (it != null) {
                val mPublisherInfo = it
                if (mPublisherInfo.size == mPost.size) {
                    val user = mPublisherInfo[position]
                    holder.textUsername.text = user.getUserName()
                    holder.textPublisher.text = user.getUserName()
                    if(user.getImage().isNotEmpty()) {////
                        Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile)
                            .into(holder.circleProfileImage)
                        holder.circleProfileImage.tag = user!!.getImage()
                    }//////

                }
            }
        })

    }

    override fun getItemCount(): Int {
        return mPost.size
    }

}


