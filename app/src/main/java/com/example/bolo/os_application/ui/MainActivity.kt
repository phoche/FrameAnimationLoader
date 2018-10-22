package com.example.bolo.os_application.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import butterknife.ButterKnife
import butterknife.OnClick
import com.example.bolo.os_application.R
import com.example.bolo.os_application.permission.PermissionCheckHelper
import com.example.bolo.os_application.permission.PermissionRequestInfo
import com.example.bolo.os_application.utils.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import java.io.File


const val natantVideoName = "supernatant.mp4"
const val natantVideoCoverName = "supernatant_cover.mp4"
const val externalVideoPath = "ad_video"


class MainActivity : BaseActivity() {


    override fun init() {
        obtainPermission()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)
    }

    @OnClick(R.id.bt_handle_video, R.id.bt_play_video, R.id.bt_handle_image_test, R.id.bt_handle_image)
    fun onClick(view: View) {
        when (view.id) {
            R.id.bt_play_video -> startActivity(Intent(this, VideoPlayerActivity::class.java))
            R.id.bt_handle_video -> startActivity(Intent(this, HandleVideoActivity::class.java))
            R.id.bt_handle_image_test -> startActivity(Intent(this, HandleImageTestActivity::class.java))
            R.id.bt_handle_image -> startActivity(Intent(this, HandleImageActivity::class.java))
        }

    }

    private fun obtainPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && !PermissionCheckHelper.isPermissionGranted(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            val requestInfo = PermissionRequestInfo.Builder()
                    .setRequestCode(0)
                    .setRequestPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE)
                    .setTipMessages("read file", "write file")
                    .setCallbackListener { requestCode, _, grantResults ->
                        if (requestCode != 0 || grantResults[0] != PackageManager
                                        .PERMISSION_GRANTED) {

                            toast { res = R.string.non_permission_tip }
                        } else {

                            copyVideoFile()
                        }
                    }
                    .build()
            PermissionCheckHelper.instance().requestPermissions(this, requestInfo)
        } else {
            copyVideoFile()
        }
    }

    private fun copyVideoFile() {
        showLoading()
        launch(CommonPool) {
            mutableMapOf(
                    natantVideoName to getCacheDirectory().absolutePath +
                            File.separator + externalVideoPath,
                    natantVideoCoverName to getCacheDirectory().absolutePath +
                            File.separator + externalVideoPath).forEach {
                copyFileFromAssets(it.key, it.value)
            }
            info("copy task complete", copyVideoTaskTag)
            launch(UI) {
                hideLoading()
            }
        }
    }
}

