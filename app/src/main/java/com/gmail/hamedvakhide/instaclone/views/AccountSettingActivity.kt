package com.gmail.hamedvakhide.instaclone.views

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.gmail.hamedvakhide.instaclone.R
import com.gmail.hamedvakhide.instaclone.utils.KeyboardUtil
import com.gmail.hamedvakhide.instaclone.viewmodel.MainViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_account_setting.*
import java.util.*


class AccountSettingActivity : AppCompatActivity() {
    private lateinit var firebaseUser: FirebaseUser
    private var imageChanged = false
    private var imageUri: Uri? = null

    private lateinit var mainViewModel: MainViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_setting)

        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        //////observe for logout
        mainViewModel.getLoggedOutMutableLiveData().observe(this, androidx.lifecycle.Observer {
            if (it) {
                val intent = Intent(this, SignInActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        })

        //////////observe and add user info in fields
        mainViewModel.getUserInfoMutableLiveData().observe(this, {
            if (it != null) {
                Picasso.get().load(it.getImage()).placeholder(R.drawable.profile)
                    .into(circle_Image_setting)
                edit_text_full_name_setting.setText(it.getFullName())
                edit_text_username_setting.setText(it.getUserName())
                edit_text_bio_setting.setText(it.getBio())
            }
        })

        /// observe profile update stat
        /// if still updating show loading progress
        mainViewModel.profileUpdateDoneMutableLiveData.observe(this, {
            if(it != null) {
                when (it) {
                    "updating" -> {
                        progress_bar_acount_setting.visibility = View.VISIBLE
                    }
                    "failed" -> {
                        progress_bar_acount_setting.visibility = View.GONE
                    }
                    "done" -> {
                        progress_bar_acount_setting.visibility = View.GONE
                        finish()
                    }
                }
            }
        })

        firebaseUser = FirebaseAuth.getInstance().currentUser!!

        btn_close_setting.setOnClickListener {
            finish()
        }

        btn_delete_account_setting.setOnClickListener {
            Toast.makeText(this,"Not Implemented",Toast.LENGTH_SHORT).show()
        }

        btn_logout_setting.setOnClickListener {
            mainViewModel.logOut()
        }

        btn_change_pic_setting.setOnClickListener {
            imageChanged = true
            CropImage.activity().setAspectRatio(1, 1)
                .start(this)
        }

        circle_Image_setting.setOnClickListener {
            imageChanged = true
            CropImage.activity().setAspectRatio(1, 1)
                .start(this)
        }

        btn_save_setting.setOnClickListener {
            KeyboardUtil().hideKeyboard(this)
            if (imageChanged) {
                uploadImgAndUpdateInfo()
            } else {
                updateUserInfoNoImage()
            }
        }

        val userId = mainViewModel.getUserId()
        mainViewModel.getUserInfo(userId)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val result = CropImage.getActivityResult(data)
            imageUri = result.uri
            circle_Image_setting.setImageURI(imageUri)
        } else {
            ///error handle
        }
    }

    private fun uploadImgAndUpdateInfo() {
        when {
            imageUri == null -> Toast.makeText(this, "select an profile image", Toast.LENGTH_SHORT)
                .show()
            edit_text_full_name_setting.text.toString() == "" -> {
                Toast.makeText(this, "full name cant be empty", Toast.LENGTH_SHORT).show()
            }
            edit_text_username_setting.text.toString() == "" -> {
                Toast.makeText(this, "username cant be empty", Toast.LENGTH_SHORT).show()
            }
            else -> {
                mainViewModel.updateUserProfileInfoWithImage(edit_text_full_name_setting.text.toString().toLowerCase(
                    Locale.getDefault()
                ), edit_text_username_setting.text.toString().toLowerCase(
                    Locale.getDefault()
                ), edit_text_bio_setting.text.toString().toLowerCase(
                    Locale.getDefault()
                ),imageUri!!)
            }

        }
    }

    private fun updateUserInfoNoImage() {
        when {
            edit_text_full_name_setting.text.toString() == "" -> {
                Toast.makeText(this, "full name cant be empty", Toast.LENGTH_SHORT).show()
            }
            edit_text_username_setting.text.toString() == "" -> {
                Toast.makeText(this, "username cant be empty", Toast.LENGTH_SHORT).show()
            }
            else -> {

                mainViewModel.updateUserProfileInfo(
                    edit_text_full_name_setting.text.toString().toLowerCase(
                        Locale.getDefault()
                    ), edit_text_username_setting.text.toString().toLowerCase(
                        Locale.getDefault()
                    ), edit_text_bio_setting.text.toString().toLowerCase(
                        Locale.getDefault()
                    )
                )
            }
        }


    }


}