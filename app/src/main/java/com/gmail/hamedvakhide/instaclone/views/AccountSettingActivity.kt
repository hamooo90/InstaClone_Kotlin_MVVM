package com.gmail.hamedvakhide.instaclone.views

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.gmail.hamedvakhide.instaclone.R
import com.gmail.hamedvakhide.instaclone.utils.KeyboardUtil
import com.gmail.hamedvakhide.instaclone.viewmodel.MainViewModel
import com.google.android.material.snackbar.Snackbar
//import com.gmail.hamedvakhide.instaclone.viewmodel.MainViewModel
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_account_setting.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.util.*

@AndroidEntryPoint
class AccountSettingActivity : AppCompatActivity() {
    private val viewModel : MainViewModel by viewModels()
    private var imageChanged = false
    private var imageUri: Uri? = null

    @ExperimentalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_setting)

        //////observe for logout
        viewModel.stateMutableLiveData.observe(this, androidx.lifecycle.Observer {
            if (it == "Signed out") {
                val intent = Intent(this, SignInActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        })

        //////////observe and add user info in fields
        viewModel.userInfoMutableLiveData.observe(this, {
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
        viewModel.profileUpdateDoneMutableLiveData.observe(this, {
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

        btn_close_setting.setOnClickListener {
            finish()
        }

        btn_delete_account_setting.setOnClickListener {
            Snackbar.make(root_account_setting,"Not Implemented",Snackbar.LENGTH_SHORT)
//                .setBackgroundTint(Color.RED)
                .show()
        }
//
        btn_logout_setting.setOnClickListener {
            viewModel.logOut()
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

        val userId = viewModel.getUserId()
        viewModel.getUserInfo(userId)

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
                viewModel.updateUserProfileInfoWithImage(edit_text_full_name_setting.text.toString().toLowerCase(
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

                viewModel.updateUserProfileInfo(
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