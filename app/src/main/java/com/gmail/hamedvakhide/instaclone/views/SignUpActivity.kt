package com.gmail.hamedvakhide.instaclone.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.gmail.hamedvakhide.instaclone.R
import com.gmail.hamedvakhide.instaclone.utils.KeyboardUtil
import com.gmail.hamedvakhide.instaclone.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_sign_up.*

@AndroidEntryPoint
class SignUpActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        ////// show loader when trying to sign up
        viewModel.stateMutableLiveData.observe(this, androidx.lifecycle.Observer {
            when(it){
                "loading" -> {
                    progress_bar_sign_up.visibility = View.VISIBLE
                }
                "done" -> {
                    progress_bar_sign_up.visibility = View.GONE
                    Toast.makeText(this,"done", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }
                else -> {
                    progress_bar_sign_up.visibility = View.GONE
                    Toast.makeText(this,it, Toast.LENGTH_SHORT).show()
                }
            }
        })
        //////////

        btn_signin_sign_up.setOnClickListener {
            finish()
        }

        btn_register_sign_up.setOnClickListener {
            KeyboardUtil().hideKeyboard(this)

            val name = edit_text_full_name_sign_up.text.trim().toString()
            val userName = edit_text_user_name_sign_up.text.trim().toString()
            val email = edit_text_email_sign_up.text.trim().toString()
            val password = edit_text_password_sign_up.text.trim().toString()

            //// form validation
            if(TextUtils.isEmpty(password)){
                edit_text_password_sign_up.error="Enter a password"
            }
            if(TextUtils.isEmpty(name)){
                edit_text_full_name_sign_up.error="Enter your name"
            }
            if(TextUtils.isEmpty(userName)){
                edit_text_user_name_sign_up.error="Enter a username"
            }
            when {
                TextUtils.isEmpty(email) -> {
                    edit_text_email_sign_up.error="Enter email address!"
                }
                !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    edit_text_email_sign_up.error="Email is badly formatted!"
                }
                TextUtils.isEmpty(password) -> {
                }
                TextUtils.isEmpty(name) -> {
                }
                TextUtils.isEmpty(userName) -> {
                }
                else -> {
                    viewModel.register(name,userName,email, password)
                }
            }

        }
    }


}