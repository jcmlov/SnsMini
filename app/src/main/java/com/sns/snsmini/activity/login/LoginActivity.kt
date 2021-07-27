package com.sns.snsmini.activity.login

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.sns.snsmini.MainActivity
import com.sns.snsmini.R
import com.sns.snsmini.activity.profile.PassWordSetActivity
import com.sns.snsmini.activity.signUp.SignUpActivity
import kotlinx.android.synthetic.main.actiity_login.*
import kotlinx.android.synthetic.main.actiity_login.et_id
import kotlinx.android.synthetic.main.actiity_login.et_pass

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    val builder by lazy {
        AlertDialog.Builder(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.actiity_login)

        // Initialize Firebase Auth
        auth = Firebase.auth

        initView()
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if(currentUser != null){
            reload();
        }
    }

    private fun initView() {

        rl_login_container.setOnClickListener {
            if(validationCheck()) {
                builder.setMessage(getString(R.string.confirm_login))
                    .setPositiveButton(getString(R.string.login),
                        DialogInterface.OnClickListener { dialogInterface, i ->
                            loginAccount(et_id.text.toString(), et_pass.text.toString())
                        })
                    .setNegativeButton(R.string.cancel,
                        DialogInterface.OnClickListener { dialogInterface, i ->
                        })
                    .create().show()
            }
        }

        btn_sign_up.setOnClickListener {
            moveSignUp()
        }

        btn_reset_pw.setOnClickListener {
            moveResetPw()
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

        if("" == et_pass.text.toString()) {
            Toast.makeText(this, getString(R.string.input_pass), Toast.LENGTH_SHORT).show()
            et_pass.requestFocus();
            var imm : InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
            result = false
            return result
        }

        return result
    }

    private fun reload() {

    }

    private fun updateUI(user: FirebaseUser?) {
        if(user != null) {
            Toast.makeText(this, getString(R.string.authentication_success), Toast.LENGTH_SHORT).show()
            moveMain()
        } else {
            Toast.makeText(baseContext, getString(R.string.authentication_fail), Toast.LENGTH_SHORT).show()
        }
    }

    private fun loginAccount(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    updateUI(null)
                }
            }

    }

    private fun moveSignUp() {
        val intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)
    }

    private fun moveMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
        startActivity(intent)
        finish()
    }

    private fun moveResetPw() {
        val intent = Intent(this, PassWordSetActivity::class.java)
        startActivity(intent)
    }

}