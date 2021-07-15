package com.gmail.hamedvakhide.instaclone.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.gmail.hamedvakhide.instaclone.R
import com.gmail.hamedvakhide.instaclone.utils.KeyboardUtil
import com.gmail.hamedvakhide.instaclone.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignInActivity : AppCompatActivity() {
    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        ////////
        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        ///////////if user is signed at app start go to main activity/////////////
        if(mainViewModel.isUserLoggedIn()){
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            overridePendingTransition(0,0)
            finish()
        }

        //////observe if user signed in
        mainViewModel.getUserMutableLiveData().observe(this, androidx.lifecycle.Observer {
            if(it != null){
//                Toast.makeText(this, it.email.toString(), Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        })

        ////// show loader when trying to sign in
        mainViewModel.getLoadingMutableLiveData().observe(this, Observer {
            if(it != null){
                if(it){
                    progress_bar_sign_in.visibility = View.VISIBLE
                } else{
                    progress_bar_sign_in.visibility = View.GONE
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
                    mainViewModel.login(email , password)
                }
            }

        }

    }

}