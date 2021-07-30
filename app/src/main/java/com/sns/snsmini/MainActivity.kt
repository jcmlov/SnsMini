package com.sns.snsmini

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.sns.snsmini.activity.login.LoginActivity
import com.sns.snsmini.activity.post.PostRegistActivity
import com.sns.snsmini.activity.profile.ProfileActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()
    }

    private fun initView() {

        val user = Firebase.auth.currentUser
        if(user != null) {
            user?.let {
                for (profile in it.providerData) {
                    // Id of the provider (ex: google.com)
                    val providerId = profile.providerId

                    // UID specific to the provider
                    val uid = profile.uid

                    // Name, email address, and profile photo Url
                    val name = profile.displayName
                    val email = profile.email
                    val photoUrl = profile.photoUrl
                }
            }
        } else {
            moveLogin()
        }

        btn_add_post.setOnClickListener {
            moveAddPost()
        }

        profile_btn.setOnClickListener {
            moveProfile()
        }

        logout_btn.setOnClickListener {
            Firebase.auth.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun moveLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun moveAddPost() {
        val intent = Intent(this, PostRegistActivity::class.java)
        startActivity(intent)
    }

    private fun moveProfile() {
        val intent = Intent(this, ProfileActivity::class.java)
        startActivity(intent)
    }

}