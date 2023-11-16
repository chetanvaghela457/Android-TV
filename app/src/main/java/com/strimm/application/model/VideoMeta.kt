package com.strimm.application.model

data class VideoMeta(
    val current_page: Int,
    val from: Int,
    val last_page: Int,
    val links: List<Link>,
    val path: String,
    val per_page: String,
    val to: Int,
    val total: Int
)