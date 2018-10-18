package com.example.bolo.os_application.ui

import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.bolo.ffmpeglibrary.FFmpegUtil
import com.example.bolo.os_application.R
import com.example.bolo.os_application.utils.info
import com.example.bolo.os_application.utils.toast
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.launch

const val FRAME_COLORFUL_IMAGE_FILE_PATH = "/storage/emulated/0/Movies/image"
const val FRAME_MONOCHROME_IMAGE_FILE_PATH = "/storage/emulated/0/Movies/cover_image"
const val FRAME_CONVERT_COMPLETE_FILE_PATH = "/storage/emulated/0/Movies/complete_image"

class HandleVideoActivity : BaseActivity() {

    private val mParseTag = "Parse Video info"

    private lateinit var mVideoCoverUrl: String
    private lateinit var mVideoUrl: String

    private val mFPS = 25

    private var mParseVideoJob: Job? = null

    private var mShowImageJob: Job? = null

    private var mList = listOf<Job>()

    @BindView(R.id.iv_parse_result)
    lateinit var mParseResultImg: ImageView

    @BindView(R.id.gsy_video)
    lateinit var mGSYVideoView: StandardGSYVideoPlayer

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_handle_video)
        ButterKnife.bind(this)
    }

    override fun init() {
//        mVideoCoverUrl = getVideoInRaw(R.raw.supernatant_cover)
        mVideoCoverUrl = "/storage/emulated/0/Movies/supernatant_cover.mp4"
        mVideoUrl = getVideoInRaw(R.raw.supernatant)

        mGSYVideoView.setUp(mVideoCoverUrl, false, "supernatant_cover")

        toast { text = FFmpegUtil().stringFromJNI() }
    }

    @OnClick(R.id.bt_start_handle)
    fun onClick() {
        startHandleVideoQuickly()
    }

    override fun onPause() {
        super.onPause()
        mGSYVideoView.onVideoPause()
    }

    override fun onResume() {
        super.onResume()
        mGSYVideoView.onVideoResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        mParseVideoJob?.cancel()
        mList.forEach {
            it.cancel()
        }
    }

    private fun startHandleVideoQuickly() {

        val ffmpegUtil = FFmpegUtil()
        mParseVideoJob = launch(CommonPool) {

            val videoUrls = arrayOf("/storage/emulated/0/Movies/supernatant.mp4",
                    "/storage/emulated/0/Movies/supernatant_cover.mp4")

            val imagePaths = arrayOf("$FRAME_COLORFUL_IMAGE_FILE_PATH/%09d.png",
                    "$FRAME_MONOCHROME_IMAGE_FILE_PATH/%09d.png")

            videoUrls.forEachIndexed { index, s ->
                toast {
                    text = "正在解析第 ${index + 1} 段视频..."
                    duration = Toast.LENGTH_LONG
                }

                try {
                    val startTime = System.currentTimeMillis()
                    val cmd = "ffmpeg -i %s -ss 00:00:00 -f image2 %s".format(s, imagePaths[index])
                    val split = cmd.split(" ")
                    ffmpegUtil.run(split.size, split.toTypedArray())
                    info("视频解析完成, 耗时 ${System.currentTimeMillis() - startTime}ms", mParseTag)
                } catch (e: Exception) {
                    toast { text = "解析失败" }
                }
            }
            info("视频解析完成", mParseTag)
        }
    }
}
