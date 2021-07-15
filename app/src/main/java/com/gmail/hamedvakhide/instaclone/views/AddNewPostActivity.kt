package com.gmail.hamedvakhide.instaclone.views

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.gmail.hamedvakhide.instaclone.R
import com.gmail.hamedvakhide.instaclone.utils.KeyboardUtil
import com.gmail.hamedvakhide.instaclone.viewmodel.MainViewModel
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_add_new_post.*
import java.util.*

class AddNewPostActivity : AppCompatActivity() {

    private lateinit var mainViewModel: MainViewModel

    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_post)

        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        /// observe post upload state
        /// if still uploading show loading progress
        mainViewModel.addPostDoneMutableLiveData.observe(this,{
            if(it != null){
                when (it) {
                    "adding" -> {
                        progress_bar_new_post.visibility = View.VISIBLE
                    }
                    "failed" -> {
                        progress_bar_new_post.visibility = View.GONE
                        Toast.makeText(this,"Failed", Toast.LENGTH_SHORT).show()
                    }
                    "done" -> {
                        progress_bar_new_post.visibility = View.GONE
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            }
        })

        btn_close_new_post.setOnClickListener {
            finish()
        }

        btn_save_new_post.setOnClickListener {
            KeyboardUtil().hideKeyboard(this)
            uploadPost()
        }

        image_view_new_post.setOnClickListener {
            ///start image picker
            CropImage.activity()
                .start(this)
        }

    }

    private fun uploadPost() {
        when{
            imageUri == null -> Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
            edit_text_caption_new_post.text.toString() == "" -> {
                Toast.makeText(this, "Caption cant be empty", Toast.LENGTH_SHORT).show()
            }
            else ->{
                mainViewModel.addNewPost(edit_text_caption_new_post.text.toString().toLowerCase(Locale.getDefault()), imageUri!!)
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data!=null){
            val result = CropImage.getActivityResult(data)
            imageUri = result.uri
            image_view_new_post.setImageURI(imageUri)
        } else {
            ///error handle
        }
    }
}