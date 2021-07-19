package com.sns.snsmini.activity.post

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.sns.snsmini.R

class PostRegistActivity : AppCompatActivity() {

    val builder by lazy {
        AlertDialog.Builder(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.actiity_post_regist)
    }
}