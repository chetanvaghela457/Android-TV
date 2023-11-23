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

package com.strimm.application.ui.interfaces

import com.strimm.application.lib.entity.ProgramGuideSchedule
import com.strimm.application.model.CategoriesItem
import com.strimm.application.model.Video
import com.strimm.application.model.VideoItem


interface OnCategoryItemClick {
    fun categoryItemClick(item: CategoriesItem, position: Int)
}


interface OnChannelsItemClick {
    fun channelsItemClick(
        item: ProgramGuideSchedule<VideoItem>,
        position: Int
    )
}


interface OnSearchItemClick {
    fun videoItemClick(item: Video, position: Int)
}