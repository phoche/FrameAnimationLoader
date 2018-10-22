package com.example.bolo.os_application.utils

import android.util.Log

/**
 * Create by bolo on 17/09/2018
 */

const val DEFAULT_LOG_TAG = "OS_Application"

fun info(msg: String?, tag: String = DEFAULT_LOG_TAG) {
    Log.i(tag, msg)
}

fun error(msg: String?, tag: String = DEFAULT_LOG_TAG) {
    Log.e(tag, msg)
}