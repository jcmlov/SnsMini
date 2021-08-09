package com.sns.snsmini.vo.user

import android.net.Uri

data class User(
    val userName: String? = "",
    val birthDay: String? = "",
    val celPhone: String? = "",
    val address: String? = "",
    val addressDetail: String? = "",
    val profileUri: String? = ""
)
