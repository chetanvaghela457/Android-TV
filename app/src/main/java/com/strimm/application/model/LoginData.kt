package com.strimm.application.model

data class LoginData(
    val access_token: String,
    val expires_in: Int,
    val token_type: String
)