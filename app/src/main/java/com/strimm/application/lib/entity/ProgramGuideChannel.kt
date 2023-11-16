/*
 * Copyright (c) 2020, Egeniq
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.strimm.application.lib.entity

/**
 * A channel which may be associated with multiple programmes.
 * Channels are displayed on the left side of the screen, and display the image you have defined in the URL,
 * and the name to the right of the image. ID is only used for identification purposes, and should be unique.
 */
interface ProgramGuideChannel {
    val averageMark: Int
    val categoryId: String
    val channelPosition: String
    val customLabel: String
    val customLogo: Any
    val customPlayerControlsEnabled: String
    val description: String
    val guideMode: String
    val hasMatureContent: String
    val id: String
    val isCustomBrandingEnabled: Boolean
    val isLocked: String
    val isLogoModeActive: String
    val isPrivate: Boolean
    val isVerified: Boolean
    val isWhiteLabeled: String
    val keepGuideOpened: String
    val liveAvailable: Boolean
    val marked: Boolean
    val matureContentEnabled: String
    val name: String
    val pagesCount: Int
    val pictureUrl: String
    val placeHolderImage: Any
    val playLiveFirst: Boolean
    val privateChannel: Boolean
    val showPlaceHolderImage: Boolean
    val subscriberCount: String
    val url: String
    val userId: String
}