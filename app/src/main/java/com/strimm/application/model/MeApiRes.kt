package com.strimm.application.model

data class MeApiRes(
    val channels: Channels,
    val email: String,
    val full_name: String,
    val id: Int,
    val publicUrl: String,
    val public_name: String
)