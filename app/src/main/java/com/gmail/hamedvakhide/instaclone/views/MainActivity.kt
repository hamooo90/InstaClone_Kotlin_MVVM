package com.gmail.hamedvakhide.instaclone.views

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.gmail.hamedvakhide.instaclone.R
import com.gmail.hamedvakhide.instaclone.views.fragments.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    /// bottom navigation select action
    private val onNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    showFragment(HomeFragment())
                    return@OnNavigationItemSelectedListener true
                }
                R.id.nav_search -> {
                    showFragment(SearchFragment())
                    return@OnNavigationItemSelectedListener true
                }
                R.id.nav_add -> {
                    /////////// start AddNewPostActivity
                    val intent = Intent(this, AddNewPostActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_notification -> {
                    showFragment(NotificationFragment())
                    return@OnNavigationItemSelectedListener true
                }
                R.id.nav_profile -> {
                    showFragment(ProfileFragment())
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }

    //// handle backPress on different fragments
    override fun onBackPressed() {
        val currentFragment = this.supportFragmentManager.findFragmentById(R.id.fragment_container)
        when (currentFragment) {
            is PostDetailFragment -> {
                var userId = ""
                val pref = this.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
                if (pref != null) {
                    userId = pref.getString("userId", "").toString()
                }

                val prefsEdit = pref.edit()
                prefsEdit.putString("profileId",userId)
                prefsEdit.apply()
                showFragment(ProfileFragment())
            }
            !is HomeFragment -> {
                showFragment(HomeFragment())
                nav_bar_view.selectedItemId = R.id.nav_home
            }
            else -> {
                super.onBackPressed()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        nav_bar_view.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
        showFragment(HomeFragment())
    }

    private fun showFragment(fragment: Fragment) {
        val fragmentTransX = supportFragmentManager.beginTransaction();
        fragmentTransX.replace(
            R.id.fragment_container,
            fragment
        ).commit()
    }
}