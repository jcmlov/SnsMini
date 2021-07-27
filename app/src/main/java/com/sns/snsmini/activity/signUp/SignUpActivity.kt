package com.sns.snsmini.activity.signUp

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Process
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.sns.snsmini.R
import com.sns.snsmini.activity.login.LoginActivity
import kotlinx.android.synthetic.main.actiity_sign_up.*
import kotlinx.android.synthetic.main.actiity_sign_up.et_id
import kotlinx.android.synthetic.main.actiity_sign_up.et_pass

class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    val builder by lazy {
        AlertDialog.Builder(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.actiity_sign_up)

        auth = Firebase.auth

        initView()
    }

    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if(currentUser != null){
            reload();
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        moveTaskToBack(true)
        Process.killProcess(Process.myPid())
        System.exit(1)
    }

    private fun updateUI(user: FirebaseUser?) {
        if(user != null) {
            Toast.makeText(this, getString(R.string.confirm_sgin_up_success), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(baseContext, getString(R.string.confirm_sgin_up_fail), Toast.LENGTH_SHORT).show()
        }
    }

    private fun reload() {

    }

    private fun initView() {
        rl_sign_up_container.setOnClickListener {
            if(validationCheck()) {
                builder.setMessage(getString(R.string.confirm_sgin_up))
                    .setPositiveButton(getString(R.string.sign_up),
                        DialogInterface.OnClickListener { dialogInterface, i ->
                            createAccount(et_id.text.toString(), et_pass.text.toString())
                        })
                    .setNegativeButton(R.string.cancel,
                        DialogInterface.OnClickListener { dialogInterface, i ->
                        })
                    .create().show()
            }
        }

        btn_login.setOnClickListener {
            moveLogin()
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

        if("" == et_pass_confirm.text.toString()) {
            Toast.makeText(this, getString(R.string.input_pass_confirm), Toast.LENGTH_SHORT).show()
            et_pass_confirm.requestFocus();
            var imm : InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
            result = false
            return result
        }

        if("" != et_pass.text.toString() && "" != et_pass_confirm.text.toString()) {
            if(et_pass.text.toString() != et_pass_confirm.text.toString()) {
                Toast.makeText(this, getString(R.string.not_equal_pass_confirm), Toast.LENGTH_SHORT).show()
                et_pass_confirm.setText("")
                et_pass_confirm.requestFocus();
                var imm : InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                result = false
                return result
            }
        }

        return result
    }

    private fun createAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    if(task.exception != null) {
                        Toast.makeText(baseContext, getString(R.string.fail_to_already_use_email), Toast.LENGTH_SHORT).show()
                    } else {
                        updateUI(null)
                    }
                }
            }
    }

    private fun moveLogin() {
        finish()
    }

}