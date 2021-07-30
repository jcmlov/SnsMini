package com.sns.snsmini.activity.profile

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import android.telephony.PhoneNumberUtils
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.sns.snsmini.R
import com.sns.snsmini.utils.StringUtils
import kotlinx.android.synthetic.main.activity_profile.*
import java.util.*


class ProfileActivity : AppCompatActivity() {

    private val TAG: String = "ProfileActivity"

    val builder by lazy {
        AlertDialog.Builder(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        initView()
    }

    @SuppressLint("NewApi")
    private fun initView() {

        et_birth_day.showSoftInputOnFocus = false
        et_birth_day.isEnabled = true
        et_birth_day.isFocusable = false

        ll_birth_day.setOnClickListener {
            showCalendar()
        }
        et_birth_day.setOnClickListener {
            showCalendar()
        }

        et_celphone.addTextChangedListener(PhoneNumberFormattingTextWatcher())

        rl_update_profile_container.setOnClickListener {
            if(validationCheck()) {
                builder.setMessage(getString(R.string.confirm_save))
                        .setPositiveButton(getString(R.string.save),
                                DialogInterface.OnClickListener { dialogInterface, i ->
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

        if("" == et_celphone.text.toString()) {
            Toast.makeText(this, getString(R.string.input_email), Toast.LENGTH_SHORT).show()
            et_celphone.requestFocus();
            var imm : InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

            result = false
            return result
        } else {
            if(!StringUtils.isValidCellPhoneNumber(et_celphone.text.toString())) {
                Toast.makeText(this, getString(R.string.input_phone_not_match), Toast.LENGTH_SHORT).show()

                result = false
                return result
            }
        }

        return result
    }

    private fun showCalendar() {
        val c = Calendar.getInstance()
        val year = c[Calendar.YEAR]
        val month = c[Calendar.MONTH]
        val day = c[Calendar.DAY_OF_MONTH]

        DatePickerDialog(this, DatePickerDialog.OnDateSetListener { datePicker, year, month, day ->
            Log.i(TAG, "onDateSet year : $year")
            Log.i(TAG, "onDateSet month : $month")
            Log.i(TAG, "onDateSet day : $day")
            val dateString = String.format("%04d%02d%02d", year, month + 1, day)
            val conDateString = dateString.substring(0, 4) + "-" + dateString.substring(4, 6) + "-" + dateString.substring(6, 8)
            et_birth_day.setText(conDateString)

        }, year, month, day).show()
    }

    private fun updateProfile(email: String, password: String) {
    }

}