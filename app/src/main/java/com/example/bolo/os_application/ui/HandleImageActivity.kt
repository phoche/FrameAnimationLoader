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

    private val mDiaplayTag = "Display Completed Image"

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
        val colorFulImgFile = File(FRAME_COLORFUL_IMAGE_FILE_PATH)
        val monochromeImgFile = File(FRAME_MONOCHROME_IMAGE_FILE_PATH)


        val colorfulList = colorFulImgFile.list()
        val monochromeList = monochromeImgFile.list()

        colorfulList.sortWithNatureIndex()
        monochromeList.sortWithNatureIndex()

//        val options = BitmapFactory.Options()
//        options.inPreferredConfig = Bitmap.Config.RGB_565
        if ((colorfulList.isNotEmpty() && monochromeList.isNotEmpty()) &&
                colorfulList.size == monochromeList.size) {

            showLoading()
            launch(CommonPool) {
                val opts = BitmapFactory.Options()
                opts.inJustDecodeBounds = true
                opts.inSampleSize = opts.calculateSampleSize(mImageSize.width, mImageSize.height)
                opts.inJustDecodeBounds = false

                val resultFile = File(FRAME_CONVERT_COMPLETE_FILE_PATH)
                if (!resultFile.exists()) {
                    resultFile.mkdirs()
                }

//                for ((index, value) in colorfulList.withIndex()) {
//
//
//                }

                colorfulList.forEachIndexed { index, path ->

                    //                    val colorfulIs = FileInputStream(path)

                    val colorfulImgPath = "$FRAME_COLORFUL_IMAGE_FILE_PATH${File.separator}$path"
                    val colorfulBitmap = BitmapFactory.decodeFile(colorfulImgPath/*, opts*/)

                    val monochromeImgPath = "$FRAME_MONOCHROME_IMAGE_FILE_PATH${File.separator}${monochromeList[index]}"
                    val monochromeBitmap = BitmapFactory.decodeFile(monochromeImgPath/*, opts*/)

                    info("colorfulPath : $colorfulImgPath; monochromePath : $monochromeImgPath")

                    val gpuImage = GPUImage(OsApplication.mApplication)
                    gpuImage.setImage(colorfulBitmap)
                    val filter = ConvertImageFilter()
                    filter.bitmap = monochromeBitmap
                    gpuImage.setFilter(filter)

                    val resultBitmap = gpuImage.bitmapWithFilterApplied


                    info("正在保存第 ${index + 1} 张图片", mDiaplayTag)
                    val fos = FileOutputStream(FRAME_CONVERT_COMPLETE_FILE_PATH +
                            File.separator + path.replaceAfter(".", "webp"))
                    resultBitmap.compress(Bitmap.CompressFormat.WEBP, 100, fos)
                    fos.flush()
                    fos.closeQuietly()

//                    mResultBitmap = gpuImage.bitmapWithFilterApplied
//                    launch(UI) {
//                        mCompletedImg.setImageBitmap(null)
//                        mCompletedImg.setImageBitmap(gpuImage.bitmapWithFilterApplied)
//                    }
//                    colorfulIs.closeQuietly()
//                    monochromeIs.closeQuietly()
//                    colorfulBitmap.recycle()
//                    monochromeBitmap.recycle()
                }
                launch(UI) {
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
