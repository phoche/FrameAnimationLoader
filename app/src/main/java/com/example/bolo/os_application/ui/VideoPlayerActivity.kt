package com.example.bolo.os_application.ui

import android.media.MediaPlayer
import android.os.Bundle
import butterknife.ButterKnife
import com.example.bolo.os_application.R
import com.example.bolo.os_application.loader.FrameDispatcher
import com.example.bolo.os_application.utils.copyImageFromAssets
import kotlinx.android.synthetic.main.activity_video_player.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch

class VideoPlayerActivity : BaseActivity() {

    private val tag = "VideoPlayerActivity"

    private var mDispatcher: FrameDispatcher? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_player)
        ButterKnife.bind(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        mDispatcher?.cancel()
    }

    override fun init() {
        copyImage()
    }

    private fun copyImage() {
        showLoading()
        launch(CommonPool) {
            copyImageFromAssets {
                launch(UI) {
                    hideLoading()

                    openVideo()
                }
            }
        }
    }

    private var mIsRunning = false

    private fun openVideo() {
        mDispatcher = FrameDispatcher(video_view, image_view)
        mDispatcher!!.preLoad {
            launch(UI) {
                if (!mIsRunning) {
                    video_view.start()
                    mDispatcher?.start()
                    mIsRunning = true
                }
            }
        }

        val uri = getVideoInRaw(R.raw.frame_test_720)
        video_view.setVideoPath(uri)
    }
}
