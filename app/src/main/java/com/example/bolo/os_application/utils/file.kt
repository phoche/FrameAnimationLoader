package com.example.bolo.os_application.utils

import android.content.pm.PackageManager
import android.os.Environment
import android.os.Environment.MEDIA_MOUNTED
import android.support.annotation.WorkerThread
import android.util.Log
import com.example.bolo.os_application.OsApplication
import java.io.Closeable
import java.io.File
import java.io.FileOutputStream

/**
 * Create by bolo on 17/09/2018
 */
const val EXTERNAL_STORAGE_PERMISSION = "android.permission.WRITE_EXTERNAL_STORAGE"

fun getFileDirectory(): File? {
    return if (existSDCard() && hasExternalStoragePermission()) {
        val dataDir = File(File(Environment.getExternalStorageDirectory(), "Android"), "data")
        File(File(dataDir, OsApplication.mApplication.packageName), "file")
    } else {
        OsApplication.mApplication.filesDir
    }
}

fun existSDCard(): Boolean {
    var externalStorageState = try {
        Environment.getExternalStorageState()
    } catch (e: NullPointerException) { // (sh)it happens (Issue #660)
        ""
    }

    return MEDIA_MOUNTED == externalStorageState
}

@WorkerThread
fun copyImageFromAssets(block: () -> Unit) {
    val imageDir = getImageFileDir()
    val assets = OsApplication.mApplication.assets
    var num = 0

    val list = assets.list("image")
    if (mImageDirExisted) {
        info("copy task end_______________")
        block()
        return
    }
    info("copy task start_______________")
    list.forEach {
        it?.let {
            var fos: FileOutputStream? = null
            try {
                val ins = assets.open("image" + File.separator + it)
                fos = FileOutputStream(File(imageDir + File.separator + "image_$num.png"))

                val buffer = ByteArray(8192)
                do {
                    val count = ins.read(buffer)
                    if (count <= 0) {
                        break
                    }

                    fos.write(buffer, 0, count)
                } while (true)
                num ++
            } catch (e: Exception) {
            } finally {
                fos.closeQuietly()
            }
        }
    }
    block()
}

private var mImageDirExisted: Boolean = false


fun getImageFileDir(): String? {
    return getFileDirectory()?.let {
        val imageDir = File(it.absolutePath + File.separator + "image")
        if (!imageDir.exists()) {
            imageDir.mkdirs()
        }
        mImageDirExisted = imageDir.list().isNotEmpty()
        imageDir.absolutePath
    }
}

private fun hasExternalStoragePermission(): Boolean {
    return OsApplication.mApplication
            .checkCallingOrSelfPermission(EXTERNAL_STORAGE_PERMISSION) == PackageManager.PERMISSION_GRANTED
}

inline fun Closeable?.closeQuietly() {
    try {
        this?.close()
    } catch (e: Exception) {
    }
}

