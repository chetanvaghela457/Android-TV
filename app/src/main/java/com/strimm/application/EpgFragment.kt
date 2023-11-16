package com.strimm.application

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.drm.DefaultDrmSessionManagerProvider
import com.google.android.exoplayer2.drm.DrmSessionManagerProvider
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.trackselection.TrackSelectionParameters
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.StyledPlayerControlView
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.upstream.HttpDataSource
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.loadOrCueVideo
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import com.pierfrancescosoffritti.androidyoutubeplayer.core.ui.DefaultPlayerUiController
import com.pierfrancescosoffritti.androidyoutubeplayer.core.ui.onFloatingButtonClick
import com.strimm.application.lib.ProgramGuideFragment
import com.strimm.application.lib.entity.ProgramGuideSchedule
import com.strimm.application.lib.util.FixedLocalDateTime
import com.strimm.application.model.CategoriesItem
import com.strimm.application.model.ChannelItem
import com.strimm.application.model.VideoItem
import com.strimm.application.utils.API_LOCAL_DATE_FORMAT
import com.strimm.application.utils.MySpannable
import com.strimm.application.utils.convertTimeFormat
import com.strimm.application.utils.format
import com.strimm.application.utils.mainThemeData
import com.strimm.application.utils.timeToMillis
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit


class EpgFragment : ProgramGuideFragment<VideoItem>(), onFloatingButtonClick {


    companion object {
        private val TAG = EpgFragment::class.java.name
    }

//    override val mainViewModel: MainViewModel by activityViewModels()

    lateinit var descriptionView: TextView
    lateinit var mediaDataSourceFactory: ExoPlayer.Builder

    lateinit var player: ExoPlayer
    lateinit var exoPlayerView: com.google.android.exoplayer2.ui.StyledPlayerView
    lateinit var progressBar: ProgressBar
    lateinit var youtubePlayerView: YouTubePlayerView
    lateinit var onFloatingButtonClick: onFloatingButtonClick
    lateinit var youTubePlayerMain: YouTubePlayer
//    lateinit var recyclerviewCategory: RecyclerView

    var isYoutubeInitialized = false
    var channelsArray = ArrayList<ChannelItem>()
    var videosItemsArray = ArrayList<VideoItem>()

    /* data class SimpleChannel(
         override val id: String,
         override val name: Spanned?,
         override val imageUrl: String?
     ) : ProgramGuideChannel*/

    // You can put your own data in the program class
    /*data class SimpleProgram(
        val id: String,
        val description: String,
        val metadata: String
    )*/

