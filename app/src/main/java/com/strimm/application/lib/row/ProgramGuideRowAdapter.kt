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

package com.strimm.application.lib.row

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.strimm.application.R
import com.strimm.application.lib.ProgramGuideHolder
import com.strimm.application.lib.ProgramGuideListAdapter
import com.strimm.application.lib.ProgramGuideManager
import com.strimm.application.lib.entity.ProgramGuideChannel
import com.strimm.application.lib.entity.ProgramGuideSchedule
import com.strimm.application.model.VideoItem
import com.strimm.application.ui.interfaces.OnChannelsItemClick
import com.strimm.application.ui.viewmodel.MainViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

/**
 * Adapts the [ProgramGuideListAdapter] list to the body of the program guide table.
 */
internal class ProgramGuideRowAdapter(
    private var context: Context,
    private val programGuideHolder: ProgramGuideHolder<*>,
    private val onChannelsItemClick: OnChannelsItemClick,
    private val mainViewModel: MainViewModel
) :
    RecyclerView.Adapter<ProgramGuideRowAdapter.ProgramRowViewHolder>(),
    ProgramGuideManager.Listener {
    private val programManager: ProgramGuideManager<*> = programGuideHolder.programGuideManager
    private val programListAdapters = ArrayList<ProgramGuideListAdapter<*>>()
    private val recycledViewPool: RecyclerView.RecycledViewPool =
        RecyclerView.RecycledViewPool().also {
            it.setMaxRecycledViews(
                R.layout.programguide_item_row,
                context.resources.getInteger(R.integer.max_recycled_view_pool_table_item)
            )
        }


    companion object {
        private val TAG: String = ProgramGuideRowAdapter::class.java.name
        private var selectedPosition = RecyclerView.NO_POSITION
    }

    init {
        update()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun update() {
        programListAdapters.clear()
        val channelCount = programManager.channelCount
        for (i in 0 until channelCount) {
            val listAdapter = ProgramGuideListAdapter(context.resources, programGuideHolder, i)
            programListAdapters.add(listAdapter)
        }
        Log.i(TAG, "Updating program guide with $channelCount channels.")
        notifyDataSetChanged()
    }

    fun updateProgram(program: ProgramGuideSchedule<*>): Int? {
        // Find the match in the row adapters
        programListAdapters.forEachIndexed { index, adapter ->
            if (adapter.updateProgram(program)) {
                return index
            }
        }
        return null
    }

    override fun getItemCount(): Int {
        return programListAdapters.size
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.programguide_item_row
    }

    override fun onBindViewHolder(holder: ProgramRowViewHolder, position: Int) {
        holder.onBind(position, programManager, programListAdapters, programGuideHolder)

        holder.channelContainer.setOnClickListener {

//            if (programGuideHolder.programGuideManager.getCurrentProgram(
//                    programManager.getChannel(
//                        position
//                    )?.id
//                ) != null
//            ) {

            onChannelsItemClick.channelsItemClick(
                programManager.getScheduleForChannelIdAndIndex(
                    programManager.getChannel(
                        position
                    )?.id.toString(), programManager.channelEntriesMap[programManager.getChannel(
                        position
                    )?.id.toString()]!!.size - 1
                ) as ProgramGuideSchedule<VideoItem>,
                position
            )

//            }

            changeBackground(position)
        }

        for (data in mainViewModel.getFavouriteData()) {

            if (data == programGuideHolder.programGuideManager.getChannel(position)!!.id) {

                holder.channelLogoView.setBackgroundResource(R.drawable.programguide_star)

            }

        }


    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProgramRowViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        val gridView = itemView.findViewById<ProgramGuideRowGridView>(R.id.row)
        gridView.setRecycledViewPool(recycledViewPool)
        return ProgramRowViewHolder(itemView)
    }

    override fun onTimeRangeUpdated() {
        // Do nothing
    }

    override fun onSchedulesUpdated() {
        // Do nothing
    }

    internal class ProgramRowViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val container: ViewGroup = itemView as ViewGroup
        private val rowGridView: ProgramGuideRowGridView = container.findViewById(R.id.row)
        val channelContainer =
            container.findViewById<ViewGroup>(R.id.programguide_channel_container)
        private val channelNameView: TextView =
            container.findViewById(R.id.programguide_channel_name)
        val channelLogoView: ImageView = container.findViewById(R.id.programguide_channel_logo)

        init {

            channelContainer.viewTreeObserver.addOnGlobalFocusChangeListener { _, _ ->
                channelContainer.isActivated = rowGridView.hasFocus()
                channelContainer.isFocusable = true

            }
        }

        fun onBind(
            position: Int,
            programManager: ProgramGuideManager<*>,
            programListAdapters: List<RecyclerView.Adapter<*>>,
            programGuideHolder: ProgramGuideHolder<*>
        ) {
            onBindChannel(programManager.getChannel(position))
            rowGridView.swapAdapter(programListAdapters[position], true)

            if (selectedPosition == position) {
                channelContainer.setBackgroundResource(R.drawable.bottom_bg)
            } else {
                channelContainer.setBackgroundResource(R.drawable.channel_bg)
            }

            rowGridView.setProgramGuideFragment(programGuideHolder)
            rowGridView.setChannel(programManager.getChannel(position)!!)
            rowGridView.resetScroll(programGuideHolder.getTimelineRowScrollOffset())


        }

        private fun onBindChannel(channel: ProgramGuideChannel?) {
            if (channel == null) {
                channelNameView.visibility = View.GONE
//                channelLogoView.visibility = View.GONE
                return
            }

            channelNameView.text = channel.name
            channelNameView.visibility = View.VISIBLE
        }

        internal fun updateLayout() {
            rowGridView.post {
                rowGridView.updateChildVisibleArea()
//                channelContainer.setBackgroundColor(context.resources.getColor(R.color.skyblue))

            }
        }


    }

    fun changeBackground(position: Int) {

        selectedPosition = position
        notifyDataSetChanged()

    }
}