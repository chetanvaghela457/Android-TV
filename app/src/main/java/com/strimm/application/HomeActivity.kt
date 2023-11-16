package com.strimm.application

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.strimm.application.databinding.ActivityHomeBinding
import com.strimm.application.di.ApplicationComponent
import com.strimm.application.di.DaggerApplicationComponent
import com.strimm.application.ui.viewmodel.MainViewModel
import com.strimm.application.ui.viewmodel.MainViewModelFactory
import com.strimm.application.utils.SharedPrefsManager
import javax.inject.Inject

class HomeActivity : AppCompatActivity() {

    lateinit var mainViewModel: MainViewModel

    lateinit var applicationComponent: ApplicationComponent

    @Inject
    lateinit var mainViewModelFactory: MainViewModelFactory

    private var _binding: ActivityHomeBinding? = null
    private val binding get() = _binding!!
//    lateinit var mediaDataSourceFactory: ExoPlayer.Builder
//
//    lateinit var player: ExoPlayer
//    lateinit var exoPlayerView: com.google.android.exoplayer2.ui.StyledPlayerView
//    lateinit var progressBar: ProgressBar
//
//    private var currentVideoIndex = 0
//
//    val arrayOfVideo = arrayOf(
//        "http://sample.vodobox.net/skate_phantom_flex_4k/skate_phantom_flex_4k.m3u8",
//        "http://playertest.longtailvideo.com/adaptive/wowzaid3/playlist.m3u8",
//        "http://content.jwplatform.com/manifests/vM7nH0Kl.m3u8",
//        "https://vimeo.com/510860875",
//        "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
//        "https://vimeo.com/524933864",
//        "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4",
//        "https://vimeo.com/481040219",
//        "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerMeltdowns.mp4"
//    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(
            SharedPrefsManager.newInstance(this).getInt("AppTheme", R.style.Theme_EStrimTheme3)
        )
        _binding = ActivityHomeBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)

        applicationComponent = DaggerApplicationComponent.builder().build()
        applicationComponent.injectHome(this)

        mainViewModel = ViewModelProvider(this, mainViewModelFactory).get(MainViewModel::class.java)

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_browse_fragment, EpgFragment())
                .commitNow()
        }

        /*exoPlayerView = findViewById<StyledPlayerView>(R.id.exoPlayerView)
        progressBar = findViewById<ProgressBar>(R.id.progressBar)

        setupExoPlayer()*/
    }

    /*fun setupExoPlayer() {

        val build = DefaultLoadControl.Builder()
            .setBufferDurationsMs(32 * 1024, 64 * 1024, 1024, 1024).createDefaultLoadControl()

        val extensionRendererMode =
            DefaultRenderersFactory(this).setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER)
//        factory.defaultRequestProperties.set("Referer", "https://gocast2.com/")
        val build2 = TrackSelectionParameters.Builder(this).build()
        mediaDataSourceFactory =
            ExoPlayer.Builder(this, extensionRendererMode).setLoadControl(build)
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


        exoPlayerView.controllerShowTimeoutMs = 3500
        exoPlayerView.setControllerVisibilityListener(StyledPlayerControlView.VisibilityListener { i ->
            if (i == 0) {
                exoPlayerView.systemUiVisibility = 4871
            } else if (i == 8) {
//                toolbarPlayer.setVisibility(View.GONE)
            }
        })

//        val youtubeLink = "https://youtu.be/tUesv5u5bvA"
//
//        object : YouTubeExtractor(this) {
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
////                    val itag = 22
////                    val downloadUrl: String = ytFiles[itag].getUrl()
//
//                }
//            }
//        }.extract(Objects.requireNonNull(youtubeLink).toString(),true,true)


        player.addListener(object : Player.Listener {
            @SuppressLint("WrongConstant")
            override fun onPlaybackStateChanged(playbackState: Int) {

                if (playbackState == Player.STATE_ENDED) {
                    playNextVideo()
                }
                if (playbackState == Player.STATE_READY) {
                    progressBar.visibility = 8
//                    playVideo(arrayOfVideo[currentVideoIndex])
//                    contentLoaded = true
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                player.stop()
                Log.e("TAG", "onPlayerError: " + error.message)
//                errorDialog()
            }
        })


        playVideo(arrayOfVideo[currentVideoIndex])

        *//*VimeoExtractor.getInstance()
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

                        }, 2000)

                    }

                    override fun onFailure(throwable: Throwable) {
                        //Error handling here
                    }
                })*//*


        *//*val extractor: YouTubeExtractor = YouTubeExtractor.create()
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
        })
*//*


    }

    private fun playVideo(videoUrl: String) {

        if (videoUrl.contains("vimeo.com")) {

            Log.e("TAG", "playVideo: 111111111")
            VimeoExtractor.getInstance()
                .fetchVideoWithURL(
                    videoUrl,
                    null,
                    object : OnVimeoExtractionListener {
                        override fun onSuccess(video: VimeoVideo) {
                            val hdStream = video.streams["720p"]

                            android.os.Handler(Looper.getMainLooper()).postDelayed({
                                val mediaItem = MediaItem.Builder()
                                    .setMimeType("video/mp4")
                                    .setUri(hdStream)
                                    .build()

                                player.setMediaItem(mediaItem)
                                player.prepare()
                                player.playWhenReady = true

                            }, 1000)

                        }

                        override fun onFailure(throwable: Throwable) {
                            Log.e(TAG, "onFailure: " + throwable.message)
                            //Error handling here
                        }
                    })

        } else {

            Log.e("TAG", "playVideo: 2222222")
            android.os.Handler(Looper.getMainLooper()).postDelayed({
                val mediaItem = MediaItem.Builder()
                    .setUri(videoUrl)
                    .build()
                player.setMediaItem(mediaItem)
                player.prepare()
                player.playWhenReady = true
            }, 2000)
        }
    }


    private fun playNextVideo() {
        currentVideoIndex++
        if (currentVideoIndex < arrayOfVideo.size) {

            playVideo(arrayOfVideo[currentVideoIndex])
        } else {
            // All videos have been played
            // You can handle this case as needed, e.g., show a message or return to the first video
            currentVideoIndex = 0
            playVideo(arrayOfVideo[currentVideoIndex])
        }
    }


    private fun setRenderersFactory(builder: ExoPlayer.Builder, z: Boolean) {
        builder.setRenderersFactory(DemoUtil.buildRenderersFactory(this, z))
    }

    private fun createMediaSourceFactory(): MediaSource.Factory {
        val defaultDrmSessionManagerProvider = DefaultDrmSessionManagerProvider()
        defaultDrmSessionManagerProvider.setDrmHttpDataSourceFactory(
            DemoUtil.getHttpDataSourceFactory(
                this
            ) as HttpDataSource.Factory
        )
        return DefaultMediaSourceFactory(this).setDrmSessionManagerProvider(
            defaultDrmSessionManagerProvider as DrmSessionManagerProvider
        )
    }*/
}