    override fun onScheduleClicked(programGuideSchedule: ProgramGuideSchedule<VideoItem>) {
        val innerSchedule = programGuideSchedule.program

        exoPlayerView = view?.findViewById<StyledPlayerView>(R.id.exoPlayerView)!!
        exoPlayerView = view?.findViewById<StyledPlayerView>(R.id.exoPlayerView)!!
        youtubePlayerView = view?.findViewById<YouTubePlayerView>(R.id.youtube_player_view)!!
        progressBar = view?.findViewById<ProgressBar>(R.id.progressBar)!!


        if (programGuideSchedule.program != null) {

            val durationTillNow =
                ((System.currentTimeMillis() / 1000).toLong() - (programGuideSchedule.program.startDate.timeToMillis(
                    programGuideSchedule.program.startDate
                ) / 1000).toLong())


            if (programGuideSchedule.program.providerName == "youtube") {


                Handler(Looper.getMainLooper()).postDelayed({

                    youtubePlayerView.visibility = View.VISIBLE
                    exoPlayerView.visibility = View.GONE
                    setupYoutubePlayer(
                        programGuideSchedule.program.providerVideoId,
                        durationTillNow.toFloat()
                    )

                }, 300)

            } else {
                youtubePlayerView.visibility = View.GONE
                exoPlayerView.visibility = View.VISIBLE
                setupExoPlayer(
                    programGuideSchedule.program.providerVideoId,
                    durationTillNow.toFloat()
                )
            }
        } else {

            youtubePlayerView.visibility = View.GONE
            exoPlayerView.visibility = View.GONE
            progressBar.visibility = View.GONE

        }
//        if (innerSchedule == null) {
//            // If this happens, then our data source gives partial info
//            Log.w(TAG, "Unable to open schedule!")
//            return
//        }
//        if (programGuideSchedule.isCurrentProgram) {
//            Toast.makeText(context, "Open live player", Toast.LENGTH_LONG).show()
//        } else {
//            Toast.makeText(context, "Open detail page", Toast.LENGTH_LONG).show()
//        }
        // Example of how a program can be updated. You could also change the underlying program.
        updateProgram(programGuideSchedule.copy(displayTitle = programGuideSchedule.displayTitle + " [clicked]"))
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onScheduleSelected(programGuideSchedule: ProgramGuideSchedule<VideoItem>?) {
        val titleView = view?.findViewById<TextView>(R.id.programguide_detail_title)
        titleView?.setTextColor(Color.parseColor(mainThemeData.primaryColor))
        titleView?.text = programGuideSchedule?.displayTitle
        val metadataView = view?.findViewById<TextView>(R.id.programguide_detail_metadata)
        metadataView?.setTextColor(Color.parseColor(mainThemeData.textSecondaryColor))
        metadataView?.text = programGuideSchedule?.program?.durationInDate
        descriptionView = view?.findViewById<TextView>(R.id.programguide_detail_description)!!
        descriptionView.text = programGuideSchedule?.program?.description
        descriptionView.setTextColor(Color.parseColor(mainThemeData.textSecondaryColor))

//        makeTextViewResizable(descriptionView, 5, "View More", true)

        exoPlayerView = view?.findViewById<StyledPlayerView>(R.id.exoPlayerView)!!
        exoPlayerView = view?.findViewById<StyledPlayerView>(R.id.exoPlayerView)!!
        youtubePlayerView = view?.findViewById<YouTubePlayerView>(R.id.youtube_player_view)!!
        progressBar = view?.findViewById<ProgressBar>(R.id.progressBar)!!


        if (programGuideSchedule?.program != null) {

            val durationTillNow =
                ((System.currentTimeMillis() / 1000).toLong() - (programGuideSchedule.program.startDate.timeToMillis(
                    programGuideSchedule.program.startDate
                ) / 1000).toLong())


            if (programGuideSchedule.program.providerName == "youtube") {


                Handler(Looper.getMainLooper()).postDelayed({

                    youtubePlayerView.visibility = View.VISIBLE
                    exoPlayerView.visibility = View.GONE
                    setupYoutubePlayer(
                        programGuideSchedule.program.providerVideoId,
                        durationTillNow.toFloat()
                    )

                }, 300)

            } else {
                youtubePlayerView.visibility = View.GONE
                exoPlayerView.visibility = View.VISIBLE
                setupExoPlayer(
                    programGuideSchedule.program.providerVideoId,
                    durationTillNow.toFloat()
                )
            }
        } else {

            youtubePlayerView.visibility = View.GONE
            exoPlayerView.visibility = View.GONE
            progressBar.visibility = View.GONE

        }
    }


    fun makeTextViewResizable(tv: TextView, maxLine: Int, expandText: String, viewMore: Boolean) {
        if (tv.tag == null) {
            tv.tag = tv.text
        }
        val vto = tv.viewTreeObserver
        vto.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val text: String
                val lineEndIndex: Int
                val obs = tv.viewTreeObserver
                obs.removeOnGlobalLayoutListener(this)
                if (maxLine == 0) {
                    lineEndIndex = tv.layout.getLineEnd(0)
                    text = tv.text.subSequence(0, lineEndIndex - expandText.length + 1)
                        .toString() + " " + expandText
                } else if (maxLine > 0 && tv.lineCount >= maxLine) {
                    lineEndIndex = tv.layout.getLineEnd(maxLine - 1)
                    text = tv.text.subSequence(0, lineEndIndex - expandText.length + 1)
                        .toString() + " " + expandText
                } else {
                    lineEndIndex = tv.layout.getLineEnd(tv.layout.lineCount - 1)
                    text = tv.text.subSequence(0, lineEndIndex).toString() + " " + expandText
                }
                tv.text = text
                tv.movementMethod = LinkMovementMethod.getInstance()
                tv.setText(
                    addClickablePartTextViewResizable(
                        SpannableString(tv.text.toString()), tv, lineEndIndex, expandText,
                        viewMore
                    ), TextView.BufferType.SPANNABLE
                )
            }
        })
    }

    private fun addClickablePartTextViewResizable(
        strSpanned: Spanned, tv: TextView,
        maxLine: Int, spanableText: String, viewMore: Boolean
    ): SpannableStringBuilder {
        val str = strSpanned.toString()
        val ssb = SpannableStringBuilder(strSpanned)
        if (str.contains(spanableText)) {
            ssb.setSpan(object : MySpannable(true) {
                override fun onClick(widget: View) {
                    tv.layoutParams = tv.layoutParams
                    tv.setText(tv.tag.toString(), TextView.BufferType.SPANNABLE)
                    tv.invalidate()
                    if (viewMore) {
                        makeTextViewResizable(tv, -1, "View Less", false)
                    } else {
                        makeTextViewResizable(tv, 5, "View More", true)
                    }
                }
            }, str.indexOf(spanableText), str.indexOf(spanableText) + spanableText.length, 0)
        }
        return ssb
    }

    private fun setupYoutubePlayer(videoId: String, durationStart: Float) {
        onFloatingButtonClick = this
        lifecycle.addObserver(youtubePlayerView)

        if (isYoutubeInitialized) {

            youTubePlayerMain.loadOrCueVideo(
                lifecycle,
                videoId,
                if (durationStart < 0f) 0f else durationStart
            )

//            youTubePlayerMain.seekTo(durationStart)

            progressBar.visibility = View.GONE

        } else {
            val listener: YouTubePlayerListener = object : AbstractYouTubePlayerListener() {
                override fun onReady(youTubePlayer: YouTubePlayer) {

                    progressBar.visibility = View.GONE
                    isYoutubeInitialized = true
                    youTubePlayerMain = youTubePlayer
                    youTubePlayer.addListener(object : YouTubePlayerListener {
                        override fun onReady(youTubePlayer: YouTubePlayer) {
                            //Dont
                        }

                        override fun onStateChange(
                            youTubePlayer: YouTubePlayer,
                            state: PlayerConstants.PlayerState
                        ) {
                            /*if (state.equals(PlayerConstants.PlayerState.ENDED)) {
                                //When video playing ended, seek back to 0 and then pause video
                                //Its purpose is to avoid displaying suggested related videos from youtube.
                                //Option one
                                youTubePlayer.cueVideo(
                                    videoId,
                                    durationStart
                                );

                                //Option two
                                youTubePlayer.seekTo(durationStart);
                                youTubePlayer.pause();

                            }*/
                        }

                        override fun onPlaybackQualityChange(
                            youTubePlayer: YouTubePlayer,
                            playbackQuality: PlayerConstants.PlaybackQuality
                        ) {

                        }

                        override fun onPlaybackRateChange(
                            youTubePlayer: YouTubePlayer,
                            playbackRate: PlayerConstants.PlaybackRate
                        ) {
                            //Dont
                        }

                        override fun onError(
                            youTubePlayer: YouTubePlayer,
                            error: PlayerConstants.PlayerError
                        ) {
                            //Dont
                        }

                        override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
                            //Dont
                        }

                        override fun onVideoDuration(
                            youTubePlayer: YouTubePlayer,
                            duration: Float
                        ) {
                            //Dont
                        }

                        override fun onVideoLoadedFraction(
                            youTubePlayer: YouTubePlayer,
                            loadedFraction: Float
                        ) {
                            //Dont
                        }

                        override fun onVideoId(youTubePlayer: YouTubePlayer, videoId: String) {
                            //Dont
                        }

                        override fun onApiChange(youTubePlayer: YouTubePlayer) {
                            //Dont
                        }

                    })

                    // using pre-made custom ui
                    val defaultPlayerUiController =
                        DefaultPlayerUiController(
                            youtubePlayerView,
                            youTubePlayer,
                            onFloatingButtonClick
                        )
                    youtubePlayerView.setCustomPlayerUi(defaultPlayerUiController.rootView)
//                setPlayNextVideoButtonClickListener(youTubePlayer)
                    youTubePlayer.loadOrCueVideo(
                        lifecycle,
                        videoId,
                        durationStart
                    )
//                    youTubePlayer.seekTo(durationStart)
                }
            }

            // disable web ui
            val options = IFramePlayerOptions.Builder().controls(0).build()
            youtubePlayerView.initialize(listener, options)
        }

    }

    override fun floatingButtonClick() {

    }

    override fun isTopMenuVisible(): Boolean {
        return false
    }

    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("CheckResult")
    override fun requestingProgramGuideFor(localDate: LocalDate) {
        // Faking an asynchronous loading here
        setState(State.Loading)

        mainViewModel.getChannelsList("").observeForever {

            channelsArray = it

            val cal = Calendar.getInstance()
            cal.timeInMillis = System.currentTimeMillis()
            val date = cal.time.format(API_LOCAL_DATE_FORMAT, Locale.ENGLISH)

//            val videosData =
//                mainViewModel.getLanguages(
//                    channelsArray[0].id,
//                    date
//                )

            val videosData =
                mainViewModel.getLanguages(
                    channelsArray.joinToString { it.id.toString() }.replace(" ", ""),
                    date
                )

            videosData.observeForever {

                videosItemsArray.addAll(it.data)

                Single.fromCallable {

                    val channelMap = mutableMapOf<String, List<ProgramGuideSchedule<VideoItem>>>()

                    GlobalScope.launch(Dispatchers.Main) {

                        channelsArray.forEach { channel ->
                            val scheduleList = mutableListOf<ProgramGuideSchedule<VideoItem>>()
                            for (videoItem in videosItemsArray) {

                                if (channel.id == videoItem.channelId) {

                                    val ldt: LocalDateTime = LocalDateTime.parse(
                                        convertTimeFormat(
                                            videoItem.startDate,
                                            "yyyy-MM-dd HH:mm:ss",
                                            "yyyy-MM-dd'T'HH:mm:ss"
                                        ),
                                    )
                                    val ldtEnd: LocalDateTime = LocalDateTime.parse(
                                        convertTimeFormat(
                                            videoItem.endDate,
                                            "yyyy-MM-dd HH:mm:ss",
                                            "yyyy-MM-dd'T'HH:mm:ss"
                                        ),
                                    )
                                    val instantStartDate: Instant =
                                        ldt.atZone(
                                            ZoneId.systemDefault()
                                        ).toInstant()
                                    val instantEndDate: Instant =
                                        ldtEnd.atZone(
                                            ZoneId.systemDefault()
                                        ).toInstant()

                                    val schedule = createSchedule(
                                        videoItem.id,
                                        videoItem.title,
                                        instantStartDate,
                                        instantEndDate,
                                        videoItem
                                    )
                                    scheduleList.add(schedule)

                                }

                            }

                            channelMap[channel.id] = scheduleList
                        }

                    }

                    return@fromCallable Pair(channelsArray, channelMap)

                }.delay(1, TimeUnit.SECONDS)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        setData(it.first, it.second, localDate)
                        if (it.first.isEmpty() || it.second.isEmpty()) {
                            setState(State.Error("No channels loaded."))
                        } else {
                            setState(State.Content)
                        }
                    }, {
                        Log.e(TAG, "Unable to load example data!", it)
                    })

            }


        }


    }

    private fun createSchedule(
        id: String,
        scheduleName: String,
        startTime: Instant,
        endTime: Instant,
        videoItem: VideoItem
    ): ProgramGuideSchedule<VideoItem> {
//        val id = Random.nextLong(100_000L)
        return ProgramGuideSchedule.createScheduleWithProgram(
            id,
            startTime,
            endTime,
            true,
            scheduleName,
            videoItem
        )
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun requestRefresh() {
        // You can refresh other data here as well.
        requestingProgramGuideFor(currentDate)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun categoryItemClick(item: CategoriesItem, position: Int) {

        setState(State.Loading)
        adapter.selectedItemPos(position)
        adapter.notifyDataSetChanged()
        channelsArray.clear()
        mainViewModel.getChannelsList(item.categoryId.toString()).observeForever {

            channelsArray = it

            val cal = Calendar.getInstance()
            cal.timeInMillis = System.currentTimeMillis()
            val date = cal.time.format(API_LOCAL_DATE_FORMAT, Locale.ENGLISH)

            val videosData =
                mainViewModel.getLanguages(
                    channelsArray.joinToString { it.id.toString() }.replace(" ", ""),
                    date
                )

            videosData.observeForever {

                videosItemsArray.addAll(it.data)

                Single.fromCallable {

                    val channelMap = mutableMapOf<String, List<ProgramGuideSchedule<VideoItem>>>()

                    GlobalScope.launch(Dispatchers.Main) {

                        channelsArray.forEach { channel ->
                            val scheduleList = mutableListOf<ProgramGuideSchedule<VideoItem>>()
                            for (videoItem in videosItemsArray) {

                                if (channel.id == videoItem.channelId) {

                                    val ldt: LocalDateTime = LocalDateTime.parse(
                                        convertTimeFormat(
                                            videoItem.startDate,
                                            "yyyy-MM-dd HH:mm:ss",
                                            "yyyy-MM-dd'T'HH:mm:ss"
                                        ),
                                    )
                                    val ldtEnd: LocalDateTime = LocalDateTime.parse(
                                        convertTimeFormat(
                                            videoItem.endDate,
                                            "yyyy-MM-dd HH:mm:ss",
                                            "yyyy-MM-dd'T'HH:mm:ss"
                                        ),
                                    )
                                    val instantStartDate: Instant =
                                        ldt.atZone(
                                            ZoneId.systemDefault()
                                        ).toInstant()
                                    val instantEndDate: Instant =
                                        ldtEnd.atZone(
                                            ZoneId.systemDefault()
                                        ).toInstant()

                                    val schedule = createSchedule(
                                        videoItem.id,
                                        videoItem.title,
                                        instantStartDate,
                                        instantEndDate,
                                        videoItem
                                    )
                                    scheduleList.add(schedule)

                                }

                            }

                            channelMap[channel.id] = scheduleList
                        }

                    }

                    return@fromCallable Pair(channelsArray, channelMap)

                }.delay(1, TimeUnit.SECONDS)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        setData(it.first, it.second, FixedLocalDateTime.now().toLocalDate())
                        if (it.first.isEmpty() || it.second.isEmpty()) {
                            setState(State.Error("No channels loaded."))
                        } else {
                            setState(State.Content)
                        }
                    }, {
                        Log.e(TAG, "Unable to load example data!", it)
                    })

            }


        }

    }

    fun setupExoPlayer(video_url: String, durationStart: Float) {

        val build = DefaultLoadControl.Builder()
            .setBufferDurationsMs(32 * 1024, 64 * 1024, 1024, 1024).createDefaultLoadControl()

        val extensionRendererMode =
            DefaultRenderersFactory(requireContext()).setExtensionRendererMode(
                DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER
            )
//        factory.defaultRequestProperties.set("Referer", "https://gocast2.com/")
        val build2 = TrackSelectionParameters.Builder(requireContext()).build()
        mediaDataSourceFactory =
            ExoPlayer.Builder(requireContext(), extensionRendererMode).setLoadControl(build)
                .setMediaSourceFactory(createMediaSourceFactory())


        setRenderersFactory(mediaDataSourceFactory, false)
        player = mediaDataSourceFactory.build()
        player.trackSelectionParameters = build2

        exoPlayerView.player = player
        exoPlayerView.useController = true
        exoPlayerView.requestFocus()
        exoPlayerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
        exoPlayerView.controllerAutoShow = false
        exoPlayerView.keepScreenOn = true
        exoPlayerView.setShowSubtitleButton(true)
//        player.setMediaSource(
//            ProgressiveMediaSource.Factory(factory)
//                .createMediaSource(MediaItem.fromUri(Uri.parse(video_url)))
//        )

        val mediaItem = MediaItem.Builder()
            .setMimeType("video/mp4")
            .setUri(video_url)
            .build()

        player.setMediaItem(mediaItem)
        player.prepare()
        player.playWhenReady = true
        player.seekTo(durationStart.toLong())
        player.addListener(object : Player.Listener {
            @SuppressLint("WrongConstant")
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == 3) {
                    progressBar.visibility = 8
//                    contentLoaded = true
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                player.stop()
                Log.e("TAG", "onPlayerError: " + error.message)
//                errorDialog()
            }
        }
        )

        exoPlayerView.controllerShowTimeoutMs = 3500
        exoPlayerView.setControllerVisibilityListener(StyledPlayerControlView.VisibilityListener { i ->
            if (i == 0) {
                exoPlayerView.systemUiVisibility = 4871
            } else if (i == 8) {
//                toolbarPlayer.setVisibility(View.GONE)
            }
        })


        /*VimeoExtractor.getInstance()
            .fetchVideoWithURL(
                "https://vimeo.com/524933864",
                null,
                object : OnVimeoExtractionListener {
                    override fun onSuccess(video: VimeoVideo) {
                        val hdStream = video.streams["1080p"]

                        android.os.Handler(Looper.getMainLooper()).postDelayed({
                            val mediaItem = MediaItem.Builder()
                                .setMimeType("video/mp4")
                                .setUri(hdStream)
                                .build()

                            player.setMediaItem(mediaItem)
                            player.prepare()
                            player.playWhenReady = true
                            player.addListener(object : Player.Listener {
                                @SuppressLint("WrongConstant")
                                override fun onPlaybackStateChanged(playbackState: Int) {
                                    if (playbackState == 3) {
                                        progressBar.visibility = 8
//                    contentLoaded = true
                                    }
                                }

                                override fun onPlayerError(error: PlaybackException) {
                                    player.stop()
                                    Log.e("TAG", "onPlayerError: " + error.message)
//                errorDialog()
                                }
                            }
                            )
                        }, 2000)

                    }

                    override fun onFailure(throwable: Throwable) {
                        //Error handling here
                    }
                })*/


        /*val extractor: YouTubeExtractor = YouTubeExtractor.create()
        mExtractor.extract("9d8wWcJLnFI").enqueue(object : Callback<YouTubeExtractionResult?>() {
            fun onResponse(
                call: Call<YouTubeExtractionResult?>?,
                response: Response<YouTubeExtractionResult?>?
            ) {
                val hdUri: Uri = result.getHd1080VideoUri()
                //See the sample for more
            }

            fun onFailure(call: Call<YouTubeExtractionResult?>?, t: Throwable) {
                t.printStackTrace()
                //Alert your user!
            }
        })*/


