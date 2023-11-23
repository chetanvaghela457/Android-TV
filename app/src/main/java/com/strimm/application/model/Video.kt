package com.strimm.application.model

data class Video(
    val CategoryId: String,
    val CategoryName: String,
    val ChannelScheduleId: String,
    val ChannelTubeId: String,
    val Description: String,
    val Duration: String,
    val EndDate: String,
    val IsInPublicLibrary: String,
    val IsPrivate: String,
    val IsRRated: String,
    val IsRemovedByProvider: String,
    val IsRestrictedByProvider: String,
    val PlaybackEndTime: String,
    val PlaybackOrderNumber: String,
    val PlaybackStartTime: String,
    val ProviderVideoId: String,
    val StartDate: String,
    val Thumbnail: String,
    val Title: String,
    val VideoKey: Any,
    val VideoProviderId: String,
    val VideoProviderName: String,
    val VideoTubeId: String,
    val VideoTubeTitle: String,
    val providerName: String
)