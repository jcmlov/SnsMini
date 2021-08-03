package com.sns.snsmini.activity.profile

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.telephony.PhoneNumberFormattingTextWatcher
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import com.google.firebase.storage.ktx.storageMetadata
import com.sns.snsmini.R
import com.sns.snsmini.common.Constants
import com.sns.snsmini.utils.AppHelperUtil
import com.sns.snsmini.utils.StringUtils
import com.sns.snsmini.vo.user.User
import kotlinx.android.synthetic.main.activity_profile.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class ProfileActivity : AppCompatActivity() {

    private val TAG: String = "ProfileActivity"

    val db = Firebase.firestore
    val storage = Firebase.storage
    var storageRef = storage.reference

    private var saved: Boolean = false

    private var mCurrentPhotoPath: String = ""
    private var mCurrentUri: Uri? = null
    private var profileImgPath: String = ""
    private var profileImgUri: Uri? = null

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
        }

        val docRef = db.collection("user").document(user!!.uid)
        docRef.get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val data : Map<String, Object> = document.data as Map<String, Object>

                        val userName = data.get("userName").toString()
                        val birthDay = data.get("birthDay").toString()
                        val celPhone = data.get("celPhone").toString()
                        val address = data.get("address").toString()
                        val addressDetail = data.get("addressDetail").toString()

                        et_user_name.setText(userName)
                        et_birth_day.setText(birthDay)
                        et_celphone.setText(celPhone)
                        et_address.setText(address)
                        et_address_detail.setText(addressDetail)
                    } else {
                        Toast.makeText(this, getString(R.string.fail_load_data_nothing), Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, getString(R.string.fail_load_data), Toast.LENGTH_SHORT).show()
                }

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

        rl_update_picture_container.setOnClickListener {
            dispatchTakePictureIntent()
        }

        rl_update_profile_container.setOnClickListener {
            if(validationCheck()) {
                builder.setMessage(getString(R.string.confirm_save))
                        .setPositiveButton(getString(R.string.save),
                                DialogInterface.OnClickListener { dialogInterface, i ->
                                    updateProfile()
                                })
                        .setNegativeButton(R.string.cancel,
                                DialogInterface.OnClickListener { dialogInterface, i ->
                                })
                        .create().show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Constants.REQUEST_TAKE_PHOTO) {
            if (resultCode == RESULT_OK) {
                val bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath)
                var exif: ExifInterface? = null
                try {
                    exif = ExifInterface(mCurrentPhotoPath)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                val exifOrientation: Int
                val exifDegree: Int
                if (exif != null) {
                    exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
                    exifDegree = AppHelperUtil.exifOrientationToDegrees(exifOrientation)
                    profileImgPath = mCurrentPhotoPath
                    profileImgUri = mCurrentUri

                    uploadProfilePicture(profileImgPath, profileImgUri)
                } else {
                    exifDegree = 0
                }
                // visitorProfileImg.setImageBitmap(AppHelperUtil.rotate(bitmap, exifDegree))
            }
        }
    }

    private fun validationCheck(): Boolean {
        var result = true

        if("" == et_user_name.text.toString()) {
            Toast.makeText(this, getString(R.string.input_name), Toast.LENGTH_SHORT).show()
            et_user_name.requestFocus();
            var imm : InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

            result = false
            return result
        }

        if("" == et_birth_day.text.toString()) {
            Toast.makeText(this, getString(R.string.input_birth_day), Toast.LENGTH_SHORT).show()
            et_birth_day.requestFocus();
            var imm : InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

            result = false
            return result
        }

        if("" == et_celphone.text.toString()) {
            Toast.makeText(this, getString(R.string.input_phone), Toast.LENGTH_SHORT).show()
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

    private fun updateProfile() {
        val userName = et_user_name.text.toString()
        val birthDay = et_birth_day.text.toString()
        val celPhone = et_celphone.text.toString()
        val address = et_address.text.toString()
        val addressDetail = et_address_detail.text.toString()

        val user = Firebase.auth.currentUser
        val userInfo = User(userName, birthDay, celPhone, address, addressDetail)
        if (user != null) {
            db.collection("user").document(user.uid).set(userInfo).addOnSuccessListener { documentReference ->
                Toast.makeText(this, getString(R.string.confirm_update_profile_success), Toast.LENGTH_SHORT).show()
            }.addOnFailureListener { e ->
                Toast.makeText(this, getString(R.string.fail_update_profile), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uploadProfilePicture(profileImgPath: String, profileImgUri: Uri?) {

        val user = Firebase.auth.currentUser
        // File or Blob
        var file = Uri.fromFile(File(profileImgPath))
        var sessionUri: Uri? = null

        var metadata = storageMetadata {
            contentType = "image/jpeg"
        }
        val uploadTask = storageRef.child("images/profile/${user!!.uid}/${file.lastPathSegment}").putFile(file, metadata)
        uploadTask.addOnProgressListener {
            val progress = (100.0 * it.bytesTransferred) / it.totalByteCount

        }.addOnProgressListener { taskSnapshot ->
            sessionUri = taskSnapshot.uploadSessionUri
            if (sessionUri != null && !saved) {
                saved = true
                // A persisted session has begun with the server.
                // Save this to persistent storage in case the process dies.
            }
        }.addOnFailureListener {
            Toast.makeText(this, getString(R.string.fail_upload_picture), Toast.LENGTH_SHORT).show()
        }.addOnSuccessListener {
            Toast.makeText(this, getString(R.string.confirm_upload_picture_success), Toast.LENGTH_SHORT).show()
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File? {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",  /* suffix */
                storageDir /* directory */
        )
        mCurrentPhotoPath = image.absolutePath
        return image
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            // Create the File where the photo should go
            var photoFile: File? = null
            try {
                photoFile = createImageFile()
            } catch (ex: IOException) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                mCurrentUri = FileProvider.getUriForFile(this,
                        "com.sns.snsmini",
                        photoFile)
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCurrentUri)
                startActivityForResult(takePictureIntent, Constants.REQUEST_TAKE_PHOTO)
            }
        }
    }

}