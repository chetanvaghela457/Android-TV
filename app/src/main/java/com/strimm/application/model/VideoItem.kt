package com.strimm.application.model

data class VideoItem(
    val channelId: String,
    val createdDate: String,
    val description: String,
    val duration: String,
    val durationInDate: String,
    val endDate: String,
    val id: String,
    val isInPublicLibrary: String,
    val isInWatchLater: Boolean,
    val isPrivate: Boolean,
    val isRemovedByProvider: String,
    val isRestrictedByProvider: String,
    val privateVideoModeEnabled: String,
    val providerName: String,
    val providerVideoId: String,
    val startDate: String,
    val thumbnail: Any,
    val timeDuration: String,
    val title: String,
    val userId: String,
    val videoStatus: Any,
    val videoStatusMessage: Any,
    val videoTubeId: String
)