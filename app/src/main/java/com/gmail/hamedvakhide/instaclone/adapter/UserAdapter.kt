package com.gmail.hamedvakhide.instaclone.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.gmail.hamedvakhide.instaclone.R
import com.gmail.hamedvakhide.instaclone.views.fragments.ProfileFragment
import com.gmail.hamedvakhide.instaclone.model.User
import com.gmail.hamedvakhide.instaclone.viewmodel.MainViewModel
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class UserAdapter(
    private val activity: FragmentActivity?,
    private val mContext: Context,
    private val mUser: List<User>,
    private val viewModel: MainViewModel///

) : RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.user_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = mUser[position]
        holder.userNameTXT.text = user.getUserName()
        holder.userFullNameTXT.text = user.getFullName()
        Picasso.get().load(user.getImage()).placeholder(R.drawable.profile)
            .into(holder.userProfileImage)

        holder.itemView.setOnClickListener {

            viewModel.saveProfileIdToPref(user.getUid())

            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.replace(R.id.fragment_container, ProfileFragment())?.commit()

        }


    }

    override fun getItemCount(): Int {
        return mUser.size
    }

    class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {

        var userNameTXT: TextView = itemView.findViewById(R.id.txt_user_name_search)

        var userFullNameTXT: TextView = itemView.findViewById(R.id.txt_full_name_search)
        var userProfileImage: CircleImageView = itemView.findViewById(R.id.circle_Image_search)
    }



}