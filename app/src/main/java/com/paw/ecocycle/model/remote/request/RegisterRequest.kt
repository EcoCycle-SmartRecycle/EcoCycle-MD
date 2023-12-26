package com.paw.ecocycle.model.remote.request

import com.google.gson.annotations.SerializedName

data class RegisterRequest(

    @field:SerializedName("password")
    val password: String,

    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("email")
    val email: String
)
