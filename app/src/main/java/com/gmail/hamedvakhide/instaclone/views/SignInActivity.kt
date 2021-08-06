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
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_sign_in.*

@AndroidEntryPoint
class SignInActivity : AppCompatActivity() {
    private val viewModel : MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        ///////////if user is signed at app start go to main activity/////////////
        if(FirebaseAuth.getInstance().currentUser != null){
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            overridePendingTransition(0,0)
            finish()
        }

        //////observe if user signed in
        viewModel.stateMutableLiveData.observe(this, {
            when (it) {
                "loading" -> {
                    progress_bar_sign_in.visibility = View.VISIBLE
                }
                "done" -> {
                    progress_bar_sign_in.visibility = View.GONE
                    Toast.makeText(this,"done",Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }
                else -> {
                    progress_bar_sign_in.visibility = View.GONE
                    Toast.makeText(this,it,Toast.LENGTH_SHORT).show()
                }

            }

        })

        btn_new_account_sign_in.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        btn_login_sign_in.setOnClickListener {
            KeyboardUtil().hideKeyboard(this)

            val email = edit_text_email_address_sign_in.text.trim().toString()
            val password = edit_text_password_sign_in.text.toString()


            // form validator
            if(TextUtils.isEmpty(password)){
                edit_text_password_sign_in.error="Enter a password"
            }
            when {
                TextUtils.isEmpty(email) -> {
                    edit_text_email_address_sign_in.error="Enter email address!"
                }
                !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    edit_text_email_address_sign_in.error="Email is badly formatted!"
                }
                TextUtils.isEmpty(password) -> {
                }
                else -> {
                    viewModel.login(email , password)
                }
            }
        }
    }

}