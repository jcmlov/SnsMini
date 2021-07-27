package com.sns.snsmini.activity.profile

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.sns.snsmini.R
import kotlinx.android.synthetic.main.activity_pass_word_set.*

class PassWordSetActivity : AppCompatActivity() {

    val builder by lazy {
        AlertDialog.Builder(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pass_word_set)

        initView()
    }

    private fun initView() {

        rl_reset_pw_container.setOnClickListener {
            if(validationCheck()) {
                builder.setMessage(getString(R.string.confirm_send_mail))
                    .setPositiveButton(getString(R.string.send),
                        DialogInterface.OnClickListener { dialogInterface, i ->
                            send()
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

        if("" == et_id.text.toString()) {
            Toast.makeText(this, getString(R.string.input_email), Toast.LENGTH_SHORT).show()
            et_id.requestFocus();
            var imm : InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

            result = false
            return result
        }

        return result
    }

    private fun send() {
        val emailAddress = et_id.text.toString()
        Firebase.auth.sendPasswordResetEmail(emailAddress)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, getString(R.string.send_mail_success), Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, getString(R.string.send_mail_fail), Toast.LENGTH_SHORT).show()
                }
            }
    }

}