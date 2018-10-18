package com.example.bolo.os_application.utils

import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import android.widget.Toast
import com.example.bolo.os_application.OsApplication
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch

/**
 * Create by bolo on 09/10/2018
 */
object ToastUtil {

    private var mToast: Toast? = null

    fun showToast(@StringRes strRes: Int, duration: Int) {
        launch(UI) {
            mToast?.cancel()
            mToast = Toast.makeText(OsApplication.mApplication, strRes, duration)
            mToast?.show()
        }
    }

    fun showToast(str: String, duration: Int) {
        launch(UI) {
            mToast?.cancel()
            mToast = Toast.makeText(OsApplication.mApplication, str, duration)
            mToast?.show()
        }
    }
}

class ToastWrapper {

    @DrawableRes
    var res: Int? = null

    var text: String? = null

    var duration: Int = Toast.LENGTH_SHORT
}

fun toast(init: ToastWrapper.() -> Unit) {
    val wrap = ToastWrapper()
    wrap.init()
    execute(wrap)
}

private fun execute(wrap: ToastWrapper) {

    wrap.res?.let {
        if (it != 0) {
            ToastUtil.showToast(it, wrap.duration)
        }
    }

    wrap.text?.let {
        if (it.isNotEmpty()) {
            ToastUtil.showToast(it, wrap.duration)
        }
    }
}