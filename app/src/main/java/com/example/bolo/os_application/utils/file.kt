package com.example.bolo.os_application.utils

import android.content.pm.PackageManager
import android.os.Environment
import android.os.Environment.MEDIA_MOUNTED
import android.support.annotation.WorkerThread
import com.example.bolo.os_application.OsApplication
import com.example.bolo.os_application.ui.FRAME_CONVERT_COMPLETE_FILE_PATH
import java.io.Closeable
import java.io.File
import java.io.FileOutputStream
import java.util.regex.Pattern

/**
 * Create by bolo on 17/09/2018
 */
private const val EXTERNAL_STORAGE_PERMISSION = "android.permission.WRITE_EXTERNAL_STORAGE"

private const val ASSETS_IMAGE_FILE = "image1"
//private const val ASSETS_IMAGE_FILE = "image"

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

class FileString(val name: String)

@WorkerThread
fun copyImageFromAssets(block: () -> Unit) {
    val imageDir = getImageFileDir()
    val assets = OsApplication.mApplication.assets
    var num = 0

    val list = assets.list(ASSETS_IMAGE_FILE)
    if (mImageDirExisted) {
        info("copy task end_______________")
        block()
        return
    }
    info("copy task start_______________")

    list?.sortWithNatureIndex()


    list.forEach {
        it?.let {
            info("copy task ============ $it")
            var fos: FileOutputStream? = null
            try {
                val ins = assets.open(ASSETS_IMAGE_FILE + File.separator + it)
                fos = FileOutputStream(File(imageDir, "image_$num.png"))

                val buffer = ByteArray(8192)
                do {
                    val count = ins.read(buffer)
                    if (count <= 0) {
                        break
                    }

                    fos.write(buffer, 0, count)
                } while (true)
                num++
            } catch (e: Exception) {
            } finally {
                fos.closeQuietly()
            }
        }
    }
    info("copy task end_______________")
    block()
}

private var mImageDirExisted: Boolean = false


fun getImageFileDir(): File? {
    return getFileDirectory()?.let {
        val imageDir = File(it.absolutePath + File.separator + ASSETS_IMAGE_FILE)
        if (!imageDir.exists()) {
            imageDir.mkdirs()
        }

        mImageDirExisted = imageDir.list().isNotEmpty()
        imageDir
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

inline fun Array<String>.sortWithNatureIndex() {
    val pattern = Pattern.compile("(\\d+)\\..*")
    sortWith(Comparator { o1, o2 ->

        val matcherO1 = pattern.matcher(o1)
        val matcherO2 = pattern.matcher(o2)

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
        i
    })
}

