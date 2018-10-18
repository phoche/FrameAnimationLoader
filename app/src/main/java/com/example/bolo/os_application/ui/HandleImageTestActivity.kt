package com.example.bolo.os_application.ui

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import butterknife.BindView
import butterknife.ButterKnife
import com.example.bolo.os_application.ConvertImageFilter
import com.example.bolo.os_application.OsApplication
import com.example.bolo.os_application.R
import com.example.bolo.os_application.utils.closeQuietly
import jp.co.cyberagent.android.gpuimage.GPUImage

private const val MONOCHROME_IMG_NAME = "monochrome.png"
private const val COLORFUL_IMG_NAME = "colorful.png"

class HandleImageTestActivity : BaseActivity() {

    @BindView(R.id.iv_monochrome)
    lateinit var mMonochromeImg: ImageView
    @BindView(R.id.iv_colorful)
    lateinit var mColorfulImg: ImageView
    @BindView(R.id.iv_after)
    lateinit var mResultImage: ImageView


    override fun init() {
        assets.apply {
            val monochromeIs = open(MONOCHROME_IMG_NAME)
            val colorfulIs = open(COLORFUL_IMG_NAME)

            val monochromeBitmap = BitmapFactory.decodeStream(monochromeIs)
            mMonochromeImg.setImageBitmap(monochromeBitmap)

            val bitmap = BitmapFactory.decodeStream(colorfulIs)
            mColorfulImg.setImageBitmap(bitmap)

            val gpuImage = GPUImage(OsApplication.mApplication)
            gpuImage.setImage(bitmap)
            val convertImageFilter = ConvertImageFilter()
            convertImageFilter.bitmap = monochromeBitmap
            gpuImage.setFilter(convertImageFilter)

            mResultImage.setImageBitmap(gpuImage.bitmapWithFilterApplied)

            monochromeIs.closeQuietly()
            colorfulIs.closeQuietly()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_handle_image_test)
        ButterKnife.bind(this)
    }
}