//        val youtubeLink = "https://youtu.be/tUesv5u5bvA"
//
//        object : YouTubeExtractor(requireContext()) {
//            override fun onExtractionComplete(ytFiles: SparseArray<YtFile>?, vMeta: VideoMeta?) {
//                if (ytFiles != null) {
//
//                    if (vMeta != null) {
//                        Log.e(TAG, "onExtractionComplete: "+vMeta.channelId )
//                        Log.e(TAG, "onExtractionComplete: "+vMeta.hqImageUrl )
//                        Log.e(TAG, "onExtractionComplete: "+vMeta.videoId )
//                        Log.e(TAG, "onExtractionComplete: "+vMeta.videoLength )
//                        Log.e(TAG, "onExtractionComplete: "+vMeta.title )
//                    }
//
//                    val itag = 22
//                    val downloadUrl: String = ytFiles[itag].getUrl()
//
//                    val mediaItem = MediaItem.Builder()
//                        .setMimeType("video/mp4")
//                        .setUri(downloadUrl)
//                        .build()
//
//                    player.setMediaItem(mediaItem)
//                    player.prepare()
//                    player.playWhenReady = true
//                    player.addListener(object : Player.Listener {
//                        @SuppressLint("WrongConstant")
//                        override fun onPlaybackStateChanged(playbackState: Int) {
//                            if (playbackState == 3) {
//                                progressBar.visibility = 8
////                    contentLoaded = true
//                            }
//                        }
//
//                        override fun onPlayerError(error: PlaybackException) {
//                            player.stop()
//                            Log.e("TAG", "onPlayerError: "+error.message )
////                errorDialog()
//                        }
//                    }
//                    )
//                }
//            }
//        }.extract(Objects.requireNonNull(youtubeLink).toString(),true,true)


    }

    private fun setRenderersFactory(builder: ExoPlayer.Builder, z: Boolean) {
        builder.setRenderersFactory(DemoUtil.buildRenderersFactory(requireContext(), z))
    }

    private fun createMediaSourceFactory(): MediaSource.Factory {
        val defaultDrmSessionManagerProvider = DefaultDrmSessionManagerProvider()
        defaultDrmSessionManagerProvider.setDrmHttpDataSourceFactory(
            DemoUtil.getHttpDataSourceFactory(
                requireContext()
            ) as HttpDataSource.Factory
        )
        return DefaultMediaSourceFactory(requireContext()).setDrmSessionManagerProvider(
            defaultDrmSessionManagerProvider as DrmSessionManagerProvider
        )
    }

}
