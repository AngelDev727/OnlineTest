package com.example.onlineTest

import com.google.gson.annotations.SerializedName

data class UserModel(
    @SerializedName("avatar_url") val avatarUrl: String = "",
    @SerializedName("login") val userName: String = "",
    @SerializedName("type") val type: String = ""
)