package com.strimm.application.model

data class SearchedData(
    val channels: List<Any>,
    val channels_count: Int,
    val videos: List<Video>,
    val videos_count: Int
)