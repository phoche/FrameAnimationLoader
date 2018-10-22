package com.example.bolo.os_application.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.example.bolo.os_application.ConvertImageFilter
import com.example.bolo.os_application.OsApplication
import com.example.bolo.os_application.R
import com.example.bolo.os_application.loader.ViewSize
import com.example.bolo.os_application.utils.*
import jp.co.cyberagent.android.gpuimage.GPUImage
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.CoroutineStart
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import java.io.File
import java.io.FileOutputStream

class HandleImageActivity : BaseActivity() {

    private val mNatantVideoFile: File by lazy {
        File(getCacheDirectory(), externalVideoPath +
                File.separator + FRAME_COLORFUL_IMAGE_FILE_PATH)
    }

    private val mNatantCoverVideoFile: File by lazy {
        File(getCacheDirectory(), externalVideoPath
                + File.separator + FRAME_MONOCHROME_IMAGE_FILE_PATH)
    }

    private val mFrameConvertFilePath: String by lazy {
        getCacheDirectory().absolutePath + File.separator + externalVideoPath +
                File.separator + FRAME_CONVERT_COMPLETE_FILE_PATH
    }

    private val mDisplayTag = "DisplayCompletedImage"

    @BindView(R.id.iv_completed)
    lateinit var mCompletedImg: ImageView

    private var mResultBitmap: Bitmap? = null

    private val mDisplayImageJob: Job by lazy {
        launch(UI, CoroutineStart.LAZY) {
            mCompletedImg.setImageBitmap(mResultBitmap)
        }
    }

    private val mImageSize: ViewSize by lazy {
        mCompletedImg.calculateImageViewSize()
    }

    @OnClick(R.id.bt_start_handle)
    fun onClick() {

        val colorfulList = mNatantVideoFile.list()
        val monochromeList = mNatantCoverVideoFile.list()


        val list = mNatantVideoFile.list()
        val list1 = mNatantCoverVideoFile.list()
        if ((mNatantVideoFile.exists() && mNatantCoverVideoFile.exists()) &&
                list.isNotEmpty() && list1.isNotEmpty() &&
                list.size == list1.size) {
            return
        }

        mNatantVideoFile.deleteRecursively()
        mNatantCoverVideoFile.deleteRecursively()

        mNatantVideoFile.mkdirs()
        mNatantCoverVideoFile.mkdirs()

        colorfulList.sortWithNatureIndex()
        monochromeList.sortWithNatureIndex()

        if ((colorfulList.isNotEmpty() && monochromeList.isNotEmpty()) &&
                colorfulList.size == monochromeList.size) {

            showLoading()
            launch(CommonPool) {
                val opts = BitmapFactory.Options()
                opts.inJustDecodeBounds = true
                opts.inSampleSize = 2/*opts.calculateSampleSize(mImageSize.width, mImageSize
                .height)*/
                opts.inJustDecodeBounds = false

                val resultFile = File(mFrameConvertFilePath)
                if (!resultFile.exists()) {
                    resultFile.mkdirs()
                }


                colorfulList.forEachIndexed { index, path ->

                    val colorfulImgPath = "${mNatantVideoFile.absolutePath}${File.separator}$path"
                    val colorfulBitmap = BitmapFactory.decodeFile(colorfulImgPath, opts)

                    val monochromeImgPath = "${mNatantCoverVideoFile.absolutePath}${File.separator}${monochromeList[index]}"
                    val monochromeBitmap = BitmapFactory.decodeFile(monochromeImgPath, opts)

                    info("colorfulPath : $colorfulImgPath; monochromePath : $monochromeImgPath")

                    val gpuImage = GPUImage(OsApplication.mApplication)
                    gpuImage.setImage(colorfulBitmap)
                    val filter = ConvertImageFilter()
                    filter.bitmap = monochromeBitmap
                    gpuImage.setFilter(filter)

                    val resultBitmap = gpuImage.bitmapWithFilterApplied


                    info("正在保存第 ${index + 1} 张图片", mDisplayTag)
                    val fos = FileOutputStream(mFrameConvertFilePath +
                            File.separator + path.replaceAfter(".", "webp"))
                    resultBitmap.compress(Bitmap.CompressFormat.WEBP, 100, fos)
                    fos.flush()
                    fos.closeQuietly()
                }
                launch(UI) {
                    toast { text = "mission completed" }
                    hideLoading()
                }
            }
        }
    }

    override fun init() {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_handle_image)
        ButterKnife.bind(this)
    }
}
