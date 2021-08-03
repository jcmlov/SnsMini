package com.sns.snsmini.activity.post

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.sns.snsmini.R
import com.sns.snsmini.vo.post.Post
import kotlinx.android.synthetic.main.actiity_post_regist.*

class PostRegistActivity : AppCompatActivity() {

    val db = Firebase.firestore

    val builder by lazy {
        AlertDialog.Builder(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.actiity_post_regist)

        initView()
    }

    private fun initView() {

        rl_post_container.setOnClickListener {
            if(validationCheck()) {
                builder.setMessage(getString(R.string.confirm_save))
                        .setPositiveButton(getString(R.string.save),
                                DialogInterface.OnClickListener { dialogInterface, i ->
                                    registPost()
                                })
                        .setNegativeButton(R.string.cancel,
                                DialogInterface.OnClickListener { dialogInterface, i ->
                                })
                        .create().show()
            }
        }

    }

    private fun validationCheck(): Boolean {
        var result = true

        if("" == et_post_title.text.toString()) {
            Toast.makeText(this, getString(R.string.input_title), Toast.LENGTH_SHORT).show()
            et_post_title.requestFocus();
            var imm : InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

            result = false
            return result
        }

        if("" == et_post_content.text.toString()) {
            Toast.makeText(this, getString(R.string.input_content), Toast.LENGTH_SHORT).show()
            et_post_content.requestFocus();
            var imm : InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

            result = false
            return result
        }

        return result
    }

    private fun registPost() {
        val postTitle = et_post_title.text.toString()
        val postContent = et_post_content.text.toString()
        val showYn = true

        val user = Firebase.auth.currentUser
        val postInfo = Post(postTitle, postContent, showYn)
        if (user != null) {
            db.collection("post").document(user.uid).set(postInfo).addOnSuccessListener { documentReference ->
                Toast.makeText(this, getString(R.string.confirm_regist_post_success), Toast.LENGTH_SHORT).show()
                finish()
            }.addOnFailureListener { e ->
                Toast.makeText(this, getString(R.string.fail_regist_post), Toast.LENGTH_SHORT).show()
            }
        }
    }

}