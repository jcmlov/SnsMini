package com.sns.snsmini

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.sns.snsmini.activity.post.PostRegistActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()
    }

    private fun initView() {
        btn_add_post.setOnClickListener {
            moveAddPost()
        }
    }

    private fun moveAddPost() {
        val intent = Intent(this, PostRegistActivity::class.java)
        startActivity(intent)
    }

}