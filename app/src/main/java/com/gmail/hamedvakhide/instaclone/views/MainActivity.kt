package com.gmail.hamedvakhide.instaclone.views

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.gmail.hamedvakhide.instaclone.R
import com.gmail.hamedvakhide.instaclone.viewmodel.MainViewModel
import com.gmail.hamedvakhide.instaclone.views.fragments.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

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
                ////////////////
                val backUid = viewModel.readBackUserIdFromPref()
                viewModel.saveProfileIdToPref(backUid)
                ////////////////

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