package com.example.bolo.os_application.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.example.bolo.os_application.R
import com.example.bolo.os_application.loader.FrameDispatcher
import com.example.bolo.os_application.permission.PermissionCheckHelper
import com.example.bolo.os_application.permission.PermissionRequestInfo
import com.example.bolo.os_application.utils.copyImageFromAssets
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch

class MainActivity : AppCompatActivity() {

    private val tag = "MainActivity"

    private var mInitialized = false

    private var mDispatcher: FrameDispatcher? = null

    private val mLoadingDialog: LoadingDialog by lazy {
        LoadingDialog()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    override fun onStart() {
        super.onStart()
        if (!mInitialized) {
            init()
            mInitialized = true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mDispatcher?.cancel()
    }

    private fun init() {
        obtainPermission()
//        openVideo()
    }

    private fun copyImage() {
        mLoadingDialog.show(supportFragmentManager, "loading")
        launch(CommonPool) {
            copyImageFromAssets {
                launch(UI) {
                    mLoadingDialog.dismiss()

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

        val uri = "android.resource://$packageName/${R.raw.frame_test_720}"
        video_view.setVideoPath(uri)
//        video_view.start()
    }


    private fun obtainPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && !PermissionCheckHelper.isPermissionGranted(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            val requestInfo = PermissionRequestInfo.Builder()
                    .setRequestCode(0)
                    .setRequestPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .setTipMessages("read pic")
                    .setCallbackListener { requestCode, _, grantResults ->
                        if (requestCode == 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                            copyImage()
                        } else {
                            Toast.makeText(this, R.string.non_permission_tip, Toast.LENGTH_SHORT)
                                    .show()
                        }
                    }
                    .build()
            PermissionCheckHelper.instance().requestPermissions(this, requestInfo)
        } else {
            copyImage()
        }
    }
}
