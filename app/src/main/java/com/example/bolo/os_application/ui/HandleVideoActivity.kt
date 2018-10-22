package com.example.bolo.os_application.ui

import android.os.Bundle
import android.widget.Toast
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.bolo.ffmpeglibrary.FFmpegUtil
import com.example.bolo.os_application.R
import com.example.bolo.os_application.utils.getCacheDirectory
import com.example.bolo.os_application.utils.info
import com.example.bolo.os_application.utils.toast
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.CoroutineStart
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import java.io.File

const val FRAME_COLORFUL_IMAGE_FILE_PATH = "image"
const val FRAME_MONOCHROME_IMAGE_FILE_PATH = "cover_image"
const val FRAME_CONVERT_COMPLETE_FILE_PATH = "complete_image"

class HandleVideoActivity : BaseActivity() {

    private val mNatantVideoFile: File by lazy {
        File(getCacheDirectory(), externalVideoPath + File.separator + natantVideoName)
    }

    private val mFrameColorfulImageFilePath: String by lazy {
        val path = mNatantVideoFile.parent + File.separator + FRAME_COLORFUL_IMAGE_FILE_PATH
        val file = File(path)
        if (!file.exists()) {
            file.mkdirs()
        }
        path
    }

    private val mNatantCoverVideoFile: File by lazy {
        File(getCacheDirectory(), externalVideoPath + File.separator + natantVideoCoverName)
    }

    private val mFrameMonochromeImageFilePath: String by lazy {
        val path = mNatantCoverVideoFile.parent + File.separator + FRAME_MONOCHROME_IMAGE_FILE_PATH
        val file = File(path)
        if (!file.exists()) {
            file.mkdirs()
        }
        path
    }


    private val mParseTag = "ParseVideoInfo"

    private var mParseVideoJob: Job? = null

    private var mList = listOf<Job>()

    @BindView(R.id.gsy_video)
    lateinit var mGSYVideoView: StandardGSYVideoPlayer

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_handle_video)
        ButterKnife.bind(this)
    }

    override fun init() {
        if (!mNatantCoverVideoFile.exists() && !mNatantVideoFile.exists()) {
            toast { text = "视频文件不存在" }
        }

        mGSYVideoView.setUp(mNatantVideoFile.absolutePath, false, "supernatant")

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
        showLoading("正在解析视频......")
        val ffmpegUtil = FFmpegUtil()
        val completeJob = launch(UI, CoroutineStart.LAZY) {
            toast { res = R.string.parse_video_complete }
            hideLoading()
        }
        mParseVideoJob = launch(CommonPool) {

            val videoUrls = arrayOf(mNatantVideoFile.absolutePath,
                    mNatantCoverVideoFile.absolutePath)

            val colorfulFile = File(mFrameColorfulImageFilePath)
            val monochromeFile = File(mFrameMonochromeImageFilePath)
            val list = colorfulFile.list()
            val list1 = monochromeFile.list()
            if ((colorfulFile.exists() && monochromeFile.exists()) &&
                    list.isNotEmpty() && list1.isNotEmpty() &&
                    list.size == list1.size) {
                completeJob.join()
                return@launch
            }

            colorfulFile.deleteRecursively()
            monochromeFile.deleteRecursively()

            colorfulFile.mkdirs()
            monochromeFile.mkdirs()

            val imagePaths = arrayOf("$mFrameColorfulImageFilePath/%09d.png",
                    "$mFrameMonochromeImageFilePath/%09d.png")

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
            info(getString(R.string.parse_video_complete), mParseTag)
            completeJob.join()
        }
    }
}
