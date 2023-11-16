package com.strimm.application.model

import com.strimm.application.lib.entity.ProgramGuideChannel

data class ChannelItem(
    override val averageMark: Int,
    override val categoryId: String,
    override val channelPosition: String,
    override val customLabel: String,
    override val customLogo: Any,
    override val customPlayerControlsEnabled: String,
    override val description: String,
    override val guideMode: String,
    override val hasMatureContent: String,
    override val id: String,
    override val isCustomBrandingEnabled: Boolean,
    override val isLocked: String,
    override val isLogoModeActive: String,
    override val isPrivate: Boolean,
    override val isVerified: Boolean,
    override val isWhiteLabeled: String,
    override val keepGuideOpened: String,
    override val liveAvailable: Boolean,
    override val marked: Boolean,
    override val matureContentEnabled: String,
    override val name: String,
    override val pagesCount: Int,
    override val pictureUrl: String,
    override val placeHolderImage: Any,
    override val playLiveFirst: Boolean,
    override val privateChannel: Boolean,
    override val showPlaceHolderImage: Boolean,
    override val subscriberCount: String,
    override val url: String,
    override val userId: String
): ProgramGuideChannel