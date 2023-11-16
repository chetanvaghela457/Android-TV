package com.strimm.application.model

data class VideoData(
    val data: List<VideoItem>,
    val links: Links,
    val meta: VideoMeta
)