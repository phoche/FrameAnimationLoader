package com.example.bolo.os_application.ui

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.bolo.os_application.R
import com.example.bolo.os_application.permission.PermissionCheckHelper
import com.example.bolo.os_application.permission.PermissionRequestInfo
import com.example.bolo.os_application.utils.copyImageFromAssets
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch

class MainActivity : AppCompatActivity() {

    private var mInitialized = false

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

    private fun init() {
        obtainPermission()
    }

    private fun copyImage() {
        mLoadingDialog.show(supportFragmentManager, "loading")
        launch(CommonPool) {
            copyImageFromAssets {
                launch(UI) {
                    mLoadingDialog.dismiss()
                }
            }
        }
    }


    private fun obtainPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && !PermissionCheckHelper.isPermissionGranted(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            val requestInfo = PermissionRequestInfo.Builder()
                    .setRequestCode(0)
                    .setRequestPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .setTipMessages("read pic")
                    .setCallbackListener { _, _, _ ->
                        copyImage()
                    }
                    .build()
            PermissionCheckHelper.instance().requestPermissions(this, requestInfo)
        } else {
            copyImage()
        }
    }
}
