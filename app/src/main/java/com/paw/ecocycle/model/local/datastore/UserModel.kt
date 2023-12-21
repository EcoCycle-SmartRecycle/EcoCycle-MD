package com.paw.ecocycle.model.local.datastore

data class UserModel(
    val name: String,
    val email: String,
    val token: String,
    val isLogin: Boolean = false
)