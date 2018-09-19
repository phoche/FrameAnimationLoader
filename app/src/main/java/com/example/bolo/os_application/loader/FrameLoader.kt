package com.example.bolo.os_application.loader

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.LruCache
import android.widget.ImageView
import android.widget.MediaController
import com.example.bolo.os_application.OsApplication
import com.example.bolo.os_application.R
import com.example.bolo.os_application.utils.calculateImageViewSize
import com.example.bolo.os_application.utils.getImageFileDir
import com.example.bolo.os_application.utils.info
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import java.io.File
import java.lang.ref.WeakReference
import java.util.*
import java.util.regex.Pattern

/**
 * Create by bolo on 18/09/2018
 */
private const val MSG_PRE_LOAD = 0X110
private const val MSG_DISPLAY_IMAGE = 0X220

class FrameDispatcher(private val controller: MediaController.MediaPlayerControl,
                      private val imageView: ImageView) {

    private val TAG = "FrameDispatcher"

    private val mViewSize: ViewSize

    init {
        initImageFiles()

        calculateDisplayInfo()

        mViewSize = imageView.calculateImageViewSize()
    }

    private val mLruCache: LruCache<Int, Bitmap> by lazy {
        object : LruCache<Int, Bitmap>((Runtime.getRuntime().maxMemory() / 8).toInt()) {
            override fun sizeOf(key: Int?, value: Bitmap?): Int {
                return value?.byteCount ?: super.sizeOf(key, value)
            }
        }
    }

    private var mPreloadJob: Job? = null

    private var mDisplayImageJob: Job? = null

    private val mDisplayHandle = DisplayHandler(this)

    private var mImageList: Array<File>? = null

    private var mImageDisplayDelay = 0L

    private var mFPS = 0

    private var mMillisPercentFPS = 0

    private var mPreLoadBitmapDelay = 1000L

    private var mTotalSize = 0

    private var mPreIndex = -1

    fun start() {
        mDisplayImageJob?.cancel()
        mDisplayImageJob = launch(UI) {
            if (mImageList == null) {
                return@launch
            }

            val currentPosition = controller.currentPosition
            val index = currentPosition / mMillisPercentFPS
            if (index >= mImageList!!.size) {
                imageView.background = null
                return@launch
            }

            if (mPreIndex != index && controller.isPlaying) {
                info("currentFrame : $index, ---- currentPosition = $currentPosition", TAG)
                imageView.setImageBitmap(getBitmap(index))
//                mLruCache.get(index)?.apply {
//                    imageView.setImageBitmap(this)
//                }
                mPreIndex = index
            }

            mDisplayHandle.sendEmptyMessageDelayed(MSG_DISPLAY_IMAGE, 16)
        }
    }

    /**
     * 预加载当前播放时间
     */
    fun preLoad() {
        mPreloadJob?.cancel()
        mPreloadJob = launch(CommonPool) {

            mImageList?.apply {
                // 加载当前时间之后两秒的 bitmap
                info("start cache", TAG)
                if (mPreIndex > mTotalSize) {
                    return@launch
                }
                info("CacheMaxSize mPreIndex = : $mPreIndex")
                val index = if (mPreIndex < 0) 0 else mPreIndex
                val afterPosition = index + mFPS/* * 2*/
                for (i in index..afterPosition) {
                    if (i >= size) {
                        return@launch
                    }

                    val bitmap = mLruCache.get(i)
                    if (null == bitmap || bitmap.isRecycled) {
                        val file = this[i]


                        val opts = BitmapFactory.Options()
                        decodeBitmap(file, opts)
                        opts.inJustDecodeBounds = true
                        opts.inSampleSize = calculateSampleSize(opts, mViewSize.width, mViewSize.height)
                        opts.inJustDecodeBounds = false
//                        info("cacheFileName --- : ${file.name}", TAG)
                        val decodeBitmap = decodeBitmap(file, opts)
                        info("CacheMaxSize decodeBitmap byteCount = : ${decodeBitmap.byteCount}")
                        if (decodeBitmap != null) {
                            mLruCache.put(i, decodeBitmap)
                            info("CacheMaxSize : ${mLruCache.maxSize()}, \n CacheSize : " +
                                    "${mLruCache.size()} \n index : $i", TAG)
                        }
                    }
                }
//                mPosition = afterPosition
                mDisplayHandle.sendEmptyMessageDelayed(MSG_PRE_LOAD, mPreLoadBitmapDelay)
            }
        }
    }

    private fun getBitmap(index: Int): Bitmap? {
        var bgBitmap: Bitmap? = null
        mImageList?.let {
            if (index in 0..(it.size - 1)) {
                val bitmap = mLruCache.get(index)
                if (null == bitmap || bitmap.isRecycled) {
                    val opts = BitmapFactory.Options()
                    opts.inJustDecodeBounds = true
                    opts.inSampleSize = calculateSampleSize(opts, mViewSize.width, mViewSize.height)
                    opts.inJustDecodeBounds = false
                    val file = it[index]
                    info("cacheFileName --- : ${file.name}", TAG)
                    bgBitmap = decodeBitmap(file, opts)
                    if (bgBitmap != null) {
                        mLruCache.put(index, bgBitmap)
                    }
                }
            }
        }
        return bgBitmap
    }

    private fun decodeBitmap(file: File, opts: BitmapFactory.Options): Bitmap {
        opts.inPreferredConfig = Bitmap.Config.RGB_565
        return BitmapFactory.decodeFile(file.absolutePath, opts)
    }


    private fun calculateSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int)
            : Int {
        val width = options.outWidth
        val height = options.outHeight

        var inSampleSize = 1
        if (width > reqWidth || height > reqHeight) {
            val widthRound = Math.round(width * 1f / reqWidth)
            val heightRound = Math.round(height * 1f / reqWidth)

            inSampleSize = Math.max(widthRound, heightRound)
        }
        return inSampleSize + 1
    }

    /**
     * 计算播放信息
     */
    private fun calculateDisplayInfo() {
        val uri = "android.resource://${OsApplication.mApplication.packageName}/${R.raw.frame_test_720}"
        try {
            val mmr = MediaMetadataRetriever()
            mmr.setDataSource(OsApplication.mApplication, Uri.parse(uri))
            val duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)

            mImageList?.apply {
                mTotalSize = size
                (size / (duration.toInt().div(1000))).apply {
                    // 计算帧数
                    mFPS = this
                    mMillisPercentFPS = 1000.div(mFPS)
                    // 计算图片播放的间隔
                    mImageDisplayDelay = 1000.div(this).toLong()
                }
            }
        } catch (e: Exception) {
        }
    }

    /**
     * 获取图片文件
     */
    private fun initImageFiles() {
        getImageFileDir()?.apply {
            val pattern = Pattern.compile("(\\d+)\\..*")
            mImageList = listFiles()
            mImageList?.sortWith(Comparator { o1, o2 ->

                val matcherO1 = pattern.matcher(o1?.name)
                val matcherO2 = pattern.matcher(o2?.name)

                var indexO1 = 0
                var indexO2 = 0
                if (matcherO1.find() && matcherO2.find()) {
                    indexO1 = matcherO1.group(1).toInt()
                    indexO2 = matcherO2.group(1).toInt()
                }

                val i = when {
                    indexO1 > indexO2 -> 1
                    indexO1 == indexO2 -> 0
                    else -> -1
                }
                info("comparator result : $i")
                i
            })
        }
    }


    class DisplayHandler(loader: FrameDispatcher) : Handler(Looper.getMainLooper()) {

        private val mReference = WeakReference<FrameDispatcher>(loader)

        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)

            mReference.get()?.apply {
                when (msg?.what) {
                    MSG_PRE_LOAD -> preLoad()
                    MSG_DISPLAY_IMAGE -> start()
                }
            }
        }
    }
}

data class ViewSize(val width: Int, val height: Int)